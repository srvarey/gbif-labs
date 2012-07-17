package org.gbif.occurrence.cube.gmap;

import org.gbif.cube.gmap.density.Cube;
import org.gbif.cube.gmap.density.ops.DensityTileOp;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.urbanairship.datacube.BoxedByteArray;
import com.urbanairship.datacube.DataCube;
import com.urbanairship.datacube.DataCubeIo;
import com.urbanairship.datacube.DbHarness;
import com.urbanairship.datacube.DbHarness.CommitType;
import com.urbanairship.datacube.IdService;
import com.urbanairship.datacube.SyncLevel;
import com.urbanairship.datacube.dbharnesses.MapDbHarness;
import com.urbanairship.datacube.idservices.CachingIdService;
import com.urbanairship.datacube.idservices.MapIdService;
import org.junit.Test;


public class CubeTest {

  /**
   * Verify the cube rolls up counts as expected.
   */
  @Test
  public void testCube() {
    IdService idService = new CachingIdService(5, new MapIdService());
    ConcurrentMap<BoxedByteArray, byte[]> backingMap = new ConcurrentHashMap<BoxedByteArray, byte[]>();
    DbHarness<DensityTileOp> dbHarness =
      new MapDbHarness<DensityTileOp>(backingMap, DensityTileOp.DESERIALIZER, CommitType.READ_COMBINE_CAS, idService);

    DataCube<DensityTileOp> cube = Cube.INSTANCE;
    DataCubeIo<DensityTileOp> cubeIo = new DataCubeIo<DensityTileOp>(cube, dbHarness, 1, Long.MAX_VALUE, SyncLevel.FULL_SYNC);

    /*
     * DensityTileOp op = new DensityTileOp(10.0d, 10.0d, 1, 1);
     * try {
     * cubeIo.writeSync(op, new WriteBuilder(cube).at(Cube.TAXON_ID, 1L).at(Cube.TILE_COORD, new TileCoord(0, 0, 0)));
     * Optional<DensityTileOp> tileOp = cubeIo.get(new ReadBuilder(cube).at(Cube.TAXON_ID, 1L).at(Cube.TILE_COORD, new
     * TileCoord(0, 0, 0)));
     * Assert.assertTrue(tileOp.isPresent());
     * int[][] tile = tileOp.get().getVal();
     * // TODO test
     * for (int[] element : tile) {
     * for (int element2 : element) {
     * System.out.print(element2);
     * }
     * System.out.print("\n");
     * }
     * } catch (Exception e) {
     * Assert.fail(e.getMessage());
     * }
     */
  }
}
