package org.gbif.cube.gmap.density.backfill;


import org.gbif.cube.gmap.density.backfill.io.TileKeyWritable;
import org.gbif.cube.gmap.density.backfill.io.TileValueWritable;

import java.io.IOException;

import com.urbanairship.datacube.backfill.HBaseBackfillCallback;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;

/**
 * The callback used from the backfill process to spawn the job to write the new data in the cube.
 */
public class BackfillCallback implements HBaseBackfillCallback {

  // Property keys passed in on the job conf to the Mapper
  static final String TARGET_TABLE_KEY = "gbif:cubewriter:targetTable";
  static final String TARGET_CF_KEY = "gbif:cubewriter:targetCF";
  // Controls the scanner caching size for the source data scan (100-5000 is reasonable)
  private static final int SCAN_CACHE = 200;
  // The source data table
  // private static final String SOURCE_TABLE = "uat_occurrence";
  private static final String SOURCE_TABLE = "tim_occurrence";

  // private static final String SOURCE_TABLE = "tim_occurrence";

  @Override
  public void backfillInto(Configuration conf, byte[] table, byte[] cf, long snapshotFinishMs) throws IOException {
    conf = HBaseConfiguration.create();
    conf.set(TARGET_TABLE_KEY, Bytes.toString(table));
    conf.set(TARGET_CF_KEY, Bytes.toString(cf));
    Job job = new Job(conf, "CubeWriterMapper");

    job.setJarByClass(TileCollectMapper.class);
    Scan scan = new Scan();
    scan.setCaching(SCAN_CACHE);
    scan.setCacheBlocks(false);

    // we do not want to get bad counts in the cube!
    job.getConfiguration().set("mapred.map.tasks.speculative.execution", "false");
    job.getConfiguration().set("mapred.reduce.tasks.speculative.execution", "false");
    // TODO make configurable, but this is effectively controls load on cube puts
    job.setNumReduceTasks(120);
    TableMapReduceUtil.initTableMapperJob(SOURCE_TABLE, scan, TileCollectMapper.class, TileKeyWritable.class, TileValueWritable.class, job);
    job.setReducerClass(CubeWriteReducer.class);
    job.setOutputFormatClass(NullOutputFormat.class);
    try {
      boolean b = job.waitForCompletion(true);
      if (!b) {
        throw new IOException("Unknown error with job.  Check the logs.");
      }
    } catch (Exception e) {
      throw new IOException(e);
    }
  }
}
