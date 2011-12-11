package models.occurrence.harvest.dataaccess;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;

import models.Occurrence;

import org.apache.log4j.Logger;

import play.db.jpa.JPA;

import com.mysql.jdbc.PreparedStatement;

/**
 * Utility to synchronise a single occurrence record with the database
 * @author tim
 */
public class OccurrenceToDBHandler {
  public void synchronize(List<Occurrence> occurrences) throws SQLException {
    for (Occurrence o : occurrences) 	
    {	
	  Long id = o.id;
	  if (id == null) 
	  {
	    insert(o);
	  }
	  else
	  {
	    o.id = id;
	    update(o);
	  }
    }
    
  }
  
  private void insert(Occurrence o) throws SQLException {
	o.save();
  }
  
  private void update(Occurrence o) throws SQLException {
	o.save();
  }
  
  /*private Long getId(Occurrence o) throws SQLException {
	return o.id;  
  }*/
}