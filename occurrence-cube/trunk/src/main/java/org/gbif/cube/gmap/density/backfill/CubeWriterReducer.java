package org.gbif.cube.gmap.density.backfill;

import org.gbif.cube.gmap.density.DensityCube;
import org.gbif.cube.gmap.density.DensityTile;
import org.gbif.cube.gmap.io.LatLngWritable;
import org.gbif.cube.gmap.io.TileKeyWritable;

import java.io.IOException;

import com.urbanairship.datacube.DataCubeIo;
import com.urbanairship.datacube.DbHarness;
import com.urbanairship.datacube.DbHarness.CommitType;
import com.urbanairship.datacube.IdService;
import com.urbanairship.datacube.SyncLevel;
import com.urbanairship.datacube.WriteBuilder;
import com.urbanairship.datacube.dbharnesses.HBaseDbHarness;
import com.urbanairship.datacube.idservices.HBaseIdService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableFactory;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Collects the tile, and writes to the cube.
 */
public class CubeWriterReducer extends Reducer<TileKeyWritable, LatLngWritable, NullWritable, NullWritable> {

  private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
  private int pixelsPerCluster = Backfill.DEFAULT_PIXELS_PER_CLUSTER;
  private int writeBatchSize = Backfill.DEFAULT_WRITE_BATCH_SIZE;
  private DataCubeIo<DensityTile> dataCubeIo;
  private HTablePool pool;
  private byte[] targetTable;

  @Override
  protected void cleanup(Context context) throws IOException, InterruptedException {
    super.cleanup(context);
    // ensure we're all flushed since batch mode
    dataCubeIo.flush();
    dataCubeIo = null;
    // close the pool to ensure flushing
    try {
      pool.closeTablePool(targetTable);
    } catch (NullPointerException e) {
      // thrown when the pool has never issued this table, indicating an error
      throw new IOException("Attempt to close HBase pool, when table has not been written in this job.  Failing as very suspicious.");
    }
  }

  /**
   * Builds the tile, and pushes to the cube.
   */
  @Override
  protected void reduce(TileKeyWritable k, Iterable<LatLngWritable> locations, Context context) throws IOException {
    DensityTile.Builder b = DensityTile.builder(k.getZ(), k.getX(), k.getY(), pixelsPerCluster);
    int count = 0;
    for (LatLngWritable l : locations) {
      b.collect(l.getLat(), l.getLng(), l.getCount());
      count++;
    }
    DensityTile t = b.build();

    context.setStatus("Type[" + k.getType() + "],  Key[" + k.getKey() + "], Z[" + k.getZ() + "], X[" + k.getX() + "], Y[" + k.getY() + "], Count["
      + count + "], cells[" + t.cells().size() + "]");

    try {

      WriteBuilder wb =
        new WriteBuilder(DensityCube.INSTANCE).at(DensityCube.ZOOM, k.getZ()).at(DensityCube.TILE_X, k.getX()).at(DensityCube.TILE_Y, k.getY())
          .at(DensityCube.KEY, k.getKey()).at(DensityCube.TYPE, k.getType());
      dataCubeIo.writeAsync(t, wb);

    } catch (Exception e) {
      throw new IOException(e);
    }
  }

  // Sets up the DataCubeIO with IdService
  @Override
  protected void setup(Context context) throws IOException, InterruptedException {
    super.setup(context);
    Configuration conf = context.getConfiguration();
    pixelsPerCluster = conf.getInt(Backfill.KEY_PIXELS_PER_CLUSTER, Backfill.DEFAULT_PIXELS_PER_CLUSTER);
    writeBatchSize = conf.getInt(Backfill.KEY_WRITE_BATCH_SIZE, Backfill.DEFAULT_WRITE_BATCH_SIZE);

    // We are going to issue a LOT of PUTs, so we need no autoflushing or else throughput is terrible.
    pool = new HTablePool(conf, Integer.MAX_VALUE, new HTableFactory() {

      @Override
      public HTableInterface createHTableInterface(Configuration config, byte[] tableName) {
        try {
          HTable table = new HTable(config, tableName);
          table.setAutoFlush(false);
          return table;
        } catch (IOException ioe) {
          throw new RuntimeException(ioe);
        }
      }
    });

    IdService idService =
      new HBaseIdService(conf, Bytes.toBytes(conf.get(Backfill.KEY_LOOKUP_TABLE)), Bytes.toBytes(conf.get(Backfill.KEY_COUNTER_TABLE)),
        Bytes.toBytes(conf.get(Backfill.KEY_CF)), EMPTY_BYTE_ARRAY);

    // Commit type overwrite since we have grouped by Tile already
    targetTable = Bytes.toBytes(conf.get(Backfill.KEY_BACKFILL_TABLE));
    DbHarness<DensityTile> hbaseDbHarness =
      new HBaseDbHarness<DensityTile>(pool, EMPTY_BYTE_ARRAY, targetTable, Bytes.toBytes(conf.get(Backfill.KEY_CF)), DensityTile.DESERIALIZER,
        idService, CommitType.OVERWRITE);

    dataCubeIo = new DataCubeIo<DensityTile>(DensityCube.INSTANCE, hbaseDbHarness, writeBatchSize, Long.MAX_VALUE, SyncLevel.BATCH_ASYNC);

  }
}
