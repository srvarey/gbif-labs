package models.occurrence.harvest.ipt;

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

import models.occurrence.harvest.dataaccess.DBToDatasetHandler;
import models.occurrence.harvest.dataaccess.OccurrenceToDBHandler;
import models.*;

public class Harvester extends models.occurrence.harvest.Harvester  
{
  private static final play.Logger LOG = new play.Logger();
	
  //private Connection conn;
  private Dataset dataset;
  private String targetDirectory;
  
  public List<Occurrence> occurrences = new ArrayList<Occurrence>();
  private OccurrenceToDBHandler databaseSync = new OccurrenceToDBHandler();
  
  final static int BUFFER = 2048;
	

  /**
  /**
   * Creates the class 
   */
  public Harvester(Dataset dataset, String targetDirectory) 
  {
	LOG.info("IPT Harvester is started. Dataset = " + dataset.name);
	this.dataset = dataset;
    this.targetDirectory = targetDirectory;
    
    File f = new File (targetDirectory + File.separator + "resource-" + dataset.id);
    if (!f.exists()) {
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
  /*public Harvester(List<Dataset> datasets, String targetDirectory)
  { 
	for (Dataset dataset : datasets)
    {
		if (dataset.type.equals("ipt"))
    	{
			LOG.info("IPT Harvester is started");;
    		Harvester app = new Harvester(dataset, targetDirectory);
    		app.run();
    	}
    }
  }*/
  

/**
   * Initiates a crawl
   */
  public void run() 
  {
    try 
    {
      harvest(); 
    } 
    catch (Exception e) 
    {
      LOG.error("Harvesting failed terminally", e);
      e.printStackTrace();
    }
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
	File fileName = downloadFile (this.dataset.url, targetDirectoryFile);  
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
      //Connection initialization
	  URL url = new URL(address);
	  URLConnection conn = url.openConnection();
	  LOG.info("Connection to the URL " + url);
	  
	  String FileType = conn.getContentType();
	  LOG.info("Type File : " + FileType);

	  // Response Reader
	  in = conn.getInputStream();
	  reader = new BufferedReader(new InputStreamReader(in));
	  
	  fileName = url.getFile();
	  fileName = dest + File.separator + fileName.substring(fileName.lastIndexOf('/') + 1);
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

  public int createOccurences(File fileDirectory) throws IOException, UnsupportedArchiveException, SQLException 
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
	  occurrence.dataset = this.dataset;
	  occurrence.occurrenceId = rec.id();
	  occurrence.institutionCode = rec.value(DwcTerm.institutionCode);
	  occurrence.collectionId = rec.value(DwcTerm.collectionID);
	  occurrence.collectionCode = rec.value(DwcTerm.collectionCode);
	  occurrence.catalogNumber = rec.value(DwcTerm.catalogNumber);
	  occurrence.sex = rec.value(DwcTerm.sex);
	  occurrence.kingdom = rec.value(DwcTerm.kingdom);
	  occurrence.phylum = rec.value(DwcTerm.phylum);
	  occurrence.klass = rec.value(DwcTerm.classs);
	  occurrence.taxOrder = rec.value(DwcTerm.order);
	  occurrence.family = rec.value(DwcTerm.family);
	  occurrence.genus = rec.value(DwcTerm.genus);
	  occurrence.subgenus = rec.value(DwcTerm.subgenus);
	  occurrence.specificEpithet = rec.value(DwcTerm.specificEpithet);
	  occurrence.infraSpecificEpithet = rec.value(DwcTerm.infraspecificEpithet);
	  occurrence.scientificName = rec.value(DwcTerm.scientificName);
	  occurrence.scientificNameAuthorship = rec.value(DwcTerm.scientificNameAuthorship);
	  occurrence.taxonRank = rec.value(DwcTerm.taxonRank);
	  occurrence.dateIdentified = rec.value(DwcTerm.dateIdentified);
	  occurrence.identifiedBy = rec.value(DwcTerm.identifiedBy);
	  occurrence.typeStatus = rec.value(DwcTerm.typeStatus);
	  occurrence.continent = rec.value(DwcTerm.continent);
	  occurrence.waterBody = rec.value(DwcTerm.waterBody);
	  occurrence.country = rec.value(DwcTerm.country);
	  occurrence.stateProvince = rec.value(DwcTerm.stateProvince);
	  occurrence.locality = rec.value(DwcTerm.locality);
	  occurrence.decimalLatitude = rec.value(DwcTerm.decimalLatitude);
	  occurrence.decimalLongitude = rec.value(DwcTerm.decimalLongitude);
	  occurrence.coordinatePrecision = rec.value(DwcTerm.coordinatePrecision);
	  occurrence.minimumElevationInMeters = rec.value(DwcTerm.minimumElevationInMeters);
	  occurrence.maximumElevationInMeters = rec.value(DwcTerm.maximumElevationInMeters);
	  occurrence.minimumDepthInMeters = rec.value(DwcTerm.minimumDepthInMeters);
	  occurrence.maximumDepthInMeters = rec.value(DwcTerm.maximumDepthInMeters);
	  occurrences.add(occurrence);
	}
	
	// now synchronise the results to the database
    LOG.info("Number of results: " + occurrences.size());
    try 
    {
      databaseSync.synchronize(occurrences);
      return 1;
    }
    catch (RuntimeException e) 
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
	return 0; 
  }
}
