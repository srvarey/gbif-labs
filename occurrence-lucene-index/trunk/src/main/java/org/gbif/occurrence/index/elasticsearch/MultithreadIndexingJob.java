/**
 * 
 */
package org.gbif.occurrence.index.elasticsearch;

import org.gbif.ocurrence.index.lucene.FileSplitter;
import org.gbif.ocurrence.index.lucene.LocalndexingJob;
import org.gbif.ocurrence.index.lucene.OccurrenceLuceneFields;
import org.gbif.ocurrence.index.lucene.ThreadPoolRunner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.StopWatch;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.action.bulk.BulkRequestBuilder;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;

/**
 * @author fede
 */
public class MultithreadIndexingJob extends ThreadPoolRunner<Integer> {

  public static class Job implements Callable<Integer> {

    private String inputFile;
    private int logBatchSize;
    private final String indexName;

    private Client client;

    public Job(String indexName, String inputFile, int logBatchSize, Client client) {
      this.inputFile = inputFile;
      this.logBatchSize = logBatchSize;
      this.client = client;
      this.indexName = indexName;
      /*
       * for (TransportAddress transportAddress : transportAddresses) {
       * this.client.addTransportAddress(transportAddress);
       * }
       */
    }

    public int addDocuments() throws IOException {
      BufferedReader br = null;
      File fileInputFile = null;
      try {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        fileInputFile = new File(inputFile);
        br = new BufferedReader(new FileReader(fileInputFile));
        String text = "";
        log.info(String.format("Adding documents using input file %s", inputFile));
        int docCount = 0;
        int fieldsCount = 0;
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        while ((text = br.readLine()) != null) {
          StringTokenizer stringTokenizer = new StringTokenizer(text, LocalndexingJob.FIELD_SEPARATOR);
          fieldsCount = 0;
          HashMap<String, Object> doc;
          doc = new HashMap<String, Object>();
          while (stringTokenizer.hasMoreElements()) {
            String token = stringTokenizer.nextToken();
            if (token.equals("\\N")) {
              token = null;
            }
            doc.put(OccurrenceLuceneFields.accFieldsValues[fieldsCount].name(), token);
            fieldsCount += 1;
          }
          bulkRequest.add(this.client.prepareIndex(indexName, "occurrence", (String) doc.get("id")).setContentType(
              XContentType.JSON).setSource(doc));
          if (docCount == logBatchSize || (((docCount > logBatchSize) && ((docCount % logBatchSize) == 0)))) {
            log.info(String.format("%s Documents added using input file %s", docCount, inputFile));
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            if (bulkResponse.hasFailures()) {
              log.info(String.format("Error indexing file %s", bulkResponse.buildFailureMessage()));
            }
            bulkRequest = client.prepareBulk();
          }

          docCount += 1;
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
          log.info(String.format("Error indexing file %s", bulkResponse.buildFailureMessage()));
        }
        stopWatch.stop();
        log.info(String.format("# of documents: %s", docCount));
        log.info(String.format("Total time: %s", stopWatch.getTime()));
        return docCount;
      } catch (Exception ex) {
        log.error(String.format("Error adding documents using file %", this.inputFile), ex);
      } finally {
        br.close();
        FileUtils.deleteQuietly(fileInputFile);
      }
      return 0;
    }

    @Override
    public Integer call() throws Exception {
      return addDocuments();
    }

  }

  private static final String ES_CONFIG = "es.config";

  private String inputFile;

  private Integer splitSize;

  private Integer logBatchSize;

  private String indexName;

  private TransportAddress[] transportAddresses;

  private String configFile;

  public MultithreadIndexingJob(String configFile) {
    super(configFile);
    this.inputFile = this.config.getProperty("inputFile");
    this.splitSize = Integer.parseInt(this.config.getProperty("splitSize"));
    this.logBatchSize = Integer.parseInt(this.config.getProperty("logBatchSize"));
    this.indexName = this.config.getProperty("indexName");
    String transportAddressesStr[] = this.config.getProperty("hosts").split(",");
    this.transportAddresses = new TransportAddress[transportAddressesStr.length];
    for (int i = 0; i < transportAddressesStr.length; i++) {
      String addressPort[] = transportAddressesStr[i].split(":");
      this.transportAddresses[i] = new InetSocketTransportAddress(addressPort[0], Integer.parseInt(addressPort[1]));
    }
    this.configFile = this.config.getProperty("es.config");
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    File file = new File(args[0]);
    MultithreadIndexingJob multithreadIndexingJob = new MultithreadIndexingJob(file.getAbsolutePath());
    multithreadIndexingJob.run();
  }

  @Override
  public List<? extends Callable<Integer>> createJobList() {
    try {
      StopWatch stopWatch = new StopWatch();
      stopWatch.start();
      log.info("Splitting input file...");
      List<String> files = FileSplitter.getData(inputFile, splitSize);
      log.info("Done Splitting input file");
      ArrayList<Job> jobs = new ArrayList<Job>();
      NodeBuilder nodeBuilder = NodeBuilder.nodeBuilder();
      File file = new File(this.configFile);

      Settings settings = nodeBuilder.settings().loadFromUrl(file.toURI().toURL()).build();
      Node node = nodeBuilder.client(true).settings(settings).node();
      Client client = node.client();
      for (String fileName : files) {
        Job job = new Job(this.indexName, fileName, logBatchSize, client);
        jobs.add(job);
      }
      stopWatch.stop();
      log.info(String.format("Job creation done in: %s", stopWatch.getTime()));
      return jobs;
    } catch (Exception e) {
      log.error("Error splitting file", e);
    }
    return null;
  }

}
