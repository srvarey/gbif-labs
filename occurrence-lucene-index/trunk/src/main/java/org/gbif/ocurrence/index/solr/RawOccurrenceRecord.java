/***************************************************************************
 * Copyright (C) 2006 Global Biodiversity Information Facility Secretariat.
 * All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ***************************************************************************/
package org.gbif.ocurrence.index.solr;

import java.util.Date;
import org.apache.solr.client.solrj.beans.Field;


/**
 * RawOccurrenceRecord Model Object represents an unparsed occurrence record. 
 *
 *@author fmendez
 *
 * * 
 */
public class RawOccurrenceRecord {
	
	/** The primary key */
	@Field("id")
	protected Long id;
	
	/** The id for the provider of the record*/
	@Field("data_provider_id")
	protected Long dataProviderId;
	
	
	/** The resource within the provider that contains this record */
	@Field("data_resource_id")
	protected Long dataResourceId;

	/** The resource access point for this record */
	@Field("resource_access_point_id")
	protected Integer resourceAccessPointId;	
	
	/**  Institution code*/
	@Field("institution_code")
	protected String institutionCode;
	
	/** Collection code */
	@Field("collection_code")
	protected String collectionCode;
	
	/** Catalogue Number */
	@Field("catalogue_number")
	protected String catalogueNumber;
	
	/** The scientific name */
	@Field("scientific_name")
	protected String scientificName;
	
	/** Author of the scientific name */
	@Field("author")
	protected String author;
	
	/** Rank of the scientific name */
	@Field("rank")
	protected String rank;
	
	/** Kingdom */
	@Field("kingdom")
	protected String kingdom;
	
	/** Phylum */
	@Field("phylum")
	protected String phylum;
	
	/** Class */
	@Field("class")
	protected String bioClass;
	
	/** Order */
	@Field("order")
	protected String order;
	
	/** Family */
	@Field("family")
	protected String family;
	
	/** Genus */
	@Field("genus")
	protected String genus;
	
	/** Species */
	@Field("species")
	protected String species;
	
	/** Species */
	@Field("subspecies")
	protected String subspecies;
	
	/** Latitude */
	@Field("latitude")
	protected String latitude;
	
	
	/** Longitude */
	@Field("longitude")
	protected String longitude;
	
	/** Precision of latitude and longitude */
	@Field("lat_long_precision")
	protected String latLongPrecision;	
	
	/** Minimum altitude of occurrence */
	@Field("min_altitude")
	protected String minAltitude;
	
	/** Maximum altitude of occurrence */
	@Field("max_altitude")
	protected String maxAltitude;
	
	/** Precision of altitude of occurrence */
	@Field("altitude_precision")
	protected String altitudePrecision;
	
	/** Minimum depth of occurrence */
	@Field("min_depth")
	protected String minDepth;
	
	/** Maximum depth of occurrence */
	@Field("max_depth")
	protected String maxDepth;
	
	/** Precision of depth of occurrence */
	@Field("depth_precision")
	protected String depthPrecision;
	
	/** Continent or ocean of occurrence */
	@Field("continent_ocean")
	protected String continentOrOcean;
	
	/** Country of occurrence */
	@Field("country")
	protected String country;
	
	/** The state/province of occurrence */
	@Field("state_province")
	protected String stateOrProvince;
	
	/** The county of occurrence */
	@Field("county")
	protected String county;
	
	/** The name of the collector */
	@Field("collector_name")
	protected String collectorName;
	
	/** The name of the identifier */
	@Field("identifier_name")
	protected String identifierName;
	
	/** The name of the collector */
	@Field("identification_date")
	protected Date identificationDate;
	
	/** The locality name */
	@Field("locality")
	protected String locality;
	
	/** The year of occurrence */
	@Field("year")
	protected String year;
	
	/** The month of occurrence */
	@Field("month")
	protected String month;
	
	/** The day (in month) of occurrence */
	@Field("day")
	protected String day;
	
	/** The basis of record */
	@Field("basis_of_record")
	protected String basisOfRecord;
	
	/** The record modification date */
	@Field("created")
	protected Date created;	
	
	/** The record modification date */
	@Field("modified")
	protected Date modified;

