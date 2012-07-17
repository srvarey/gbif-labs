package org.gbif.cube.gmap.density.backfill.io;

import org.gbif.cube.util.SerDeUtils;

import java.io.IOException;

import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

class IOSerDeUtils extends SerDeUtils {

  private static final GenericDatumWriter<TileKey> TILEKEY_WRITER = new SpecificDatumWriter<TileKey>(TileKey.class);
  private static final SpecificDatumReader<TileKey> TILEKEY_READER = new SpecificDatumReader<TileKey>(TileKey.class);
  private static final GenericDatumWriter<TileValue> TILEVALUE_WRITER = new SpecificDatumWriter<TileValue>(TileValue.class);
  private static final SpecificDatumReader<TileValue> TILEVALUE_READER = new SpecificDatumReader<TileValue>(TileValue.class);

  public static TileKey decodeTileKey(final byte[] data) throws IOException {
    return decodeObject(new TileKey(), data, TILEKEY_READER);
  }

  public static TileValue decodeTileValue(final byte[] data) throws IOException {
    return decodeObject(new TileValue(), data, TILEVALUE_READER);
  }

  public static byte[] encodeTileKey(final TileKey tileKey) throws IOException {
    return encodeObject(tileKey, TILEKEY_WRITER);
  }

  public static byte[] encodeTileValue(final TileValue tileValue) throws IOException {
    return encodeObject(tileValue, TILEVALUE_WRITER);
  }
}
