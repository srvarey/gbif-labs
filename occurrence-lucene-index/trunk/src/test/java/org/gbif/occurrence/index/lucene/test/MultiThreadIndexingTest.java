/**
 * 
 */
package org.gbif.occurrence.index.lucene.test;

import org.gbif.ocurrence.index.lucene.MultiThreadIndexingJob;

import org.junit.Test;

/**
 * @author fede
 */
public class MultiThreadIndexingTest {

  private static final String inputFile = "splitTestFile.txt";

  /**
   * 
   */
  public MultiThreadIndexingTest() {
    super();
  }

  @Test
  public void splitIndexTest() {
    String testFile = MultiThreadIndexingTest.class.getClassLoader().getResource("testConf.properties").getPath();
    MultiThreadIndexingJob multiThreadIndexingJob = new MultiThreadIndexingJob(testFile);
    multiThreadIndexingJob.setSplitSize(2);
    multiThreadIndexingJob.setInputFile(MultiThreadIndexingJob.class.getClassLoader().getResource(inputFile).getPath());
    multiThreadIndexingJob.run();
  }

}
