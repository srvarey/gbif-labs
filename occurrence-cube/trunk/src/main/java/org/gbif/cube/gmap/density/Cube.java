package org.gbif.cube.gmap.density;

import org.gbif.cube.gmap.density.ops.DensityTileOp;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.urbanairship.datacube.DataCube;
import com.urbanairship.datacube.Dimension;
import com.urbanairship.datacube.Rollup;
import com.urbanairship.datacube.bucketers.BigEndianIntBucketer;

/**
 * The cube definition.
 * TODO: write public utility exposing a simple API enabling validated read/write access to cube.
 */
public class Cube {

  // no id substitution, 4 bytes for int
  public static final Dimension<Integer> TAXON_ID = new Dimension<Integer>("taxonID", new BigEndianIntBucketer(), false, 4);
  public static final Dimension<Integer> ZOOM = new Dimension<Integer>("zoom", new BigEndianIntBucketer(), false, 4);
  public static final Dimension<Integer> TILE_X = new Dimension<Integer>("tileX", new BigEndianIntBucketer(), false, 4);
  public static final Dimension<Integer> TILE_Y = new Dimension<Integer>("tileY", new BigEndianIntBucketer(), false, 4);

  // Singleton instance if accessed through the instance() method
  public static final DataCube<DensityTileOp> INSTANCE = newInstance();

  // Not for instantiation
  private Cube() {
  }

  private static DataCube<DensityTileOp> newInstance() {
    List<Dimension<?>> dimensions = ImmutableList.<Dimension<?>>of(TAXON_ID, ZOOM, TILE_X, TILE_Y);
    List<Rollup> rollups = ImmutableList.of(new Rollup(ZOOM, TILE_X, TILE_Y), new Rollup(TAXON_ID, ZOOM, TILE_X, TILE_Y));
    return new DataCube<DensityTileOp>(dimensions, rollups);
  }
}
