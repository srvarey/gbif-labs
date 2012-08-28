package org.gbif.occurrence.spatial.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.exec.description;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

/**
 * A simple UDF for Hive to project to google tile coordinates.
 */
@description(
  name = "toGoogleTileY",
  value = "_FUNC_(longitude,zoomLevel)"
)
public class GoogleTileYUDF extends UDF {

	IntWritable val = new IntWritable();
	
	public IntWritable evaluate(Text latitude, IntWritable zoomLevel) {
		if (latitude == null || zoomLevel == null) {
			return null;
		}

		try {
			double lat = Double.parseDouble(latitude.toString());
			val.set(toTileY(lat, zoomLevel.get()));
			return val;
		} catch (NumberFormatException e) {
			// swallow bad input ja
			return null;
		}
	}
	
	public int toTileY(double lat, int zoom) {
		lat = 0.5 - ((Math.log(Math.tan((Math.PI / 4) + ((0.5 * Math.PI * lat) / 180))) / Math.PI) / 2.0);
		int scale = 1 << zoom;

		// can just truncate to integer, this looses the fractional "pixel offset"
		return (int) (lat * scale);
	}
}
