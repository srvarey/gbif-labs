package org.gbif.cube.gmap.density.model;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class DensityTileTest {

  @Test
  public void cellId() {
    assertEquals(10, DensityTile.toCellId(129, 129, 64));
    assertEquals(36, DensityTile.toCellId(129, 129, 32));
  }

  /**
   * Simple API illustration
   */
  @Test
  public void testCell() {

    DensityTile tile = DensityTile.builder(0, 0, 0, 64) // Z, X, Y, ClusterSize
      .collect(-0.1d, 0.1d, 1) // lat, lng, count
      .collect(-0.1d, 0.1d, 2).build();

    assertEquals(3, tile.cell(2, 2));
    assertEquals(0, tile.cell(0, 0));
    assertEquals(1, tile.cells().size());

    try {
      byte[] b = tile.serialize();
      tile = DensityTile.DESERIALIZE(b);
      assertEquals(3, tile.cell(2, 2));
      assertEquals(0, tile.cell(0, 0));
      assertEquals(1, tile.cells().size());
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

}
