/**
 * 
 */
package org.gbif.ocurrence.index.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.StopWatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;

/**
 * @author fede
 */
public abstract class BaseMultiThreadIndexingJob<T> extends ThreadPoolRunner<Integer> {

  public abstract static class Job<T> implements Callable<Integer> {

    protected T indexWriter;
    protected String inputFile;
    protected int logBatchSize;
    protected boolean useBatchCommit;
    protected boolean removeInputFile;
    protected Long fileStartPos;
    protected Long fileEndPos;

    public Job(T indexWriter, String inputFile, int logBatchSize, boolean useBatchCommit, boolean removeInputFile) {
      this.indexWriter = indexWriter;
      this.inputFile = inputFile;
      this.logBatchSize = logBatchSize;
      this.useBatchCommit = useBatchCommit;
      this.removeInputFile = removeInputFile;
    }

    public Job(T indexWriter, String inputFile, int logBatchSize, boolean useBatchCommit, boolean removeInputFile,
        Long fileStartPos, Long fileEndPos) {
      this.indexWriter = indexWriter;
      this.inputFile = inputFile;
      this.logBatchSize = logBatchSize;
      this.useBatchCommit = useBatchCommit;
      this.removeInputFile = removeInputFile;
      this.fileStartPos = fileStartPos;
      this.fileEndPos = fileEndPos;
    }

    public abstract void addDocument(StringTokenizer stringTokenizer);

    public int addDocuments() throws IOException {
      BufferedReader br = null;
      File fileInputFile = null;
      try {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        fileInputFile = new File(inputFile);
        br = new BufferedReader(new FileReader(fileInputFile));
        br.skip(this.fileStartPos);
        String text = "";
        log.info(String.format("Adding documents using input file %s", inputFile));
        int docCount = 0;
        long currentPosition = this.fileStartPos;
        while ((currentPosition < this.fileEndPos) && (text = br.readLine()) != null) {
          StringTokenizer stringTokenizer = new StringTokenizer(text, LocalndexingJob.FIELD_SEPARATOR);
          if (docCount == logBatchSize || (((docCount > logBatchSize) && ((docCount % logBatchSize) == 0)))) {
            log.info(String.format("%s Documents added using input file %s", docCount, inputFile));
            if (useBatchCommit) {
              this.sendCommit();
            }
          }
          this.addDocument(stringTokenizer);
          docCount += 1;
          currentPosition += text.getBytes().length;
        }
        stopWatch.stop();
        log.info(String.format("# of documents: %s", docCount));
        log.info(String.format("Total time: %s", stopWatch.getTime()));
        return docCount;
      } catch (Exception ex) {
        log.error(String.format("Error adding documents using file %", this.inputFile), ex);
      } finally {
        br.close();
        if (this.removeInputFile) {
          FileUtils.deleteQuietly(fileInputFile);
        }
      }
      return 0;
    }

    @Override
    public Integer call() throws Exception {
      return addDocuments();
    }

    public T getIndexWriter() {
      return indexWriter;
    }

    public abstract void sendCommit();

    public void setIndexWriter(T indexWriter) {
      this.indexWriter = indexWriter;
    }
  }

  protected String inputFile;

  protected List<String> inputSplitFiles;

  protected Integer splitSize;

  protected Integer logBatchSize;

  protected boolean removeInputFile;

  protected boolean optimize;

  public static final Character lineBreak = new Character('\n');

  public BaseMultiThreadIndexingJob(String confFile) {
    super(confFile);
    this.inputFile = this.config.getProperty("inputFile");
    this.splitSize = Integer.parseInt(this.config.getProperty("splitSize"));
    this.logBatchSize = Integer.parseInt(this.config.getProperty("logBatchSize"));
    String splitFiles = this.config.getProperty("inputSplitFiles");
    if (splitFiles != null && !splitFiles.isEmpty()) {
      this.inputSplitFiles = Arrays.asList(splitFiles.split(","));
    }
    this.removeInputFile = Boolean.parseBoolean(this.config.getProperty("removeInputFile"));
    this.optimize = Boolean.parseBoolean(this.config.getProperty("optimize"));
  }

  @Override
  public List<? extends Callable<Integer>> createJobList() {
    try {
      StopWatch stopWatch = new StopWatch();
      stopWatch.start();
      if (this.inputSplitFiles == null) {
        File rootFile = new File(this.inputFile);
        if (rootFile.isDirectory()) {
          this.inputSplitFiles = new ArrayList<String>();
          for (File f : rootFile.listFiles()) {
            this.inputSplitFiles.add(f.getAbsolutePath());
          }
        }
      }
      List<? extends Job<T>> jobs = this.getJobsFromFiles(this.inputSplitFiles);
      stopWatch.stop();
      log.info(String.format("Job creation done in: %s", stopWatch.getTime()));
      return jobs;
    } catch (Exception e) {
      log.error("Error splitting file", e);
    }
    return null;
  }

  public String getInputFile() {
    return inputFile;
  }

  public abstract List<? extends Job<T>> getJobsFromFiles(List<String> files);

  public Integer getSplitSize() {
    return splitSize;
  }

  protected abstract void postShutdown();

  public void setInputFile(String inputFile) {
    this.inputFile = inputFile;
  }

  public void setSplitSize(Integer splitSize) {
    this.splitSize = splitSize;
  }

  @Override
  protected void shutdownService(int tasksCount) {
    try {
      super.shutdownService(tasksCount);
      this.postShutdown();
      log.info("Shuttingdown completed!");
    } catch (Exception e) {
      log.error("Error shutingdown the index", e);
    }
  }

}
