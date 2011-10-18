package org.gbif.geocode;

import org.gbif.geocode.api.model.Location;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

public class WsClient {

  private static final int NUM_RETRIES = 5;
  private static final int RETRY_PERIOD_MSEC = 2000;
  private static final String WEB_SERVICE_URL = "http://boma.gbif.org:8080/geocode-ws/reverse/";

  private static final WebResource RESOURCE;

  static {
    ClientConfig cc = new DefaultClientConfig();
    cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
    cc.getClasses().add(JacksonJsonProvider.class);

    Client client = ApacheHttpClient4.create(cc);

    //Client client = Client.create(cc);
    //client.setConnectTimeout(10000);

    RESOURCE = client.resource(WEB_SERVICE_URL);
  }

  public static void main(String[] args) {
    Random random = new Random();
    for (int i = 0; i < Integer.valueOf(args[0]); i++) {
      jerseyConnection(String.valueOf(random.nextInt(201)), "10");
      //manualConnection();
    }
  }

  private static void jerseyConnection(String lat, String lng) throws UniformInterfaceException {
    MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
    queryParams.add("lat", lat);
    queryParams.add("lng", lng);

    for (int i = 0; i < NUM_RETRIES; i++) {
      try {
        long beforeCall = System.nanoTime();
        RESOURCE.queryParams(queryParams).get(Location[].class);
        long afterCall = System.nanoTime();
        System.out.println("--> Successful WS call: " + TimeUnit.NANOSECONDS.toMillis(afterCall - beforeCall) + " ms");
        break; // from retry loop
      } catch (UniformInterfaceException e) {
        System.out.println("--> Got exception: " + e.toString());
        if (i >= NUM_RETRIES) {
          System.out.println("--> Retries exhausted, giving up");
          throw e;
        }

        try {
          System.out.println("--> Sleeping for retry");
          Thread.sleep(RETRY_PERIOD_MSEC);
        } catch (InterruptedException e1) {
        }
      }
    } // retry loop
  }

  private static void manualConnection() {
    HttpURLConnection connection = null;
    BufferedReader rd = null;
    StringBuilder sb = null;
    String line = null;

    URL serverAddress = null;

    try {
      serverAddress = new URL("http://boma.gbif.org:8080/geocode-ws/reverse?lat=0&lng=-90");
      //set up out communications stuff
      connection = null;

      //Set up the initial connection
      connection = (HttpURLConnection) serverAddress.openConnection();
      connection.setRequestMethod("GET");
      connection.setDoOutput(true);
      connection.setReadTimeout(10000);

      connection.connect();

      rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      sb = new StringBuilder();

      while ((line = rd.readLine()) != null) {
        sb.append(line + '\n');
      }

      //System.out.println(sb.toString());

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      connection.disconnect();
      rd = null;
      sb = null;
      connection = null;
    }
  }

}
