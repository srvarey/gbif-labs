/**
 * 
 */
package org.gbif.occurrence.spatial.udf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

/**
 * This UDTF will emit multiple rows from a single input row as follows:
 * 
 * For each identifiable taxon (kingdom, phylum etc) a record is emitted.  
 * 
 * @author timrobertson
 */
@description(
		  name = "taxonIdUDTF",
		  value = "_FUNC_(k, p, c, o, f, g, s, nub, lat, lng, count)"
		)
public class TaxonIdUDTF extends GenericUDTF {
	
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
	    if (args.length != 11) {
	    	throw new UDFArgumentException("taxonIdUDTF() takes 11 arguments");
	    }		
		ArrayList<String> fieldNames = new ArrayList<String>();
		ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();
		fieldNames.add("taxonId");
		fieldNames.add("lat");
		fieldNames.add("lng");
		fieldNames.add("count");
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaIntObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaDoubleObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaDoubleObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaIntObjectInspector);
		return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames,fieldOIs);
	}

	/**
	 * @see org.apache.hadoop.hive.ql.udf.generic.GenericUDTF#process(java.lang.Object[])
	 */
	@Override
	public void process(Object[] args) throws HiveException {
    Double lat = extractDouble(args, 8);
    Double lng = extractDouble(args, 9);
    if (lat!=null && lng!=null) {
      Set<Integer> identifications = new HashSet<Integer>();
      identifications.add(extractInt(args, 0)); // k
      identifications.add(extractInt(args, 1)); // p
      identifications.add(extractInt(args, 2)); // c
      identifications.add(extractInt(args, 3)); // o
      identifications.add(extractInt(args, 4)); // f
      identifications.add(extractInt(args, 5)); // g
      identifications.add(extractInt(args, 6)); // d
      identifications.add(extractInt(args, 7)); // nub
      
      Integer count = extractInt(args, 10);
		
      // emit a row for each identification
		  for (Integer id : identifications) {
		    if (id != null){
		      Object[] forwardObj = new Object[4];
		      forwardObj[0] = id;
		      forwardObj[1] = lat;
		      forwardObj[2] = lng;
		      forwardObj[3] = count;
		      forward(forwardObj);
		    }
		    
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