/**
 * 
 */
package org.gbif.occurrence.spatial.wdpa;

import gnu.trove.THashMap;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.digester.Digester;
import org.gbif.occurrence.spatial.polygon.Point;
import org.gbif.occurrence.spatial.polygon.Polygon;
import org.gbif.occurrence.util.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.SpatialIndex;
import com.infomatiq.jsi.rtree.RTree;



/**
 * Builds an index of the protectedareas using the CSV download format from 
 * http://protectedplanet
 * @author timrobertson
 */
public class ProtectedAreaIndexBuilder {
	protected static Logger LOG = LoggerFactory.getLogger(ProtectedAreaIndexBuilder.class);

	/**
	 * @param f The input data in the form
	 * wdpaid,objectid,wdpa_pid,country,name,orig_name,sub_loc,desig,desig_eng,desig_type,iucncat,marine,rep_m_area,rep_area,status,status_yr,gov_type,mang_auth,int_crit,mang_plan,official,is_point,kml
	 */
	public static SpatialIndex build(File f, Map<Integer, String> idToPaId, Map<Integer, Polygon> idToGeom) throws IOException {
		SpatialIndex index = new RTree();
		index.init(new Properties());
		CSVReader csv = new CSVReader(f, "UTF-8", ",", '"', 1);
		long time = System.currentTimeMillis();
		try {
			int count=0;
			
			// reuse patterns for performance
			Pattern space = Pattern.compile(" ");
			Pattern comma = Pattern.compile(",");
			
			while (csv.hasNext()) {
				String[] atoms = csv.next();
				count++;
				
				// bad lines are in there.  catch the worst up front
				if (atoms.length!=23) {
					LOG.warn("Incorrect number of terms[found "+ atoms.length+" expected "+ 23 +"] on line["+ count + "]. Start of line:" + atoms[0]);
					continue;
				}
				
				// we ignore any PAs that are points
				if ("1".equalsIgnoreCase(atoms[21])
						|| "Y".equalsIgnoreCase(atoms[21])
						|| "YES".equalsIgnoreCase(atoms[21])
						|| "T".equalsIgnoreCase(atoms[21])
						|| "TRUE".equalsIgnoreCase(atoms[21])) {
					continue;
				}
				
				// TODO should we use wdpaId or objectId - craig.mills@unep-wcmc.org has been asked - awaiting reply
				String wdpaId = atoms[0];
				String kml = atoms[22];
				
				try {
					// pull out the coordinates from the KML
					Digester digester = new Digester();
					digester.addObjectCreate("MultiGeometry/Polygon/outerBoundaryIs/LinearRing/coordinates", StringBuffer.class);
					digester.addCallMethod("MultiGeometry/Polygon/outerBoundaryIs/LinearRing/coordinates", "append", 1);
					digester.addCallParam("MultiGeometry/Polygon/outerBoundaryIs/LinearRing/coordinates", 0);					
					StringBuffer coords = (StringBuffer) digester.parse(new StringReader(kml));
					
					String[] vertices = space.split(coords);
					Point[] points = new Point[vertices.length];
					int vertex = 0;
					for (String v : vertices) {
						String[] lngLat = comma.split(v);
						if (lngLat.length==2) {
							points[vertex] = new Point(Double.parseDouble(lngLat[0]), Double.parseDouble(lngLat[1]));
						}
						vertex++;
					}
					
					// create the geometry, add it to the index
					Polygon polygon = new Polygon(points);
					index.add(new Rectangle(new Float(polygon.minx),new Float(polygon.miny),new Float(polygon.maxx),new Float(polygon.maxy)), count);
					idToGeom.put(count, polygon);
					idToPaId.put(count, wdpaId);
					
					
				} catch (SAXException e) {
					LOG.warn("Bad KML or parsing issue on line " + count + ": "+ kml);
					continue;
				}
				
				
				
				// keep a running LOG
				if (count%1000 == 0) {
					LOG.debug("Read " + count + " input lines in " + (System.currentTimeMillis()-time) + " msecs");
				}
			}
		} finally {
			csv.close();
		}
		return index;
	}
	
	
	/**
	 * @param args Takes the CSV file as an input
	 */
	public static void main(String[] args) {
		File f = new File(args[0]);
		try {
			Map<Integer, String> idToPaId = new THashMap();
			Map<Integer, Polygon> idToGeom = new THashMap();
			SpatialIndex index = ProtectedAreaIndexBuilder.build(f, idToPaId, idToGeom);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

}
