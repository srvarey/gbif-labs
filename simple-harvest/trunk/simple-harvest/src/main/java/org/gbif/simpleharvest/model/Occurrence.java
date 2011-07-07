/**
 * 
 */
package org.gbif.simpleharvest.model;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This represents the occurrence record in the database
 * @author tim
 */
public class Occurrence {
  private int id;
  private int datasetId;
  private String occurrenceId;
  private String institutionCode;
  private String collectionId;
  private String collectionCode;
  private String catalogueNumber;
  private String sex;
  private String kingdom;
  private String phylum;
  private String klass;
  private String order;
  private String family;
  private String genus;
  private String subgenus;
  private String specificEpithet;
  private String infraSpecificEpithet;
  private String scientificName;
  private String scientificNameAuthorship;
  private String taxonRank;
  private String dateIdentified;
  private String identifiedBy;
  private String typeStatus;
  private String continent;
  private String waterBody;
  private String country;
  private String stateProvince;
  private String locality;
  private String decimalLatitude;
  private String decimalLongitude;
  private String coordinatePrecision;
  private String minimumElevationInMeters;
  private String maximumElevationInMeters;
  private String minimumDepthInMeters;
  private String maximumDepthInMeters;
  
  
  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public String getInstitutionCode() {
    return institutionCode;
  }
  public void setInstitutionCode(String institutionCode) {
    this.institutionCode = institutionCode;
  }
  public String getCollectionCode() {
    return collectionCode;
  }
  public void setCollectionCode(String collectionCode) {
    this.collectionCode = collectionCode;
  }
  public String getCatalogueNumber() {
    return catalogueNumber;
  }
  public void setCatalogueNumber(String catalogueNumber) {
    this.catalogueNumber = catalogueNumber;
  }
  public String getScientificName() {
    return scientificName;
  }
  public void setScientificName(String scientificName) {
    this.scientificName = scientificName;
  }
  public int getDatasetId() {
    return datasetId;
  }
  public void setDatasetId(int datasetId) {
    this.datasetId = datasetId;
  }
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
public void setLocality(String locality) {
	this.locality = locality;
}
public String getLocality() {
	return locality;
}
public String getOccurrenceId() {
	return occurrenceId;
}
public void setOccurrenceId(String occurrenceId) {
	this.occurrenceId = occurrenceId;
}
public String getCollectionId() {
	return collectionId;
}
public void setCollectionId(String collectionId) {
	this.collectionId = collectionId;
}
public String getSex() {
	return sex;
}
public void setSex(String sex) {
	this.sex = sex;
}
public String getKingdom() {
	return kingdom;
}
public void setKingdom(String kingdom) {
	this.kingdom = kingdom;
}
public String getPhylum() {
	return phylum;
}
public void setPhylum(String phylum) {
	this.phylum = phylum;
}
public String getKlass() {
	return klass;
}
public void setKlass(String klass) {
	this.klass = klass;
}
public String getOrder() {
	return order;
}
public void setOrder(String order) {
	this.order = order;
}
public String getFamily() {
	return family;
}
public void setFamily(String family) {
	this.family = family;
}
public String getGenus() {
	return genus;
}
public void setGenus(String genus) {
	this.genus = genus;
}
public String getSubgenus() {
	return subgenus;
}
public void setSubgenus(String subgenus) {
	this.subgenus = subgenus;
}
public String getSpecificEpithet() {
	return specificEpithet;
}
public void setSpecificEpithet(String specificEpithet) {
	this.specificEpithet = specificEpithet;
}
public String getInfraSpecificEpithet() {
	return infraSpecificEpithet;
}
public void setInfraSpecificEpithet(String infraSpecificEpithet) {
	this.infraSpecificEpithet = infraSpecificEpithet;
}
public String getScientificNameAuthorship() {
	return scientificNameAuthorship;
}
public void setScientificNameAuthorship(String scientificNameAuthorship) {
	this.scientificNameAuthorship = scientificNameAuthorship;
}
public String getDateIdentified() {
	return dateIdentified;
}
public void setDateIdentified(String dateIdentified) {
	this.dateIdentified = dateIdentified;
}
public String getTypeStatus() {
	return typeStatus;
}
public void setTypeStatus(String typeStatus) {
	this.typeStatus = typeStatus;
}
public String getContinent() {
	return continent;
}
public void setContinent(String continent) {
	this.continent = continent;
}
public String getWaterBody() {
	return waterBody;
}
public void setWaterBody(String waterBody) {
	this.waterBody = waterBody;
}
public String getCountry() {
	return country;
}
public void setCountry(String country) {
	this.country = country;
}
public String getStateProvince() {
	return stateProvince;
}
public void setStateProvince(String stateProvince) {
	this.stateProvince = stateProvince;
}
public String getDecimalLatitude() {
	return decimalLatitude;
}
public void setDecimalLatitude(String decimalLatitude) {
	this.decimalLatitude = decimalLatitude;
}
public String getDecimalLongitude() {
	return decimalLongitude;
}
public void setDecimalLongitude(String decimalLongitude) {
	this.decimalLongitude = decimalLongitude;
}
public String getCoordinatePrecision() {
	return coordinatePrecision;
}
public void setCoordinatePrecision(String coordinatePrecision) {
	this.coordinatePrecision = coordinatePrecision;
}
public String getMinimumElevationInMeters() {
	return minimumElevationInMeters;
}
public void setMinimumElevationInMeters(String minimumElevationInMeters) {
	this.minimumElevationInMeters = minimumElevationInMeters;
}
public String getMaximumElevationInMeters() {
	return maximumElevationInMeters;
}
public void setMaximumElevationInMeters(String maximumElevationInMeters) {
	this.maximumElevationInMeters = maximumElevationInMeters;
}
public String getMinimumDepthInMeters() {
	return minimumDepthInMeters;
}
public void setMinimumDepthInMeters(String minimumDepthInMeters) {
	this.minimumDepthInMeters = minimumDepthInMeters;
}
public String getMaximumDepthInMeters() {
	return maximumDepthInMeters;
}
public void setMaximumDepthInMeters(String maximumDepthInMeters) {
	this.maximumDepthInMeters = maximumDepthInMeters;
}
public String getTaxonRank() {
	return taxonRank;
}
public void setTaxonRank(String taxonRank) {
	this.taxonRank = taxonRank;
}
public String getIdentifiedBy() {
	return identifiedBy;
}
public void setIdentifiedBy(String identifiedBy) {
	this.identifiedBy = identifiedBy;
}
}
