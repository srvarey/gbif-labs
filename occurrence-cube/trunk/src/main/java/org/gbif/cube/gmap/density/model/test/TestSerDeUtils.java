package org.gbif.cube.gmap.density.model.test;

import org.gbif.cube.util.SerDeUtils;

import java.io.IOException;

import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

class TestSerDeUtils extends SerDeUtils {

  private static final GenericDatumWriter<Tile> TILE_WRITER = new SpecificDatumWriter<Tile>(Tile.class);
  private static final SpecificDatumReader<Tile> TILE_READER = new SpecificDatumReader<Tile>(Tile.class);

  public static Tile decodeTile(final byte[] data) throws IOException {
    return decodeObject(new Tile(), data, TILE_READER);
  }

  public static byte[] encodeTile(final Tile tile) throws IOException {
    return encodeObject(tile, TILE_WRITER);
  }
}
