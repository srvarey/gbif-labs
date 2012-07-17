package org.gbif.cube.gmap.density.model.test;

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;


public class SerdeTest {

  /**
   * @param args
   */
  public static void main(String[] args) {
    Tile tile = new Tile();
    Map<CharSequence, Integer> cells = Maps.newHashMap();
    tile.setCells(cells);
    for (int x = 0; x < 64; x++) {
      for (int y = 0; y < 32; y++) {
        cells.put(String.valueOf((x * 64) + y), 1);
      }
    }

    try {
      byte[] b = TestSerDeUtils.encodeTile(tile);
      System.out.println(b.length);


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
