package org.gbif.cube.gmap.density.backfill;

import org.gbif.cube.gmap.io.LatLngWritable;
import org.gbif.cube.gmap.io.TileKeyWritable;

import java.io.IOException;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * A combiner to help optimize processing when it is known that there is repetition at a single location.
 * A LatLngWritable contains a lat, lng and a count. When Mappers are grouping LatLngWritables for a single tile,
 * for the purposes of producing density tiles, this combiner can be used to pre-group the counts at the locations,
 * before they are further clustered in the Reducer. This only helps when there is repetition at a single point, but
 * cardinality of the GBIF index is around 40 occurrences per point.
 */
public class LocationCombiner extends Reducer<TileKeyWritable, LatLngWritable, TileKeyWritable, LatLngWritable> {

  // Utility to allow for indexing in Maps by a single point
  private static class Location {

    private final double lat;
    private final double lng;

    public Location(LatLngWritable llw) {
      this.lat = llw.getLat();
      this.lng = llw.getLng();
    }

    @Override
    public boolean equals(Object object) {
      if (this == object) {
        return true;
      }
      if (object instanceof Location) {
        Location that = (Location) object;
        return Objects.equal(this.lat, that.lat) && Objects.equal(this.lng, that.lng);
      }
      return false;
    }

    @Override
    public int hashCode() {
      int hash = 7;
      hash = (int) (hash * 17 + lat);
      hash = (int) (hash * 31 + lng);
      return hash;
    }
  }

  /**
   * Roll up the counts within a single location.
   */
  @Override
  protected void reduce(TileKeyWritable key, Iterable<LatLngWritable> values, Context context) throws IOException, InterruptedException {
    Map<Location, Integer> index = Maps.newHashMap();
    for (LatLngWritable llw : values) {
      Location l = new Location(llw);
      if (!index.containsKey(l)) {
        index.put(l, llw.getCount());
      } else {
        // roll up the count since location is the same
        int count = index.get(l) + llw.getCount();
        index.put(l, count);
      }
    }
    for (Map.Entry<Location, Integer> i : index.entrySet()) {
      context.write(key, new LatLngWritable(i.getKey().lat, i.getKey().lng, i.getValue()));
    }
  }
}
