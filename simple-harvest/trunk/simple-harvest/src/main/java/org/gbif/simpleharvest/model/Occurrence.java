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
  private String institutionCode;
  private String collectionCode;
  private String catalogueNumber;
  private String scientificName;
  
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
}
