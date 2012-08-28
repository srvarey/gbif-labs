/**
 * 
 */
package org.gbif.occurrence.spatial.wdpa;

import gnu.trove.THashMap;
import gnu.trove.TIntProcedure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.gbif.occurrence.spatial.polygon.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.SpatialIndex;

/**
 * @author timrobertson
 *
 */
public class IntersectTest {
	protected static Logger LOG = LoggerFactory.getLogger(IntersectTest.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File pa = new File(args[0]);
		File latLng = new File(args[1]);
		
		try {
			FileWriter fw = new FileWriter(args[2]);
			LOG.info("Building indexes");
			Map<Integer, String> idToPaId = new THashMap();
			Map<Integer, Polygon> idToGeom = new THashMap();
			SpatialIndex index = ProtectedAreaIndexBuilder.build(pa, idToPaId, idToGeom);
			
			LOG.info("Checking the point in polygons");
			BufferedReader r= new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(latLng))));
			Pattern tab = Pattern.compile("\t");
			String line = r.readLine();
			long total=0;
			long time = System.currentTimeMillis();
			Diagnostics d = new Diagnostics();
			d.idToGeom = idToGeom;
			d.fw=fw;
			
			while (line!=null) {
				String[] atoms = tab.split(line);
				total++;
				try {
					Point p = new Point(Float.parseFloat(atoms[1]), Float.parseFloat(atoms[0]));
					d.p = new org.gbif.occurrence.spatial.polygon.Point(p.x, p.y);
					index.nearest(p, d, 10f);
	
				} catch (RuntimeException e) {
					//e.printStackTrace();
				}
				
				if (total%1000==0) {
					LOG.info("Found[{}] candidates[{}] from points[{}] in {} msecs", new Object[] {d.found, d.candidate, total, (System.currentTimeMillis() - time)});
				}
				
				line = r.readLine();
			}
			LOG.info("Found[{}] candidates[{}] from points[{}] in {} msecs", new Object[] {d.found, d.candidate, total, (System.currentTimeMillis() -time)});
						
			fw.close();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	static class Diagnostics implements TIntProcedure {
		public int candidate=0;
		public int found=0;
		public Map<Integer, Polygon> idToGeom;
		public FileWriter fw;
		public org.gbif.occurrence.spatial.polygon.Point p;
		@Override
		public boolean execute(int id) {
			candidate++;
			Polygon geom = idToGeom.get(id);
			if (geom.contains(p)) {
				found++;
				try {
					fw.write(p.y + "\t" + p.x + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			return false;
		}
		
	}
}
