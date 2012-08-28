package org.gbif.solr;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.handler.ContentStreamLoader;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrQueryResponse;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.text.*;
import org.gbif.metadata.BasicMetadata;
import org.gbif.utils.file.CompressionUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DwcaLoader extends ContentStreamLoader {

  static String OVERWRITE = "overwrite";
  static String FIELDNAMES = "fieldnames";
  String errHeader = "DwcaLoader:";

  final IndexSchema schema;
  final SolrParams params;
  final UpdateRequestProcessor processor;
  final AddUpdateCommand templateAdd;

  Archive archive;

  DwcaLoader(SolrQueryRequest req, UpdateRequestProcessor processor) {
    this.processor = processor;
    this.params = req.getParams();
    schema = req.getSchema();

    templateAdd = new AddUpdateCommand();
    templateAdd.allowDups = false;
    templateAdd.overwriteCommitted = true;
    templateAdd.overwritePending = true;

    if (params.getBool(OVERWRITE, true)) {
      templateAdd.allowDups = false;
      templateAdd.overwriteCommitted = true;
      templateAdd.overwritePending = true;
    } else {
      templateAdd.allowDups = true;
      templateAdd.overwriteCommitted = false;
      templateAdd.overwritePending = false;
    }
  }

  private static void copyInputStream(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[8192];
    int len;

    while ((len = in.read(buffer)) >= 0) {
      out.write(buffer, 0, len);
    }

    in.close();
    out.close();
  }

  /**
   * this must be MT safe... may be called concurrently from multiple threads.
   */
  void addRecord(String resourceId, StarRecord record, AddUpdateCommand template) throws IOException {
    // first, create the lucene document
    SolrInputDocument doc = new SolrInputDocument();

    // index core record
    Record r = record.core();
    ArchiveFile af = archive.getCore();
    doc.addField("id", resourceId + ":" + r.id());
    for (ArchiveField f : af.getFields().values()) {
      doc.addField(fieldname(null, f.getTerm()), r.value(f.getTerm()), 1.0f);
    }

    // index extensions
    for (Map.Entry<String, List<Record>> ext : record.extensions().entrySet()) {
      af = archive.getExtension(ext.getKey(), false);
      String extPrefix = "ext:" + StringUtils.substringAfterLast(af.getRowType(), "/").toLowerCase();
      for (ArchiveField f : af.getFields().values()) {
        List<String> vals = new ArrayList<String>();
        for (Record erec : ext.getValue()) {
          vals.add(erec.value(f.getTerm()));
        }
        doc.addField(fieldname(extPrefix, f.getTerm()), vals, 1.0f);
      }
    }
    template.solrDoc = doc;
    processor.processAdd(template);
  }

  private String fieldname(String prefix, ConceptTerm term) {
    if (prefix == null) {
      return term.simpleName().toLowerCase();
    }
    return prefix + ":" + term.simpleName().toLowerCase();
  }

  private void input_err(String msg) {
    StringBuilder sb = new StringBuilder();
    sb.append(errHeader).append(", ").append(msg).append('\n');
    throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, sb.toString());
  }

  /**
   * load the DWC-A input
   */
  @Override
  public void load(SolrQueryRequest req, SolrQueryResponse rsp, ContentStream stream) throws IOException {
    errHeader = "DwcaLoader: input=" + stream.getSourceInfo();

    // decompress archive stream into individual files and read archive
    parseArchive(stream);

    // define +/- unique resource id.
    // Required to generate globally unique document ids based on dwca record ids
    String resourceId = archive.getResourceKey();
    if (resourceId == null) {
      // no resource key existing - try to generate one based on title?
      try {
        BasicMetadata bm = archive.getMetadata();
        resourceId = bm.getIdentifier();
      } catch (Exception e) {
      }
      if (resourceId == null) {
        // nothing still - use hashcode
        resourceId = String.valueOf(archive.hashCode());
      }
    }
    int i = 0;
    for (StarRecord rec : archive) {
      if (i % 1000 == 0) {
        System.out.println("Adding record " + rec.core().id());
      }
      addRecord(resourceId, rec, templateAdd);
      i++;
    }
  }

  private void parseArchive(ContentStream stream) throws SolrException {
    try {
      File dwca = File.createTempFile("dwcaFile-", null);
      System.out.println("Parsing archive into dir " + dwca.getAbsolutePath());
      // copy stream to file
      OutputStream bos = new FileOutputStream(dwca);
      InputStream in = stream.getStream();
      copyInputStream(in, bos);
      // decompress
      File location = CompressionUtil.decompressFile(dwca);
      // read archive
      archive = ArchiveFactory.openArchive(location);
    } catch (Exception e) {
      e.printStackTrace();
      throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "Cannot read dwc archive");
    }
  }
}
