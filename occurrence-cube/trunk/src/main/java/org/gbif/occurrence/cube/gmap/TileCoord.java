package org.gbif.occurrence.cube.gmap;


/**
 * A basic structure to contain the coordinates for a single tile.
 */
class TileCoord {

  private final int zoom;
  private final int x;
  private final int y;

  TileCoord(int zoom, int x, int y) {
    this.zoom = zoom;
    this.x = x;
    this.y = y;
  }

  int getX() {
    return x;
  }

  int getY() {
    return y;
  }

  int getZoom() {
    return zoom;
  }
}
