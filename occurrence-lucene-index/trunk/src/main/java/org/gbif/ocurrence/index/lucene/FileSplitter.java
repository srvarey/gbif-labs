package org.gbif.ocurrence.index.lucene;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileSplitter {

  private final static String NEWLINE = System.getProperty("line.separator");

  private final static Logger log = LoggerFactory.getLogger(FileSplitter.class);

  /**
   * reads a the content of a file and returns is as a String
   * 
   * @param filename
   *        the filename to read
   * @throws IOException
   */
  public static List<String> getData(String filename, int partitionSize) throws IOException {
    BufferedReader bufferedReader = null;
    try {
      // opens the file in a string buffer
      bufferedReader = new BufferedReader(new FileReader(filename));
      ArrayList<String> fileNames = new ArrayList<String>();
      // performs the splitting
      String line;
      int i = 0;
      int counter = 0;
      FileOutputStream fos = null;
      while ((line = bufferedReader.readLine()) != null) {
        if ((i == partitionSize) || ((i > partitionSize) && ((i % partitionSize) == 0)) || (i == 0)) {
          final String newFileName = filename + counter;
          if (fos != null) {
            log.debug("Closing file: " + filename + counter);
            fos.close();
          }

          fos = new FileOutputStream(newFileName);
          log.debug("New file created : " + newFileName);
          fileNames.add(newFileName);
          counter++;
        }
        fos.write(line.getBytes());
        fos.write(NEWLINE.getBytes());
        fos.flush();
        i++;
      }
      if (fos != null && fos.getChannel().isOpen()) {
        fos.close();
      }
      return fileNames;
    } catch (IOException e) {
      throw new IOException("unable to read from file: " + filename);
    } finally {
      IOUtils.closeQuietly(bufferedReader);
    }
  }

  public static int getLinesCount(String fileName) throws IOException {
    BufferedReader bufferedReader = null;
    try {
      // opens the file in a string buffer
      bufferedReader = new BufferedReader(new FileReader(fileName));
      int count = 0;
      while (bufferedReader.readLine() != null) {
        count++;
      }
      return count;
    } catch (IOException e) {
      throw new IOException("unable to read from file: " + fileName);
    } finally {
      IOUtils.closeQuietly(bufferedReader);
    }
  }
}
