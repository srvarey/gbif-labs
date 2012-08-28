package org.gbif.occurrence.spatial.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.exec.description;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

/**
 * A simple UDF for Hive to project to google tile coordinates.
 */
@description(
  name = "toGoogleTileX",
  value = "_FUNC_(longitude,zoomLevel)"
)
public class GoogleTileXUDF extends UDF {

	IntWritable val = new IntWritable();
	
	public IntWritable evaluate(Text longitude, IntWritable zoomLevel) {
		if (longitude == null || zoomLevel == null) {
			return null;
		}
		try {
			double lng = Double.parseDouble(longitude.toString());
			val.set(toTileX(lng, zoomLevel.get()));
			return val;
		} catch (NumberFormatException e) {
			// swallow bad input ja
			return null;
		}
	}
	
	public int toTileX(double lng, int zoom) {
		// first convert to Mercator projection
		if (lng > 180) {
			lng -= 360;
		}

		lng /= 360;
		lng += 0.5;

		int scale = 1 << zoom;

		// can just truncate to integer, this looses the fractional "pixel offset"
		return (int) (lng * scale);
		
	}
}
