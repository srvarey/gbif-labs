package org.gbif.ocurrence.index.solr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.impl.StreamingUpdateSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class SolrIndexerJob {
	
	protected static final Logger log = Logger.getLogger(SolrIndexerJob.class);
	
	private static String url = "http://b9g1.gbif.org:8080/solr";
	
	private static final String[] recordFileds = new String[]{"id", "data_provider_id", "data_resource_id", "resource_access_point_id", "institution_code", "collection_code", "catalogue_number", 
															  "scientific_name", "author", "rank", "kingdom", "phylum", "class", "order_rank", 
															  "family", "genus", "species", "subspecies", "latitude", "longitude", "lat_long_precision", "max_altitude", "min_altitude", 
															  "altitude_precision", "min_depth", "max_depth", "depth_precision", "continent_ocean", "country", "state_province", "county", "collector_name", 
															  "locality", "year", "month", "day", "basis_of_record", "identifier_name", "identification_date", "unit_qualifier", "created", "modified", "deleted"};

	private int documentsCount;
	
	private int documentsSavePoint = 100;
	
	private SolrServer server;
	
	
	
	public SolrServer getServerInstance() {		
		if(server == null){ 
			try {
				server = new StreamingUpdateSolrServer(url,10000,3);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
		return server;
	}
	
	
	public void insert(final String line) throws Exception{		
		StringTokenizer lineStringTokenizer = new StringTokenizer(line, "\001");
		int fieldId = 0;
		SolrInputDocument rawOccurrenceRecordDocument = new SolrInputDocument();
		while(lineStringTokenizer.hasMoreTokens()){
			String token = lineStringTokenizer.nextToken();		
			if(token != null && token.trim().toLowerCase().equals("null")) token = null;			
			rawOccurrenceRecordDocument.setField(recordFileds[fieldId], token);
			fieldId+=1;
		}
		this.documentsCount += 1;		
		getServerInstance().add(rawOccurrenceRecordDocument);		
	}
	
	public void storeFile(File aFile) {	   	    
	    try {
	      this.documentsCount = 0;
	      BufferedReader input =  new BufferedReader(new FileReader(aFile));
	      try {
	        String line = null;
	        input.readLine();//header
	        while (( line = input.readLine()) != null){
	         insert(line);	    
	        }
	      }catch (Exception ex){
	    	 log.error(ex);
	      }
	      finally {
	        input.close();
	      }
	    }
	    catch (IOException ex){
	      log.error(ex);
	    }
	  } 
	
	public static void main(String[] args) throws Exception{
		SolrIndexerJob solrIndexerJob = new SolrIndexerJob();
		solrIndexerJob.storeFile(new File("/Users/fede/dev/workspace/lucene-index/src/main/java/gbif/ocurrence/solr/index/rawOcurrence10kSample.txt"));
		solrIndexerJob.getServerInstance().commit();
	}
}
