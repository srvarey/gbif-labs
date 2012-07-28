package org.gbif.cube.gmap.density.backfill;


import org.gbif.cube.gmap.io.LatLngWritable;
import org.gbif.cube.gmap.io.TileKeyWritable;

import java.io.IOException;

import com.urbanairship.datacube.backfill.HBaseBackfillCallback;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;

/**
 * The callback used from the backfill process to spawn the job to write the new data in the cube.
 */
public class BackfillCallback implements HBaseBackfillCallback {

  @Override
  public void backfillInto(Configuration conf, byte[] table, byte[] cf, long snapshotFinishMs) throws IOException {
    conf = HBaseConfiguration.create(conf);
    Job job = new Job(conf, "density-cube backfill");
    job.setJarByClass(TileCollectorMapper.class); // required to set up MR classpaths

    // Set up efficient source table scanning
    Scan scan = new Scan();
    scan.setCaching(conf.getInt(Backfill.KEY_SCANNER_CACHE, Backfill.DEFAULT_SCANNER_CACHE));
    scan.setCacheBlocks(false);

    // we do not want to get bad counts in the cube!
    job.getConfiguration().set("mapred.map.tasks.speculative.execution", "false");
    job.getConfiguration().set("mapred.reduce.tasks.speculative.execution", "false");

    job.setNumReduceTasks(conf.getInt(Backfill.KEY_NUM_REDUCERS, Backfill.DEFAULT_NUM_REDUCERS));
    TableMapReduceUtil.initTableMapperJob(conf.get(Backfill.KEY_SOURCE_TABLE), scan, TileCollectorMapper.class, TileKeyWritable.class,
      LatLngWritable.class, job);
    job.setReducerClass(CubeWriterReducer.class);
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
