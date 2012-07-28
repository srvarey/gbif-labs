package org.gbif.cube.gmap.io;

/**
 * For indicating the source of content for a tile.
 * Be aware that calling hashCode() on an ENUM is not consistent across JVMs!
 */
public enum TileContentType {
  ALL(0), TAXON(1), DATASET(2), PUBLISHER(3), COUNTRY(4), NETWORK(5);

  private int id;

  TileContentType(int id) {
    this.id = id;
  }

  public static TileContentType INSTANCE(int id) {
    switch (id) {
      case 1:
        return TAXON;
      case 2:
        return DATASET;
      default:
        return ALL;
    }
  }

  public int getId() {
    return id;
  }
}
