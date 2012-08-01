package org.gbif.cube.gmap.density.backfill;

import org.gbif.cube.gmap.density.io.OccurrenceWritable;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Groups by the occurrence emitting the counts.
 */
public class GroupByOccurrenceReducer extends Reducer<OccurrenceWritable, IntWritable, OccurrenceWritable, IntWritable> {

  /**
   * Groups the counts.
   */
  @Override
  protected void reduce(OccurrenceWritable o, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
    int count = 0;
    Iterator<IntWritable> iter = values.iterator();
    while (iter.hasNext()) {
      iter.next();
      count++;
    }
    context.setStatus("Latitude[" + o.getLatitude() + "], Longitude[" + o.getLongitude() + "] has count[" + count + "]");
    context.write(o, new IntWritable(count));
  }
}