	/** The record deletion date */
	@Field("deleted")
	protected Date deleted;
	
	
	/**
	 * @return Returns the id.
	 * @hibernate.id 
	 *	generator-class="native" 
	 *	unsaved-value="null"
	 *	column="id"
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the altitudePrecision
	 */
	public String getAltitudePrecision() {
		return altitudePrecision;
	}
	/**
	 * @param altitudePrecision the altitudePrecision to set
	 */
	public void setAltitudePrecision(String altitudePrecision) {
		this.altitudePrecision = altitudePrecision;
	}
	/**
	 * @return the maxAltitude
	 */
	public String getMaxAltitude() {
		return maxAltitude;
	}
	/**
	 * @param maxAltitude the maxAltitude to set
	 */
	public void setMaxAltitude(String maxAltitude) {
		this.maxAltitude = maxAltitude;
	}
	/**
	 * @return the minAltitude
	 */
	public String getMinAltitude() {
		return minAltitude;
	}
	/**
	 * @param minAltitude the minAltitude to set
	 */
	public void setMinAltitude(String minAltitude) {
		this.minAltitude = minAltitude;
	}
	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}
	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}
	/**
	 * @return the basisOfRecord
	 */
	public String getBasisOfRecord() {
		return basisOfRecord;
	}
	/**
	 * @param basisOfRecord the basisOfRecord to set
	 */
	public void setBasisOfRecord(String basisOfRecord) {
		this.basisOfRecord = basisOfRecord;
	}
	/**
	 * @return the bioClass
	 */
	public String getBioClass() {
		return bioClass;
	}
	/**
	 * @param bioClass the bioClass to set
	 */
	public void setBioClass(String bioClass) {
		this.bioClass = bioClass;
	}
	/**
	 * @return the catalogueNumber
	 */
	public String getCatalogueNumber() {
		return catalogueNumber;
	}
	/**
	 * @param catalogueNumber the catalogueNumber to set
	 */
	public void setCatalogueNumber(String catalogueNumber) {
		this.catalogueNumber = catalogueNumber;
	}
	/**
	 * @return the collectionCode
	 */
	public String getCollectionCode() {
		return collectionCode;
	}
	/**
	 * @param collectionCode the collectionCode to set
	 */
	public void setCollectionCode(String collectionCode) {
		this.collectionCode = collectionCode;
	}
	/**
	 * @return the collectorName
	 */
	public String getCollectorName() {
		return collectorName;
	}
	/**
	 * @param collectorName the collectorName to set
	 */
	public void setCollectorName(String collectorName) {
		this.collectorName = collectorName;
	}
	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	/**
	 * @return the county
	 */
	public String getCounty() {
		return county;
	}
	/**
	 * @param county the county to set
	 */
	public void setCounty(String county) {
		this.county = county;
	}
	/**
	 * @return the dataProviderId
	 */
	public Long getDataProviderId() {
		return dataProviderId;
	}
	/**
	 * @param dataProviderId the dataProviderId to set
	 */
	public void setDataProviderId(Long dataProviderId) {
		this.dataProviderId = dataProviderId;
	}
	/**
	 * @return the dataResourceId
	 */
	public Long getDataResourceId() {
		return dataResourceId;
	}
	/**
	 * @param dataResourceId the dataResourceId to set
	 */
	public void setDataResourceId(Long dataResourceId) {
		this.dataResourceId = dataResourceId;
	}
	/**
	 * @return the day
	 */
	public String getDay() {
		return day;
	}
	/**
	 * @param day the day to set
	 */
	public void setDay(String day) {
		this.day = day;
	}
	/**
	 * @return the maxDepth
	 */
	public String getMaxDepth() {
		return maxDepth;
	}
	/**
	 * @param maxDepth the maxDepth to set
	 */
	public void setMaxDepth(String maxDepth) {
		this.maxDepth = maxDepth;
	}
	/**
	 * @return the minDepth
	 */
	public String getMinDepth() {
		return minDepth;
	}
	/**
	 * @param minDepth the minDepth to set
	 */
	public void setMinDepth(String minDepth) {
		this.minDepth = minDepth;
	}
	/**
	 * @return the depthPrecision
	 */
	public String getDepthPrecision() {
		return depthPrecision;
	}
	/**
	 * @param depthPrecision the depthPrecision to set
	 */
	public void setDepthPrecision(String depthPrecision) {
		this.depthPrecision = depthPrecision;
	}
	/**
	 * @return the family
	 */
	public String getFamily() {
		return family;
	}
	/**
	 * @param family the family to set
	 */
	public void setFamily(String family) {
		this.family = family;
	}
	/**
	 * @return the genus
	 */
	public String getGenus() {
		return genus;
	}
	/**
	 * @param genus the genus to set
	 */
	public void setGenus(String genus) {
		this.genus = genus;
	}
	/**
	 * @return the institutionCode
	 */
	public String getInstitutionCode() {
		return institutionCode;
	}
	/**
	 * @param institutionCode the institutionCode to set
	 */
	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}
	/**
	 * @return the kingdom
	 */
	public String getKingdom() {
		return kingdom;
	}
	/**
	 * @param kingdom the kingdom to set
	 */
	public void setKingdom(String kingdom) {
		this.kingdom = kingdom;
	}
	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	/**
	 * @return the latLongPrecision
	 */
	public String getLatLongPrecision() {
		return latLongPrecision;
	}
	/**
	 * @param latLongPrecision the latLongPrecision to set
	 */
	public void setLatLongPrecision(String latLongPrecision) {
		this.latLongPrecision = latLongPrecision;
	}
	/**
	 * @return the locality
	 */
	public String getLocality() {
		return locality;
	}
	/**
	 * @param locality the locality to set
	 */
	public void setLocality(String locality) {
		this.locality = locality;
	}
	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	/**
	 * @return the month
	 */
	public String getMonth() {
		return month;
	}
	/**
	 * @param month the month to set
	 */
	public void setMonth(String month) {
		this.month = month;
	}
	/**
	 * @return the order
	 */
	public String getOrder() {
		return order;
	}
	/**
	 * @param order the order to set
	 */
	public void setOrder(String order) {
		this.order = order;
	}
	/**
	 * @return the phylum
	 */
	public String getPhylum() {
		return phylum;
	}
	/**
	 * @param phylum the phylum to set
	 */
	public void setPhylum(String phylum) {
		this.phylum = phylum;
	}
	/**
	 * @return the rank
	 */
	public String getRank() {
		return rank;
	}
	/**
	 * @param rank the rank to set
	 */
	public void setRank(String rank) {
		this.rank = rank;
	}
	/**
	 * @return the resourceAccessPointId
	 */
	public Integer getResourceAccessPointId() {
		return resourceAccessPointId;
	}
	/**
	 * @param resourceAccessPointId the resourceAccessPointId to set
	 */
	public void setResourceAccessPointId(Integer resourceAccessPointId) {
		this.resourceAccessPointId = resourceAccessPointId;
	}
	/**
	 * @return the scientificName
	 */
	public String getScientificName() {
		return scientificName;
	}
	/**
	 * @param scientificName the scientificName to set
	 */
	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}
	/**
	 * @return the species
	 */
	public String getSpecies() {
		return species;
	}
	/**
	 * @param species the species to set
	 */
	public void setSpecies(String species) {
		this.species = species;
	}
	/**
	 * @return the subspecies
	 */
	public String getSubspecies() {
		return subspecies;
	}
	/**
	 * @param subspecies the subspecies to set
	 */
	public void setSubspecies(String subspecies) {
		this.subspecies = subspecies;
	}
	/**
	 * @return the stateOrProvince
	 */
	public String getStateOrProvince() {
		return stateOrProvince;
	}
	/**
	 * @param stateOrProvince the stateOrProvince to set
	 */
	public void setStateOrProvince(String stateOrProvince) {
		this.stateOrProvince = stateOrProvince;
	}
	/**
	 * @return the year
	 */
	public String getYear() {
		return year;
	}
	/**
	 * @param year the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}
	/**
	 * @return the modified
	 */
	public Date getModified() {
		return modified;
	}
	/**
	 * @param modified the modified to set
	 */
	public void setModified(Date modified) {
		this.modified = modified;
	}
	
	/**
	 * @return the continentOrOcean
	 */
	public String getContinentOrOcean() {
		return continentOrOcean;
	}
	/**
	 * @param continentOrOcean the continentOrOcean to set
	 */
	public void setContinentOrOcean(String continentOrOcean) {
		this.continentOrOcean = continentOrOcean;
	}
	/**
	 * @return the identificationDate
	 */
	public Date getIdentificationDate() {
		return identificationDate;
	}
	/**
	 * @param identificationDate the identificationDate to set
	 */
	public void setIdentificationDate(Date identificationDate) {
		this.identificationDate = identificationDate;
	}
	/**
	 * @return the identifierName
	 */
	public String getIdentifierName() {
		return identifierName;
	}
	/**
	 * @param identifierName the identifierName to set
	 */
	public void setIdentifierName(String identifierName) {
		this.identifierName = identifierName;
	}
	
	/**
	 * @return the deleted
	 */
	public Date getDeleted() {
		return deleted;
	}

	/**
	 * @param deleted the deleted to set
	 */
	public void setDeleted(Date deleted) {
		this.deleted = deleted;
	}

	/**
	 * Returns true if object is an instance of BaseObject and 
	 * the identifier value is equal.
	 * Required by ORMS for caching/collections performance.
	 * @see java.lang.Object#equals(Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (object instanceof RawOccurrenceRecord) {
			RawOccurrenceRecord other = (RawOccurrenceRecord)object;
			if (getId() != null) {
				return getId().equals(other.getId());
			}
			else if (this == object) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns hashcode of identifier value where possible.
	 * Required by ORMS for caching/collections performance.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (getId() != null) {
			return getId().hashCode();
		}
		return super.hashCode();
	}

	/**
	 * @see java.lang.Comparable#compareTo()
	 */
	public int compareTo(Object object) {
		RawOccurrenceRecord other = (RawOccurrenceRecord)object;
		if (getId() != null) {
			return getId().compareTo(other.getId());
		}
		return -1;
	}
	
	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}
	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}
}