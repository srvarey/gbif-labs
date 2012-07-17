package org.gbif.occurrence.cube.gmap;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.urbanairship.datacube.BucketType;
import com.urbanairship.datacube.Bucketer;
import com.urbanairship.datacube.CSerializable;

/**
 * A bucketer that handles the addressing of a tile (zoom, x and y)
 */
public class TileCoordBucketer implements Bucketer<TileCoord> {

  // we hold 3x4 bytes = 12
  public static final int SERIALIZED_SIZE = 12;

  private CSerializable bucket(final TileCoord tc) {
    return new TileCoordSerializable(tc);
  }

  // BucketType is ignored - we always reference will all atoms of the coordinates
  @Override
  public CSerializable bucketForRead(Object coordinateField, BucketType bucketType) {
    return bucket((TileCoord) coordinateField);
  }

  @Override
  public CSerializable bucketForWrite(TileCoord coordinateField, BucketType bucketType) {
    return bucket(coordinateField);
  }

  @Override
  public List<BucketType> getBucketTypes() {
    return ImmutableList.of(BucketType.IDENTITY);
  }
}
