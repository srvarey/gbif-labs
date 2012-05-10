package org.apache.hadoop.hbase.mapred;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.StringUtils;

/**
 * Hacky backport of PutSortReducer to the mapred api.
 */
public class OldPutSortReducer implements Reducer<ImmutableBytesWritable, Put, ImmutableBytesWritable, KeyValue> {
  private long threshold;

  public void reduce(ImmutableBytesWritable row, Iterator<Put> puts,
    OutputCollector<ImmutableBytesWritable, KeyValue> outputCollector, Reporter reporter)
    throws IOException {
    // although reduce() is called per-row, handle pathological case
    // TODO moved this to config and global variable - not sure if that makes sense, thread-wise
//    long threshold = context.getConfiguration().getLong("putsortreducer.row.threshold", 2L * (1 << 30));
    while (puts.hasNext()) {
      TreeSet<KeyValue> map = new TreeSet<KeyValue>(KeyValue.COMPARATOR);
      long curSize = 0;
      // stop at the end or the RAM threshold
      while (puts.hasNext() && curSize < threshold) {
        Put p = puts.next();
        for (List<KeyValue> kvs : p.getFamilyMap().values()) {
          for (KeyValue kv : kvs) {
            map.add(kv);
            curSize += kv.getValueLength();
          }
        }
      }
      reporter.setStatus(
        "Read " + map.size() + " entries of " + map.getClass() + "(" + StringUtils.humanReadableInt(curSize) + ")");
      int index = 0;
      for (KeyValue kv : map) {
        outputCollector.collect(row, kv);
        if (index > 0 && index % 100 == 0) reporter.setStatus("Wrote " + index);
      }

      // if we have more entries to process
      if (puts.hasNext()) {
        // force flush because we cannot guarantee intra-row sorted order
        // TODO not at all sure that collect(null, null) has same effect as new api context.write....
        outputCollector.collect(null, null);
//        context.write(null, null);
      }
    }
  }

  public void close() throws IOException {
    // TODO: Write implementation
  }

  public void configure(JobConf jobConf) {
    threshold = jobConf.getLong("putsortreducer.row.threshold", 2L * (1 << 30));
  }
}
