package models.occurrence.harvest.biocase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import models.Occurrence;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Inspects the response and builds lists of model objects
 * @author tim
 */
public class ResponseToModelHandler 
{
  public List<Occurrence> handleResponse(GZIPInputStream inputStream) throws IOException 
  {
    List<Occurrence> results = new ArrayList<Occurrence>();
    
    Digester digester = new Digester();
    // we need it to understand that /biocase:response is /response
    digester.setNamespaceAware(true);
    digester.setValidating(false);
    digester.push(results);
    // Digester uses the class loader of its own class to find classes needed for object create rules. As Digester is bundled, the wrong class loader was used leading to this Exception.
    digester.setUseContextClassLoader(true);
    // ever unit forces a new Occurrence record to be created
    digester.addObjectCreate("*/Units/Unit", "models.Occurrence");

    
    // extract the properties we are interested in
    digester.addBeanPropertySetter("*/UnitGUID", "occurrenceId");
    digester.addBeanPropertySetter("*/SourceInstitutionID", "institutionCode");
    digester.addBeanPropertySetter("*/SourceID", "collectionId");
    digester.addBeanPropertySetter("*/SourceID", "collectionCode");            
    digester.addBeanPropertySetter("*/UnitID", "catalogNumber");
    digester.addBeanPropertySetter("*/Sex", "sex");
    //digester.addBeanPropertySetter("", "kingdom");
    //digester.addBeanPropertySetter("", "phylum");
    //digester.addBeanPropertySetter("", "klass");                                                                                 
    //digester.addBeanPropertySetter("", "order");
    //digester.addBeanPropertySetter("", "family");
    //digester.addBeanPropertySetter("", "genus");
    //digester.addBeanPropertySetter("", "subgenus");
    //digester.addBeanPropertySetter("", "specificEpithet");
    //digester.addBeanPropertySetter("", "infraspecificEpithet");
    digester.addBeanPropertySetter("*/ScientificName/FullScientificNameString", "scientificName");
    //digester.addBeanPropertySetter("", "scientificNameAuthorship");
    digester.addBeanPropertySetter("*/ScientificName/NameAtomised/Botanical/Rank", "taxonRank");
    digester.addBeanPropertySetter("*/Date/DateText", "dateIdentified");
    digester.addBeanPropertySetter("*/Identifications/Identification/Identifiers/IdentifiersText", "identifiedBy");
    digester.addBeanPropertySetter("*/SpecimenUnit/NomenclaturalTypeDesignations/NomenclaturalTypeText", "typeStatus");
    //digester.addBeanPropertySetter("", "continent");
    //digester.addBeanPropertySetter("", "waterBody");
    digester.addBeanPropertySetter("*/Gathering/Country/Name", "country");
    //digester.addBeanPropertySetter("", "stateProvince");
    digester.addBeanPropertySetter("*/Gathering/AreaDetail", "locality");
    digester.addBeanPropertySetter("*/Gathering/SiteCoordinateSets/SiteCoordinates/CoordinatesLatLon/LatitudeDecimal", "decimalLatitude");
    digester.addBeanPropertySetter("*/Gathering/SiteCoordinateSets/SiteCoordinates/CoordinatesLatLon/LongitudeDecimal", "decimalLongitude");
    digester.addBeanPropertySetter("*/Gathering/SiteCoordinateSets/SiteCoordinates/CoordinatesLatLong/AccuracyStatement", "coordinatePrecision");
    digester.addBeanPropertySetter("*/Gathering/Altitude/MeasurementOrFactAtomised/LowerValue", "minimumElevationInMeters");
    digester.addBeanPropertySetter("*/Gathering/Altitude/MeasurementOrFactAtomised/UpperValue", "maximumElevationInMeters");
    digester.addBeanPropertySetter("*/Gathering/Depth/MeasurementOrFactAtomised/LowerValue", "minimumDepthInMeters");
    digester.addBeanPropertySetter("*/Gathering/Depth/MeasurementOrFactAtomised/UpperValue", "maximumDepthInMeters");
    
 // extract the properties we are interested in
    //digester.addBeanPropertySetter("*/SourceInstitutionID", "institutionCode");
    //digester.addBeanPropertySetter("*/SourceID", "collectionCode");                                                                                         
    //digester.addBeanPropertySetter("*/UnitID", "catalogueNumber");
    //digester.addBeanPropertySetter("*/FullScientificNameString", "scientificName");
    //digester.addBeanPropertySetter("*/LocalityText", "locality");
    
    
    // add the created Occurrence record to the list
    digester.addSetNext("*/Units/Unit", "add");
    try {
      digester.parse(inputStream);
    } catch (SAXException e) {
      
    }    
    return results;
  }
}
