package org.gbif.simpleharvest.dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class DBToDatasetHandler 
{
  private static final Logger LOG = Logger.getLogger(OccurrenceToDBHandler.class);
  
  public void listDatasets(String databaseUrl, String username, String password, HashMap<Integer, ArrayList<String>> list, String type) throws SQLException
  {
	  Connection conn;
	  conn = DriverManager.getConnection(databaseUrl, username, password); 
	  
	  String query = "select dataset_id, name, url from dataset where type='"+type+"'";
	  Statement stmt = conn.createStatement();
	  ResultSet rs = stmt.executeQuery(query);
	  ArrayList<String> values = new ArrayList<String>();
	  while (rs.next())
	  {
		  values.add(rs.getString("name"));
		  values.add(rs.getString("url"));
		  list.put(rs.getInt("dataset_id"), values);
	  }
	  LOG.info("providers list imported");
  }
}
