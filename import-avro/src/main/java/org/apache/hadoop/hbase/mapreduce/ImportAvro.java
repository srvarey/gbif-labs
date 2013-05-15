package org.apache.hadoop.hbase.mapreduce;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.mapred.AvroInputFormat;
import org.apache.avro.mapred.AvroJob;
import org.apache.avro.mapred.AvroWrapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapred.OldHFileOutputFormat;
import org.apache.hadoop.hbase.mapred.OldPutSortReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * A quasi-copy of ImportTsv.
 */
public class ImportAvro {

  final static String NAME = "importavro";
  final static String SKIP_LINES_CONF_KEY = "importavro.skip.bad.lines";
  final static String BULK_OUTPUT_CONF_KEY = "importavro.bulk.output";
  final static String COLUMNS_CONF_KEY = "importavro.columns";
  final static String ROWKEY_COLUMN_SPEC = "HBASE_ROW_KEY";

  /**
   * Write table content out to files in hdfs.
   */
  static class AvroImporter implements Mapper<AvroWrapper<GenericRecord>, NullWritable, ImmutableBytesWritable, Put> {

    /**
     * Timestamp for all inserted rows
     */
    private long ts;
    private byte[][] families;
    private byte[][] qualifiers;
    private int rowKeyColumnIndex;

    public void configure(JobConf jobConf) {
      // parse the list of passed in columns, including the special token representing the hbase row key
      ArrayList<String> columnStrings =
        Lists.newArrayList(Splitter.on(',').trimResults().split(jobConf.get(COLUMNS_CONF_KEY)));

      families = new byte[columnStrings.size()][];
      qualifiers = new byte[columnStrings.size()][];

      for (int i = 0; i < columnStrings.size(); i++) {
        String str = columnStrings.get(i);
        if (ROWKEY_COLUMN_SPEC.equals(str)) {
          rowKeyColumnIndex = i;
          continue;
        }
        String[] parts = str.split(":", 2);
        if (parts.length == 1) {
          families[i] = str.getBytes();
          qualifiers[i] = HConstants.EMPTY_BYTE_ARRAY;
        } else {
          families[i] = parts[0].getBytes();
          qualifiers[i] = parts[1].getBytes();
        }
      }

      ts = System.currentTimeMillis();
    }

    /**
     * Convert a line of Avro text into an HBase table row.
     */
    public void map(AvroWrapper<GenericRecord> key, NullWritable value,
      OutputCollector<ImmutableBytesWritable, Put> outputCollector, Reporter reporter) throws IOException {
      GenericRecord record = key.datum();
      Schema avroSchema = record.getSchema();
      List<Schema.Field> fields = avroSchema.getFields();

      // build row key
      Schema.Field rowKeyField = fields.get(rowKeyColumnIndex);
      Schema.Type rowKeyType = typeFromAvro(rowKeyField.schema());
      String rowKeyString = record.get(rowKeyField.name()).toString();
      byte[] rawRowKey = bytesFromTypedString(rowKeyString, rowKeyType);
      ImmutableBytesWritable rowKey = new ImmutableBytesWritable(rawRowKey);

      // build columns
      Put put = new Put(rowKey.copyBytes());
      for (int i = 0; i < fields.size(); i++) {
        if (i != rowKeyColumnIndex) {
          Schema.Field field = fields.get(i);
          Schema.Type fieldType = typeFromAvro(field.schema());
          Object fieldValue = record.get(field.name());
          if (fieldValue != null) {
            byte[] finalValue = bytesFromTypedString(fieldValue.toString(), fieldType);
            KeyValue kv = new KeyValue(rowKey.copyBytes(), families[i], qualifiers[i], ts, finalValue);
            put.add(kv);
          }
        }
      }
      outputCollector.collect(rowKey, put);
    }

    // TODO: handle other types
    private byte[] bytesFromTypedString(String stringVal, Schema.Type fieldType) {
      byte[] finalValue;
      switch (fieldType) {
        case INT:
          finalValue = Bytes.toBytes(Integer.valueOf(stringVal));
          break;
        case FLOAT:
          finalValue = Bytes.toBytes(Float.valueOf(stringVal));
          break;
        case DOUBLE:
          finalValue = Bytes.toBytes(Double.valueOf(stringVal));
          break;
        case LONG:
          finalValue = Bytes.toBytes(Long.valueOf(stringVal));
          break;
        default:
          finalValue = Bytes.toBytes(stringVal);
      }

      return finalValue;
    }

    /**
     * Extract the single, meaningful type from the schema.  There maybe be "unions" of a real type with null - that
     * case is why this method exists. Only supports union with null.
     *
     * @param fieldSchema containing one or a union of multiple types
     *
     * @return a single type
     */
    private Schema.Type typeFromAvro(Schema fieldSchema) {
      List<Schema> types = fieldSchema.getTypes();
      switch (types.size()) {
        case 1:
          return types.get(0).getType();
        case 2:
          Schema s1 = types.get(0);
          Schema s2 = types.get(1);
          if (s1.getType() == Schema.Type.NULL) {
            return s2.getType();
          } else if (s2.getType() == Schema.Type.NULL) {
            return s1.getType();
          } else {
            throw new IllegalArgumentException("Only support union with null");
          }
        default:
          throw new IllegalArgumentException("Only support union with null");
      }
    }

    public void close() throws IOException {
      // TODO: Write implementation
    }
  }

