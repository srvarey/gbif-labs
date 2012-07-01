/**
 * 
 */
package org.gbif.occurrence.cube;

import java.io.IOException;

import com.google.common.base.Optional;
import com.urbanairship.datacube.DataCube;
import com.urbanairship.datacube.DataCubeIo;
import com.urbanairship.datacube.DbHarness;
import com.urbanairship.datacube.DbHarness.CommitType;
import com.urbanairship.datacube.IdService;
import com.urbanairship.datacube.ReadBuilder;
import com.urbanairship.datacube.SyncLevel;
import com.urbanairship.datacube.backfill.HBaseBackfill;
import com.urbanairship.datacube.dbharnesses.HBaseDbHarness;
import com.urbanairship.datacube.idservices.HBaseIdService;
import com.urbanairship.datacube.ops.LongOp;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.io.hfile.Compression.Algorithm;
import org.apache.hadoop.hbase.regionserver.StoreFile.BloomType;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A cube backfill uses the DataCube HBaseBackfill approach. This follows this procedure:
 * <ol>
 * <li>Snapshot the existing live running cube into a new table</li>
 * <li>Build a new cube based on the source data (e.g. from the GBIF occurrence table)</li>
 * <li>Merge the new cube and the live running cube, taking into account any ops that the live cube has received during
 * the backfill</li>
 * </ol>
 * Note that the snapshot and backfill tables will be truncated if they already exist, or created if they do not.
 * The only custom implementation beyond what DataCube gives us is the reading of the GBIF occurrence table to build the
 * new cube. This is implemented in an HBase table scan MapReduce job.
 * The cube uses identifiers to replace some dimensions to reduce space, and the identifier service is backed by a
 * counter table and a lookup table that maps the values (e.g. Puma concolor Linneause 1771) to the assigned identifier.
 */
public class Backfill {

  private static final Logger LOG = LoggerFactory.getLogger(Backfill.class);
  // Live cube table (dc prefix is for "DataCube")
  static final byte[] CUBE_TABLE = "dc_cube".getBytes();
  // Snapshot of the live table used during backfill
  static final byte[] SNAPSHOT_TABLE = "dc_snapshot".getBytes();
  // Backfill table built from the source
  static final byte[] BACKFILL_TABLE = "dc_backfill".getBytes();
  // Utility table to provide a running count for the identifier service
  static final byte[] COUNTER_TABLE = "dc_counter".getBytes();
  // Utility table to provide a mapping from source values to assigned identifiers
  static final byte[] LOOKUP_TABLE = "dc_lookup".getBytes();
  // All DataCube tables use a single column family
  static final byte[] CF = "c".getBytes();

  // Entry point for the time being.
  // TODO: How do we expect to launch this really? (Oozie workflow?)
  public static void main(String[] args) {
    Backfill app = new Backfill();
    app.backfill();
    app.exampleRead();
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
        new HBaseBackfill(conf, new BackfillCallback(), CUBE_TABLE, SNAPSHOT_TABLE, BACKFILL_TABLE, CF, LongOp.LongOpDeserializer.class);
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
    if (!admin.tableExists(t)) {
      LOG.info("Creating table {}", Bytes.toString(t));
      HColumnDescriptor cfDesc = new HColumnDescriptor(CF);
      cfDesc.setBloomFilterType(BloomType.NONE);
      cfDesc.setMaxVersions(1);
      cfDesc.setCompressionType(Algorithm.SNAPPY);
      HTableDescriptor tableDesc = new HTableDescriptor(t);
      tableDesc.addFamily(cfDesc);
      admin.createTable(tableDesc);
    }
  }

  // A simple showcase of reading the final results from HBase
  private void exampleRead() {
    Configuration conf = HBaseConfiguration.create();
    HTablePool pool = new HTablePool(conf, Integer.MAX_VALUE);
    IdService idService = new HBaseIdService(conf, LOOKUP_TABLE, COUNTER_TABLE, CF, CubeWriterMapper.EMPTY_BYTE_ARRAY);

    try {
      DbHarness<LongOp> hbaseDbHarness =
        new HBaseDbHarness<LongOp>(pool, CubeWriterMapper.EMPTY_BYTE_ARRAY, CUBE_TABLE, CF, LongOp.DESERIALIZER, idService, CommitType.INCREMENT);

      DataCubeIo<LongOp> cubeIo = new DataCubeIo<LongOp>(Cube.instance(), hbaseDbHarness, 0, Long.MAX_VALUE, SyncLevel.BATCH_SYNC);
      DataCube<LongOp> cube = Cube.instance();
      LOG.info("Records in DK: {}", getCount(cubeIo.get(new ReadBuilder(cube).at(Cube.COUNTRY, "DK"))));
      LOG.info("Records in ES: {}", getCount(cubeIo.get(new ReadBuilder(cube).at(Cube.COUNTRY, "ES"))));
      LOG.info("Animal records in DK: {}", getCount(cubeIo.get(new ReadBuilder(cube).at(Cube.COUNTRY, "DK").at(Cube.KINGDOM, "Animalia"))));
      LOG.info("Georeferenced Animal records in US: {}",
        getCount(cubeIo.get(new ReadBuilder(cube).at(Cube.COUNTRY, "US").at(Cube.KINGDOM, "Animalia").at(Cube.GEOREFERENCED, true))));
    } catch (Exception e) {
      LOG.error("Error reading cube", e);
    }
  }

  private long getCount(Optional<LongOp> count) {
    return count == null || !count.isPresent() ? 0 : count.get().getLong();
  }

  /**
   * Removes the existing snapshot and backfill tables if present.
   * Creates the cube,counter and lookup tables if missing.
   */
  private void setup(Configuration conf) throws IOException {
    HBaseAdmin admin = new HBaseAdmin(conf);
    cleanup(admin);
    createIfMissing(admin, CUBE_TABLE);
    createIfMissing(admin, COUNTER_TABLE);
    createIfMissing(admin, LOOKUP_TABLE);
  }
}
