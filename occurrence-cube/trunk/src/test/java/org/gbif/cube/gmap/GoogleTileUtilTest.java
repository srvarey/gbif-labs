package org.gbif.cube.gmap;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class GoogleTileUtilTest {

  @Test
  public void testGetOffset() {
    assertEquals(92, GoogleTileUtil.getOffsetY(45d, -90d, 0));
    assertEquals(128, GoogleTileUtil.getOffsetX(0d, 0d, 0));
    assertEquals(128, GoogleTileUtil.getOffsetY(0d, 0d, 0));
    assertEquals(0, GoogleTileUtil.getOffsetX(0d, 0d, 1));
    assertEquals(0, GoogleTileUtil.getOffsetY(0d, 0d, 1));
    assertEquals(0, GoogleTileUtil.getOffsetX(0d, 0d, 2));
    assertEquals(0, GoogleTileUtil.getOffsetY(0d, 0d, 2));

    // Canberra, AU
    double lng = 149.1;
    double lat = -35.2;
    assertEquals(0, GoogleTileUtil.toTileX(lng, 0));
    assertEquals(1, GoogleTileUtil.toTileX(lng, 1));
    assertEquals(3, GoogleTileUtil.toTileX(lng, 2));
    assertEquals(0, GoogleTileUtil.toTileY(lat, 0));
    assertEquals(1, GoogleTileUtil.toTileY(lat, 1));
    assertEquals(2, GoogleTileUtil.toTileY(lat, 2));
  }
}
