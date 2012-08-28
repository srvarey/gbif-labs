package org.gbif.ocurrence.index.solr.simpler.batch;

import org.gbif.ocurrence.index.solr.SolrIndexerJob;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.StringTokenizer;

public class BatchIndexer {

  public static class IndexReducer extends Reducer<LongWritable, Text, LongWritable, Text> {
    protected static final Logger log = Logger.getLogger(IndexReducer.class);
    private final SolrIndexerJob solrIndexerJob = new SolrIndexerJob();

    @Override
    public void reduce(LongWritable key, Iterable<Text> values, Context context) throws IOException,
        InterruptedException {
      try {
        IndexReducer.
        // HeartBeater hb = new HeartBeater(this.)
        log.info("Reducer: " + key.toString());
        for (Text text : values) {
          try {
            solrIndexerJob.insert(text.toString());
          } catch (Exception ex) {
            log.info("Error adding record: " + text.toString(), ex);
          }
        }
        solrIndexerJob.getServerInstance().commit();
      } catch (Exception ex) {
        log.info(ex);
      }
    }
  }
  public static class TokenizerMapper extends Mapper<Object, Text, LongWritable, Text> {
    protected static final Logger log = Logger.getLogger(TokenizerMapper.class);

    private LongWritable key = new LongWritable();

    @Override
    public void map(Object inkey, Text value, Context context) throws IOException, InterruptedException {
      try {
        StringTokenizer lineStringTokenizer = new StringTokenizer(value.toString(), "\001");
        Long docId = Long.parseLong(lineStringTokenizer.nextToken()) % reducersSize;
        key.set(docId);
        context.write(key, value);
      } catch (Exception ex) {
        log.info(ex);
      }
    }
  }

  private static final int recordsCount = 267375000;

  private static final int batchSize = 10000;

  private static final int reducersSize = recordsCount / batchSize;

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
    if (otherArgs.length != 2) {
      System.err.println("Usage: SimpleIndexer <in> <out>");
      System.exit(2);
    }
    Job job = new Job(conf, "solr indexer");
    job.setJarByClass(BatchIndexer.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setReducerClass(IndexReducer.class);
    job.setOutputKeyClass(LongWritable.class);
    FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
    FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }

}
