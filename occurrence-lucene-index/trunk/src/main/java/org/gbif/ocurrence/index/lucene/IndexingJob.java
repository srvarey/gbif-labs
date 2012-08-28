package org.gbif.ocurrence.index.lucene;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapRunnable;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * Created by IntelliJ IDEA.
 * User: fede
 * Date: 6/24/11
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */
/**
 * Illustrates how to implement a indexer as hadoop map reduce job.
 */
public class IndexingJob {

  public static void main(String[] args) throws IOException {

    if (args.length != 3) {
      String usage = "IndexerJob <in text file/dir> <out katta index dir> <numOfShards>";
      System.out.println(usage);
      System.exit(1);
    }

    IndexingJob indexerJob = new IndexingJob();
    String input = args[0];
    String output = args[1];
    int numOfShards = Integer.parseInt(args[2]);
    indexerJob.startIndexer(input, output, numOfShards);

  }

  public void startIndexer(String path, String finalDestination, int numOfShards) throws IOException {
    // create job conf with class pointing into job jar.
    JobConf jobConf = new JobConf(IndexingJob.class);
    jobConf.setJobName("indexer");
    jobConf.setMapRunnerClass(Indexer.class);
    // alternative use a text file and a TextInputFormat
    //jobConf.setInputFormat(SequenceFileInputFormat.class);

    Path input = new Path(path);
    FileInputFormat.setInputPaths(jobConf, input);
    // we just set the output path to make hadoop happy.
    FileOutputFormat.setOutputPath(jobConf, new Path(finalDestination));
    // setting the folder where lucene indexes will be copied when finished.
    jobConf.set("finalDestination", finalDestination);
    // important to switch spec exec off.
    // We dont want to have something duplicated.
    jobConf.setSpeculativeExecution(false);

    // The num of map tasks is equal to the num of input splits.
    // The num of input splits by default is equal to the num of hdf blocks
    // for the input file(s). To get the right num of shards we need to
    // calculate the best input split size.

    FileSystem fs = FileSystem.get(input.toUri(), jobConf);
    FileStatus[] status = fs.globStatus(input);
    long size = 0;
    for (FileStatus fileStatus : status) {
      size += fileStatus.getLen();
    }
    long optimalSplisize = size / numOfShards;
    jobConf.set("mapred.min.split.size", "" + optimalSplisize);

    // give more mem to lucene tasks.
    jobConf.set("mapred.child.java.opts", "-Xmx2G");
    jobConf.setNumMapTasks(1);
    jobConf.setNumReduceTasks(0);
    JobClient.runJob(jobConf);
  }
      public enum OccurrenceFields{
        id,
        data_provider_id,
        data_resource_id,
        institution_code_id,
        collection_code_id,
        catalogue_number_id,
        taxon_concept_id,
        taxon_name_id,
        kingdom_concept_id,
        phylum_concept_id,
        class_concept_id,
        order_concept_id,
        family_concept_id,
        genus_concept_id,
        species_concept_id,
        nub_concept_id,
        iso_country_code,
        latitude,
        longitude,
        cell_id,
        centi_cell_id,
        mod360_cell_id,
        year,
        month,
        occurrence_date,
        basis_of_record,
        taxonomic_issue,
        geospatial_issue,
        other_issue,
        deleted,
        altitude_metres,
        depth_centimetres,
        modified;
      }
  public static class Indexer implements MapRunnable<LongWritable, Text, Text, Text> {

    private JobConf _conf;

    public void configure(JobConf conf) {
      _conf = conf;

    }

    @SuppressWarnings("deprecation")
    public void run(RecordReader<LongWritable, Text> reader, OutputCollector<Text, Text> output, final Reporter report)
            throws IOException {
      LongWritable key = reader.createKey();
      Text value = reader.createValue();

      String tmp = _conf.get("hadoop.tmp.dir");
      long millis = System.currentTimeMillis();
      String shardName = "" + millis + "-" + new Random().nextInt();
      File file = new File(tmp, shardName);
      report.progress();
      // TODO sg this should be configurable
      Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
      IndexWriter indexWriter = new IndexWriter(FSDirectory.open(file), analyzer, MaxFieldLength.UNLIMITED);
      indexWriter.setMergeFactor(100000);
      report.setStatus("Adding documents...");
      while (reader.next(key, value)) {
        report.progress();
        Document doc = new Document();
        String text = "" + value.toString();
        StringTokenizer stringTokenizer = new StringTokenizer(text,"\001");
        int fieldsCount = 0;
        OccurrenceFields[]  accFieldsValues = OccurrenceFields.values();
        while(stringTokenizer.hasMoreElements()){
            Field contentField = new Field(accFieldsValues[fieldsCount].name(), stringTokenizer.nextToken(), Store.YES, Index.ANALYZED);
            doc.add(contentField);
            fieldsCount +=1;
        }
        indexWriter.addDocument(doc);
      }

      report.setStatus("Done adding documents.");
      Thread t = new Thread() {
        public boolean stop = false;

        @Override
        public void run() {
          while (!stop) {
            // Makes sure hadoop is not killing the task in case the
            // optimization
            // takes longer than the task timeout.
            report.progress();
            try {
              sleep(10000);
            } catch (InterruptedException e) {
              // don't need to do anything.
              stop = true;
            }
          }
        }
      };
      t.start();
      report.setStatus("Optimizing index...");
      indexWriter.optimize();
      report.setStatus("Done optimizing!");
      report.setStatus("Closing index...");
      indexWriter.close();
      report.setStatus("Closing done!");
      FileSystem fileSystem = FileSystem.get(_conf);

      report.setStatus("Starting copy to final destination...");
      Path destination = new Path(_conf.get("finalDestination"));
      fileSystem.copyFromLocalFile(new Path(file.getAbsolutePath()), destination);
      report.setStatus("Copy to final destination done!");
      report.setStatus("Deleting tmp files...");
      FileUtil.fullyDelete(file);
      report.setStatus("Deleting tmp files done!");
      t.interrupt();
    }
  }
}
