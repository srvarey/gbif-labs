package org.gbif.registry.metasync.util;

import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

public class TemplateUtils {

  private static final VelocityEngine VELOCITY_ENGINE = new VelocityEngine();

  static {
    Properties properties = new Properties();
    properties.setProperty("file.resource.loader.class",
                           "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    VELOCITY_ENGINE.init(properties);
  }

  public static String getBiocaseInventoryRequest(String contentNamespace) {
    Context context = new VelocityContext();
    context.put("contentNamespace", contentNamespace);
    context.put("concept", getTitlePath(contentNamespace));

    StringWriter writer = new StringWriter();
    VELOCITY_ENGINE.mergeTemplate("biocase/inventory.xml.vm", "UTF-8", context, writer);
    return writer.toString();
  }

  public static String getBiocaseMetadataRequest(String contentNamespace, String datasetTitle) {
    Context context = new VelocityContext();
    context.put("contentNamespace", contentNamespace);
    context.put("datasetTitle", datasetTitle);
    context.put("titleConcept", getTitlePath(contentNamespace));

    if (contentNamespace.equals(Constants.ABCD_12_SCHEMA)) {
      context.put("nameConcept",
                  "/DataSets/DataSet/Units/Unit/Identifications/Identification/TaxonIdentified/NameAuthorYearString");
    } else if (contentNamespace.equals(Constants.ABCD_206_SCHEMA)) {
      context.put("nameConcept",
                  "/DataSets/DataSet/Units/Unit/Identifications/Identification/Result/TaxonIdentified/ScientificName/FullScientificNameString");
    }

    StringWriter writer = new StringWriter();
    VELOCITY_ENGINE.mergeTemplate("/biocase/metadata.xml.vm", "UTF-8", context, writer);
    return writer.toString();
  }

  private static String getTitlePath(String contentNamespace) {
    if (contentNamespace.equals(Constants.ABCD_12_SCHEMA)) {
      return "/DataSets/DataSet/OriginalSource/SourceName";
    }
    if (contentNamespace.equals(Constants.ABCD_206_SCHEMA)) {
      return "/DataSets/DataSet/Metadata/Description/Representation/Title";
    }
    return null;
  }

}

