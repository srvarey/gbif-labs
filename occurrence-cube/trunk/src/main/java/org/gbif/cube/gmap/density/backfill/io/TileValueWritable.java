package org.gbif.cube.gmap.density.backfill.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class TileValueWritable implements WritableComparable<TileValueWritable> {

  private TileValue tile;

  public TileValueWritable() {
  }

  public TileValueWritable(TileValue tile) {
    this.tile = tile;
  }

  @Override
  public int compareTo(TileValueWritable o) {
    return tile.compareTo(o.get());
  }

  public TileValue get() {
    return tile;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    int length = in.readInt();
    byte[] b = new byte[length];
    in.readFully(b, 0, length);
    tile = IOSerDeUtils.decodeTileValue(b);
  }

  @Override
  public void write(DataOutput out) throws IOException {
    if (tile != null) {
      byte[] b = IOSerDeUtils.encodeTileValue(tile);
      out.writeInt(b.length);
      out.write(b, 0, b.length);
    }
  }

}
