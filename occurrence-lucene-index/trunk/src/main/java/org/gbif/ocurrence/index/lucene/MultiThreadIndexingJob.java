/**
 * 
 */
package org.gbif.ocurrence.index.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author fede
 */
public class MultiThreadIndexingJob extends BaseMultiThreadIndexingJob<IndexWriter> {

  static final class LuceneJob extends BaseMultiThreadIndexingJob.Job<IndexWriter> {

    public LuceneJob(IndexWriter indexWriter, String inputFile, int logBatchSize, boolean removeInputFile) {
      super(indexWriter, inputFile, logBatchSize, false, removeInputFile);
    }

    @Override
    public void addDocument(StringTokenizer stringTokenizer) {
      try {
        int fieldsCount = 0;
        Document doc = LocalndexingJob.getBaseDocument();
        while (stringTokenizer.hasMoreElements()) {
          OccurrenceLuceneFields.fields[fieldsCount].setValue(stringTokenizer.nextToken());
          fieldsCount += 1;
        }
        indexWriter.addDocument(doc);
      } catch (CorruptIndexException e) {
        log.error("Error commiting changes", e);
      } catch (IOException e) {
        log.error("Error commiting changes", e);
      }
    }

    @Override
    public void sendCommit() {
      try {
        this.indexWriter.commit();
      } catch (CorruptIndexException e) {
        log.error("Error commiting changes", e);
      } catch (IOException e) {
        log.error("Error commiting changes", e);
      }
    }
  }

  private Integer mergeFactor;

  private Integer bufferSize;

  private String outputDir;

  private IndexWriter indexWriter;

  public MultiThreadIndexingJob(String confFile) {
    super(confFile);
    this.mergeFactor = Integer.parseInt(this.config.getProperty("mergeFactor"));
    this.bufferSize = Integer.parseInt(this.config.getProperty("bufferSize"));
    this.outputDir = this.config.getProperty("outputDir");
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    MultiThreadIndexingJob multiThreadIndexingJob = new MultiThreadIndexingJob(args[0]);
    multiThreadIndexingJob.run();

  }

  @Override
  public List<LuceneJob> getJobsFromFiles(List<String> files) {
    try {
      IndexWriterConfig indexWriterConfig = LocalndexingJob.getIndexWriterConfig(mergeFactor, bufferSize);
      File file = new File(outputDir);
      indexWriter = new IndexWriter(FSDirectory.open(file), indexWriterConfig);
      ArrayList<LuceneJob> jobs = new ArrayList<LuceneJob>();
      for (String fileName : files) {
        LuceneJob job = new LuceneJob(indexWriter, fileName, logBatchSize, this.removeInputFile);
        jobs.add(job);
      }
      return jobs;
    } catch (Exception e) {
      log.error("Error splitting file", e);
      throw new RuntimeException(e);
    }
  }

  public String getOutputDir() {
    return outputDir;
  }

  @Override
  protected void postShutdown() {
    try {
      if (this.optimize) {
        log.info("Optimizing index...");
        indexWriter.optimize();
        log.info("Done optimizing!");
      }
      log.info("Closing index...");
      indexWriter.close();
      log.info("Closing done!");
    } catch (Exception e) {
      log.error("Error closing/optimizing the index", e);
    }
  }
}
