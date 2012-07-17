package org.gbif.cube.gmap.density.backfill;

import org.gbif.cube.gmap.GoogleTileUtil;
import org.gbif.cube.gmap.density.backfill.io.TileKey;
import org.gbif.cube.gmap.density.backfill.io.TileKeyWritable;
import org.gbif.cube.gmap.density.backfill.io.TileType;
import org.gbif.cube.gmap.density.backfill.io.TileValue;
import org.gbif.cube.gmap.density.backfill.io.TileValueWritable;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * The Mapper used to read the source data and write into the target cube.
 * Counters are written to simplify the spotting of issues, so look to the Job counters on completion.
 */
public class TileCollectMapper extends TableMapper<TileKeyWritable, TileValueWritable> {

  // TODO: These should come from a common schema utility in the future
  // The source HBase table fields
  private static final byte[] CF = Bytes.toBytes("o");
  private static final byte[] TAXONID = Bytes.toBytes("ini");
  private static final byte[] LATITUDE = Bytes.toBytes("ilat");
  private static final byte[] LONGITUDE = Bytes.toBytes("ilng");

  /**
   * Utility to read a named field from the row.
   */
  private Double getValueAsDouble(Result row, byte[] cf, byte[] col) {
    byte[] v = row.getValue(cf, col);
    if (v != null && v.length > 0) {
      return Bytes.toDouble(v);
    }
    return null;
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

  @Override
  protected void map(ImmutableBytesWritable key, Result row, Context context) throws IOException, InterruptedException {
    Integer taxonId = getValueAsInt(row, CF, TAXONID);
    Double lat = getValueAsDouble(row, CF, LATITUDE);
    Double lng = getValueAsDouble(row, CF, LONGITUDE);
    // HColumnDescriptor c = new HColumnDescriptor();
    // c.setBloomFilterType(null);
    if (taxonId != null && lat != null && lng != null) {

      if (lat < 85 && lat > -85) {
        for (int z = 0; z < 23; z++) {
          // 1 record at the lat lng, at the zoom

          TileKey tk = new TileKey();
          tk.setId(String.valueOf(taxonId));
          tk.setX(GoogleTileUtil.toTileX(lng, z));
          tk.setY(GoogleTileUtil.toTileY(lng, z));
          tk.setType(TileType.TAXON);

          TileValue tv = new TileValue();

          tv.setCount(1);
          tv.setOffsetX(GoogleTileUtil.getOffsetX(lat, lng, z));
          tv.setOffsetY(GoogleTileUtil.getOffsetY(lat, lng, z));
          context.write(new TileKeyWritable(tk), new TileValueWritable(tv));
        }
      }
    }
  }
}
