package org.gbif.occurrence.index.elasticsearch;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequest.OpType;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import java.util.HashMap;

public class IndexingJob {

  private static final String ES_CONFIG_NAME = "elasticsearch.yml";
  private static final String ES_CONFIG = "es.config";

  public static void main(String[] args) {
    try {
      if (args.length == 0) {
        System.setProperty(ES_CONFIG, IndexingJob.class.getClassLoader().getResource(ES_CONFIG_NAME).getPath());
      } else {
        System.setProperty(ES_CONFIG, args[0]);
      }
      /*
       * Node node = NodeBuilder.nodeBuilder().client(true).node();
       * Client client = node.client();
       * CreateIndexResponse createIndexResponse =
       * client.admin().indices().prepareCreate("occurrences").execute().actionGet();
       * DeleteIndexResponse deleteIndexResponse =
       * client.admin().indices().prepareDelete("occurrences").execute().actionGet();
       * System.out.println("INDEX CREATED: " + createIndexResponse.acknowledged());
       * System.out.println("INDEX DELETED: " + deleteIndexResponse.acknowledged());
       */
      Node node = NodeBuilder.nodeBuilder().client(true).node();
      Client client = node.client();
      // TransportClient transportClient = new TransportClient();
      // transportClient.addTransportAddress(new InetSocketTransportAddress("130.226.238.145", 9302));
      HashMap<String, Object> doc = new HashMap<String, Object>();
      doc.put("id", "2");
      doc.put("desc", "2");
      IndexRequest indexRequest = new IndexRequest();
      indexRequest.index("ocurrences");
      indexRequest.opType(OpType.INDEX);
      indexRequest.contentType(XContentType.JSON);
      indexRequest.source(doc);
      IndexResponse indexResponse = client.prepareIndex("ocurrencess", "occurrence", "id").setContentType(
          XContentType.JSON).setSource(doc).execute().actionGet();
      System.out.println(indexResponse.getId());
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

}
