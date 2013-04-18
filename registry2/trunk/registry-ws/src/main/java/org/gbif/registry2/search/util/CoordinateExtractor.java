package org.gbif.registry2.search.util;

import org.gbif.api.model.registry.geospatial.BoundingBox;
import org.gbif.api.model.registry.geospatial.GeospatialCoverage;
import org.gbif.registry2.search.SolrAnnotatedDataset;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for extracting and manipulating coordinates.
 */
public class CoordinateExtractor {

  public static List<List<Double>> extractCoordinates(GeospatialCoverage coverage) {
    List<GeospatialCoverage> gcs = new ArrayList<GeospatialCoverage>(1);
    gcs.add(coverage);
    return extractCoordinates(gcs);
  }

  public static List<List<Double>> extractCoordinates(List<GeospatialCoverage> coverages) {
    List<List<Double>> coordList = new ArrayList<List<Double>>();
    if (coverages != null && !coverages.isEmpty()) {
      List<Double> north = new ArrayList<Double>();
      List<Double> east = new ArrayList<Double>();
      List<Double> south = new ArrayList<Double>();
      List<Double> west = new ArrayList<Double>();

      for (GeospatialCoverage coverage : coverages) {
        BoundingBox bb = coverage.getBoundingBox();
        if (bb != null) {
          north.add(bb.getMaxLatitude());
          south.add(bb.getMinLatitude());
          east.add(bb.getMaxLongitude());
          west.add(bb.getMinLongitude());
        }
      }

      coordList.add(north);
      coordList.add(east);
      coordList.add(south);
      coordList.add(west);
    }

    return coordList;
  }

  public static void populateCoordinates(SolrAnnotatedDataset target, List<GeospatialCoverage> coverages) {
    List<List<Double>> coordList = extractCoordinates(coverages);
    if (!coordList.isEmpty()) {
      target.setNorthBoundingCoordinates(coordList.get(0));
      target.setEastBoundingCoordinates(coordList.get(1));
      target.setSouthBoundingCoordinates(coordList.get(2));
      target.setWestBoundingCoordinates(coordList.get(3));
    }
  }

  /**
   * Empty hidden constructor.
   */
  private CoordinateExtractor() {
    // empty block
  }
}