  /**
   * Sets up the actual job.
   *
   * @param jobConf The current configuration.
   * @param args    The command line parameters.
   *
   * @return The newly created job.
   *
   * @throws java.io.IOException When setting up the job fails.
   */
  public static JobConf createSubmittableJob(JobConf jobConf, String[] args) throws IOException {
    String schemaFile = args[0];
    StringBuilder sb = new StringBuilder();
    BufferedReader reader =
      new BufferedReader(new InputStreamReader(new FileInputStream(new File(schemaFile)), "UTF-8"));
    while (reader.ready()) {
      String line = reader.readLine();
      sb.append(line);
    }
    String schema = sb.toString();

    jobConf.set(AvroJob.INPUT_SCHEMA, schema);
    jobConf.set(AvroJob.INPUT_IS_REFLECT, "false");

    String tableName = args[1];
    Path inputDir = new Path(args[2]);
    jobConf.setJobName(NAME + "_" + tableName);

    jobConf.setJarByClass(AvroImporter.class);
    FileInputFormat.setInputPaths(jobConf, inputDir);
    jobConf.setInputFormat(AvroInputFormat.class);
    jobConf.setMapperClass(AvroImporter.class);

    String hfileOutPath = jobConf.get(BULK_OUTPUT_CONF_KEY);
    // TODO: either provide a valid path for null or make it a required arg like input dir
    if (hfileOutPath == null) throw new IllegalArgumentException("Missing Bulk output conf key - can't start job");
    HTable table = new HTable(jobConf, tableName);
    jobConf.setReducerClass(OldPutSortReducer.class);
    Path outputDir = new Path(hfileOutPath);
    FileOutputFormat.setOutputPath(jobConf, outputDir);
    jobConf.setMapOutputKeyClass(ImmutableBytesWritable.class);
    jobConf.setMapOutputValueClass(Put.class);
    OldHFileOutputFormat.configureIncrementalLoad(jobConf, table);

    TableMapReduceUtil.addDependencyJars(jobConf);
    TableMapReduceUtil.addDependencyJars(jobConf, org.apache.avro.mapred.AvroInputFormat.class,
      org.apache.avro.specific.SpecificDatumReader.class, com.google.common.base.Splitter.class,
      org.apache.hadoop.hdfs.DistributedFileSystem.class);

    return jobConf;
  }

  /*
  * @param errorMsg Error message.  Can be null.
  */
  private static void usage(final String errorMsg) {
    if (errorMsg != null && errorMsg.length() > 0) {
      System.err.println("ERROR: " + errorMsg);
    }
    String usage = "Usage: " + NAME + " -Dimportavro.columns=a,b,c <tablename> <inputdir>\n" + "\n"
                   + "Imports the given input directory of avro data into the specified table.\n" + "\n"
                   + "The column names of the avro data must be specified using the -Dimportavro.columns\n"
                   + "option. This option takes the form of comma-separated column names, where each\n"
                   + "column name is either a simple column family, or a columnfamily:qualifier. The special\n"
                   + "column name HBASE_ROW_KEY is used to designate that this column should be used\n"
                   + "as the row key for each imported record. You must specify exactly one column\n"
                   + "to be the row key, and you must specify a column name for every column that exists in the\n"
                   + "input data.\n" + "\n" + "In order to prepare data for a bulk data load, pass the option:\n"
                   + "  -D" + BULK_OUTPUT_CONF_KEY + "=/path/for/output\n"
                   + "  Note: if you do not use this option, then the target table must already exist in HBase\n" + "\n"
                   + "Other options that may be specified with -D include:\n" + "  -D" + SKIP_LINES_CONF_KEY
                   + "=false - fail if encountering an invalid line\n\n"
                   + "The final three arguments have no -D in front of them, just space separated:\n"
                   + "<avro_schema_file (local)> <target_hbase_tablename> <avro-dir (hdfs)>\n";
    System.err.println(usage);
  }

  /**
   * Main entry point.
   *
   * @param args The command line parameters.
   *
   * @throws Exception When running the job fails.
   */
  public static void main(String[] args) throws Exception {
    System.out.println(">> ImportAvro");
    Configuration conf = HBaseConfiguration.create();

    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
    if (otherArgs.length < 3) {
      usage("Wrong number of arguments: " + otherArgs.length);
      System.exit(-1);
    }

    // Make sure columns are specified
    String columns[] = conf.getStrings(COLUMNS_CONF_KEY);
    if (columns == null) {
      usage("No columns specified. Please specify with -D" + COLUMNS_CONF_KEY + "=...");
      System.exit(-1);
    }

    // Make sure they specify exactly one column as the row key
    int rowkeysFound = 0;
    for (String col : columns) {
      if (col.equals(ROWKEY_COLUMN_SPEC)) rowkeysFound++;
    }
    if (rowkeysFound != 1) {
      usage("Must specify exactly one column as " + ROWKEY_COLUMN_SPEC);
      System.exit(-1);
    }

    // Make sure one or more columns are specified
    if (columns.length < 2) {
      usage("One or more columns in addition to the row key are required");
      System.exit(-1);
    }

    JobConf jobConf = new JobConf(conf);
    jobConf = createSubmittableJob(jobConf, otherArgs);
    JobClient.runJob(jobConf);
    System.out.println("<< ImportAvro");
    System.exit(0);
  }
}
