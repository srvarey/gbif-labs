package models;

import play.db.jpa.*;
import javax.persistence.*;

@Entity
public class Occurrence extends Model
{
  public String occurrenceId;
  public String institutionCode;
  public String collectionId;
  public String collectionCode;
  public String catalogNumber;
  public String sex;
  public String kingdom;
  public String phylum;
  public String klass; //class is a Java keyword
  public String taxOrder; //order is a SQL keyword
  public String family;
  public String genus;
  public String subgenus;
  public String specificEpithet;
  public String infraSpecificEpithet;
  public String scientificName;
  public String scientificNameAuthorship;
  public String taxonRank;
  public String dateIdentified;
  public String identifiedBy;
  public String typeStatus;
  public String continent;
  public String waterBody;
  public String country;
  public String stateProvince;
  public String locality;
  public String decimalLatitude;
  public String decimalLongitude;
  public String coordinatePrecision;
  public String minimumElevationInMeters;
  public String maximumElevationInMeters;
  public String minimumDepthInMeters;
  public String maximumDepthInMeters;
  public String status;
  
  @ManyToOne
  public Dataset dataset;
  
  public Occurrence(){}
  
  public Occurrence(
    String occurrenceId,
    String institutionCode,
    String collectionId,
    String collectionCode,
    String catalogNumber,
    String sex,
    String kingdom,
    String phylum,
    String klass,
    String taxOrder,
    String family,
    String genus,
    String subgenus,
    String specificEpithet,
    String infraSpecificEpithet,
    String scientificName,
    String scientificNameAuthorship,
    String taxonRank,
    String dateIdentified,
    String identifiedBy,
    String typeStatus,
    String continent,
    String waterBody,
    String country,
    String stateProvince,
    String locality,
    String decimalLatitude,
    String decimalLongitude,
    String coordinatePrecision,
    String minimumElevationInMeters,
    String maximumElevationInMeters,
    String minimumDepthInMeters,
    String maximumDepthInMeters,
    String status,
    Dataset dataset)
  {
	this.occurrenceId = occurrenceId;
	this.institutionCode = institutionCode;
	this.collectionId =  collectionId;
	this.collectionCode = collectionCode;
	this.catalogNumber = catalogNumber;
	this.sex = sex;
	this.kingdom = kingdom;
	this.phylum = phylum;
	this.klass = klass;
	this.taxOrder = taxOrder;
	this.family = family;
	this.genus = genus;
	this.subgenus = subgenus;
	this.specificEpithet = specificEpithet;
	this.infraSpecificEpithet = infraSpecificEpithet;
	this.scientificName = scientificName;
	this.scientificNameAuthorship = scientificNameAuthorship;
	this.taxonRank = taxonRank;
	this.dateIdentified = dateIdentified;
	this.identifiedBy = identifiedBy;
	this.typeStatus = typeStatus;
	this.continent = continent;
	this.waterBody = waterBody;
	this.country = country;
	this.stateProvince = stateProvince;
	this.locality = locality;
	this.decimalLatitude = decimalLatitude;
	this.decimalLongitude = decimalLongitude;
	this.coordinatePrecision = coordinatePrecision;
	this.minimumElevationInMeters = minimumElevationInMeters;
	this.maximumElevationInMeters = maximumElevationInMeters;
	this.minimumDepthInMeters = minimumDepthInMeters;
	this.maximumDepthInMeters = maximumDepthInMeters;
	this.status = status;
	this.dataset = dataset;
  }
}
