package org.gbif.ocurrence.index.solr;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class CSVReducer extends Reducer<LongWritable, Text, LongWritable, Text> {

  @Override
  protected void setup(Context context) throws IOException, InterruptedException {
    super.setup(context);
    SolrRecordWriter.addReducerContext(context);
  }
}
