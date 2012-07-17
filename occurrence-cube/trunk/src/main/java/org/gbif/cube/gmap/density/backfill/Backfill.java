package org.gbif.cube.gmap.density.backfill;

import org.gbif.cube.gmap.density.ops.DensityTileOp;

import java.io.IOException;

import com.urbanairship.datacube.backfill.HBaseBackfill;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.io.hfile.Compression.Algorithm;
import org.apache.hadoop.hbase.regionserver.StoreFile.BloomType;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TODO make this generic
 */
public class Backfill {

  private static final Logger LOG = LoggerFactory.getLogger(Backfill.class);
  // Live cube table (gmap prefix for all tables)
  static final byte[] CUBE_TABLE = "gmap_cube".getBytes();
  // Snapshot of the live table used during backfill
  static final byte[] SNAPSHOT_TABLE = "gmap_snapshot".getBytes();
  // Backfill table built from the source
  static final byte[] BACKFILL_TABLE = "gmap_backfill".getBytes();
  // Utility table to provide a running count for the identifier service
  static final byte[] COUNTER_TABLE = "gmap_counter".getBytes();
  // Utility table to provide a mapping from source values to assigned identifiers
  static final byte[] LOOKUP_TABLE = "gmap_lookup".getBytes();
  // All DataCube tables use a single column family
  static final byte[] CF = "c".getBytes();

  // Entry point for the time being.
  // TODO: How do we expect to launch this really? (Oozie workflow?)
  public static void main(String[] args) {
    Backfill app = new Backfill();
    app.backfill();
  }

  /**
   * Runs the backfil process.
   * 
   * @throws IOException On any HBase communication errors
   */
  public void backfill() {
    Configuration conf = HBaseConfiguration.create();
    try {
      setup(conf);
      HBaseBackfill backfill =
        new HBaseBackfill(conf, new BackfillCallback(), CUBE_TABLE, SNAPSHOT_TABLE, BACKFILL_TABLE, CF, DensityTileOp.DensityTileOpDeserializer.class);

      backfill.runWithCheckedExceptions();
      cleanup(conf);
    } catch (IOException e) {
      LOG.error("Error running cube backfill", e);
    }
  }

  /**
   * Removes the existing snapshot and backfill tables if present.
   */
  private void cleanup(Configuration conf) throws IOException {
    cleanup(new HBaseAdmin(conf));
  }

  /**
   * Removes the existing snapshot and backfill tables if present.
   */
  private void cleanup(HBaseAdmin admin) throws IOException {
    if (admin.tableExists(SNAPSHOT_TABLE)) {
      LOG.info("Deleting table {}", Bytes.toString(SNAPSHOT_TABLE));
      admin.disableTable(SNAPSHOT_TABLE);
      admin.deleteTable(SNAPSHOT_TABLE);
    }
    if (admin.tableExists(BACKFILL_TABLE)) {
      LOG.info("Deleting table {}", Bytes.toString(BACKFILL_TABLE));
      admin.disableTable(BACKFILL_TABLE);
      admin.deleteTable(BACKFILL_TABLE);
    }
  }

  // utility to create a table if it does not exist
  private void createIfMissing(HBaseAdmin admin, byte[] t) throws IOException {
    createIfMissing(admin, t, null, null, 0);
  }

  // utility to create a table if it does not exist
  private void createIfMissing(HBaseAdmin admin, byte[] t, byte[] startKey, byte[] endKey, int numRegions) throws IOException {
    if (!admin.tableExists(t)) {
      LOG.info("Creating table {}", Bytes.toString(t));
      HColumnDescriptor cfDesc = new HColumnDescriptor(CF);
      cfDesc.setBloomFilterType(BloomType.NONE);
      cfDesc.setMaxVersions(1);
      cfDesc.setCompressionType(Algorithm.SNAPPY);
      HTableDescriptor tableDesc = new HTableDescriptor(t);
      tableDesc.addFamily(cfDesc);
      if (startKey != null && endKey != null && numRegions > 0) {
        admin.createTable(tableDesc, startKey, endKey, numRegions);
      } else {
        admin.createTable(tableDesc);
      }
    }
  }

  /**
   * Removes the existing snapshot and backfill tables if present.
   * Creates the cube,counter and lookup tables if missing.
   */
  private void setup(Configuration conf) throws IOException {
    HBaseAdmin admin = new HBaseAdmin(conf);
    cleanup(admin);
    // presplit it to help the load
    // TODO check this actually is a sane split strategy
    // createIfMissing(admin, CUBE_TABLE, Bytes.toBytes(1000000), Bytes.toBytes(50000000), 50);
    createIfMissing(admin, CUBE_TABLE);
    createIfMissing(admin, COUNTER_TABLE);
    createIfMissing(admin, LOOKUP_TABLE);
  }
}
