package org.gbif.ocurrence.index.solr.simpler;

import org.gbif.ocurrence.index.solr.SolrIndexerJob;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.log4j.Logger;

import java.io.IOException;

public class SimpleIndexer {
  public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
    protected static final Logger log = Logger.getLogger(TokenizerMapper.class);

    private final SolrIndexerJob solrIndexerJob = new SolrIndexerJob();

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
      try {
        solrIndexerJob.insert(value.toString());
      } catch (Exception ex) {
        log.error(ex);
        // throw new RuntimeException(ex);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
    if (otherArgs.length != 2) {
      System.err.println("Usage: SimpleIndexer <in> <out>");
      System.exit(2);
    }
    Job job = new Job(conf, "solr indexer");
    job.setJarByClass(SimpleIndexer.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setReducerClass(Reducer.class);
    job.setOutputKeyClass(Text.class);
    FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
    FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }

}
