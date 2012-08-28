package org.gbif.ocurrence.index.lucene;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.StringTokenizer;

public class IndexJobv2 extends Configured implements Tool {

  public static class Indexer extends Mapper<LongWritable, Text, IntWritable, Text> {

    private Configuration _conf;

    @Override
    public void map(LongWritable key, Text value, final Context context) throws InterruptedException, IOException {
      String tmp = _conf.get("hadoop.tmp.dir");
      long millis = System.currentTimeMillis();
      int numOfShards = Integer.parseInt(_conf.get("numOfShards"));
      log.info("numOfShards: " + numOfShards);
      log.info("task id: " + context.getTaskAttemptID().getTaskID().getId());
      log.info("tmpdir: " + tmp);
      int sharId = context.getTaskAttemptID().getTaskID().getId();
      log.info("key: " + key.get());
      log.info("sharId: " + sharId);
      String shardName = millis + "-" + new Random().nextInt();
      File file = new File(tmp, shardName);
      context.progress();
      // TODO sg this should be configurable
      Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
      IndexWriter indexWriter = new IndexWriter(FSDirectory.open(file), analyzer, MaxFieldLength.UNLIMITED);
      indexWriter.setMergeFactor(100000);
      context.setStatus("Adding documents...");
      while (context.nextKeyValue()) {
        context.progress();
        Document doc = new Document();
        String text = context.getCurrentValue().toString();
        StringTokenizer stringTokenizer = new StringTokenizer(text, "\001");
        int fieldsCount = 0;
        OccurrenceFields[] accFieldsValues = OccurrenceFields.values();
        while (stringTokenizer.hasMoreElements()) {
          Field contentField = new Field(accFieldsValues[fieldsCount].name(), stringTokenizer.nextToken(), Store.YES,
              Index.ANALYZED);
          doc.add(contentField);
          fieldsCount += 1;
        }
        indexWriter.addDocument(doc);
      }

      context.setStatus("Done adding documents.");
      Thread t = new Thread() {
        public boolean stop = false;

        @Override
        public void run() {
          while (!stop) {
            // Makes sure hadoop is not killing the task in case the
            // optimization
            // takes longer than the task timeout.
            context.progress();
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
      context.setStatus("Optimizing index...");
      indexWriter.optimize();
      context.setStatus("Done optimizing!");
      context.setStatus("Closing index...");
      indexWriter.close();
      context.setStatus("Closing done!");
      FileSystem fileSystem = FileSystem.get(_conf);

      context.setStatus("Starting copy to final destination...");
      Path destination = new Path(_conf.get("finalDestination"));
      fileSystem.copyFromLocalFile(new Path(file.getAbsolutePath()), destination);
      context.setStatus("Copy to final destination done!");
      context.setStatus("Deleting tmp files...");
      FileUtil.fullyDelete(file);
      context.setStatus("Deleting tmp files done!");
      t.interrupt();
      context.write(new IntWritable(sharId), new Text(destination.getName() + file.getName()));
    }

    @Override
    public void run(Context context) throws InterruptedException, IOException {
      _conf = context.getConfiguration();
      context.setStatus("Getting conf!!");
      super.run(context);
    }
  }

  public static class Merger extends Reducer<IntWritable, Text, Text, Text> {

    private String resultDir;

    @Override
    public void reduce(IntWritable key, Iterable<Text> values, final Context context) throws IOException {
      ArrayList<String> inputDirs = new ArrayList<String>();
      Iterator<Text> itValues = values.iterator();
      log.info("ShardId: " + key.toString());
      while (itValues.hasNext()) {
        log.info("Directory: " + itValues.next().toString());
        // inputDirs.add(itValues.next().toString());
      }
      context.setStatus("Merging indexes.");
      log.info("Merging!!");
      /*
       * Thread t = new Thread() {
       * public boolean stop = false;
       * 
       * @Override
       * public void run() {
       * while (!stop) {
       * // Makes sure hadoop is not killing the task in case the
       * // optimization
       * // takes longer than the task timeout.
       * context.progress();
       * try {
       * sleep(10000);
       * } catch (InterruptedException e) {
       * // don't need to do anything.
       * stop = true;
       * }
       * }
       * }
       * };
       * t.start();
       * String tmpOutPutDir = context.getConfiguration().get("hadoop.tmp.dir") + key.toString();
       * 
       * IndexMerger.mergeIndexes(tmpOutPutDir, (String[]) inputDirs.toArray());
       * FileSystem fileSystem = FileSystem.get(context.getConfiguration());
       * 
       * context.setStatus("Starting copy to final destination...");
       * Path destination = new Path(context.getConfiguration().get("indexResultMergedDir"));
       * fileSystem.create(destination);
       * fileSystem.copyFromLocalFile(new Path(tmpOutPutDir), destination);
       * context.setStatus("Copy to final destination done!");
       * context.setStatus("Deleting tmp files...");
       * FileUtil.fullyDelete(new File(tmpOutPutDir));
       * t.interrupt();
       */
    }

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      super.setup(context);
      resultDir = context.getConfiguration().get("hadoop.tmp.dir") + "/"
          + context.getConfiguration().get("indexResultMergedDir");
    }

  }

  /**
   * {@link Logger} instance
   */
  private static final Logger log = Logger.getLogger(IndexJobv2.class);

  public static void main(String[] args) throws Exception {

    if (args.length != 4) {
      String usage = "IndexerJob <in text file/dir> <out index dir> <numOfShards> <out merged index dir>";
      System.out.println(usage);
      System.exit(1);
    }
    IndexJobv2 indexerJob = new IndexJobv2();
    Configuration conf = new Configuration();
    int res = ToolRunner.run(new Configuration(), indexerJob, args);
    System.exit(res);

  }

  public int run(String[] args) throws Exception {
    String input = args[0];
    String output = args[1];
    Integer numOfShards = Integer.parseInt(args[2]);
    String mergedIndexDir = args[3];
    log.info("Starting indexing!!");
    return this.startIndexer(input, output, numOfShards, mergedIndexDir);

  }

  public int startIndexer(String path, String finalDestination, Integer numOfShards, String mergedIndexDir)
      throws Exception {
    Configuration conf = getConf();
    // setting the folder where lucene indexes will be copied when finished.
    conf.set("finalDestination", finalDestination);
    conf.set("numOfShards", numOfShards.toString());
    conf.set("indexResultMergedDir", mergedIndexDir);
    // important to switch spec exec off.
    // We dont want to have something duplicated.
    conf.set("mapred.map.tasks.speculative.execution", "false");
    conf.set("mapred.reduce.tasks.speculative.execution", "false");

    // The num of map tasks is equal to the num of input splits.
    // The num of input splits by default is equal to the num of hdf blocks
    // for the input file(s). To get the right num of shards we need to
    // calculate the best input split size.
    Path input = new Path(path);
    Path outPath = new Path(finalDestination);

    FileSystem fs = FileSystem.get(input.toUri(), conf);
    long size = 0;
    if (fs.getFileStatus(input).isDir()) {
      size += fs.getContentSummary(input).getLength();
    } else {
      FileStatus[] status = fs.globStatus(input);
      for (FileStatus fileStatus : status) {
        size += fileStatus.getLen();
      }
    }
    System.out.println("Input size: " + size);
    long optimalSplitSize = size / numOfShards;
    conf.set("mapred.min.split.size", "" + optimalSplitSize);
    System.out.println("Split size: " + optimalSplitSize);

    // give more mem to lucene tasks.
    conf.set("mapred.child.java.opts", "-Xmx2G");

    Job job = new Job(conf, "Lucene Indexer");
    job.setNumReduceTasks(numOfShards);
    job.setJarByClass(IndexJobv2.class);
    job.setMapperClass(IndexJobv2.Indexer.class);
    // job.setReducerClass(IndexJobv2.Merger.class);

    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(Text.class);

    FileInputFormat.addInputPath(job, input);
    FileOutputFormat.setOutputPath(job, outPath);

    return job.waitForCompletion(true) ? 0 : 1;
  }
}
