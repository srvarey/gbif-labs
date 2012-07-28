package org.gbif.cube.gmap.density;

import org.gbif.cube.gmap.GoogleTileUtil;
import org.gbif.cube.gmap.density.io.DensityTileAvro;
import org.gbif.cube.gmap.density.io.DensityTileAvroSerDeUtils;

import java.awt.Point;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;
import com.urbanairship.datacube.Deserializer;
import com.urbanairship.datacube.Op;
import org.apache.commons.lang.NotImplementedException;


/**
 * A density tile clusters counts to a configurable sized cluster of pixels.
 * For example, one might cluster to 4px by 4px. This class wraps an Avro
 * object which is used for efficient SerDe and implements the Cube operation
 * interface to allow it to be used for cube additions.
 */
public class DensityTile implements Op {

  /**
   * Simple builder to help produce DensityTiles from lat lng data.
   * Requires the
   */
  public static class Builder {

    private final int x, y, zoom, clusterSize;
    private final Map<Integer, Integer> cells = Maps.newHashMap();

    public Builder(int zoom, int x, int y, int clusterSize) {
      this.x = x;
      this.y = y;
      this.zoom = zoom;
      this.clusterSize = clusterSize;
    }

    public DensityTile build() {
      return new DensityTile(clusterSize, cells);
    }

    public Builder collect(double lat, double lng, int count) {
      // assert that we are on the correct tile
      Point t = GoogleTileUtil.toTileXY(lat, lng, zoom);
      if (t.x == x && t.y == y) {
        int offsetX = GoogleTileUtil.getOffsetX(lat, lng, zoom);
        int offsetY = GoogleTileUtil.getOffsetY(lat, lng, zoom);
        int id = toCellId(offsetX, offsetY, clusterSize);

        if (cells.containsKey(id)) {
          cells.put(id, cells.get(id) + count);
        } else {
          cells.put(id, count);
        }
      }
      return this;
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
    }

    public int getZoom() {
      return zoom;
    }
  }

  public static class DensityTileDeserializer implements Deserializer<DensityTile> {

    @Override
    public DensityTile fromBytes(byte[] bytes) {
      try {
        return DensityTile.DESERIALIZE(bytes);
      } catch (IOException e) {
        throw new RuntimeException("Unable to deserialize DensityTile: " + e.getMessage());
      }
    }
  }

  public static final int TILE_SIZE = 256;
  private final Map<Integer, Integer> cells = Maps.newHashMap();

  private final int clusterSize;
  public static final DensityTileDeserializer DESERIALIZER = new DensityTileDeserializer();

  public DensityTile(int clusterSize) {
    this.clusterSize = clusterSize;
  }

  public DensityTile(int clusterSize, Map<Integer, Integer> cells) {
    this.clusterSize = clusterSize;
    this.cells.putAll(cells);
  }

  public static Builder builder(int zoom, int x, int y, int clusterSize) {
    return new Builder(zoom, x, y, clusterSize);
  }

  /**
   * Utility to deserialize an Avro based serialization.
   * 
   * @see DensityTileAvro
   */
  public static DensityTile DESERIALIZE(byte[] b) throws IOException {
    DensityTileAvro avro = DensityTileAvroSerDeUtils.decode(b);
    Map<CharSequence, Integer> m = avro.getCells();
    DensityTile dt = new DensityTile(avro.getClusterSize());
    for (Entry<CharSequence, Integer> e : m.entrySet()) {
      dt.cells().put(Integer.parseInt(e.getKey().toString()), e.getValue());
    }
    return dt;
  }

  /**
   * Based on the cluster size, generates the cell id from the offset X and Y within
   * the tile. This is a linear form of the usual google tile addressing schema, where
   * the top row goes left to right 0,1,2,3 etc for as many cells as are in the row.
   */
  public static int toCellId(int x, int y, int clusterSize) {
    int tpc = TILE_SIZE / clusterSize;
    return (x / clusterSize) + (tpc * (y / clusterSize));
  }

  @Override
  public Op add(Op otherOp) {
    if (otherOp instanceof DensityTile) {
      DensityTile o = (DensityTile) otherOp;

      if (o.clusterSize != clusterSize) {
        throw new IllegalArgumentException("Cannot merge DensityTiles with different cluster sizes. Supplied " + o.clusterSize + " and "
          + clusterSize);
      }

      // build a new map merging the densities from the provided cells
      Map<Integer, Integer> cells = Maps.newHashMap(cells());
      for (Entry<Integer, Integer> c : o.cells().entrySet()) {
        if (cells.containsKey(c.getKey())) {
          int count = cells.get(c.getKey()) + c.getValue();
          cells.put(c.getKey(), count);
        } else {
          cells.put(c.getKey(), c.getValue());
        }
      }

      return new DensityTile(clusterSize, cells);

    } else {
      throw new IllegalArgumentException("Cannot merge DensityTiles when supplied tile is not a density tile.  Supplied: " + otherOp.getClass());
    }
  }

  /**
   * Looks up a cell value using the encoded form, returning 0 for anything not found.
   */
  public int cell(int cellId) {
    Integer v = cells.get(cellId);
    return (v == null) ? 0 : v;
  }

  /**
   * Looks up a cell value, returning 0 for anything not found.
   */
  public int cell(int x, int y) {
    int cellId = x + (TILE_SIZE / clusterSize * y);
    Integer v = cells.get(cellId);
    return (v == null) ? 0 : v;
  }

  public Map<Integer, Integer> cells() {
    return cells;
  }

  public int getClusterSize() {
    return clusterSize;
  }

  /**
   * Serializes using an Avro format.
   * 
   * @see DensityTileAvro
   */
  @Override
  public byte[] serialize() {
    DensityTileAvro avro = new DensityTileAvro();
    Map<CharSequence, Integer> m = Maps.newHashMap();
    avro.setCells(m);
    avro.setClusterSize(clusterSize);
    for (Entry<Integer, Integer> e : cells.entrySet()) {
      m.put(String.valueOf(e.getKey()), e.getValue());
    }
    try {
      return DensityTileAvroSerDeUtils.encode(avro);
    } catch (IOException e) {
      // We can't throw a checked exception, so repackage
      throw new RuntimeException("Unable to serialize: " + e);
    }
  }

  @Override
  public Op subtract(Op otherOp) {
    throw new NotImplementedException();
  }
}