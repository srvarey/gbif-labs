package org.gbif.cube.gmap.density.guice;

import org.gbif.cube.gmap.density.DensityCube;
import org.gbif.cube.gmap.density.DensityTile;
import org.gbif.cube.gmap.density.backfill.Backfill;

import java.io.IOException;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.urbanairship.datacube.DataCubeIo;
import com.urbanairship.datacube.DbHarness;
import com.urbanairship.datacube.DbHarness.CommitType;
import com.urbanairship.datacube.IdService;
import com.urbanairship.datacube.SyncLevel;
import com.urbanairship.datacube.dbharnesses.HBaseDbHarness;
import com.urbanairship.datacube.idservices.HBaseIdService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.util.Bytes;


/**
 * Sets up the CubeIO.
 */
public class DensityCubeHBaseModule extends AbstractModule {

  private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
  private final DataCubeIo<DensityTile> dataCubeIo;

  public DensityCubeHBaseModule(Properties props) throws IOException {
    Configuration conf = HBaseConfiguration.create();
    // TODO: rethink how we do this generically
    byte[] cubeTable = propertyAsBytes(props, Backfill.KEY_CUBE_TABLE);
    byte[] counterTable = propertyAsBytes(props, Backfill.KEY_COUNTER_TABLE);
    byte[] lookupTable = propertyAsBytes(props, Backfill.KEY_LOOKUP_TABLE);
    byte[] cf = propertyAsBytes(props, Backfill.KEY_CF);
    int writeBatchSize = propertyAsInt(props, Backfill.KEY_WRITE_BATCH_SIZE);


    HTablePool pool = new HTablePool();

    IdService idService = new HBaseIdService(conf, lookupTable, counterTable, cf, EMPTY_BYTE_ARRAY);

    DbHarness<DensityTile> hbaseDbHarness =
      new HBaseDbHarness<DensityTile>(pool, EMPTY_BYTE_ARRAY, cubeTable, cf, DensityTile.DESERIALIZER, idService, CommitType.READ_COMBINE_CAS);

    dataCubeIo = new DataCubeIo<DensityTile>(DensityCube.INSTANCE, hbaseDbHarness, writeBatchSize, Long.MAX_VALUE, SyncLevel.BATCH_ASYNC);
  }

  @Override
  protected void configure() {
  }

  @Singleton
  @Provides
  public DataCubeIo<DensityTile> getCubeIO() throws IOException {
    return dataCubeIo;
  }

  private byte[] propertyAsBytes(Properties p, String key) {
    String v = p.getProperty(key);
    if (v != null) {
      return Bytes.toBytes(v);
    } else {
      throw new IllegalArgumentException("Missing property for " + key);
    }
  }

  private int propertyAsInt(Properties p, String key) {
    String v = p.getProperty(key);
    if (v != null) {
      try {
        return Integer.parseInt(v);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid value[" + v + "] supplied for " + key);
      }
    } else {
      throw new IllegalArgumentException("Missing property for " + key);
    }
  }

}
