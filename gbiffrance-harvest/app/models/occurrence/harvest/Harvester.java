package models.occurrence.harvest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import models.Dataset;

public class Harvester 
{	
	/**
      * Builds a URL from the base URL and the appending an encoded content as a parameter named
      * by the key.  E.g.
      * http://blahblah?request=<encodedXML>
      * @param url the base url
      * @param parameterKey The key to use as the parameter (e.g. to have ?request= would be "request")
      * @param content The content to encode in the request
      * @return The encoded url
      */
    public String buildURL(String url, String parameterKey, String content) throws UnsupportedEncodingException {
    	if (content != null && content.length()>0) {
    		if (url.contains("?")) {
    			url = url + "&" + parameterKey + "=" + URLEncoder.encode(content, "UTF-8");
    		} else {
    			url = url + "?" + parameterKey + "=" + URLEncoder.encode(content, "UTF-8");
    		}
    	}
    	return url;
    }   

	public void run() {}
}
