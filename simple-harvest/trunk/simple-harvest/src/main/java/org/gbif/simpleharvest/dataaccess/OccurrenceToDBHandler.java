package org.gbif.simpleharvest.dataaccess;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.gbif.simpleharvest.model.Occurrence;

import com.mysql.jdbc.PreparedStatement;

/**
 * Utility to synchronise a single occurrence record with the database
 * @author tim
 */
public class OccurrenceToDBHandler {
  private static final Logger LOG = Logger.getLogger(OccurrenceToDBHandler.class);
  
  public void synchronize(Connection conn, List<Occurrence> occurences) throws SQLException {
    for (Occurrence o : occurences) 	
    {	
	  Integer id = getId(conn, o);
	  if (id == null) 
	  {
	    insert(conn, o);
	  }
	  else
	  {
	    o.setId(id);
	    update(conn, o);
	  }
    }
  }
  
  private void insert(Connection conn, Occurrence o) throws SQLException {
    String query = "INSERT INTO occurrence(" +
    		"dataset_id, " +
    		"occurrence_id, " +
    		"institution_code, " +
    		"collection_id, " +
    		"collection_code, " +
    		"catalogue_number, " +
    		"sex, " +
    		"kingdom, " +
    		"phylum, " +
    		"class, " +
    		"order_tax, " +
    		"family, " +
    		"genus, " +
    		"subgenus, " +
    		"specific_epithet, " +
    		"infraspecific_epithet, " +
    		"scientific_name, " +
    		"scientific_name_authorship, " +
    		"taxon_rank, " +
    		"date_identified, " +
    		"identified_by, " +
    		"type_status, " +
    		"continent, " +
    		"water_body, " +
    		"country, " +
    		"state_province, " +
    		"locality, " +
    		"decimal_latitude, " +
    		"decimal_longitude, " +
    		"coordinate_precision, " +
    		"minimum_elevation_in_meters, " +
    		"maximum_elevation_in_meters, " +
    		"minimum_depth_in_meters, " +
    		"maximum_depth_in_meters" +
    		") " +
      "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    PreparedStatement st = (PreparedStatement) conn.prepareStatement(query);
    st.setInt(1, o.getDatasetId());
    st.setString(2, o.getOccurrenceId());
    st.setString(3, o.getInstitutionCode());
    st.setString(4, o.getCollectionId());
    st.setString(5, o.getCollectionCode());
    st.setString(6, o.getCatalogueNumber());
    st.setString(7, o.getSex());
    st.setString(8, o.getKingdom());
    st.setString(9, o.getPhylum());
    st.setString(10, o.getKlass());
    st.setString(11, o.getOrder());
    st.setString(12, o.getFamily());
    st.setString(13, o.getGenus());
    st.setString(14, o.getSubgenus());
    st.setString(15, o.getSpecificEpithet());
    st.setString(16, o.getInfraSpecificEpithet());
    st.setString(17, o.getScientificName());
    st.setString(18, o.getScientificNameAuthorship());
    st.setString(19, o.getTaxonRank());
    st.setString(20, o.getDateIdentified());
    st.setString(21, o.getIdentifiedBy());
    st.setString(22, o.getTypeStatus());
    st.setString(23, o.getContinent());
    st.setString(24, o.getWaterBody());
    st.setString(25, o.getCountry());
    st.setString(26, o.getStateProvince());
    st.setString(27, o.getLocality());
    st.setString(28, o.getDecimalLatitude());
    st.setString(29, o.getDecimalLongitude());
    st.setString(30, o.getCoordinatePrecision());
    st.setString(31, o.getMinimumElevationInMeters());
    st.setString(32, o.getMaximumElevationInMeters());
    st.setString(33, o.getMinimumDepthInMeters());
    st.setString(34, o.getMaximumDepthInMeters());
    st.executeUpdate();
  }
  
  private void update(Connection conn, Occurrence o) throws SQLException {
    String query = "Update occurrence " +
    		"SET " +
    		"dataset_id=?, " +
    		"occurrence_id=?, " +
    		"institution_code=?, " +
    		"collection_id=?, " +
    		"collection_code=?, " +
    		"catalogue_number=?, " +
    		"sex=?, " +
    		"kingdom=?, " +
    		"phylum=?, " +
    		"class=?, " +
    		"order_tax=?, " +
    		"family=?, " +
    		"genus=?, " +
    		"subgenus=?, " +
    		"specific_epithet=?, " +
    		"infraspecific_epithet=?, " +
    		"scientific_name=?, " +
    		"scientific_name_authorship=?, " +
    		"taxon_rank=?, " +
    		"date_identified=?, " +
    		"identified_by=?, " +
    		"type_status=?, " +
    		"continent=?, " +
    		"water_body=?, " +
    		"country=?, " +
    		"state_province=?, " +
    		"locality=?, " +
    		"decimal_latitude=?, " +
    		"decimal_longitude=?, " +
    		"coordinate_precision=?, " +
    		"minimum_elevation_in_meters=?, " +
    		"maximum_elevation_in_meters=?, " +
    		"minimum_depth_in_meters=?, " +
    		"maximum_depth_in_meters=? " +
    		"WHERE id=?";
    PreparedStatement st = (PreparedStatement) conn.prepareStatement(query);
    st.setInt(1, o.getDatasetId());
    st.setString(2, o.getOccurrenceId());
    st.setString(3, o.getInstitutionCode());
    st.setString(4, o.getCollectionId());
    st.setString(5, o.getCollectionCode());
    st.setString(6, o.getCatalogueNumber());
    st.setString(7, o.getSex());
    st.setString(8, o.getKingdom());
    st.setString(9, o.getPhylum());
    st.setString(10, o.getKlass());
    st.setString(11, o.getOrder());
    st.setString(12, o.getFamily());
    st.setString(13, o.getGenus());
    st.setString(14, o.getSubgenus());
    st.setString(15, o.getSpecificEpithet());
    st.setString(16, o.getInfraSpecificEpithet());
    st.setString(17, o.getScientificName());
    st.setString(18, o.getScientificNameAuthorship());
    st.setString(19, o.getTaxonRank());
    st.setString(20, o.getDateIdentified());
    st.setString(21, o.getIdentifiedBy());
    st.setString(22, o.getTypeStatus());
    st.setString(23, o.getContinent());
    st.setString(24, o.getWaterBody());
    st.setString(25, o.getCountry());
    st.setString(26, o.getStateProvince());
    st.setString(27, o.getLocality());
    st.setString(28, o.getDecimalLatitude());
    st.setString(29, o.getDecimalLongitude());
    st.setString(30, o.getCoordinatePrecision());
    st.setString(31, o.getMinimumElevationInMeters());
    st.setString(32, o.getMaximumElevationInMeters());
    st.setString(33, o.getMinimumDepthInMeters());
    st.setString(34, o.getMaximumDepthInMeters());
    st.executeUpdate();  
  }
  
  private Integer getId(Connection conn, Occurrence o) throws SQLException {
    String query = "SELECT id FROM occurrence " +
    		"WHERE dataset_id=? AND institution_code=? AND collection_code=? AND catalogue_number=?";
    PreparedStatement st = (PreparedStatement) conn.prepareStatement(query);
    st.setInt(1, o.getDatasetId());
    st.setString(2, o.getInstitutionCode());
    st.setString(3, o.getCollectionCode());
    st.setString(4, o.getCatalogueNumber());
    
    ResultSet rs = st.executeQuery();
    try {
      Integer id = null;
      rs.last();
      while (rs.next()) {
        id = rs.getInt("id");
      }
      return id;
    } finally {
      rs.close();
      st.close();
    }
  }
}