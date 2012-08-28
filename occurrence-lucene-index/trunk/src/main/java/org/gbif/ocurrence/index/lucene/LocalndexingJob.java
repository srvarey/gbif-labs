package org.gbif.ocurrence.index.lucene;

import org.apache.commons.lang.time.StopWatch;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class LocalndexingJob {

  public static final String FIELD_SEPARATOR = "\001";
  private static Logger log = LoggerFactory.getLogger(LocalndexingJob.class);

  public static int createIndex(final String inputFile, IndexWriter indexWriter, final int batchSize)
      throws IOException {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    BufferedReader br = new BufferedReader(new FileReader(inputFile));
    String text = "";
    log.info("Adding documents...");
    int docCount = 0;
    int fieldsCount = 0;
    Document doc = getBaseDocument();
    while ((text = br.readLine()) != null) {
      StringTokenizer stringTokenizer = new StringTokenizer(text, FIELD_SEPARATOR);
      fieldsCount = 0;
      while (stringTokenizer.hasMoreElements()) {
        OccurrenceLuceneFields.fields[fieldsCount].setValue(stringTokenizer.nextToken());
        fieldsCount += 1;
      }
      if (docCount == batchSize || (((docCount > batchSize) && ((docCount % batchSize) == 0)))) {
        log.info("Documents added: " + docCount);
      }
      indexWriter.addDocument(doc);
      docCount += 1;
    }
    log.info("Optimizing index...");
    indexWriter.optimize();
    log.info("Done optimizing!");
    log.info("Closing index...");
    indexWriter.close();
    log.info("Closing done!");
    stopWatch.stop();
    log.info("# of documents: " + docCount);
    log.info("Total time: " + stopWatch.getTime());
    return docCount;
  }

  public static void createIndex(final String inputFile, final String destination, final int batchSize,
      final int mergeFactor, final int bufferSize) throws IOException {
    File file = new File(destination);
    IndexWriterConfig indexWriterConfig = getIndexWriterConfig(mergeFactor, bufferSize);
    IndexWriter indexWriter = new IndexWriter(FSDirectory.open(file), indexWriterConfig);
    createIndex(inputFile, indexWriter, batchSize);
  }

  public static Document getBaseDocument() {
    Document doc = new Document();
    for (Field field : OccurrenceLuceneFields.fields) {
      doc.add(field);
    }
    return doc;
  }

  public static IndexWriterConfig getIndexWriterConfig(final int mergeFactor, final int bufferSize) {
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_32);
    LogByteSizeMergePolicy logByteSizeMergePolicy = new LogByteSizeMergePolicy();
    logByteSizeMergePolicy.setMergeFactor(mergeFactor);
    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_32, analyzer);
    indexWriterConfig.setRAMBufferSizeMB(bufferSize);
    indexWriterConfig.setMergePolicy(logByteSizeMergePolicy);
    return indexWriterConfig;
  }

  public static void main(String[] args) throws IOException {
    try {
      String inputFile = args[0];
      String destination = args[1];
      Integer batchSize = Integer.parseInt(args[2]);
      Integer mergeFactor = Integer.parseInt(args[3]);
      Integer bufferSize = Integer.parseInt(args[4]);
      createIndex(inputFile, destination, batchSize, mergeFactor, bufferSize);
    } catch (Exception ex) {
      log.error(ex.getMessage());
    }
  }
}
