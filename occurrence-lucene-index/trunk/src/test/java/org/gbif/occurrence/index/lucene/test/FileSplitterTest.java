/**
 * 
 */
package org.gbif.occurrence.index.lucene.test;

import org.gbif.ocurrence.index.lucene.FileSplitter;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * @author fede
 */

public class FileSplitterTest {

  private static final String testFileName = "splitTestFile.txt";
  private static final int partitionSize = 9;

  /**
   * 
   */
  public FileSplitterTest() {
    super();
  }

  @Test
  public void splitFileTest() {
    try {
      String fileName = FileSplitterTest.class.getClassLoader().getResource(testFileName).getPath();
      int linesCountTotalOriginal = FileSplitter.getLinesCount(fileName);
      List<String> fileNames = FileSplitter.getData(fileName, partitionSize);
      Assert.assertTrue("File was not split", fileNames.size() > 0);
      int totalLinesCountSplit = 0;
      for (String file : fileNames) {
        totalLinesCountSplit += FileSplitter.getLinesCount(file);
        FileUtils.deleteQuietly(new File(file));
      }
      Assert.assertTrue("File was not split", linesCountTotalOriginal == totalLinesCountSplit);
    } catch (Exception ex) {
      Assert.fail(ex.getMessage());
    }
  }
}
