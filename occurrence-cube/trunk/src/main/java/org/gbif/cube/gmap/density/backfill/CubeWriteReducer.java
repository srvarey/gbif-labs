package org.gbif.cube.gmap.density.backfill;

import org.gbif.cube.gmap.density.Cube;
import org.gbif.cube.gmap.density.backfill.io.TileKey;
import org.gbif.cube.gmap.density.backfill.io.TileKeyWritable;
import org.gbif.cube.gmap.density.backfill.io.TileType;
import org.gbif.cube.gmap.density.backfill.io.TileValueWritable;
import org.gbif.cube.gmap.density.ops.DensityTileOp;
import org.gbif.cube.gmap.density.ops.Pixel;
import org.gbif.cube.gmap.density.ops.Tile;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;
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
 * Collects the tile, and writes to the cube
 */
public class CubeWriteReducer extends Reducer<TileKeyWritable, TileValueWritable, NullWritable, NullWritable> {

  private static final int CUBE_WRITE_BATCH_SIZE = 1000;
  private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

  private DataCubeIo<DensityTileOp> dataCubeIo;

  @Override
  protected void cleanup(Context context) throws IOException, InterruptedException {
    super.cleanup(context);
    // ensure we're all flushed since batch mode
    dataCubeIo.flush();
    dataCubeIo = null;
    // TODO: What about the HTable auto flushes ???
  }

  @Override
  protected void reduce(TileKeyWritable tkw, Iterable<TileValueWritable> tileValues, Context context) throws IOException, InterruptedException {
    TileKey tileKey = tkw.get();

    // build the tile
    Tile t = new Tile();
    List<Pixel> pixels = Lists.newArrayList();
    t.setPixels(pixels);
    for (TileValueWritable tvw : tileValues) {
      Pixel p = new Pixel();
      p.setCount(tvw.get().getCount());
      p.setX(tvw.get().getOffsetX());
      p.setY(tvw.get().getOffsetY());
      t.getPixels().add(p);
    }

    DensityTileOp op = new DensityTileOp(t);
    context.setStatus("TaxonId: " + tileKey.getId());

    if (TileType.TAXON == tileKey.getType()) {
      int taxonId = Integer.parseInt(String.valueOf(tileKey.getId()));
      try {
        dataCubeIo.writeAsync(
          op,
          new WriteBuilder(Cube.INSTANCE).at(Cube.TAXON_ID, taxonId).at(Cube.ZOOM, tileKey.getZ()).at(Cube.TILE_X, tileKey.getX())
            .at(Cube.TILE_Y, tileKey.getY()));
      } catch (Exception e) {
        context.getCounter("ERROR", "Asyncwrite").increment(1);
      }
    }
  }

  // Sets up the DataCubeIO with IdService etc.
  @Override
  protected void setup(Context context) throws IOException, InterruptedException {
    super.setup(context);
    Configuration conf = context.getConfiguration();

    HTablePool pool = new HTablePool(conf, Integer.MAX_VALUE, new HTableFactory() {

      @Override
      public HTableInterface createHTableInterface(Configuration config, byte[] tableName) {
        try {
          HTable table = new HTable(config, tableName);
          // We are going to issue a LOT of PUTs, so we need no autoflushing or else throughput is terrible
          table.setAutoFlush(false);
          return table;
        } catch (IOException ioe) {
          throw new RuntimeException(ioe);
        }
      }
    });

    IdService idService = new HBaseIdService(conf, Backfill.LOOKUP_TABLE, Backfill.COUNTER_TABLE, Backfill.CF, EMPTY_BYTE_ARRAY);

    byte[] table = Bytes.toBytes(conf.get(BackfillCallback.TARGET_TABLE_KEY));
    byte[] cf = Bytes.toBytes(conf.get(BackfillCallback.TARGET_CF_KEY));

    // Commit type overwrite can be used, since we have grouped by Tile in the MR job
    DbHarness<DensityTileOp> hbaseDbHarness =
      new HBaseDbHarness<DensityTileOp>(pool, EMPTY_BYTE_ARRAY, table, cf, DensityTileOp.DESERIALIZER, idService, CommitType.OVERWRITE);

    dataCubeIo = new DataCubeIo<DensityTileOp>(Cube.INSTANCE, hbaseDbHarness, CUBE_WRITE_BATCH_SIZE, Long.MAX_VALUE, SyncLevel.BATCH_ASYNC);
  }
}
