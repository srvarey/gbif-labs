/**
 * 
 */
package org.gbif.occurrence.spatial.tiles;

import java.awt.Color;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * This will render the tiles based on the processed data from the DensityPrepare, AFTER
 * it has been sorted with a Linux> sort command.
 * 
 * @author tim
 */
public class DensityRender {
	// log statements are made occasionally
	protected static final int LOOP_LOG_INTERVAL = 1000000;

	// reuse a pattern for parsing the input lines
	protected static final Pattern tab = Pattern.compile("\t");

	protected static final int tileSize = 256;

	// because Colors are cached internally in Java, we save MUCH time creating them first
	// and stopping their garbage collection (300msec to create a color on macbook pro)
	protected static final Color[] colors = {
		new Color(0xFA, 0xBA, 0x00),
		new Color(0xFA, 0xB0, 0x00),
//		new Color(0xFD, 0x8D, 0x00),
//		new Color(0xF9, 0x7A, 0x00),
		new Color(0xFC, 0x5D, 0x0E),
		new Color(0xFD, 0x3F, 0x16),
		new Color(0xFC, 0x31, 0x17),
//		new Color(0xFE, 0x21, 0x1D),
		new Color(0xFE, 0x09, 0x1F)
	};
 
	// the colors that are actually used (internally, Java will pick up a cached color from the 
	// above cache)
	protected static final int[] colorsAsInt = {
		0xFFFABA00,
		0xFFFAB000,
//		0xFFFD8D00,
//		0xFFF97A00,
		0xFFFC5D0E,
		0xFFFD3F16,
		0xFFFC3117,
//		0xFFFE211D,
		0xFFFE091F
	};	


	/**
	 * @param input The input file that should be already sorted 
	 * @param outputDir To write to
	 * @param xIndex the xIndex
	 * @param yIndex the yIndex
	 * @param countIndex the Count Index
	 * @param zoomLookAhead The number of zooms to "lookahead".  8= 1pixel, 7=2pixel etc
	 * @throws IOException on error writing the files
	 */
	public void generateGoogleTiles(File input, File outputDir, int xIndex, int yIndex, int countIndex, int zoomLookAhead) throws IOException {
		int pixelsPerCell = tileSize / (2<<(zoomLookAhead-1));
		
		BufferedReader reader = new BufferedReader(new FileReader(input));
		
		String line = reader.readLine();
		int lineCount=1;
		
		// prev row values
		int _tileX=-1;
		int _tileY=-1;
		int _x=-1;
		int _y=-1;
		int _count=0;
		File tile = null;
		
		boolean flush = false;
		
		int[] rasterData = new int[tileSize*tileSize];
		
		while (line != null) {
			String[] parts = tab.split(line);
			
			// perhaps do some checks that the row has enough columns?
			int tileX = Integer.parseInt(parts[xIndex]);
			int tileY = Integer.parseInt(parts[yIndex]);
			int x=Integer.parseInt(parts[xIndex+(2*zoomLookAhead)]);
			int y=Integer.parseInt(parts[yIndex+(2*zoomLookAhead)]);
			int count = Integer.parseInt(parts[countIndex]);
			
			if (tileX!=_tileX || tileY!=_tileY) {
				//System.out.println("new tile");
				// new tile
				if (flush) {
					// write the tile
					FileOutputStream fos = new FileOutputStream(tile);
					BufferedImage bufferedImage = new BufferedImage(tileSize, tileSize, Transparency.TRANSLUCENT);
					WritableRaster raster = bufferedImage.getWritableTile(0,0);
					raster.setDataElements(0, 0, tileSize,tileSize, rasterData);
					PngEncoderB encoder = new PngEncoderB(bufferedImage, true, 0, 9);
					byte[] png = encoder.pngEncode();
					fos.write(png);
					fos.flush();
					fos.close();
						
				}
				// set up the new one
				File dir = new File(outputDir.getAbsolutePath() + "/" + tileX);
				dir.mkdirs();
				tile = new File(outputDir.getAbsolutePath() + "/" + tileX + "/" + tileY + ".png");
				rasterData = new int[tileSize*tileSize];
				
				// clear the previous data - we don't care about previous any more
				_x=-1;
				_y=-1;
				_count=0;
			}
				
			// same cell in the tile
			if ((x==_x && y==_y)
					|| (_x==-1 && _y==-1)) {
				_count=_count+count;
			} else if (flush) { // new cell in the same tile, add the last row data
				// get the pixel offset for the X,Y in this tile
				int offsetX = getTileOffset(_x, zoomLookAhead, pixelsPerCell);
				int offsetY = getTileOffset(_y, zoomLookAhead, pixelsPerCell);
				
				//System.out.println("Offset x[" + offsetX + "] y[" +offsetY + "] cnt[" + _count + "]");
				
				// determine the starting draw position
				int cellStart = (offsetY*tileSize)+(offsetX);
				int colorIndex = getColorIndex(_count);
				//System.out.println("colorIndex [" + colorIndex + "]");
				
				// for the number of rows in the cell
//					System.out.println("drawing cells");
	        	for (int i=0; i<pixelsPerCell; i++) {
	        		// System.out.println(" drawing row");
	        		// draw the cells
	        		for (int j=cellStart; j<cellStart+pixelsPerCell; j++) {
	        			 //System.out.println(" drawing column j[" + j +"]");
	        			rasterData[j] = colorsAsInt[colorIndex];
	        		}
	        		cellStart+=tileSize;
	        	}
	        	_count=count;
			} 
				
			// past the first row
			flush = true;

			
			if (lineCount%LOOP_LOG_INTERVAL == 0) {
				System.out.println("Processed " + lineCount + ": " + line);
			}
			
			_tileX=tileX;
			_tileY=tileY;
			_x=x;
			_y=y;
			//_count=count;
			
			
			line = reader.readLine();
			lineCount++;
		}
			
		// TODO - add the last row
		int offsetX = getTileOffset(_x, zoomLookAhead, pixelsPerCell);
		int offsetY = getTileOffset(_y, zoomLookAhead, pixelsPerCell);
		
		//System.out.println("Offset x[" + offsetX + "] y[" +offsetY + "]");
		
		// determine the starting draw position
		int cellStart = (offsetY*tileSize)+(offsetX);
		int colorIndex = getColorIndex(_count);
		
		// for the number of rows in the cell
    	for (int i=0; i<pixelsPerCell; i++) {
    		// System.out.println(" drawing row");
    		// draw the cells
    		for (int j=cellStart; j<cellStart+pixelsPerCell; j++) {
    			// System.out.println(" drawing column j[" + j + "]");
    			rasterData[j] = colorsAsInt[colorIndex];
    		}
    		cellStart+=tileSize;
    	}
		
		//System.out.println("Flushing last");
		// write the tile
		FileOutputStream fos = new FileOutputStream(tile);
		BufferedImage bufferedImage = new BufferedImage(tileSize, tileSize, Transparency.TRANSLUCENT);
		WritableRaster raster = bufferedImage.getWritableTile(0,0);
		raster.setDataElements(0, 0, tileSize,tileSize, rasterData);
		PngEncoderB encoder = new PngEncoderB(bufferedImage, true, 0, 9);
		byte[] png = encoder.pngEncode();
		fos.write(png);
		fos.flush();
		fos.close();
	}

