package org.gbif.simpleharvest.ipt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.log4j.Logger;

public class Harvester 
{
  private static final Logger LOG = Logger.getLogger(Harvester.class);	
	
  private Connection conn;
  private int datasetId;
  private String url;
  private String databaseUrl;
  private String username;
  private String password;
  private String targetDirectory;
	
  public Harvester(int datasetId, String url, String databaseUrl, String username, String password, String targetDirectory) 
  {  
	this.datasetId = datasetId;
	this.url = url;
	this.databaseUrl = databaseUrl;
	this.username = username;
	this.password = password;
	this.targetDirectory = targetDirectory;
	   
	File f = new File (targetDirectory + File.separator);
	if (!f.exists()) 
	{
	  f.mkdirs();
	}
	this.targetDirectory = f.getAbsolutePath() + File.separator;
	    
  }
  
  /**
   * The entry to the crawler
   * @param args Must contain:
   *  datasetID For the resource
   *  URL to crawl 
   *  database connection string
   *  database username
   *  database password
   */
  public static void main(String[] args) 
  {
    if (args.length!=6) 
    {
      LOG.error("Harvester takes 6 arguments");
      return;
    }
 
    int datasetId = Integer.parseInt(args[0]);
    String url = args[1];
    String databaseUrl = args[2];
    String username = args[3];
    String password = args[4];
    String targetDirectory = args[5];
    
    Harvester app = new Harvester(datasetId, url, databaseUrl, username, password, targetDirectory);
    app.run();
  }
  
  /**
   * Initiates a crawl
   */
  private void run() 
  {
    try 
    {
      init();
      harvest();
      
    } 
    catch (Exception e) 
    {
      LOG.error("Harvesting failed terminally", e);
      e.printStackTrace();
    }
    finally 
    {
      close();
    }
  }
  
  /**
   * Opens the connection to the database
   * @throws SQLException 
   */
  private void init() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException 
  {
    LOG.debug("Creating database connection");
    Class.forName("com.mysql.jdbc.Driver").newInstance();
    conn = DriverManager.getConnection(databaseUrl, username, password);    
    LOG.debug("Database connection created successfully");
  }
  
  /**
   * Does the harvesting
 * @throws IOException 
 * @throws MalformedURLException 
   */
  private void harvest() throws MalformedURLException, IOException 
  {
    //retrieve the archive  
	File targetDirectoryFile = new File(targetDirectory);
	downloadFile (url, targetDirectoryFile);  
	//open it
	//parse the content
	//save it in a database
  }
  
  /*
   * Download the file
   */
  public static void downloadFile(String adresse) 
  {

		downloadFile(adresse, null);
  }

  public static void downloadFile(String adresse, File dest) 
  {
	BufferedReader reader = null;
	FileOutputStream fos = null;
	InputStream in = null;
	try 
	{
      //Connection initialisation
	  URL url = new URL(adresse);
	  URLConnection conn = url.openConnection();
	  LOG.info("Connection to the URL " + adresse);
	  
	  String FileType = conn.getContentType();
	  LOG.info("Type File : " + FileType);

	  int FileLenght = conn.getContentLength();
	  if (FileLenght == -1) 
	  {
	    throw new IOException("Invalid File");
	  }
	  // Response Reader
	  in = conn.getInputStream();
	  reader = new BufferedReader(new InputStreamReader(in));
	  
	  String FileName = url.getFile();
	  FileName = dest + File.separator + FileName.substring(FileName.lastIndexOf('/') + 1);
	  LOG.info ("File downloaded at " + FileName);
	  dest = new File(FileName);
	  
	  fos = new FileOutputStream(dest);
	  byte[] buff = new byte[1024];
	  int l = in.read(buff);
	  while (l > 0) 
	  {
		fos.write(buff, 0, l);
		l = in.read(buff);
	  }
	}
	catch (Exception e) 
	{
	  e.printStackTrace();
	}
	finally
	{
	  try 
	  {
		fos.flush();
		fos.close();
	  }
	  catch (IOException e) 
	  {
		e.printStackTrace();
	  }
	  try
	  {
	    reader.close();
	  }
	  catch (Exception e) 
	  {
		e.printStackTrace();
	  }
	}
  }

  /**
   * Closes the database connection
   */
  private void close() 
  {
    if (conn!=null) 
    {
      try 
      {
        LOG.debug("Closing database connection");
        conn.close();
        LOG.debug("Database connection closed successfully");
      } 
      catch (SQLException e) 
      {
        // swallow exceptions on cleanup
      }
    }
  }
}
