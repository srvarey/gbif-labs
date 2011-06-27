package org.gbif.simpleharvest.biocase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.gbif.simpleharvest.model.Occurrence;

import com.mysql.jdbc.PreparedStatement;

/**
 * Utility to synchronise a single occurrence record with the database
 * @author tim
 */
public class OccurrenceToDBHandler {
  private static final Logger LOG = Logger.getLogger(OccurrenceToDBHandler.class);
  
  public void synchronize(Connection conn, Occurrence o) throws SQLException {
    Integer id = getId(conn, o);
    if (id == null) {
      insert(conn, o);
    } else {
      o.setId(id);
      update(conn, o);
    }
  }
  
  private void insert(Connection conn, Occurrence o) throws SQLException {
    String query = "INSERT INTO occurrence(dataset_id, institution_code, collection_code, catalogue_number,scientific_name, locality) " +
      "values(?,?,?,?,?,?)";
    PreparedStatement st = (PreparedStatement) conn.prepareStatement(query);
    st.setInt(1, o.getDatasetId());
    st.setString(2, o.getInstitutionCode());
    st.setString(3, o.getCollectionCode());
    st.setString(4, o.getCatalogueNumber());
    st.setString(5, o.getScientificName());
    st.setString(6, o.getLocality());
    st.executeUpdate();
  }
  
  private void update(Connection conn, Occurrence o) throws SQLException {
    String query = "Update occurrence " +
    		"SET dataset_id=?, institution_code=?, collection_code=?, catalogue_number=?, scientific_name=?, locality=? " +
    		"WHERE id=?";
    PreparedStatement st = (PreparedStatement) conn.prepareStatement(query);
    st.setInt(1, o.getDatasetId());
    st.setString(2, o.getInstitutionCode());
    st.setString(3, o.getCollectionCode());
    st.setString(4, o.getCatalogueNumber());
    st.setString(5, o.getScientificName());
    st.setString(6, o.getLocality());
    st.setInt(7, o.getId());
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