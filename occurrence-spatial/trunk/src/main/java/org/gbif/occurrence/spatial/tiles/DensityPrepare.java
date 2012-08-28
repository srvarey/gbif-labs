/**
 * 
 */
package org.gbif.occurrence.spatial.tiles;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * The first stage of the tile renderer for density layer tiles.
 * The input is a tab delimited file that contains somewhere lat, lng and optionally the count 
 * and the output will be a tab file which details enough informaiton to determine the pixels
 * to render on each zoom level. 
 * 
 * @author tim
 */
public class DensityPrepare {
	// log statements are made occasionally  
	protected static final int LOOP_LOG_INTERVAL = 100000;
	
	// reuse a pattern for parsing the input lines
	protected static final Pattern tab = Pattern.compile("\t");
	
	/**
	 * 
	 * @param input Source data
	 * @param output Output directory
	 * @param latIndex The index in the source data representing the latitude
	 * @param lngIndex The index in the source data representing the longitude
	 * @param countIndex The index in the source data representing the count or null for 1 per line
	 * @param numberZooms To process to inclusive
	 * @throws IOException If the output file cannot be written to
	 */
	public int generateGoogleIds(File input, File output, int latIndex, int lngIndex, Integer countIndex, int numberZooms) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(input));
		FileWriter writer = new FileWriter(output);
		int recordCount=0;
		try {
			String line = reader.readLine();
			int lineCount=1;
			
			while (line != null) {
				String[] parts = tab.split(line);
				
				if (parts.length<latIndex
						|| parts.length<lngIndex
						|| (countIndex!=null && parts.length<countIndex)) {
					System.err.println("Invalid number of parts on line ");
				} else {
					try {
						String latString = parts[latIndex];
						String lngString = parts[lngIndex];
						int count = 1;
						if (countIndex!=null) {
							count = Integer.parseInt(parts[countIndex]);	
						}
						
						
						BigDecimal lat = null;
						if (isNotNull(latString)) {
							lat = new BigDecimal(latString);
						}
						BigDecimal lng = null;
						if (isNotNull(lngString)) {
							lng = new BigDecimal(lngString);
						}
						
						// check all values are accounted for
						if (lat!=null && lng!=null && count>0) {
							
							// only deal with things in range
							if (Math.floor(lat.doubleValue())>-85 && Math.ceil(lat.doubleValue())<85
									&& lng.intValue()>=-180 && lng.intValue()<180) {
								
								// write the intermediate output file
								// this is:
								// z0_x z0_y z1_x z1_y... count
								for (int i=0; i<=numberZooms; i++) {
									Point p = GoogleTileUtil.toTileXY(lat.doubleValue(), lng.doubleValue(), i);
									//writer.write(lat + "\t" +lng + "\t" + p.x + "\t" + p.y + "\t");
									writer.write(p.x + "\t" + p.y + "\t");
								}
								writer.write(count + "\n");
								
								recordCount++;
							}
						}
						
					} catch (NumberFormatException e) {
						System.out.println(lineCount + " has bad data: " + line);
					}
				}
				
				if (lineCount%LOOP_LOG_INTERVAL == 0) {
					System.out.println("Processed " + lineCount + ": " + line);
				}
				
				line = reader.readLine();
				lineCount++;
			}
			
		} finally {
			reader.close();
			writer.close();
		}
		return recordCount;
	}
	
	/**
	 * @param s To check
	 * @return true if it is not apparently null
	 */
	private boolean isNotNull(String s) {
		if (s!=null 
				&& !"NULL".equalsIgnoreCase(s)
				&& !"\\N".equalsIgnoreCase(s)
				&& !"\\\\N".equalsIgnoreCase(s)
				&& !"".equalsIgnoreCase(s)
				&& !" ".equalsIgnoreCase(s)) {
				return true;
		} else {
			return false;	
		}
	}

	/**
	 * @param args 
	 */
	public static void main(String[] args) {
		
		if (! (args.length == 5 || args.length == 6) ) {
			System.out.println("Usage: DensityPrepare sourceFile outputFile latitudeIndex longitudeIndex numberOfZooms countIndex (optional)");
			return;
		}
		
		// 9 is lat, 10 is lng
		DensityPrepare me = new DensityPrepare();
		try {
			File fin = new File(args[0]);
			File fgoogle = new File(args[1]);
			if (!fgoogle.exists()) {
				new File(fgoogle.getParent()).mkdirs();
			}
			
			int lat = Integer.parseInt(args[2]);
			int lng = Integer.parseInt(args[3]);
			int zooms= Integer.parseInt(args[4]);
			Integer count = null;
			if (args.length == 6) {
				count = Integer.parseInt(args[5]);
			}
			
			
			int recordCount = me.generateGoogleIds(fin,fgoogle,lat, lng, count, zooms);
			System.out.println("Records with data: " + recordCount);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
