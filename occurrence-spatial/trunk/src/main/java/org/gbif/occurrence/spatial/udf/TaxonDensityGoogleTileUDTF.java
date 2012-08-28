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
 * For each of the zoom levels (up to 23),a record is emitted
 * 
 * @author timrobertson
 */
@description(
		  name = "taxonDensityUDTF2",
		  value = "_FUNC_(kingdom_concept_id, phylum_concept_id)"
		)
public class TaxonDensityGoogleTileUDTF extends GenericUDTF {
	
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
	    	throw new UDFArgumentException("taxonDensityUDTF2() takes 12 arguments");
	    }		
		ArrayList<String> fieldNames = new ArrayList<String>();
		ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();
		
		fieldNames.add("kingdom_concept_id");
		fieldNames.add("phylum_concept_id");
		fieldNames.add("class_concept_id");
		fieldNames.add("order_concept_id");
		fieldNames.add("family_concept_id");
		fieldNames.add("genus_concept_id");
		fieldNames.add("species_concept_id");
		fieldNames.add("nub_concept_id");		
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
				Object[] forwardObj = new Object[14];
				forwardObj[0] = k;
				forwardObj[1] = p;
				forwardObj[2] = c;
				forwardObj[3] = o;
				forwardObj[4] = f;
				forwardObj[5] = g;
				forwardObj[6] = s;
				forwardObj[7] = nub;
				forwardObj[8] = GoogleTileUtil.toTileX(lng, zoom);
				forwardObj[9] = GoogleTileUtil.toTileY(lat, zoom);
				forwardObj[10] = zoom;
				int z = zoom+6;
				z = z>23 ? 23 : z;
				forwardObj[11] = GoogleTileUtil.toTileX(lng, z);
				forwardObj[12] = GoogleTileUtil.toTileY(lat, z);
				forwardObj[13] = count;
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