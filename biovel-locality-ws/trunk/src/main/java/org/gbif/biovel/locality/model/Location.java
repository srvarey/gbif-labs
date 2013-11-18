package org.gbif.biovel.locality.model;

import com.google.common.base.Objects;


/**
 * The locality model.
 */
public class Location {

  private String locality;
  private String country;

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (object instanceof Location) {
      Location that = (Location) object;
      return Objects.equal(this.country, that.country) && Objects.equal(this.locality, that.locality);
    }
    return false;
  }

  public String getCountry() {
    return country;
  }

  public String getLocality() {
    return locality;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(locality, country);
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setLocality(String locality) {
    this.locality = locality;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this.getClass()).add("locality", locality).add("country", country).toString();
  }
}
