package org.gbif.cube.gmap.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.google.common.base.Objects;
import org.apache.hadoop.io.WritableComparable;

/**
 * A container object intended for use as MR values to encapsulate a latitude
 * and longitude pair.
 */
public class LatLngWritable implements WritableComparable<LatLngWritable> {

  private double lat, lng;

  public LatLngWritable() {
  }

  public LatLngWritable(double lat, double lng) {
    this.lat = lat;
    this.lng = lng;
  }

  @Override
  public int compareTo(LatLngWritable o) {
    int c = Double.compare(lat, o.getLat());
    if (c == 0) {
      return Double.compare(lng, o.getLng());
    } else {
      return c;
    }
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof LatLngWritable) {
      LatLngWritable that = (LatLngWritable) object;
      return Objects.equal(this.lat, that.lat) && Objects.equal(this.lng, that.lng);
    }
    return false;
  }

  public double getLat() {
    return lat;
  }

  public double getLng() {
    return lng;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(lat, lng);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    lat = in.readDouble();
    lng = in.readDouble();
  }

  public void setLat(double lat) {
    this.lat = lat;
  }

  public void setLng(double lng) {
    this.lng = lng;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("lat", lat).add("lng", lng).toString();
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeDouble(lat);
    out.writeDouble(lng);
  }
}
