package models.occurrence.harvest.digir;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.digester.Digester;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.log4j.Logger;
import models.Occurrence;
import org.xml.sax.SAXException;

public class ResponseToModelHandler 
{
  protected int count;
  protected boolean endOfRecords;
	  
  public List<Occurrence> handleResponse(GZIPInputStream inputStream) throws IOException 
  {
    List<Occurrence> results = new ArrayList<Occurrence>();
	try
	{
	  // log the response
	  Digester digester = new Digester();
	  digester.setNamespaceAware(false);
	  digester.setValidating(false);
	  // Digester uses the class loader of its own class to find classes needed for object create rules. As Digester is bundled, the wrong class loader was used leading to this Exception.
	  digester.setUseContextClassLoader(true);
	  digester.push(results);
	  digester.addObjectCreate("*/record", "models.Occurrence");
	  digester.addBeanPropertySetter("*/record/darwin:ScientificName", "scientificName");
	  digester.addBeanPropertySetter("*/record/darwin:InstitutionCode", "institutionCode");
	  digester.addBeanPropertySetter("*/record/darwin:CollectionCode", "collectionCode");
	  digester.addBeanPropertySetter("*/record/darwin:CatalogNumber", "catalogNumber");
	  //digester.addCallMethod("response/diagnostics/diagnostic", "incrementCount", 2);
	  //digester.addCallParam("response/diagnostics/diagnostic", 0, "code");
	  //digester.addCallParam("response/diagnostics/diagnostic", 1);
	  //digester.addCallMethod("response/diagnostics/diagnostic", "endOfRecords", 2);
	  //digester.addCallParam("response/diagnostics/diagnostic", 0, "code");
	  //digester.addCallParam("response/diagnostics/diagnostic", 1);
	  digester.addSetNext("*/record", "add");
	  digester.parse(inputStream);
	}
	catch (SAXException e) 
	{
	  throw new IOException(e.getMessage());
	} 
	finally 
	{
	  inputStream.close();
	}
	//LOG.info(results.get(0).getInstitutionCode());		
	return results;
  }
	
  public void incrementCount(String code, String value) 
  {
	if ("RECORD_COUNT".equals(code)) 
	{
	  try 
	  {
		count+=Integer.parseInt(value);
	  }
  	  catch (NumberFormatException e) 
  	  {
  	  }
	}
  }
		
  public void endOfRecords(String code, String value) 
  {
    endOfRecords = false;
    if ("END_OF_RECORDS".equals(code)) 
    {
      if ("TRUE".equalsIgnoreCase(value)) 
      {
        endOfRecords=true;
      } 
    }
  }

  public int getCount() 
  {
	return count;
  }
  
  public void setCount(int count) 
  {
	this.count = count;
  }
  
  public boolean isEndOfRecords() 
  {
	return endOfRecords;
  }
  
  public void setEndOfRecords(boolean endOfRecords) 
  {
	this.endOfRecords = endOfRecords;
  }
}