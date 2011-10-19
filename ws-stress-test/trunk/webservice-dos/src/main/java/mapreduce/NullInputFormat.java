package mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * "Borrowed" from:
 * http://grepcode.com/file_/repo1.maven.org/maven2/edu.umd/cloud9/1.0.0/edu/umd/cloud9/mapred/NullInputFormat.java/?v=source
 */
public class NullInputFormat implements InputFormat<NullWritable, NullWritable> {
  // Returns NulWritables for as many times as you tell it to
  public static class NullRecordReader implements RecordReader<NullWritable, NullWritable> {
    private int returned = 0;
    private int target = 1;
    public NullRecordReader(int count) {
      target=count;
    }

    public void close() {
    }

    public NullWritable createKey() {
      return NullWritable.get();
    }

    public NullWritable createValue() {
      return NullWritable.get();
    }

    public long getPos() throws IOException {
      return 0;
    }

    public float getProgress() throws IOException {
      return (float)returned / (float)target;
    }

    public boolean next(NullWritable key, NullWritable value) throws IOException {
      if (returned < target) {
        returned++;
        return true;
      } else {
        returned++;
        return false;
      }
    }
  }
  private static final Logger LOG = LoggerFactory.getLogger(NullInputFormat.class);

  public RecordReader<NullWritable, NullWritable> getRecordReader(InputSplit split, JobConf job, Reporter reporter) {
    return new NullRecordReader(job.getInt(DosAttack.NUM_REQUESTS_KEY, 1));
  }

  public InputSplit[] getSplits(JobConf job, int numSplits) {
    numSplits = job.getInt(DosAttack.NUM_MAPPERS_KEY, numSplits);
    LOG.info("Number of splits {}", numSplits);
    InputSplit[] splits = new InputSplit[numSplits];
    for (int i = 0; i < numSplits; i++) {
      splits[i] = new NullInputSplit();
    }
    return splits;
  }

  public void validateInput(JobConf job) {
  }
}
