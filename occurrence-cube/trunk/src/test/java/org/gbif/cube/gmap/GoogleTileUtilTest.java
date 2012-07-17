package org.gbif.cube.gmap;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class GoogleTileUtilTest {

  @Test
  public void testGetOffset() {
    assertEquals(128, GoogleTileUtil.getOffsetX(0d, 0d, 0));
    assertEquals(128, GoogleTileUtil.getOffsetY(0d, 0d, 0));
    assertEquals(0, GoogleTileUtil.getOffsetX(0d, 0d, 1));
    assertEquals(0, GoogleTileUtil.getOffsetY(0d, 0d, 1));
    assertEquals(0, GoogleTileUtil.getOffsetX(0d, 0d, 2));
    assertEquals(0, GoogleTileUtil.getOffsetY(0d, 0d, 2));
  }
}
