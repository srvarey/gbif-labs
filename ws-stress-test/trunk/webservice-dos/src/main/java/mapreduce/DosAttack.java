/*
 * Copyright 2011 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mapreduce;

import java.io.IOException;
import java.util.Random;

import javax.ws.rs.core.MultivaluedMap;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.NullOutputFormat;
import org.apache.hadoop.util.Tool;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A configurable MapReduce job that will issue a DOS attack on a specified web service.
 */
public class DosAttack extends Configured implements Tool {
  /**
   * The Mapper that does the issuing of the request
   */
  public static class DosMapper extends MapReduceBase implements Mapper<NullWritable, NullWritable, NullWritable, NullWritable> {
    private static final Logger LOG = LoggerFactory.getLogger(DosAttack.DosMapper.class);

    private static final int NUM_RETRIES = 5;
    private static final int RETRY_PERIOD_MSEC = 2000;
    private static WebResource RESOURCE;
    private int numCalls = 0;
    private static final Random r = new Random();

    public static void init(String url) {
      ClientConfig cc = new DefaultClientConfig();
      cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
      cc.getClasses().add(JacksonJsonProvider.class);
      Client client = ApacheHttpClient.create(cc);
      //client.setReadTimeout(1);
      RESOURCE = client.resource(url);
    }

    @Override
    public void configure(JobConf job) {
      super.configure(job);
      DosMapper.init(job.get(DosAttack.TARGET_URL_KEY));
      numCalls = 0;
    }

    public void map(NullWritable key, NullWritable value, OutputCollector<NullWritable, NullWritable> output,
      Reporter reporter) throws IOException {
      // add a fake param
      MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
      float lat = random();
      float lng = random();
      queryParams.add("lat", String.valueOf(lat));
      queryParams.add("lng", String.valueOf(lng));
      LOG.info("Initiating mapper to issue DOS attack on {}", RESOURCE.queryParams(queryParams).getURI());
      reporter.incrCounter("DIAGNOSTIC", "Entered", 1);
      for (int i = 0; i < NUM_RETRIES; i++) {
        try {
          reporter.setStatus("Issuing request " + numCalls++ + " lat[" +lat+ "] lng[" +lng+ "]");
          Location[] lookups = RESOURCE.queryParams(queryParams).get(Location[].class);
          //Identifier id = RESOURCE.get(Identifier.class);
          //LOG.debug("ID returned {}", id.getId());
          reporter.setStatus("Response received " + numCalls);
          reporter.incrCounter("DIAGNOSTIC", "Success", 1);
          reporter.incrCounter("DIAGNOSTIC", "COUNTRY_FOUND_" + lookups.length, 1);
          break; // from retry loop

        } catch (UniformInterfaceException e) {
          reporter.incrCounter("DIAGNOSTIC", "Exception", 1);

          // have we exhausted our attempts?
          if (i >= NUM_RETRIES) {
            reporter.incrCounter("DIAGNOSTIC", "Retry Exhausted", 1);
            throw e;
          }

          try {
            Thread.sleep(RETRY_PERIOD_MSEC);
          } catch (InterruptedException e1) {
          }
        }
        reporter.incrCounter("DIAGNOSTIC", "Retry-" + i, 1);
      } // retry loop
    }

    private float random() {
      return Float.parseFloat("" + (r.nextInt(90) + "." + (r.nextInt(10000))));
    }
  }

  public static final String NUM_MAPPERS_KEY = "job.num.mappers";
  public static final String NUM_REQUESTS_KEY = "job.num.requests";
  public static final String TARGET_URL_KEY = "job.target.url";


  private static final Logger LOG = LoggerFactory.getLogger(DosAttack.class);

  @Parameter(names = "-url", required = true)
  private String wsURL;

  @Parameter(names = "-mappers", required = true)
  private Integer numMappers;

  @Parameter(names = "-requests", required = true)
  private Integer numRequests;

  public static void main(String[] args) {
    try {
      new DosAttack().run(args);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  private void issue() throws IOException {
    LOG.info("Starting DOS on url[{}] with clients[{}]", wsURL, numMappers);
    DosMapper.init(wsURL);
    JobConf job = new JobConf(DosAttack.class);
    job.setJarByClass(DosAttack.class);
    job.setJobName("DOS Attack");
    job.setNumReduceTasks(0);
    job.setInputFormat(NullInputFormat.class);
    job.setOutputFormat(NullOutputFormat.class);
    job.setMapperClass(DosMapper.class);
    job.setMapOutputKeyClass(NullWritable.class);
    job.setMapOutputValueClass(NullWritable.class);
    job.setOutputKeyClass(NullWritable.class);
    job.setOutputValueClass(NullWritable.class);
    job.setNumMapTasks(numMappers);
    job.setInt(NUM_MAPPERS_KEY, numMappers);
    job.setInt(NUM_REQUESTS_KEY, numRequests);
    job.set(TARGET_URL_KEY, wsURL);
    JobClient.runJob(job);
  }

  public int run(String[] args) throws Exception {
    DosAttack dos = new DosAttack();
    new JCommander(dos, args);
    try {
      dos.issue();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return 0;
  }
}