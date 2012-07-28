package org.gbif.cube.gmap.density;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.urbanairship.datacube.DataCube;
import com.urbanairship.datacube.Dimension;
import com.urbanairship.datacube.Rollup;
import com.urbanairship.datacube.bucketers.BigEndianIntBucketer;
import com.urbanairship.datacube.bucketers.StringToBytesBucketer;

/**
 * The cube definition for a density map.
 */
public class DensityCube {

  // 4 bytes for int, 8 bytes for long
  public static final Dimension<Integer> TAXON_ID = new Dimension<Integer>("taxonID", new BigEndianIntBucketer(), false, 4);
  public static final Dimension<String> COUNTRY_ISO_CODE = new Dimension<String>("countryIsoCode", new StringToBytesBucketer(), false, 2);
  // id substitution reduces UUIDs to longs
  public static final Dimension<String> DATASET_KEY = new Dimension<String>("datasetKey", new StringToBytesBucketer(), true, 8);
  public static final Dimension<String> PUBLISHER_KEY = new Dimension<String>("publisherKey", new StringToBytesBucketer(), true, 8);
  public static final Dimension<Integer> ZOOM = new Dimension<Integer>("zoom", new BigEndianIntBucketer(), false, 4);
  public static final Dimension<Integer> TILE_X = new Dimension<Integer>("tileX", new BigEndianIntBucketer(), false, 4);
  public static final Dimension<Integer> TILE_Y = new Dimension<Integer>("tileY", new BigEndianIntBucketer(), false, 4);

  // Singleton instance if accessed through the instance() method
  public static final DataCube<DensityTile> INSTANCE = newInstance();

  // Not for instantiation
  private DensityCube() {
  }

  private static DataCube<DensityTile> newInstance() {
    List<Dimension<?>> dimensions = ImmutableList.<Dimension<?>>of(TAXON_ID, COUNTRY_ISO_CODE, DATASET_KEY, PUBLISHER_KEY, ZOOM, TILE_X, TILE_Y);
    List<Rollup> rollups =
      ImmutableList.of(new Rollup(ZOOM, TILE_X, TILE_Y), new Rollup(TAXON_ID, ZOOM, TILE_X, TILE_Y), new Rollup(COUNTRY_ISO_CODE, ZOOM, TILE_X,
        TILE_Y), new Rollup(DATASET_KEY, ZOOM, TILE_X, TILE_Y), new Rollup(PUBLISHER_KEY, ZOOM, TILE_X, TILE_Y));
    return new DataCube<DensityTile>(dimensions, rollups);
  }
}
