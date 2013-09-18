package org.gbif.registry.migration.report;

import java.util.Arrays;
import java.util.List;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.LoggerFactory;


public class DatasetReport extends ReportGenerator {

// private static final List<String> FIELDS_MYSQL = Arrays.asList("key", "title", "created", "deleted",
// "owningOrganizationKey", "technicalInstallationKey");
// private static final List<String> FIELDS_PGSQL = Arrays.asList("key", "title", "created", "deleted",
// "owningOrganizationKey", "installationKey");

  private static final List<String> FIELDS_MYSQL = Arrays.asList("key", "title", "deleted",
    "owningOrganizationKey", "technicalInstallationKey");
  private static final List<String> FIELDS_PGSQL = Arrays.asList("key", "title", "deleted",
    "owningOrganizationKey", "installationKey");

  public DatasetReport(WebResource mysqlRegistry, WebResource pgsqlRegistry) {
    super(mysqlRegistry, pgsqlRegistry, LoggerFactory.getLogger("ORG-REPORT"), FIELDS_MYSQL, FIELDS_PGSQL);
  }

  /**
   * Takes 2 urls (old then new), e.g.
   * http://staging.gbif.org:8080/registry-ws/organization
   * http://staging.gbif.org:8080/registry2-ws/organization
   */
  public static void main(String[] args) {
    Client client = Client.create();
    DatasetReport me = new DatasetReport(client.resource(args[0]), client.resource(args[1]));
    try {
      me.generate();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
