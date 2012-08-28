/**
 * 
 */
package org.gbif.occurrence.spatial.udf;

import java.util.ArrayList;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.description;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.lazy.LazyFloat;
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
 * For each of the zoom levels (up to 23), and for each identifiable taxon (kingdom, phylum etc)
 * a record is emitted.  Therefore, there is potential for up to 23x8 emissions per input record
 * 
 * @author timrobertson
 */
@description(
		  name = "taxonDensityUDTF",
		  value = "_FUNC_(kingdom_concept_id, phylum_concept_id)"
		)
public class TaxonDensityUDTF extends GenericUDTF {
	
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
	    if (args.length != 12) {
	    	throw new UDFArgumentException("taxonDensityUDTF() takes 12 arguments");
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
	 */
	@Override
	public void process(Object[] args) throws HiveException {
		Float lat = extractFloat(args, 8);
		Float lng = extractFloat(args, 9);
		
		// no point in wasting time on records with no coords
		if (lat!=null && lng!=null) {
			Integer k = extractInt(args, 0);
			Integer p = extractInt(args, 1);
			Integer c = extractInt(args, 2);
			Integer o = extractInt(args, 3);
			Integer f = extractInt(args, 4);
			Integer g = extractInt(args, 5);
			Integer s = extractInt(args, 6);
			Integer nub = extractInt(args, 7);
			Integer count = extractInt(args, 10);
			Integer zooms = extractInt(args, 11);
			for (int zoom=0; zoom<zooms; zoom++) {
				emit(lat, lng, k, zoom, count);
				emit(lat, lng, p, zoom, count);
				emit(lat, lng, c, zoom, count);
				emit(lat, lng, o, zoom, count);
				emit(lat, lng, f, zoom, count);
				emit(lat, lng, g, zoom, count);
				emit(lat, lng, s, zoom, count);
				// only emit nub if it did not equal a higher taxon or species
				if (nub!=null 
						&& !nub.equals(k) && !nub.equals(p) && !nub.equals(c)
						&& !nub.equals(o) && !nub.equals(f) && !nub.equals(g)
						&& !nub.equals(s)) {
					emit(lat, lng, nub, zoom, count);	
				}
			}			
		}
	}

	/**
	 * Utility builder
	 */
	private void emit(Float lat, Float lng, Integer conceptId, int zoom, int count) throws HiveException {
		if (conceptId!=null){
			Object[] forwardObj = new Object[7];
			forwardObj[0] = conceptId;
			forwardObj[1] = GoogleTileUtil.toTileX(lng, zoom);
			forwardObj[2] = GoogleTileUtil.toTileY(lat, zoom);
			forwardObj[3] = zoom;
			zoom = zoom+6;
			zoom = zoom>23 ? 23 : zoom;
			forwardObj[4] = GoogleTileUtil.toTileX(lng, zoom+6);
			forwardObj[5] = GoogleTileUtil.toTileY(lat, zoom+6);
			forwardObj[6] = count;
			forward(forwardObj);
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
	 * Utility to get floats
	 */
	private Float extractFloat(Object[] args, int index) {
		Float f = null;
		try {
			f = ((LazyFloat) args[index]).getWritableObject().get();
		} catch (RuntimeException e) {
		}
		return f;
	}
}