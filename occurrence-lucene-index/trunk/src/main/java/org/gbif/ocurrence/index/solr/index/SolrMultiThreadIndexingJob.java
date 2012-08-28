package org.gbif.ocurrence.index.solr.index;

import org.gbif.ocurrence.index.lucene.BaseMultiThreadIndexingJob;
import org.gbif.ocurrence.index.lucene.OccurrenceFields;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;

public class SolrMultiThreadIndexingJob extends BaseMultiThreadIndexingJob<SolrServer> {

  private static final class SolrIndexingJob extends BaseMultiThreadIndexingJob.Job<SolrServer> {

    public final static OccurrenceFields[] accFieldsValues = OccurrenceFields.values();

    public SolrIndexingJob(SolrServer indexWriter, String inputFile, int logBatchSize, boolean removeInputFile,
        Long fileStartPos, Long fileEndPos) {
      super(indexWriter, inputFile, logBatchSize, true, removeInputFile, fileStartPos, fileEndPos);
    }

    @Override
    public void addDocument(StringTokenizer stringTokenizer) {
      int fieldsCount = 0;
      SolrInputDocument doc = new SolrInputDocument();
      SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat dateAndTimeOnlyFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
      while (stringTokenizer.hasMoreElements()) {
        String fieldValue = stringTokenizer.nextToken();
        if (!fieldValue.equals("\\N")) {
          doc.addField(accFieldsValues[fieldsCount].name(), fieldValue);
        }
        fieldsCount += 1;
      }
      try {
        Object strLatitude = doc.getFieldValue(OccurrenceFields.latitude.name());
        Object strLongitude = doc.getFieldValue(OccurrenceFields.longitude.name());
        if (strLatitude != null && strLongitude != null) {
          doc.addField("location", strLatitude + "," + strLongitude);
          doc.removeField(OccurrenceFields.latitude.name());
          doc.removeField(OccurrenceFields.longitude.name());
        }
        this.setDateField(doc, OccurrenceFields.occurrence_date, dateOnlyFormat);
        this.setDateField(doc, OccurrenceFields.modified, dateAndTimeOnlyFormat);
        this.indexWriter.add(doc);
      } catch (SolrServerException e) {
        log.error("Error adding document", e);
      } catch (IOException e) {
        log.error("Error adding document", e);
      }
    }

    @Override
    public void sendCommit() {
      try {
        this.indexWriter.commit();
      } catch (SolrServerException e) {
        log.error("Error adding document", e);
      } catch (IOException e) {
        log.error("Error adding document", e);
      }
    }

    private void setDateField(SolrInputDocument doc, OccurrenceFields field, SimpleDateFormat dateFormat) {
      Object occDate = doc.getFieldValue(field.name());
      if (occDate != null) {
        try {
          doc.setField(field.name(), dateFormat.parse((String) occDate));
        } catch (ParseException e) {
          log.error("Error parsing date occ_record id: {0}  date: {1}", doc.getFieldValue("id"), occDate);
        }
      }
    }
  }

  private static final String SOLR_HOME = "solr.solr.home";

  private static final String SOLR_CONF = "solr.properties";

  private CoreContainer coreContainer;

  private EmbeddedSolrServer server;

  public SolrMultiThreadIndexingJob() {
    super(SOLR_CONF);
    this.init();
  }

  public SolrMultiThreadIndexingJob(String configFile) {
    super(configFile);
    this.init();
  }

  public static LinkedHashMap<Long, Long> getReaders(String fileName, int splitQty) {
    File file = new File(fileName);
    LinkedHashMap<Long, Long> positions = new LinkedHashMap<Long, Long>();
    long length = file.length();
    long chunkSize = length / splitQty;
    long startPos = 0;
    long endPos = chunkSize;
    if (length == chunkSize) {
      positions.put(startPos, endPos);
    } else {
      try {
        FileInputStream fileInputStreamMain = new FileInputStream(file);
        fileInputStreamMain.skip(chunkSize);
        int intch = 0;
        /*
         * for (int i = 0; i < splitQty; i++) {
         * while ((intch != lineBreak) && (intch != -1)) {
         * intch = fileInputStreamMain.read();
         * endPos = endPos + 1;
         * }
         * positions.put(startPos, endPos);
         * startPos = endPos + 1;
         * endPos = Math.min(length, startPos + chunkSize);
         * if (endPos == length) {
         * positions.put(startPos, endPos);
         * break;
         * }
         * fileInputStreamMain.close();
         * fileInputStreamMain = new FileInputStream(file);
         * fileInputStreamMain.skip(endPos);
         * }
         */
        while (intch != -1) {
          intch = fileInputStreamMain.read();
          if (((char) intch) != lineBreak) {
            endPos = endPos + 1;
          } else {
            positions.put(startPos, endPos);
            startPos = endPos + 1;
            endPos = Math.min(length, startPos + chunkSize);
            if (endPos == length) {
              positions.put(startPos, endPos);
              break;
            }
            fileInputStreamMain.close();
            fileInputStreamMain = new FileInputStream(file);
            fileInputStreamMain.skip(endPos);
          }
        }
      } catch (Exception e) {
        log.error("Error calculating readers for file", e);
        throw new RuntimeException(e);
      }
    }
    log.info("Threads postions for file: " + fileName + ", positions: " + positions.toString());
    return positions;
  }

