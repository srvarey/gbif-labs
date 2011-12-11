package models.occurrence.taxonomy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import models.Occurrence;

import org.apache.log4j.Logger;
import org.gbif.ecat.model.ParsedName;
import org.gbif.ecat.parser.NameParser;
import models.occurrence.harvest.biocase.Harvester;


import com.mysql.jdbc.PreparedStatement;

public class Taxonomy 
{
	private static final Logger LOG = Logger.getLogger(Taxonomy.class);
	public Taxonomy(List<Occurrence> occurrences) throws SQLException
	{
		Connection conn;
		//Connection to catalog of life
		conn = DriverManager.getConnection("jdbc:mysql://localhost/simple_harvest?useUnicode=yes&characterEncoding=UTF-8", "root", "michael");
		updateClassification(occurrences, conn);
	}
	
	private static void updateClassification(List<Occurrence> occurrences, Connection conn) throws SQLException 
	{	  
	  NameParser parser = new NameParser();
	  parser.debug = false;
	  String scientificName = null;
	  String query2 = null;
	  for (Occurrence o : occurrences)
	  {
	    /* Si la classification a déja été renseignée, passer au suivant */
		if (o.family != null) continue;
		scientificName = o.scientificName;
		LOG.debug(scientificName);
	    ParsedName<String> parsedName = parser.parse(scientificName);
	    /* le nom scientifique n'a pas pu être parsé, mettre à jour le status en 'invalid' et passer au suivant*/
	    if (parsedName == null) 
	    {
	    	o.status = "invalid";
	    	o.save();
	    	continue;
	    }
	    String genus = parsedName.genusOrAbove;
	    LOG.debug(genus);
	    String specificEpithet = parsedName.specificEpithet;
	    LOG.debug(specificEpithet);
	    String infraSpecificEpithet = /*(parsedName.infraSpecificEpithet == null)? "" :*/ parsedName.infraSpecificEpithet;
	    LOG.debug(infraSpecificEpithet);
	    
	    /* Chercher la classification pour le nom scientifique donné */
    	if (specificEpithet != null)
    	{
    		query2 = "select * from col2011acv12.families f, col2011acv12.scientific_names s " +
	  		"where f.record_id = s.family_id " +
	  		"and s.genus like '"+genus+"' " +
	  		"and s.species like '"+specificEpithet+"' " +
	  		"and s.is_accepted_name = '1'" +
	  		"group by f.record_id;";
    	}
    	else 
    	{
    		query2 = "select * from col2011acv12.families f, col2011acv12.scientific_names s " +
	  		"where f.record_id = s.family_id " +
	  		"and s.genus like '"+genus+"' " +
	  		"and s.is_accepted_name = '1'" +
	  		"group by f.record_id;";
    	}
	   
	    PreparedStatement st2 = (PreparedStatement) conn.prepareStatement(query2);
	    LOG.debug(st2.toString());
	    ResultSet rs2 = st2.executeQuery();
		
	    rs2.last();
	    int rs2count = rs2.getRow();
	    /* si plusieurs classifications sont retournées, mettre à jour le status en 'ambiguous' */
	    if (rs2count > 1) 
	    {
	    	o.status = "ambiguous";
	    	o.save();
	    	continue;
	    }
	    rs2.beforeFirst(); 
		while (rs2.next()) 
		{
			o.kingdom = rs2.getString("kingdom");
			o.phylum = rs2.getString("phylum");
			o.klass = rs2.getString("class");
			o.taxOrder = rs2.getString("order");
			o.family = rs2.getString("family");
			o.genus = rs2.getString("genus");
			o.specificEpithet = specificEpithet;
			o.infraSpecificEpithet = infraSpecificEpithet;
			o.status = "valid";
			o.save();
		}
	  }
	}
}

