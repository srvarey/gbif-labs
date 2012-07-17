package org.gbif.occurrence.cube.gmap;

import java.nio.ByteBuffer;

import com.urbanairship.datacube.CSerializable;

/**
 * Use this in your bucketer if you're using tile coordinates as dimension coordinates.
 */
public class TileCoordSerializable implements CSerializable {

  private final TileCoord tc;

  public TileCoordSerializable(TileCoord tc) {
    this.tc = tc;
  }

  /**
   * GMaps goes up to 23 zooms, so 2^23-1 is the theoretical maximum coordinate reference.
   * 8,388,607 is therefore the maximum value, which requires 4 bytes for each X and Y.
   */
  @Override
  public byte[] serialize() {
    return ByteBuffer.allocate(TileCoordBucketer.SERIALIZED_SIZE).putInt(tc.getZoom()).putInt(tc.getX()).putInt(tc.getY()).array();
  }
}
