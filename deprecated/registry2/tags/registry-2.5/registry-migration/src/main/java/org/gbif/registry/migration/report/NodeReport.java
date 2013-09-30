package org.gbif.registry.migration.report;

import java.util.Arrays;
import java.util.List;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.LoggerFactory;


public class NodeReport extends ReportGenerator {

  private static final List<String> FIELDS_MYSQL = Arrays.asList("key", "title", "created", "deleted");
  private static final List<String> FIELDS_PGSQL = Arrays.asList("key", "title", "created", "deleted");


  public NodeReport(WebResource mysqlRegistry, WebResource pgsqlRegistry) {
    super(mysqlRegistry, pgsqlRegistry, LoggerFactory.getLogger("NODE-REPORT"), FIELDS_MYSQL, FIELDS_PGSQL);
  }

  /**
   * Takes 2 urls (old then new), e.g.
   * http://staging.gbif.org:8080/registry-ws/node
   * http://staging.gbif.org:8080/registry2-ws/node
   */
  public static void main(String[] args) {
    Client client = Client.create();
    NodeReport me = new NodeReport(client.resource(args[0]), client.resource(args[1]));
    try {
      me.generate();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
