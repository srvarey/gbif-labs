package models.occurrence.harvest.digir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;

import models.occurrence.harvest.dataaccess.DBToDatasetHandler;
import models.occurrence.harvest.dataaccess.OccurrenceToDBHandler;
import models.*;
import models.occurrence.harvest.util.TemplateUtils;


/**
 * Performs a harvest
 * @author tim
 */
@SuppressWarnings("deprecation")
public class Harvester extends models.occurrence.harvest.Harvester  
{
  private static final play.Logger LOG = new play.Logger();
  
  // TODO: This does not need to be a multithreaded manager
  protected static ThreadSafeClientConnManager connectionManager;
  protected static HttpParams params = new BasicHttpParams();
  static {
    HttpConnectionParams.setConnectionTimeout(params, 600000);
    HttpConnectionParams.setSoTimeout(params, 600000);    
    ConnManagerParams.setMaxTotalConnections(params, 10);
    SchemeRegistry schemeRegistry = new SchemeRegistry();
    schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
    connectionManager = new ThreadSafeClientConnManager(params, schemeRegistry);
  }
  
  
  //private Connection conn;
  private Dataset dataset;
  private String targetDirectory;
  private Map<String, String> templateParams = new HashMap<String, String>();
  private String templateLocation = "models/occurrence/harvest/resources/template/digir/search.vm";
  private HttpClient httpClient = new DefaultHttpClient(connectionManager, params);
  private ResponseToModelHandler modelFactory = new ResponseToModelHandler();
  private OccurrenceToDBHandler occurenceSync = new OccurrenceToDBHandler();
  private static int maxResults = 1000;
  
  /**
   * Creates the class 
   */
  public Harvester(Dataset dataset, String targetDirectory) 
  {
    this.dataset = dataset;
    this.targetDirectory = targetDirectory;
    templateParams.put("destination", this.dataset.url);
    templateParams.put("maxResults", Integer.toString(maxResults));
    templateParams.put("resource", dataset.name);
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
  public Harvester(List<Dataset> datasets, String targetDirectory)
  { 
	for (Dataset dataset : datasets)
    {
		if (dataset.type.equals("digir"))
    	{
			LOG.info("Digir Harvester is started");
    		Harvester app = new Harvester(dataset, targetDirectory);
    		app.run();
    	}
    }
  }

  /**
   * Initiates a crawl
   */
  public void run() {
    try {
      harvest();
    } catch (Exception e) {
      LOG.error("Harvesting failed terminally", e);
      e.printStackTrace();
    }
  }
  
  /**
   * Does the harvesting
   */
  private void harvest() {
    LOG.info("Starting harvesting");
    // get the things before A
    try {
      pageRange(null, "AAA", 0);
    } catch (Exception e) {
      LOG.error("Error in range [null-AAA]", e);
    }
  
    // loop on the name basis Aaa-Aaz
    for (char c1='A'; c1<='Z'; c1++) {
      for (char c2='a'; c2<='z'; c2++) {
        String lower = c1 +"" + c2 +"a";
        String upper =  c1 +"" + c2 +"z";
        try {
          pageRange(lower, upper, 0);
        } catch (Exception e) {
          LOG.error("Error in range ["+lower+"-"+upper+"]", e);
        }
      }
    }

    // get the things after z
    try {
      pageRange("zzz", null, 0);
    } catch (Exception e) {
      LOG.error("Error in range [zzz-AAA]", e);
    }
    LOG.info("Finished harvesting");
  }
  
  /**
   * Issues a call to get a page of results
   */
  private void pageRange(String lower, String upper, int startAt) throws Exception {
	templateParams.put("lower", lower);
	templateParams.put("upper", upper);  
	templateParams.put("startAt", Integer.toString(startAt)); 
    LOG.info("Starting lower[" + lower + "] upper[" + upper + "] start[" + startAt + "]");
    String query = TemplateUtils.getAndMerge(templateLocation, templateParams);
    String request = buildURL(this.dataset.url, "request", query);
    
    String requestFile = targetDirectory + lower + "-" + upper + "-" + startAt +"_.txt.gz";
    String responseFile = targetDirectory + lower + "-" + upper + "-" + startAt +".txt.gz";
    
    System.out.println(requestFile);
    
    // store the request
    GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(requestFile));
    IOUtils.write(query, gos);
    gos.close();

    // issue request and store response
    HttpGet httpget = new HttpGet(request);
    LOG.info("Initiating Request[" + requestFile + "] for Range[" + lower + "-" + upper + "] starting at[" + startAt + "]");
    httpClient.execute(httpget, new ResponseToFileHandler(responseFile));
    LOG.info("Range[" + lower + "-" + upper + "] starting at[" + startAt + "] returned response[" + responseFile + "]");
    
    // now process the response to build the records
    GZIPInputStream contentStream = null;
    List<Occurrence> results = new ArrayList<Occurrence>();
    try {
      contentStream = new GZIPInputStream(new FileInputStream(responseFile));
      results = modelFactory.handleResponse(contentStream);
    } finally {
      IOUtils.closeQuietly(contentStream);
    }
    
    // now synchronise the results to the database
    LOG.info("Number of results: " + results.size());
    for (Occurrence o : results) {
    	o.dataset = this.dataset;
    }
    try {
      occurenceSync.synchronize(results);
    } catch (RuntimeException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } 
    //}
    
    LOG.info("Finished lower[" + lower + "] upper[" + upper + "] start[" + startAt + "]");
    
    if (results.size() >= maxResults)
    {
    	results = null; // make eligible for garbage collection
    	pageRange(lower, upper, startAt + maxResults);
    }
  }  
}
