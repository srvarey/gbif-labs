package org.gbif.simpleharvest.biocase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;
import org.gbif.simpleharvest.model.Occurrence;
import org.xml.sax.SAXException;

/**
 * Inspects the response and builds lists of model objects
 * @author tim
 */
public class ResponseToModelHandler {
  private static final Logger LOG = Logger.getLogger(ResponseToModelHandler.class);
  
  public List<Occurrence> handleResponse(GZIPInputStream inputStream) throws IOException {
    List<Occurrence> results = new ArrayList<Occurrence>();
    
    Digester digester = new Digester();
    // we need it to understand that /biocase:response is /response
    digester.setNamespaceAware(true);
    digester.setValidating(false);
    digester.push(results);
    
    // ever unit forces a new Occurrence record to be created
    digester.addObjectCreate("*/Units/Unit", "org.gbif.simpleharvest.model.Occurrence");
    
    // extract the properties we are interested in
    digester.addBeanPropertySetter("*/SourceInstitutionID", "institutionCode");
    digester.addBeanPropertySetter("*/SourceID", "collectionCode");                                                                                         
    digester.addBeanPropertySetter("*/UnitID", "catalogueNumber");
    digester.addBeanPropertySetter("*/FullScientificNameString", "scientificName");
    digester.addBeanPropertySetter("*/LocalityText", "locality");
    
   
    // add the created Occurrence record to the list
    digester.addSetNext("*/Units/Unit", "add");
    try {
      digester.parse(inputStream);
    } catch (SAXException e) {
      
    }    
    return results;
  }
}
