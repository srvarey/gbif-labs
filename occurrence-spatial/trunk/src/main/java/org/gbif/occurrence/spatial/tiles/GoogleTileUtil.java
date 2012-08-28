/**
 * 
 */
package org.gbif.occurrence.spatial.tiles;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author tim
 *
 */
public class GoogleTileUtil {

	   /**
	    * returns the lat/lng as an "Offset Normalized Mercator" pixel coordinate,
	    * this is a coordinate that runs from 0..1 in latitude and longitude with 0,0 being
	    * top left. Normalizing means that this routine can be used at any zoom level and
	    * then multiplied by a power of two to get actual pixel coordinates.
	    * @param lat in degrees
	    * @param lng in degrees
	    * @return
	    */
	public static Point2D toNormalisedPixelCoords(double lat, double lng) {
	   // first convert to Mercator projection
	   // first convert the lat lon to mercator coordintes.
	   if (lng > 180) {
	      lng -= 360;
	   }

	   lng /= 360;
	   lng += 0.5;

	   lat = 0.5 - ((Math.log(Math.tan((Math.PI / 4) + ((0.5 * Math.PI * lat) / 180))) / Math.PI) / 2.0);

	   return new Point2D.Double(lng, lat);
	}
	
	
	public static int toTileX(double lng, int zoom) {
		if (lng > 180) {
			lng -= 360;
		}

		lng /= 360;
		lng += 0.5;
		int scale = 1 << zoom;
		return (int)(lng * scale);
	}
	
	public static int toTileY(double lat, int zoom) {
		lat = 0.5 - ((Math.log(Math.tan((Math.PI / 4) + ((0.5 * Math.PI * lat) / 180))) / Math.PI) / 2.0);
		int scale = 1 << zoom;
		return (int)(lat * scale);
	}
	
	/*
	public static Point toLatLng(int x, int y, int zoom) {
		int tilesAtThisZoom = 1 << (17 - zoom);
		double lngWidth = 360.0 / tilesAtThisZoom; // width in degrees longitude
		double lng = -180 + (x * lngWidth); // left edge in degrees longitude

		double latHeightMerc = 1.0 / tilesAtThisZoom; // height in "normalized" mercator 0,0 top left
		double topLatMerc = y * latHeightMerc; // top edge in "normalized"
												// mercator 0,0 top left
		double bottomLatMerc = topLatMerc + latHeightMerc;

		// convert top and bottom lat in mercator to degrees
		// note that in fact the coordinates go from about -85 to +85 not -90 to
		// 90!
		double bottomLat = Math.toDegrees((2 * Math.atan(Math.exp(Math.PI
				* (1 - (2 * bottomLatMerc)))))
				- (Math.PI / 2));

		double topLat = Math.toDegrees((2 * Math.atan(Math.exp(Math.PI
				* (1 - (2 * topLatMerc)))))
				- (Math.PI / 2));

		double latHeight = topLat - bottomLat;

		return new Point(lng, lat);
	}*/

	/**
	 * returns a point that is a google tile reference for the tile containing
	 * the lat/lng and at the zoom level.
	 * 
	 * @param lat
	 * @param lng
	 * @param zoom
	 * @return
	 */
	public static Point toTileXY(double lat, double lng, int zoom) {
	   Point2D normalised = toNormalisedPixelCoords(lat, lng);
	   int scale = 1 << zoom;

	   // can just truncate to integer, this looses the fractional "pixel offset"
	   return new Point((int) (normalised.getX() * scale), (int) (normalised.getY() * scale));
	}   
	
	public static void main(String[] args) {
//		System.out.println(toTileXY(42.7882, -73.0796, 0));
//		System.out.println(toTileXY(42.7882, -73.0796, 1));
//		System.out.println(toTileXY(42.7882, -73.0796, 2));
//		System.out.println(toTileXY(42.7882, -73.0796, 3));
//		System.out.println(toTileXY(42.7882, -73.0796, 4));
//		System.out.println(toTileXY(42.7882, -73.0796, 5));
		
		int zoom = 3;
		int scale = 1 << zoom;
		int tileSize=256;
		double x = toNormalisedPixelCoords(0,0).getX();
		double y = toNormalisedPixelCoords(0,0).getY();
		x*=scale*tileSize;
		y*=scale*tileSize;
		System.out.println(x + ":" + y);
	}
	
	
	 /**
	   * returns a Rectangle2D with x = lon, y = lat, width=lonSpan, height=latSpan
	   * for an x,y,zoom as used by google.
	   */
	   public static Rectangle2D.Double getTileRect(int x, int y, int zoom) {
	      int tilesAtThisZoom = 1 << (17 - zoom);
	      double lngWidth     = 360.0 / tilesAtThisZoom; // width in degrees longitude
	      double lng          = -180 + (x * lngWidth); // left edge in degrees longitude

	      double latHeightMerc = 1.0 / tilesAtThisZoom; // height in "normalized" mercator 0,0 top left
	      double topLatMerc    = y * latHeightMerc; // top edge in "normalized" mercator 0,0 top left
	      double bottomLatMerc = topLatMerc + latHeightMerc;

	      // convert top and bottom lat in mercator to degrees
	      // note that in fact the coordinates go from about -85 to +85 not -90 to 90!
	      double bottomLat = Math.toDegrees((2 * Math.atan(Math.exp(Math.PI * (1 - (2 * bottomLatMerc)))))
	                         - (Math.PI / 2));

	      double topLat = Math.toDegrees((2 * Math.atan(Math.exp(Math.PI * (1 - (2 * topLatMerc))))) - (Math.PI / 2));

	      double latHeight = topLat - bottomLat;

	      return new Rectangle2D.Double(lng, bottomLat, lngWidth, latHeight);
	   }	
}
