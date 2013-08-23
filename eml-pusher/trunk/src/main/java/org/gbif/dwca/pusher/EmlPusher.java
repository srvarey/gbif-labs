package org.gbif.dwca.pusher;

import org.gbif.api.service.registry.DatasetService;
import org.gbif.dwc.text.Archive;
import org.gbif.dwc.text.ArchiveFactory;
import org.gbif.dwc.text.UnsupportedArchiveException;
import org.gbif.registry.ws.client.guice.RegistryWsClientModule;
import org.gbif.ws.client.guice.SingleUserAuthModule;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility that will inspect a crawling filesystem and push all metadata documents found into the registry.
 */
public class EmlPusher {

  private static final Logger LOG = LoggerFactory.getLogger(EmlPusher.class);
  private static String dwcaSuffix = ".dwca";
  private int pushCounter;
  private int failCounter;
  private DatasetService datasetService;

  public void start(File rootDirectory, String registryUrl, String user, String password) throws UnsupportedArchiveException, IOException {
    initClients(registryUrl, user, password);

    pushCounter = 0;
    File[] archiveFiles = findArchives(rootDirectory);
    LOG.info("Found {} archives", archiveFiles.length);

    for (File f : archiveFiles) {
      push(f);
    }
    LOG.info("Done. {} metadata documents from {} archives pushed to registry, {} failed", pushCounter, archiveFiles.length, failCounter);
  }

  private void initClients(String registryUrl, String user, String password) {
    Properties p = new Properties();
    p.put("registry.ws.url", registryUrl);
    Injector inj = Guice.createInjector(new SingleUserAuthModule(user, password), new RegistryWsClientModule(p));
    datasetService = inj.getInstance(DatasetService.class);
  }

  private void push(File archiveFile) {
    UUID key = getDatasetKey(archiveFile);
    try {
      Archive arch = open(archiveFile);
      File eml = arch.getMetadataLocationFile();
      if (eml != null && eml.exists()) {
        InputStream in = new FileInputStream(eml);
        try {
          datasetService.insertMetadata(key, in);
          LOG.info("Pushed metadata document for dataset {} into registry", key);
          pushCounter++;
        } finally {
          in.close();
        }
      }
    } catch (UnsupportedArchiveException e) {
      LOG.warn("Skipping archive {} because of error[{}]", key, e.getMessage());
      failCounter++;

    } catch (Exception e) {
      LOG.error("Unexpected exception when pushing metadata for dataset {}: {}", key, e);
      failCounter++;
    }
  }

  private UUID getDatasetKey(File archiveFile) {
    File dir = archiveDir(archiveFile);
    return UUID.fromString(dir.getName());
  }

  private File archiveDir(File archiveFile) {
    return new File(archiveFile.getParentFile(), FilenameUtils.getBaseName(archiveFile.getName()));
  }

  private Archive open(File archiveFile) throws UnsupportedArchiveException {
    // does the folder already exist (it should have the same name as the archive)?
    File dir = archiveDir(archiveFile);
    try {
      if (dir.exists()) {
        return ArchiveFactory.openArchive(dir);
      } else {
        LOG.info("Decompress archive {}", archiveFile.getAbsoluteFile());
        return ArchiveFactory.openArchive(archiveFile, dir);
      }
    } catch (IOException e) {
      throw new UnsupportedArchiveException(e);
    }
  }

  // gets the list of archives
  private File[] findArchives(File rootDirectory) {
    return rootDirectory.listFiles((FileFilter) new SuffixFileFilter(dwcaSuffix, IOCase.INSENSITIVE));
  }

  public static void main(String[] args) {
    if (args.length != 4) {
      System.out.println("The EML pusher requires 4 commandline arguments: path-to-dwca-dir  registry-url  username  password");
      System.exit(1);
    }

    EmlPusher app = new EmlPusher();
    try {
      app.start(new File(args[0]), args[1], args[2], args[3]);

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }
}