	// works out the pixel offset for the top left corner of the cell
	// within the tile being rendered
	protected static int getTileOffset(int x, int zoomLookAhead, int pixelsPerCell) {
		// System.out.println("2<<" +(zoomLookAhead-1) + "=" +
		// (2<<zoomLookAhead-1));
		int cellIdInTile = x % (2 << zoomLookAhead - 1);
		// System.out.println("cellIdInTile: " + cellIdInTile);
		return cellIdInTile * pixelsPerCell;
	}

	/**
	 * @param count
	 *            to use in lookup
	 * @return the color index to use
	 */
	private static int getColorIndex(int count) {
		int colorIndex = 0;
		if (10 < count && count <= 99) {
			colorIndex = 1;
		} else if (100 <= count && count <= 999) {
			colorIndex = 2;
		} else if (1000 <= count && count <= 9999) {
			colorIndex = 3;
		} else if (10000 <= count && count <= 99999) {
			colorIndex = 4;
		} else if (100000 <= count) {
			colorIndex = 5;
		}
		return colorIndex;
	}

	/**
	 * @param args 0 is the input file, 1 is the output directory
	 */
	public static void main(String[] args) {
		DensityRender me = new DensityRender();
		try {
			
			if (args.length!=2) {
				System.out.println("Usage: DensityRender sourceFile outputDirectory");
				return;
			}
			
			File fin = new File(args[0]);
			File base = new File(args[1]);
				
			long time = System.currentTimeMillis();
			
			System.out.println("Zoom 0");
			File fout = new File(base + "/z0");
			me.generateGoogleTiles(fin, fout, 0,1, 30, 8);
			System.out.println("Zoom 1");
			fout = new File(base + "/z1");
			me.generateGoogleTiles(fin, fout, 2,3, 30, 8);
			fout = new File(base + "/z2");
			System.out.println("Zoom 2");
			me.generateGoogleTiles(fin, fout, 4,5, 30, 8);
			fout = new File(base + "/z3");
			System.out.println("Zoom 3");
			me.generateGoogleTiles(fin, fout, 6,7, 30, 7);
			fout = new File(base + "/z4");
			System.out.println("Zoom 4");
			me.generateGoogleTiles(fin, fout, 8,9, 30, 7);
			fout = new File(base + "/z5");
			System.out.println("Zoom 5");
			me.generateGoogleTiles(fin, fout, 10,11, 30, 6);
			fout = new File(base + "/z6");
			System.out.println("Zoom 6");
			me.generateGoogleTiles(fin, fout, 12,13, 30, 6);
			fout = new File(base + "/z7");
			System.out.println("Zoom 7");
			me.generateGoogleTiles(fin, fout, 14,15, 30, 6);
			fout = new File(base + "/z8");
			System.out.println("Zoom 8");
			me.generateGoogleTiles(fin, fout, 16,17, 30, 6);
			
			System.out.println("Msecs: " + (System.currentTimeMillis() - time));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
