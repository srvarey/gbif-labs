package org.gbif.occurrence.cube;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.base.Optional;
import com.urbanairship.datacube.BoxedByteArray;
import com.urbanairship.datacube.DataCube;
import com.urbanairship.datacube.DataCubeIo;
import com.urbanairship.datacube.DbHarness;
import com.urbanairship.datacube.DbHarness.CommitType;
import com.urbanairship.datacube.IdService;
import com.urbanairship.datacube.ReadBuilder;
import com.urbanairship.datacube.SyncLevel;
import com.urbanairship.datacube.WriteBuilder;
import com.urbanairship.datacube.dbharnesses.MapDbHarness;
import com.urbanairship.datacube.idservices.CachingIdService;
import com.urbanairship.datacube.idservices.MapIdService;
import com.urbanairship.datacube.ops.LongOp;
import org.junit.Assert;
import org.junit.Test;


public class CubeTest {

  /**
   * Verify the cube rolls up counts as expected.
   */
  @Test
  public void testCube() {
    IdService idService = new CachingIdService(5, new MapIdService());
    ConcurrentMap<BoxedByteArray, byte[]> backingMap = new ConcurrentHashMap<BoxedByteArray, byte[]>();
    DbHarness<LongOp> dbHarness = new MapDbHarness<LongOp>(backingMap, LongOp.DESERIALIZER, CommitType.READ_COMBINE_CAS, idService);

    DataCube<LongOp> cube = Cube.INSTANCE;
    DataCubeIo<LongOp> cubeIo = new DataCubeIo<LongOp>(cube, dbHarness, 1, Long.MAX_VALUE, SyncLevel.FULL_SYNC);

    try {
      cubeIo.writeSync(new LongOp(1), new WriteBuilder(cube).at(Cube.COUNTRY, "DK").at(Cube.KINGDOM, "Animalia").at(Cube.GEOREFERENCED, false));
      cubeIo.writeSync(new LongOp(1), new WriteBuilder(cube).at(Cube.COUNTRY, "DK").at(Cube.KINGDOM, "Animalia").at(Cube.GEOREFERENCED, true));
      cubeIo.writeSync(new LongOp(1), new WriteBuilder(cube).at(Cube.COUNTRY, "DK").at(Cube.KINGDOM, "Plantae").at(Cube.GEOREFERENCED, true));
      cubeIo.writeSync(new LongOp(1), new WriteBuilder(cube).at(Cube.COUNTRY, "ES").at(Cube.KINGDOM, "Plantae").at(Cube.GEOREFERENCED, false));

      Optional<LongOp> count = cubeIo.get(new ReadBuilder(cube).at(Cube.COUNTRY, "DK"));
      Assert.assertTrue(count.isPresent());
      Assert.assertEquals(3L, count.get().getLong());
      count = cubeIo.get(new ReadBuilder(cube).at(Cube.COUNTRY, "ES"));
      Assert.assertTrue(count.isPresent());
      Assert.assertEquals(1L, count.get().getLong());
      count = cubeIo.get(new ReadBuilder(cube).at(Cube.COUNTRY, "DK").at(Cube.KINGDOM, "Animalia"));
      Assert.assertTrue(count.isPresent());
      Assert.assertEquals(2L, count.get().getLong());
      count = cubeIo.get(new ReadBuilder(cube).at(Cube.COUNTRY, "DK").at(Cube.KINGDOM, "Animalia").at(Cube.GEOREFERENCED, false));
      Assert.assertTrue(count.isPresent());
      Assert.assertEquals(1L, count.get().getLong());
      // Ensure there returns no counts for missing things
      count = cubeIo.get(new ReadBuilder(cube).at(Cube.COUNTRY, "ES").at(Cube.KINGDOM, "Animalia"));
      Assert.assertFalse(count.isPresent());
    } catch (Exception e) {
      Assert.fail(e.getMessage());
    }
  }
}
