package org.gbif.cube.gmap.density.backfill;

import org.gbif.cube.gmap.density.DensityTile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.urbanairship.datacube.backfill.HBaseBackfill;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.regionserver.StoreFile.BloomType;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Runs a backfill to populate the tile densities.
 */
public class Backfill {

  private static final Logger LOG = LoggerFactory.getLogger(Backfill.class);
  private static final String APPLICATION_PROPERTIES = "/cube.properties";

  // Configuration that is read from the APPLICATION_PROPERTIES
  // We use bytes as they are most commonly used but some APIs require
  // them to be converted back to Strings. This is out of our hands though.
  private final byte[] cubeTable;
  private final byte[] snapshotTable;
  private final byte[] backfillTable;
  private final byte[] counterTable;
  private final byte[] lookupTable;
  private final byte[] cf;
  private final byte[] sourceTable;
  private final int scannerCache;
  private final int numReducers;
  private final int numZooms;
  private final int writeBatchSize;
  private final int pixelsPerCluster;

  // sensible defaults when omitted
  final static int DEFAULT_SCANNER_CACHE = 200;
  final static int DEFAULT_NUM_REDUCERS = 12;
  final static int DEFAULT_WRITE_BATCH_SIZE = 1000;
  final static int DEFAULT_PIXELS_PER_CLUSTER = 4;
  final static int DEFAULT_NUM_ZOOMS = 4;

  // Keys used for the application properties, and in the Hadoop context,
  // since that is the only way to pass things to the launched MR tasks.
  public static final String KEY_CUBE_TABLE = "density-cube.cubeTable";
  public static final String KEY_SNAPSHOT_TABLE = "density-cube.snapshotTable";
  public static final String KEY_BACKFILL_TABLE = "density-cube.backfillTable";
  public static final String KEY_COUNTER_TABLE = "density-cube.counterTable";
  public static final String KEY_LOOKUP_TABLE = "density-cube.lookupTable";
  public static final String KEY_CF = "density-cube.columnFamily";
  public static final String KEY_SOURCE_TABLE = "density-cube.backfillSourceTable";
  public static final String KEY_SCANNER_CACHE = "density-cube.backfillScannerCaching";
  public static final String KEY_HBASE_SCANNER_CACHE = "hbase.client.scanner.caching"; // for HBase job conf
  public static final String KEY_NUM_REDUCERS = "density-cube.backfillNumReduceTasks";
  public static final String KEY_NUM_ZOOMS = "density-cube.numZooms";
  public static final String KEY_WRITE_BATCH_SIZE = "density-cube.writeBatchSize";
  public static final String KEY_PIXELS_PER_CLUSTER = "density-cube.tilePixelsPerCluster";


  public Backfill(Properties p) throws IllegalArgumentException {
    cubeTable = propertyAsBytes(p, KEY_CUBE_TABLE);
    snapshotTable = propertyAsBytes(p, KEY_SNAPSHOT_TABLE);
    backfillTable = propertyAsBytes(p, KEY_BACKFILL_TABLE);
    counterTable = propertyAsBytes(p, KEY_COUNTER_TABLE);
    lookupTable = propertyAsBytes(p, KEY_LOOKUP_TABLE);
    cf = propertyAsBytes(p, KEY_CF);
    sourceTable = propertyAsBytes(p, KEY_SOURCE_TABLE);
    scannerCache = propertyAsInt(p, KEY_SCANNER_CACHE, DEFAULT_SCANNER_CACHE);
    numReducers = propertyAsInt(p, KEY_NUM_REDUCERS, DEFAULT_NUM_REDUCERS);
    writeBatchSize = propertyAsInt(p, KEY_WRITE_BATCH_SIZE);
    pixelsPerCluster = propertyAsInt(p, KEY_PIXELS_PER_CLUSTER);
    numZooms = propertyAsInt(p, KEY_NUM_ZOOMS);
  }

  public static void main(String[] args) {
    Properties p = new Properties();
    InputStream is = Backfill.class.getResourceAsStream(APPLICATION_PROPERTIES);
    if (is != null) {
      try {
        p.load(is);
        Backfill app = new Backfill(p);
        app.backfill();
      } catch (IOException e) {
        throw new IllegalArgumentException("Unable to backfill.  Cannot read " + APPLICATION_PROPERTIES);
      }
    } else {
      throw new IllegalArgumentException("Unable to backfill.  Missing " + APPLICATION_PROPERTIES);
    }
  }

