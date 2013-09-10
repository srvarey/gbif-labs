package org.gbif.registry.migration.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.WebResource;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Once completed, this can be used to generate a report on the outcomes to help gain confidence that the migration
 * performed as expected.
 * This uses web services on the MySQL DB (e.g. Registry 1.0 WS and compares the content with those provided through
 * Registry 2.0 WS).
 */
public abstract class ReportGenerator {

  // generic logging
  private static final Logger LOG = LoggerFactory.getLogger(ReportGenerator.class);
  // the report
  private final Logger reportLogger;
  private final WebResource mysqlRegistry;
  private final WebResource pgsqlRegistry;
  private static final ObjectMapper MAPPER = new ObjectMapper();
  protected int missingCount = 0; // missing entities
  protected int differingCount = 0; // entities do not match
  private final List<String> equalityFieldsMysql; // the fields to check for equality
  private final List<String> equalityFieldsPgsql; // the fields to check for equality


  public ReportGenerator(WebResource mysqlRegistry, WebResource pgsqlRegistry, Logger reportLogger,
    List<String> equalityFieldsMysql, List<String> equalityFieldsPgsql) {
    this.mysqlRegistry = mysqlRegistry;
    this.pgsqlRegistry = pgsqlRegistry;
    this.reportLogger = reportLogger;
    this.equalityFieldsMysql = equalityFieldsMysql;
    this.equalityFieldsPgsql = equalityFieldsPgsql;
  }

  protected void generate() throws JsonParseException, IOException {
    LOG.debug("Starting report");

    StringBuffer sb = new StringBuffer();
    sb.append("SourceDB");
    sb.append("\t");
    for (String field : equalityFieldsMysql) {
      sb.append(field);
      sb.append("\t");
    }
    reportLogger.info(sb.toString());

    boolean isEndOfRecords = true;
    int offset = 0;
    int limit = 100;

    do {
      LOG.debug("Getting page offset[{}], limit[{}]", offset, limit);
      JsonNode response = readJson(mysqlRegistry.queryParam("offset", String.valueOf(offset + limit)));

      for (JsonNode mysqlEntity : fieldAsList(response, "results")) {
        String key = fieldAsString(mysqlEntity, "key");

        // we don't care about entities with non UUID keys (e.g. the KNB datasets)
        try {
          UUID.fromString(key);
        } catch (Exception e) {
          continue;
        }

        // get the pgsql representation by the key
        JsonNode pgEntity = readJson(pgsqlRegistry.path(key));

        LOG.debug("MySQL: {}", mysqlEntity);
        LOG.debug("PgSQL: {}", pgEntity);

        if (pgEntity == null) {
          report(key, mysqlEntity, null);
          missingCount++;
        } else {
          boolean equal = equals(key, mysqlEntity, pgEntity);
          if (!equal) {
            differingCount++;
            // report("Source entity[{}] is different to the target[{}]", mysqlEntity, pgEntity);
          }
        }
      }

      // setup for next page
      offset = fieldAsInt(response, "offset");
      limit = fieldAsInt(response, "limit");
      isEndOfRecords = fieldAsBoolean(response, "endOfRecords");
    } while (!isEndOfRecords);

    LOG.info("{} missing entities", missingCount);
    LOG.info("{} differing entities", differingCount);
  }

  /**
   * Override in subclasses to do specific checking of fields.
   */
  protected boolean equals(String key, JsonNode mysqlEntity, JsonNode pgEntity) {
    boolean equals = true;

    for (int i = 0; i < equalityFieldsMysql.size(); i++) {
      String mysqlField = equalityFieldsMysql.get(i);
      String pgsqlField = equalityFieldsPgsql.get(i);
      equals &= equal(mysqlField, pgsqlField, mysqlEntity, pgEntity);
    }

    if (!equals) {
      report(key, mysqlEntity, pgEntity);
    }

    return equals;
  }

  private void report(String key, JsonNode mysqlEntity, JsonNode pgEntity) {
    StringBuffer sb = new StringBuffer();
    sb.append("MySQL");
    sb.append("\t");
    for (int i = 0; i < equalityFieldsMysql.size(); i++) {
      String s = fieldAsString(mysqlEntity, equalityFieldsMysql.get(i));
      s = s == null ? "" : s;
      sb.append(s);
      if (i < equalityFieldsMysql.size()) {
        sb.append("\t");
      }
    }
    reportLogger.info(sb.toString());

    sb = new StringBuffer();
    sb.append("PgSQL");
    sb.append("\t");
    for (int i = 0; i < equalityFieldsPgsql.size(); i++) {
      String s = fieldAsString(pgEntity, equalityFieldsPgsql.get(i));
      s = s == null ? "" : s;
      sb.append(s);
      if (i < equalityFieldsPgsql.size()) {
        sb.append("\t");
      }
    }
    reportLogger.info(sb.toString());
  }

  private static Integer fieldAsInt(JsonNode node, String field) {
    if (node == null) {
      return null;
    }
    return node.path(field).asInt();
  }

  private static Boolean fieldAsBoolean(JsonNode node, String field) {
    if (node == null) {
      return null;
    }

    return node.path(field).asBoolean();
  }

  private static String fieldAsString(JsonNode node, String field) {
    if (node == null) {
      return null;
    }
    return node.path(field).asText();
  }

  private static List<JsonNode> fieldAsList(JsonNode node, String field) {
    Iterator<JsonNode> iter = node.path(field).getElements();
    List<JsonNode> result = new ArrayList<JsonNode>();
    while (iter.hasNext()) {
      result.add(iter.next());
    }
    return result;
  }

  /**
   * Returns true if the field is the same on the source and target
   */
  protected boolean equal(String fieldSource, String fieldTarget, JsonNode source, JsonNode target) {
    Object s = source.get(fieldSource);
    Object o = target.get(fieldTarget);
    if (s == null && o == null) {
      return true;
    } else if (s != null) {
      return s.equals(o);
    } else {
      // report("Value[{}] exists in target[{}], but source[{}] has no value", o, target, source);
      return false;
    }
  }

  private JsonNode readJson(WebResource resource) throws JsonParseException, IOException {
    // surely betters of doing this, but not worth wasting time on it...
    try {
      String response = resource.accept(MediaType.APPLICATION_JSON).get(String.class);
      JsonParser jp = MAPPER.getJsonFactory().createJsonParser(response);
      return MAPPER.readTree(jp);
    } catch (Exception e) {
      return null;
    }
  }
}
