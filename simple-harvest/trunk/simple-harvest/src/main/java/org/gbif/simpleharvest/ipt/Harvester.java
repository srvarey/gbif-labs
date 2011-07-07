package org.gbif.simpleharvest.ipt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.gbif.dwc.record.StarRecord;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.Archive;
import org.gbif.dwc.text.ArchiveFactory;
import org.gbif.dwc.text.UnsupportedArchiveException;
import org.gbif.simpleharvest.dataaccess.DBToDatasetHandler;
import org.gbif.simpleharvest.dataaccess.OccurrenceToDBHandler;
import org.gbif.simpleharvest.model.Occurrence;

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
  
  public List<Occurrence> occurrences = new ArrayList<Occurrence>();
  private OccurrenceToDBHandler databaseSync = new OccurrenceToDBHandler();
  private static DBToDatasetHandler datasetSync = new DBToDatasetHandler();
  
  final static int BUFFER = 2048;
	
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
 * @throws SQLException 
   */
  public static void main(String[] args) throws SQLException 
  {
    if (args.length!=4) 
    {
      LOG.error("Harvester takes 4 arguments");
      return;
    }
 
    String databaseUrl = args[0];
    String username = args[1];
    String password = args[2];
    String targetDirectory = args[3];
    
    Map<Integer, String> datasetsList = new HashMap<Integer, String>();
    
    //The dataset table is in the same database as the occurence table
    datasetSync.listDatasets(databaseUrl, username, password, datasetsList, "ipt");
    int datasetId;
    String url = null;
    
    for (Map.Entry<Integer, String> entry : datasetsList.entrySet())
    {
      datasetId = entry.getKey();
      url = entry.getValue();
      Harvester app = new Harvester(datasetId, url, databaseUrl, username, password, targetDirectory);
      app.run();
    }
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
 * @throws UnsupportedArchiveException 
 * @throws SQLException 
   */
  private void harvest() throws MalformedURLException, IOException, UnsupportedArchiveException, SQLException 
  {
    //retrieves the archive and downloads it
	File targetDirectoryFile = new File(targetDirectory);
	File fileName = downloadFile (url, targetDirectoryFile);  
	//extracts the archive
	File fileDirectory = extractFile(fileName, targetDirectory);
	//parses the data
	createOccurences(fileDirectory);
	//saves it in a database
  }
  
  /**
   * Downloads the archive
 * @throws IOException 
   */
  
  public static void downloadFile(String address) 
  {
		downloadFile(address, null);
  }

  public static File downloadFile(String address, File dest) 
  {
	BufferedReader reader = null;
	FileOutputStream fos = null;
	InputStream in = null;
	String fileName = null;
	try 
	{
      //Connection initialisation
	  URL url = new URL(address);
	  URLConnection conn = url.openConnection();
	  LOG.info("Connection to the URL " + url);
	  
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
	  
	  fileName = url.getFile();
	  fileName = dest + File.separator + fileName.substring(fileName.lastIndexOf('/') + 1);
	  LOG.info ("File downloaded at " + fileName);
	  dest = new File(fileName);
	  
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
	return dest;
  }

  /**
   * Extracts the content of the archive in the specified directory
 * @throws IOException 
   */
  
  public static File extractFile(File file, String targetDirectory)
  {
	File fileDirectory = null;
    try
	{
	  BufferedOutputStream dest = null;
      FileInputStream fis = new FileInputStream(file);
      ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
      ZipEntry entry;
	  while ((entry = zis.getNextEntry()) != null)
	  {
		LOG.info("Extracting directory: " + entry.getName());  
		int count;
		byte data[] = new byte [BUFFER];
		// write the files to the disk
		String directory = file.getName().substring(0, file.getName().length() - 4);
		fileDirectory = new File (targetDirectory + File.separator + directory);
		if (!fileDirectory.exists()) 
		{
			fileDirectory.mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(targetDirectory + File.separator + directory + File.separator + entry.getName());
		dest = new BufferedOutputStream(fos, BUFFER);
		while ((count = zis.read(data, 0, BUFFER)) != -1)
		{
			dest.write(data, 0, count);
		}
		dest.flush();
		dest.close();
	  }
	  zis.close();
	}
    catch (Exception e)
    {
    	e.printStackTrace();
    }
    return fileDirectory;
  }

  public void createOccurences(File fileDirectory) throws IOException, UnsupportedArchiveException, SQLException 
  {
	// opens csv files with headers or dwc-a directories with a meta.xml descriptor
	Archive arch = ArchiveFactory.openArchive(fileDirectory);
	 
	// does scientific name exist?
	if (!arch.getCore().hasTerm(DwcTerm.scientificName))
	{
	  System.out.println("This application requires dwc-a with scientific names");
	  System.exit(1);
	}
	 
	// loop over star records. i.e. core with all linked extension records
	for (StarRecord rec : arch)
	{
	  Occurrence occurrence = new Occurrence();
	  //occurrence.setId(Integer.parseInt(rec.id()));
	  occurrence.setDatasetId(this.datasetId);
	  occurrence.setOccurrenceId(rec.id());
	  occurrence.setInstitutionCode(rec.value(DwcTerm.institutionCode));
	  occurrence.setCollectionId(rec.value(DwcTerm.collectionID));
	  occurrence.setCollectionCode(rec.value(DwcTerm.collectionCode));
	  occurrence.setCatalogueNumber(rec.value(DwcTerm.catalogNumber));
	  occurrence.setSex(rec.value(DwcTerm.sex));
	  occurrence.setKingdom(rec.value(DwcTerm.kingdom));
	  occurrence.setPhylum(rec.value(DwcTerm.phylum));
	  occurrence.setKlass(rec.value(DwcTerm.classs));
	  occurrence.setOrder(rec.value(DwcTerm.order));
	  occurrence.setFamily(rec.value(DwcTerm.family));
	  occurrence.setGenus(rec.value(DwcTerm.genus));
	  occurrence.setSubgenus(rec.value(DwcTerm.subgenus));
	  occurrence.setSpecificEpithet(rec.value(DwcTerm.specificEpithet));
	  occurrence.setInfraSpecificEpithet(rec.value(DwcTerm.infraspecificEpithet));
	  occurrence.setScientificName(rec.value(DwcTerm.scientificName));
	  occurrence.setScientificNameAuthorship(rec.value(DwcTerm.scientificNameAuthorship));
	  occurrence.setTaxonRank(rec.value(DwcTerm.taxonRank));
	  occurrence.setDateIdentified(rec.value(DwcTerm.dateIdentified));
	  occurrence.setIdentifiedBy(rec.value(DwcTerm.identifiedBy));
	  occurrence.setTypeStatus(rec.value(DwcTerm.typeStatus));
	  occurrence.setContinent(rec.value(DwcTerm.continent));
	  occurrence.setWaterBody(rec.value(DwcTerm.waterBody));
	  occurrence.setCountry(rec.value(DwcTerm.country));
	  occurrence.setStateProvince(rec.value(DwcTerm.stateProvince));
	  occurrence.setLocality(rec.value(DwcTerm.locality));
	  occurrence.setDecimalLatitude(rec.value(DwcTerm.decimalLatitude));
	  occurrence.setDecimalLongitude(rec.value(DwcTerm.decimalLongitude));
	  occurrence.setCoordinatePrecision(rec.value(DwcTerm.coordinatePrecision));
	  occurrence.setMinimumElevationInMeters(rec.value(DwcTerm.minimumElevationInMeters));
	  occurrence.setMaximumElevationInMeters(rec.value(DwcTerm.maximumElevationInMeters));
	  occurrence.setMinimumDepthInMeters(rec.value(DwcTerm.minimumDepthInMeters));
	  occurrence.setMaximumDepthInMeters(rec.value(DwcTerm.maximumDepthInMeters));
	  occurrences.add(occurrence);
	}
	
	// now synchronise the results to the database
    LOG.info("Number of results: " + occurrences.size());
    try 
    {
      databaseSync.synchronize(conn, occurrences);
    }
    catch (RuntimeException e) 
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
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
