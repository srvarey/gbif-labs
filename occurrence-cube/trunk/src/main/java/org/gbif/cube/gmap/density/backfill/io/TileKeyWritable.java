package org.gbif.cube.gmap.density.backfill.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;


public class TileKeyWritable implements WritableComparable<TileKeyWritable> {

  private TileKey tile;

  public TileKeyWritable() {
  }

  public TileKeyWritable(TileKey tile) {
    this.tile = tile;
  }

  @Override
  public int compareTo(TileKeyWritable o) {
    return tile.compareTo(o.get());
  }

  public TileKey get() {
    return tile;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    int length = in.readInt();
    byte[] b = new byte[length];
    in.readFully(b, 0, length);
    tile = IOSerDeUtils.decodeTileKey(b);
  }

  @Override
  public void write(DataOutput out) throws IOException {
    if (tile != null) {
      byte[] b = IOSerDeUtils.encodeTileKey(tile);
      out.writeInt(b.length);
      out.write(b, 0, b.length);
    }
  }
}
