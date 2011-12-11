import org.junit.*;

import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {
  
	/*@Before
	public void setup()
	{
		Fixtures.deleteDatabase();
	}*/
    @Test
    public void createAndRetrieveOccurrence()
    {
    	// Create a dataset and save it
    	Dataset dataset = new Dataset(
    	  "T_name",
    	  "T_url",
    	  "T_type");
    	dataset.save();
    	
    	
    	// Create an occurrence and save it
    	new Occurrence(
    	  "T_occurrenceId",
    	  "T_institutionCode",
    	  "T_collectionId",
    	  "T_collectionCode",
    	  "T_catalogueNumber",
    	  "T_sex",
    	  "T_kingdom",
    	  "T_phylum",
    	  "T_klass",
    	  "T_order",
    	  "T_family",
    	  "T_genus",
    	  "T_subgenus",
    	  "T_specificEpithet",
    	  "T_infraSpecificEpithet",
    	  "T_scientificName",
    	  "T_scientificNameAuthorship",
    	  "T_taxonRank",
    	  "T_dateIdentified",
    	  "T_identifiedBy",
    	  "T_typeStatus",
    	  "T_continent",
    	  "T_waterBody",
    	  "T_country",
    	  "T_stateProvince",
    	  "T_locality",
    	  "T_decimalLatitude",
    	  "T_decimalLongitude",
    	  "T_coordinatePrecision",
    	  "T_minimumElevationInMeters",
    	  "T_maximumElevationInMeters",
    	  "T_minimumDepthInMeters",
    	  "T_maximumDepthInMeters",
    	  "T_status",
    	  dataset).save();
    	
    	//Retrieve the occurrence with scientific name "T_scientificName"
    	Occurrence occurrence = Occurrence.find("byScientificName", "T_scientificName").first();
    	
    	//Test
    	assertNotNull(occurrence);
    	assertEquals("T_occurrenceId", occurrence.occurrenceId);
    	assertEquals("T_url", occurrence.dataset.url);
    }
    
    @Test
    public void createAndRetrieveDataset()
    {
    	// Create a dataset and save it
    	new Dataset(
    	  "T_name",
    	  "T_url",
    	  "T_type").save();
    	
    	//Retrieve the dataset with name "T_name"
    	Dataset dataset = Dataset.find("byName", "T_name").first();
    	
    	//Test
    	assertNotNull(dataset);
    	assertEquals("T_name", dataset.name);    	
    }
   
    
}
