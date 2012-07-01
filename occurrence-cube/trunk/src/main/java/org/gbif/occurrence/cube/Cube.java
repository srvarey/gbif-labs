package org.gbif.occurrence.cube;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.urbanairship.datacube.DataCube;
import com.urbanairship.datacube.Dimension;
import com.urbanairship.datacube.DimensionAndBucketType;
import com.urbanairship.datacube.Rollup;
import com.urbanairship.datacube.bucketers.BooleanBucketer;
import com.urbanairship.datacube.bucketers.StringToBytesBucketer;
import com.urbanairship.datacube.ops.LongOp;

/**
 * The cube definition (package access only).
 * Dimensions are Country, Kingdom and Georeferenced with counts available for:
 * <ol>
 * <li>Country (e.g. number of record in DK)</li>
 * <li>Kingdom (e.g. number of animal records)</li>
 * <li>Georeferenced (e.g. number of records with coordinates)</li>
 * <li>Country and kingdom (e.g. number of plant records in the US)</li>
 * <li>Country and georeferenced (e.g. number of records with coordinates in the UK)</li>
 * <li>Country and kingdom and georeferenced (e.g. number of bacteria records with coordinates in Spain)</li>
 * <ol>
 * 
 * @todo write public utility exposing a simple API enabling validated read/write access to cube.
 */
class Cube {

  // no id substitution
  static final Dimension<String> COUNTRY = new Dimension<String>("dwc:country", new StringToBytesBucketer(), false, 2);
  // id substitution applies
  static final Dimension<String> KINGDOM = new Dimension<String>("dwc:kingdom", new StringToBytesBucketer(), true, 7);
  // no id substitution
  static final Dimension<Boolean> GEOREFERENCED = new Dimension<Boolean>("gbif:georeferenced", new BooleanBucketer(), false, 1);

  // Singleton instance if accessed through the instance() method
  private static DataCube<LongOp> instance;

  static synchronized DataCube<LongOp> instance() {
    if (instance == null) {
      instance = newInstance();
    }
    return instance;
  }

  /**
   * Creates the cube.
   */
  private static DataCube<LongOp> newInstance() {
    // The dimensions of the cube
    List<Dimension<?>> dimensions = ImmutableList.<Dimension<?>>of(COUNTRY, KINGDOM, GEOREFERENCED);

    // The way the dimensions are "rolled up" for summary counting
    List<Rollup> rollups =
      ImmutableList.of(new Rollup(COUNTRY),
        new Rollup(KINGDOM),
        new Rollup(GEOREFERENCED),
        new Rollup(COUNTRY, KINGDOM),
        new Rollup(COUNTRY, GEOREFERENCED),
        new Rollup(KINGDOM, GEOREFERENCED),
        // more than 2 requires special syntax
        new Rollup(ImmutableSet.<DimensionAndBucketType>of(new DimensionAndBucketType(COUNTRY), new DimensionAndBucketType(KINGDOM),
          new DimensionAndBucketType(GEOREFERENCED))));

    return new DataCube<LongOp>(dimensions, rollups);
  }
}
