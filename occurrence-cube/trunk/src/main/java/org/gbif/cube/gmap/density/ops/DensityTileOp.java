package org.gbif.cube.gmap.density.ops;

import java.io.IOException;

import com.urbanairship.datacube.Deserializer;
import com.urbanairship.datacube.Op;
import org.apache.commons.lang.NotImplementedException;


/**
 * Holds a data structure suitable for rendering a single 256x256 pixel tile representing density.
 */
public class DensityTileOp implements Op {

  public static class DensityTileOpDeserializer implements Deserializer<DensityTileOp> {

    @Override
    public DensityTileOp fromBytes(byte[] bytes) {
      try {
        return new DensityTileOp(OpSerDeUtils.decodeTile(bytes));
      } catch (IOException e) {
        throw new RuntimeException("Unable to decode tile: " + e.getMessage());
      }
    }
  }

  final static int TILE_SIZE = 256;

  public static final DensityTileOpDeserializer DESERIALIZER = new DensityTileOpDeserializer();

  // the tile data
  private final Tile tile;

  public DensityTileOp(Tile t) {
    tile = t;
  }

  /**
   * @see com.urbanairship.datacube.Op#add(com.urbanairship.datacube.Op)
   */
  @Override
  public Op add(Op other) {
    if (!(other instanceof DensityTileOp)) {
      throw new RuntimeException("Bad code.  Expecting DensityTileOp for comparison, received " + other.getClass().getName());
    }
    DensityTileOp o = (DensityTileOp) other;
    if (o.tile.getPixels() != null) {
      // hopelessly expensive but Avro only has String support for keys in maps
      // https://issues.apache.org/jira/browse/AVRO-680
      // Since we backfill only and collect in MR, we know this will be rarely called
      // from the Reduce
      for (Pixel p : o.tile.getPixels()) {
        for (Pixel p2 : tile.getPixels()) {
          if (p.getX().equals(p2.getX()) && p.getY().equals(p2.getY())) {
            p2.setCount(p2.getCount() + p.getCount());
          }
        }
      }
    }
    return this; // Not an immutable copy!
  }

  public Tile getTile() {
    return tile;
  }

  /**
   * @see com.urbanairship.datacube.CSerializable#serialize()
   */
  @Override
  public byte[] serialize() {
    try {
      return OpSerDeUtils.encodeTile(tile);
    } catch (IOException e) {
      throw new RuntimeException("Unable to encode tile: " + e.getMessage());
    }
  }


  /**
   * @see com.urbanairship.datacube.Op#subtract(com.urbanairship.datacube.Op)
   */
  @Override
  public Op subtract(Op arg0) {
    throw new NotImplementedException("Current subtractions are not possible for density tiles");
  }
}
