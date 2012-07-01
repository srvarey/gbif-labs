package org.gbif.occurrence.cube;

import java.io.IOException;

import com.urbanairship.datacube.DataCubeIo;
import com.urbanairship.datacube.DbHarness;
import com.urbanairship.datacube.DbHarness.CommitType;
import com.urbanairship.datacube.IdService;
import com.urbanairship.datacube.SyncLevel;
import com.urbanairship.datacube.WriteBuilder;
import com.urbanairship.datacube.dbharnesses.HBaseDbHarness;
import com.urbanairship.datacube.idservices.HBaseIdService;
import com.urbanairship.datacube.ops.LongOp;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

/**
 * The Mapper used to read the source data and write into the target cube.
 * Counters are written to simplify the spotting of issues, so look to the Job counters on completion.
 */
public class CubeWriterMapper extends TableMapper<Text, Text> {

  // TODO: These should come from a common schema utility in the future
  // The source HBase table fields
  private static final byte[] CF = Bytes.toBytes("o");
  private static final byte[] COUNTRY = Bytes.toBytes("icc");
  private static final byte[] KINGDOM = Bytes.toBytes("ik");
  private static final byte[] CELL = Bytes.toBytes("icell");

  // Names for counters used in the Hadoop Job
  private static final String STATS = "Stats";
  private static final String STAT_COUNTRY = "Country present";
  private static final String STAT_KINGDOM = "Kingdom present";
  private static final String STAT_GEOREFENCED = "Georeferenced";
  private static final String STAT_SKIPPED = "Skipped record";
  private static final String KINGDOMS = "Kingdoms";

  // The batch size to use when writing the cube
  private static final int CUBE_WRITE_BATCH_SIZE = 1000;

  static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

  private DataCubeIo<LongOp> dataCubeIo;

  @Override
  protected void cleanup(Context context) throws IOException, InterruptedException {
    super.cleanup(context);
    // ensure we're all flushed since batch mode
    dataCubeIo.flush();
    dataCubeIo = null;
  }

  /**
   * Utility to read a named field from the row.
   */
  private Integer getValueAsInt(Result row, byte[] cf, byte[] col) {
    byte[] v = row.getValue(cf, col);
    if (v != null && v.length > 0) {
      return Bytes.toInt(v);
    }
    return null;
  }

  /**
   * Utility to read a named field from the row.
   */
  private String getValueAsString(Result row, byte[] cf, byte[] col) {
    byte[] v = row.getValue(cf, col);
    if (v != null && v.length > 0) {
      return Bytes.toString(v);
    }
    return null;
  }


  @Override
  protected void map(ImmutableBytesWritable key, Result row, Context context) throws IOException, InterruptedException {
    String country = getValueAsString(row, CF, COUNTRY);
    String kingdom = getValueAsString(row, CF, KINGDOM);
    Integer cell = getValueAsInt(row, CF, CELL);

    WriteBuilder b = new WriteBuilder(Cube.instance());
    if (country != null) {
      b.at(Cube.COUNTRY, country);
      context.getCounter(STATS, STAT_COUNTRY).increment(1);
    }
    if (kingdom != null) {
      b.at(Cube.KINGDOM, kingdom);
      context.getCounter(STATS, STAT_KINGDOM).increment(1);
      context.getCounter(KINGDOMS, kingdom).increment(1);
    }
    if (cell != null) {
      b.at(Cube.GEOREFERENCED, true);
      context.getCounter(STATS, STAT_GEOREFENCED).increment(1);
    }
    if (b.getBuckets() != null && !b.getBuckets().isEmpty()) {
      dataCubeIo.writeSync(new LongOp(1), b);
    } else {
      context.getCounter(STATS, STAT_SKIPPED).increment(1);
    }
  }

  // Sets up the DataCubeIO with IdService etc.
  @Override
  protected void setup(Context context) throws IOException, InterruptedException {
    super.setup(context);
    Configuration conf = context.getConfiguration();
    HTablePool pool = new HTablePool(conf, Integer.MAX_VALUE);

    IdService idService = new HBaseIdService(conf, Backfill.LOOKUP_TABLE, Backfill.COUNTER_TABLE, Backfill.CF, EMPTY_BYTE_ARRAY);

    byte[] table = Bytes.toBytes(conf.get(BackfillCallback.TARGET_TABLE_KEY));
    byte[] cf = Bytes.toBytes(conf.get(BackfillCallback.TARGET_CF_KEY));


    DbHarness<LongOp> hbaseDbHarness =
      new HBaseDbHarness<LongOp>(pool, EMPTY_BYTE_ARRAY, table, cf, LongOp.DESERIALIZER, idService, CommitType.INCREMENT);

    dataCubeIo = new DataCubeIo<LongOp>(Cube.instance(), hbaseDbHarness, CUBE_WRITE_BATCH_SIZE, Long.MAX_VALUE, SyncLevel.BATCH_SYNC);

  }
}
