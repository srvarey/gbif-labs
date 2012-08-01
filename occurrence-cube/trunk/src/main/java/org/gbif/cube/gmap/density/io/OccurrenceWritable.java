package org.gbif.cube.gmap.density.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.google.common.base.Objects;
import org.apache.hadoop.io.WritableComparable;

/**
 * A container object for use in MR, that encapsulates the fields needed to locate records
 * at the same point.
 */
public class OccurrenceWritable implements WritableComparable<OccurrenceWritable> {

  private Integer kingdomID, phylumID, classID, orderID, familyID, genusID, speciesID, taxonID, issues, datasetID, count;
  private String publishingOrganisationKey, datasetKey, countryIsoCode;
  private Double latitude, longitude;

  private static final int NULL_INT = -1;
  private static final String NULL_STRING = "NULL";
  private static final double NULL_DOUBLE = -999; // invalid for coordinates

  public OccurrenceWritable() {
  }

  public OccurrenceWritable(Integer kingdomID, Integer phylumID, Integer classID, Integer orderID, Integer familyID, Integer genusID,
    Integer speciesID, Integer taxonID, Integer issues, Integer datasetID, String publishingOrganisationKey, String datasetKey,
    String countryIsoCode, Double latitude, Double longitude, Integer count) {
    this.kingdomID = kingdomID;
    this.phylumID = phylumID;
    this.classID = classID;
    this.orderID = orderID;
    this.familyID = familyID;
    this.genusID = genusID;
    this.speciesID = speciesID;
    this.taxonID = taxonID;
    this.issues = issues;
    this.datasetID = datasetID;
    this.publishingOrganisationKey = publishingOrganisationKey;
    this.datasetKey = datasetKey;
    this.countryIsoCode = countryIsoCode;
    this.latitude = latitude;
    this.longitude = longitude;
    this.count = count;
  }

