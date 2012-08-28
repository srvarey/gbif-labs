/**
 * 
 */
package org.gbif.occurrence.spatial.udf;

import java.util.ArrayList;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.description;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.lazy.LazyDouble;
import org.apache.hadoop.hive.serde2.lazy.LazyInteger;
import org.apache.hadoop.hive.serde2.lazy.LazyString;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.IntWritable;
import org.gbif.occurrence.util.GoogleTileUtil;


/**
 * This UDTF will emit multiple rows from a single input row as follows:
 * 
 * For each of the zoom levels (up to 23), a record is emitted
 * 
 * @author timrobertson
 */
@description(
		  name = "densityTileUDTF",
		  value = "_FUNC_(taxonId, lat, lng, count, zoomLevels, zoomLookAhead)"
		)
public class GoogleDensityTileUDTF extends GenericUDTF {
	
	/**
	 * @see org.apache.hadoop.hive.ql.udf.generic.GenericUDTF#close()
	 */
	@Override
	public void close() throws HiveException {
	}

	/**
	 * @see org.apache.hadoop.hive.ql.udf.generic.GenericUDTF#initialize(org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector[])
	 */
	@Override
	public StructObjectInspector initialize(ObjectInspector[] args) throws UDFArgumentException {
	    if (args.length != 6) {
	    	throw new UDFArgumentException("densityTileUDTF() takes 6 arguments");
	    }		
		ArrayList<String> fieldNames = new ArrayList<String>();
		ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();
		
		fieldNames.add("taxonId");
		fieldNames.add("tileX");
		fieldNames.add("tileY");
		fieldNames.add("zoom");
		fieldNames.add("clusterX");
		fieldNames.add("clusterY");
		fieldNames.add("count");
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaIntObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaIntObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaIntObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaIntObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaIntObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaIntObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaIntObjectInspector);
		return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames,fieldOIs);
	}

	/**
	 * @see org.apache.hadoop.hive.ql.udf.generic.GenericUDTF#process(java.lang.Object[])
	 * (taxonId, lat, lng, count, zoomLevels, zoomLookAhead)"
	 */
	@Override
	public void process(Object[] args) throws HiveException {
		Double lat = extractDouble(args, 1);
		Double lng = extractDouble(args, 2);
		
		// no point in wasting time on records with no coords
		if (lat!=null && lng!=null) {
			Integer taxonId = extractInt(args, 0);
			Integer count = extractInt(args, 3);
			Integer zooms = extractInt(args, 4);
			Integer lookAhead = extractInt(args, 5);

			for (int zoom=0; zoom<zooms; zoom++) {
				Object[] forwardObj = new Object[14];
				forwardObj[0] = taxonId;
				forwardObj[1] = GoogleTileUtil.toTileX(lng, zoom);
				forwardObj[2] = GoogleTileUtil.toTileY(lat, zoom);
				forwardObj[3] = zoom;
				int z = zoom+lookAhead;
				z = z>23 ? 23 : z;
				forwardObj[4] = GoogleTileUtil.toTileX(lng, z);
				forwardObj[5] = GoogleTileUtil.toTileY(lat, z);
				forwardObj[6] = count;
				forward(forwardObj);
			}			
		}
	}

	/**
	 * Utility to get ints
	 */
	private Integer extractInt(Object[] args, int index) {
		Integer i = null;
		try {
			Object o = args[index];
			if (o instanceof LazyInteger) {
				i = ((LazyInteger) o).getWritableObject().get();
			} else if (o instanceof IntWritable) {
				i = ((IntWritable) o).get();
			} else if (o instanceof LazyString) {
				i=Integer.parseInt(((LazyString)o).getWritableObject().toString());
			} else {
				// try and parse it at least
				String s = o.toString();
				i = Integer.parseInt(s);
			}
			
		} catch (RuntimeException e) {			
		}
		return i;
	}
	
	/**
	 * Utility to get doubles
	 */
	private Double extractDouble(Object[] args, int index) {
	  Double d = null;
		try {
			d = ((LazyDouble) args[index]).getWritableObject().get();
		} catch (RuntimeException e) {
		}
		return d;
	}
}