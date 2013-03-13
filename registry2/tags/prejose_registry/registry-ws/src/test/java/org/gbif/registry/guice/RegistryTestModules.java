package org.gbif.registry.guice;

import org.gbif.registry.grizzly.RegistryServer;
import org.gbif.registry.persistence.guice.RegistryMyBatisModule;
import org.gbif.registry.ws.client.guice.RegistryWsClientModule;
import org.gbif.registry.ws.resources.NodeResource;
import org.gbif.registry.ws.resources.OrganizationResource;

import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Properties;

import javax.sql.DataSource;

import com.google.common.base.Throwables;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.jolbox.bonecp.BoneCPDataSource;
import org.apache.bval.guice.ValidationModule;
import org.apache.ibatis.io.Resources;

/**
 * Utility to provide the different Guice configurations for:
 * <ol>
 * <li>The WS service layer</li>
 * <li>The WS service client layer</li>
 * <li>A management configuration to allow utilities to manipulate the database (Liquibase etc)</li>
 * </ol>
 */
public class RegistryTestModules {

  /**
   * @return An injector that is bound for the webservice layer.
   */
  public static Injector webservice() {
    try {
      Properties p = new Properties();
      p.load(Resources.getResourceAsStream("registry-test.properties"));
      return Guice.createInjector(
        new AbstractModule() {

          @Override
          protected void configure() {
            bind(NodeResource.class);
            bind(OrganizationResource.class);
          }
        }, new RegistryMyBatisModule(p), new ValidationModule());
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  /**
   * @return An injector that is bound for the webservice client layer.
   */
  public static Injector webserviceClient() {
    Properties props = new Properties();
    props.put("registry.ws.url", "http://localhost:" + RegistryServer.getPort());
    return Guice.createInjector(new RegistryWsClientModule(props));
  }

  /**
   * @return A datasource that is for use in management activities such as Liquibase, or cleaning between tests.
   */
  public static DataSource database() {
    return RegistryTestModules.management().getInstance(DataSource.class);
  }


  /**
   * @return An injector configured to issue a Datasource suitable for database management activities (Liquibase etc).
   */
  private static Injector management() {
    try {
      final Properties p = new Properties();
      p.load(Resources.getResourceAsStream("registry-test.properties"));
      return Guice.createInjector(new AbstractModule() {

        @Override
        protected void configure() {
          Names.bindProperties(binder(), p);
          bind(DataSource.class).toProvider(ManagementProvider.class);
        }
      });
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  /**
   * Provides a datasource that can issue connections for management activities, such as Liquibase or
   * clearing tables before tests run etc.
   */
  @Singleton
  public static class ManagementProvider implements Provider<DataSource> {

    // Limit to a single (reusable) connection
    public static final int PARTITION_COUNT = 1;
    public static final int POOL_SIZE_PER_PARTITION = 1;
    private final String url;
    private final String username;
    private final String password;

    @Inject
    public ManagementProvider(@Named("registry.db.JDBC.driver") String driver,
      @Named("registry.db.JDBC.url") String url,
      @Named("registry.db.JDBC.username") String username, @Named("registry.db.JDBC.password") String password) {
      this.url = url;
      this.username = username;
      this.password = password;
      try {
        DriverManager.registerDriver((Driver) Class.forName(driver).newInstance());
      } catch (Exception e) {
        Throwables.propagate(e);
      }

    }

    @Override
    public DataSource get() {
      BoneCPDataSource ds = new BoneCPDataSource();
      ds.setJdbcUrl(url);
      ds.setUsername(username);
      ds.setPassword(password);
      ds.setMaxConnectionsPerPartition(PARTITION_COUNT);
      ds.setPartitionCount(POOL_SIZE_PER_PARTITION);
      return ds;
    }
  }
}