  @Override
  public int compareTo(OccurrenceWritable o) {
    // TODO: Do a proper comparison
    return this.toString().compareTo(o.toString());
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof OccurrenceWritable) {
      OccurrenceWritable that = (OccurrenceWritable) object;
      return Objects.equal(this.kingdomID, that.kingdomID) && Objects.equal(this.phylumID, that.phylumID)
        && Objects.equal(this.classID, that.classID) && Objects.equal(this.orderID, that.orderID) && Objects.equal(this.familyID, that.familyID)
        && Objects.equal(this.genusID, that.genusID) && Objects.equal(this.speciesID, that.speciesID) && Objects.equal(this.taxonID, that.taxonID)
        && Objects.equal(this.issues, that.issues) && Objects.equal(this.datasetID, that.datasetID)
        && Objects.equal(this.publishingOrganisationKey, that.publishingOrganisationKey) && Objects.equal(this.datasetKey, that.datasetKey)
        && Objects.equal(this.countryIsoCode, that.countryIsoCode) && Objects.equal(this.latitude, that.latitude)
        && Objects.equal(this.longitude, that.longitude) && Objects.equal(this.count, that.count);
    }
    return false;
  }

  public Integer getClassID() {
    return classID;
  }


  public Integer getCount() {
    return count;
  }

  public String getCountryIsoCode() {
    return countryIsoCode;
  }

  public Integer getDatasetID() {
    return datasetID;
  }

  public String getDatasetKey() {
    return datasetKey;
  }


  public Integer getFamilyID() {
    return familyID;
  }


  public Integer getGenusID() {
    return genusID;
  }


  public Integer getIssues() {
    return issues;
  }


  public Integer getKingdomID() {
    return kingdomID;
  }


  public Double getLatitude() {
    return latitude;
  }


  public Double getLongitude() {
    return longitude;
  }


  public Integer getOrderID() {
    return orderID;
  }


  public Integer getPhylumID() {
    return phylumID;
  }


  public String getPublishingOrganisationKey() {
    return publishingOrganisationKey;
  }


  public Integer getSpeciesID() {
    return speciesID;
  }


  public Integer getTaxonID() {
    return taxonID;
  }


  @Override
  public int hashCode() {
    return Objects.hashCode(kingdomID, phylumID, classID, orderID, familyID, genusID, speciesID, taxonID, issues, datasetID,
      publishingOrganisationKey, datasetKey, countryIsoCode, latitude, longitude, count);
  }


  private Double readDouble(DataInput in) throws IOException {
    double v = in.readDouble();
    return (v == NULL_DOUBLE) ? null : v;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    kingdomID = readInt(in);
    phylumID = readInt(in);
    classID = readInt(in);
    orderID = readInt(in);
    familyID = readInt(in);
    genusID = readInt(in);
    speciesID = readInt(in);
    taxonID = readInt(in);
    issues = readInt(in);
    datasetID = readInt(in);
    publishingOrganisationKey = readString(in);
    datasetKey = readString(in);
    countryIsoCode = readString(in);
    latitude = readDouble(in);
    longitude = readDouble(in);
    count = readInt(in);
  }


  private Integer readInt(DataInput in) throws IOException {
    int v = in.readInt();
    return (v == NULL_INT) ? null : v;
  }

  private String readString(DataInput in) throws IOException {
    String v = in.readUTF();
    return (NULL_STRING.equals(v)) ? null : v;
  }


  public void setClassID(Integer classID) {
    this.classID = classID;
  }


  public void setCount(Integer count) {
    this.count = count;
  }


  public void setCountryIsoCode(String countryIsoCode) {
    this.countryIsoCode = countryIsoCode;
  }


  public void setDatasetID(Integer datasetID) {
    this.datasetID = datasetID;
  }


  public void setDatasetKey(String datasetKey) {
    this.datasetKey = datasetKey;
  }


  public void setFamilyID(Integer familyID) {
    this.familyID = familyID;
  }


  public void setGenusID(Integer genusID) {
    this.genusID = genusID;
  }


  public void setIssues(Integer issues) {
    this.issues = issues;
  }


  public void setKingdomID(Integer kingdomID) {
    this.kingdomID = kingdomID;
  }


  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }


  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }


  public void setOrderID(Integer orderID) {
    this.orderID = orderID;
  }


  public void setPhylumID(Integer phylumID) {
    this.phylumID = phylumID;
  }


  public void setPublishingOrganisationKey(String publishingOrganisationKey) {
    this.publishingOrganisationKey = publishingOrganisationKey;
  }


  public void setSpeciesID(Integer speciesID) {
    this.speciesID = speciesID;
  }

  public void setTaxonID(Integer taxonID) {
    this.taxonID = taxonID;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("kingdomID", kingdomID).add("phylumID", phylumID).add("classID", classID).add("orderID", orderID)
      .add("familyID", familyID).add("genusID", genusID).add("speciesID", speciesID).add("taxonID", taxonID).add("issues", issues)
      .add("datasetID", datasetID).add("publishingOrganisationKey", publishingOrganisationKey).add("datasetKey", datasetKey)
      .add("countryIsoCode", countryIsoCode).add("latitude", latitude).add("longitude", longitude).add("count", count).toString();
  }

  @Override
  public void write(DataOutput out) throws IOException {
    write(out, kingdomID);
    write(out, phylumID);
    write(out, classID);
    write(out, orderID);
    write(out, familyID);
    write(out, genusID);
    write(out, speciesID);
    write(out, taxonID);
    write(out, issues);
    write(out, datasetID);
    write(out, publishingOrganisationKey);
    write(out, datasetKey);
    write(out, countryIsoCode);
    write(out, latitude);
    write(out, longitude);
    write(out, count);
  }

  private void write(DataOutput out, Double d) throws IOException {
    double v = (d == null) ? NULL_DOUBLE : d;
    out.writeDouble(v);
  }

  private void write(DataOutput out, Integer i) throws IOException {
    int v = (i == null) ? NULL_INT : i;
    out.writeInt(v);
  }

  private void write(DataOutput out, String s) throws IOException {
    String v = (s == null || s.length() == 0) ? NULL_STRING : s;
    out.writeUTF(v);
  }
}