  public static LinkedHashMap<String, Integer> getThreadsPerFile(List<String> fileNames, int totalNumOfThreads) {
    List<File> files = new ArrayList<File>();
    LinkedHashMap<String, Integer> threadsPerFile = new LinkedHashMap<String, Integer>();
    long totalLength = 0;
    for (String fileName : fileNames) {
      File file = new File(fileName);
      files.add(file);
      totalLength += file.length();
    }
    long chunkAvgSize = totalLength / totalNumOfThreads;
    int numberOfThreads = 1;
    int totalAssignedThreads = 0;
    for (File f : files) {
      numberOfThreads = (int) Math.max(f.length() / chunkAvgSize, 1);
      totalAssignedThreads += numberOfThreads;
      threadsPerFile.put(f.getAbsolutePath(), numberOfThreads);
    }
    int remainingThreads = totalNumOfThreads - totalAssignedThreads;
    int remainingThreadsPerFile = Math.max(1, remainingThreads / threadsPerFile.keySet().size());
    for (int i = 0; i < remainingThreads;) {
      for (String key : threadsPerFile.keySet()) {
        threadsPerFile.put(key, threadsPerFile.get(key) + remainingThreadsPerFile);
        i += remainingThreadsPerFile;
        if (i >= remainingThreads) {
          break;
        }
      }
    }
    log.info("Assigned threads: " + threadsPerFile.toString());
    return threadsPerFile;
  }

  public static void main(String[] args) throws Exception {
    if (args.length > 2) {
      System.setProperty(SOLR_HOME, args[1]);
    }
    SolrMultiThreadIndexingJob solrMultithreadIndexingJob = new SolrMultiThreadIndexingJob(args[0]);
    solrMultithreadIndexingJob.run();
  }

  @Override
  public List<SolrIndexingJob> getJobsFromFiles(List<String> files) {
    LinkedHashMap<String, Integer> threadsPerFile = getThreadsPerFile(files, this.poolSize);
    List<SolrIndexingJob> solrIndexingJobs = new ArrayList<SolrMultiThreadIndexingJob.SolrIndexingJob>();

    for (String fileName : threadsPerFile.keySet()) {
      LinkedHashMap<Long, Long> readers = getReaders(fileName, threadsPerFile.get(fileName));
      for (Entry<Long, Long> entry : readers.entrySet()) {
        SolrIndexingJob job = new SolrIndexingJob(this.server, fileName, this.logBatchSize, this.removeInputFile,
            entry.getKey(), entry.getValue());
        solrIndexingJobs.add(job);
      }
    }
    return solrIndexingJobs;
  }

  private void init() {
    try {
      String solrHome = this.config.getProperty(SOLR_HOME);
      if (solrHome != null && !solrHome.isEmpty()) {
        System.setProperty(SOLR_HOME, solrHome);
      }
      CoreContainer.Initializer initializer = new CoreContainer.Initializer();
      coreContainer = initializer.initialize();
      server = new EmbeddedSolrServer(coreContainer, "");
      SolrPingResponse solrPingResponse = server.ping();
      log.info("Solr server started, ping response in: " + solrPingResponse.getQTime());
    } catch (Exception e) {
      log.error("Error starting up the server", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void postShutdown() {
    try {
      if (this.optimize) {
        log.info("Optimizing index...");
        server.optimize();
        log.info("Done optimizing!");
      }
      log.info("Commiting last changes and Stopping Solr cores container...");
      server.commit();
      coreContainer.shutdown();
      log.info("Solr cores container stopped and commit sent");
    } catch (Exception e) {
      log.error("Error shutingdown the index", e);
    }
  }

  public long totalFilesLength(List<String> fileNames) {
    long totalLength = 0;
    for (String fileName : fileNames) {
      File file = new File(fileName);
      totalLength += file.length();
    }
    return totalLength;
  }
}