  /**
   * Runs the backfill process.
   * 
   * @throws IOException On any HBase communication errors
   */
  public void backfill() {
    Configuration conf = HBaseConfiguration.create();
    try {
      setup(conf);
      HBaseBackfill backfill =
        new HBaseBackfill(conf, new BackfillCallback(), cubeTable, snapshotTable, backfillTable, cf, DensityTile.DensityTileDeserializer.class);

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
    if (admin.tableExists(snapshotTable)) {
      LOG.info("Deleting table {}", Bytes.toString(snapshotTable));
      admin.disableTable(snapshotTable);
      admin.deleteTable(snapshotTable);
    }
    if (admin.tableExists(backfillTable)) {
      LOG.info("Deleting table {}", Bytes.toString(backfillTable));
      admin.disableTable(backfillTable);
      admin.deleteTable(backfillTable);
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
      HColumnDescriptor cfDesc = new HColumnDescriptor(cf);
      cfDesc.setBloomFilterType(BloomType.NONE);
      cfDesc.setMaxVersions(1);
      // cfDesc.setCompressionType(Algorithm.SNAPPY); // fails on the Snapshotter at the end
      HTableDescriptor tableDesc = new HTableDescriptor(t);
      tableDesc.addFamily(cfDesc);
      if (startKey != null && endKey != null && numRegions > 0) {
        admin.createTable(tableDesc, startKey, endKey, numRegions);
      } else {
        admin.createTable(tableDesc);
      }
    }
  }

  private byte[] propertyAsBytes(Properties p, String key) {
    String v = p.getProperty(key);
    if (v != null) {
      return Bytes.toBytes(v);
    } else {
      throw new IllegalArgumentException("Missing property for " + key);
    }
  }

  private int propertyAsInt(Properties p, String key) {
    String v = p.getProperty(key);
    if (v != null) {
      try {
        return Integer.parseInt(v);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid value[" + v + "] supplied for " + key);
      }
    } else {
      throw new IllegalArgumentException("Missing property for " + key);
    }
  }

  private int propertyAsInt(Properties p, String key, int defaultValue) {
    String v = p.getProperty(key);
    if (v != null) {
      try {
        return Integer.parseInt(v);
      } catch (NumberFormatException e) {
        LOG.warn("Invalid value[" + v + "] supplied for " + key + ", using default[" + DEFAULT_SCANNER_CACHE + "]");
        return defaultValue;
      }
    } else {
      return defaultValue;
    }
  }

  /**
   * Removes the existing snapshot and backfill tables if present.
   * Creates the cube,counter and lookup tables if missing.
   * Sets the configuration options in the Hadoop context.
   */
  private void setup(Configuration conf) throws IOException {
    HBaseAdmin admin = new HBaseAdmin(conf);
    cleanup(admin);
    createIfMissing(admin, cubeTable);
    createIfMissing(admin, counterTable);
    createIfMissing(admin, lookupTable);

    // unfortunately we need to use Strings again (Hadoop API)
    conf.set(KEY_CUBE_TABLE, Bytes.toString(cubeTable));
    conf.set(KEY_SNAPSHOT_TABLE, Bytes.toString(snapshotTable));
    conf.set(KEY_BACKFILL_TABLE, Bytes.toString(backfillTable));
    conf.set(KEY_COUNTER_TABLE, Bytes.toString(counterTable));
    conf.set(KEY_LOOKUP_TABLE, Bytes.toString(lookupTable));
    conf.set(KEY_CF, Bytes.toString(cf));
    conf.set(KEY_SOURCE_TABLE, Bytes.toString(sourceTable));
    conf.setInt(KEY_HBASE_SCANNER_CACHE, scannerCache);
    conf.setInt(KEY_NUM_REDUCERS, numReducers);
    conf.setInt(KEY_WRITE_BATCH_SIZE, writeBatchSize);
    conf.setInt(KEY_PIXELS_PER_CLUSTER, pixelsPerCluster);
    conf.setInt(KEY_NUM_ZOOMS, numZooms);
  }
}
