package org.gbif.cube.gmap.density.backfill;


import org.gbif.cube.gmap.density.io.OccurrenceWritable;
import org.gbif.cube.gmap.io.LatLngWritable;
import org.gbif.cube.gmap.io.TileKeyWritable;
import org.gbif.occurrencestore.api.model.constants.FieldName;
import org.gbif.occurrencestore.persistence.HBaseFieldUtil;

import java.io.IOException;
import java.util.UUID;

import com.urbanairship.datacube.backfill.HBaseBackfillCallback;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

/**
 * The callback used from the backfill process to spawn the job to write the new data in the cube.
 * This runs 2 MR jobs:
 * i) Group the occurrence records at the same location with the same identification in the same dataset.
 * ii) Build the cube for all the dimensions.
 * MR job i) is purely to reduce load, as MR job ii) emits several tiles at each zoom level (= a lot to shuffle and
 * sort)
 */
public class BackfillCallback implements HBaseBackfillCallback {

  private void addFieldToScan(Scan scan, FieldName fn) {
    scan.addColumn(Bytes.toBytes(HBaseFieldUtil.getHBaseField(fn).get(0)), Bytes.toBytes(HBaseFieldUtil.getHBaseField(fn).get(1)));
  }

  @Override
  public void backfillInto(Configuration conf, byte[] table, byte[] cf, long snapshotFinishMs) throws IOException {
    conf = HBaseConfiguration.create(conf);

    Job job = new Job(conf, "density-cube group by location");
    job.setJarByClass(TileCollectorMapper.class); // required to set up MR classpaths
    job.getConfiguration().set("mapred.map.tasks.speculative.execution", "false");
    job.getConfiguration().set("mapred.reduce.tasks.speculative.execution", "false");
    job.getConfiguration().set("mapred.task.timeout", "600000"); // 10 mins
    job.getConfiguration().set("hbase.regionserver.lease.period", "600000");
    job.getConfiguration().set("mapred.compress.map.output", "true");
    job.getConfiguration().set("mapred.output.compress", "true");

    Scan scan = getScanner(conf);
    TableMapReduceUtil.initTableMapperJob(conf.get(Backfill.KEY_SOURCE_TABLE), scan, LocationCollectorMapper.class, OccurrenceWritable.class,
      IntWritable.class, job);
    job.setReducerClass(GroupByOccurrenceReducer.class);
    job.setNumReduceTasks(conf.getInt(Backfill.KEY_NUM_REDUCERS, Backfill.DEFAULT_NUM_REDUCERS));
    String dir = UUID.randomUUID().toString();
    job.setOutputKeyClass(OccurrenceWritable.class);
    job.setOutputValueClass(IntWritable.class);
    job.setOutputFormatClass(SequenceFileOutputFormat.class);
    SequenceFileOutputFormat.setOutputPath(job, new Path("/tmp/" + dir));
    try {
      boolean b = job.waitForCompletion(true);
      if (!b) {
        throw new IOException("Unknown error with job.  Check the logs.");
      }
    } catch (Exception e) {
      throw new IOException(e);
    }


    job = new Job(conf, "density-cube backfill");
    job.setJarByClass(TileCollectorMapper.class); // required to set up MR classpaths

    job.setNumReduceTasks(conf.getInt(Backfill.KEY_NUM_REDUCERS, Backfill.DEFAULT_NUM_REDUCERS));
// TableMapReduceUtil.initTableMapperJob(conf.get(Backfill.KEY_SOURCE_TABLE), scan, TileCollectorMapper.class,
// TileKeyWritable.class,
// LatLngWritable.class, job);

    SequenceFileInputFormat.setInputPaths(job, new Path("/tmp/" + dir));
    SequenceFileInputFormat<OccurrenceWritable, IntWritable> sequenceInputFormat = new SequenceFileInputFormat<OccurrenceWritable, IntWritable>();
    job.setInputFormatClass(sequenceInputFormat.getClass());

    job.setMapperClass(TileCollectorMapper.class);
    job.setMapOutputKeyClass(TileKeyWritable.class);
    job.setMapOutputValueClass(LatLngWritable.class);

    // reduce the traffic between the M and R, and tell HBase and TT to relax the scan timeout to allow for combine time
// job.setCombinerClass(LocationCombiner.class);
// job.getConfiguration().set("mapred.task.timeout", "3600000"); // 1hr
// job.getConfiguration().set("hbase.regionserver.lease.period", "3600000");
    job.getConfiguration().set("mapred.compress.map.output", "true");

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

    // TODO clean the temp directory
  }

  private Scan getScanner(Configuration conf) {
    Scan scan = new Scan();
    scan.setCaching(conf.getInt(Backfill.KEY_SCANNER_CACHE, Backfill.DEFAULT_SCANNER_CACHE));
    scan.setCacheBlocks(false); // not needed for scanning

    // Optimize the scan by bringing back only what the TileCollectMapper wants
    addFieldToScan(scan, FieldName.I_KINGDOM_ID);
    addFieldToScan(scan, FieldName.I_PHYLUM_ID);
    addFieldToScan(scan, FieldName.I_CLASS_ID);
    addFieldToScan(scan, FieldName.I_ORDER_ID);
    addFieldToScan(scan, FieldName.I_FAMILY_ID);
    addFieldToScan(scan, FieldName.I_GENUS_ID);
    addFieldToScan(scan, FieldName.I_SPECIES_ID);
    addFieldToScan(scan, FieldName.I_NUB_ID);
    addFieldToScan(scan, FieldName.I_OWNING_ORG_KEY);
    addFieldToScan(scan, FieldName.I_DATASET_KEY);
    addFieldToScan(scan, FieldName.DATA_RESOURCE_ID);
    addFieldToScan(scan, FieldName.I_ISO_COUNTRY_CODE);
    addFieldToScan(scan, FieldName.I_LATITUDE);
    addFieldToScan(scan, FieldName.I_LONGITUDE);
    addFieldToScan(scan, FieldName.I_GEOSPATIAL_ISSUE);
    return scan;
  }
}
