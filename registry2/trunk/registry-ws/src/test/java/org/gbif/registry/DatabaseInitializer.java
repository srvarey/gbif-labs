package org.gbif.registry;

import org.gbif.registry.persistence.guice.RegistryServiceMyBatisModule;
import org.gbif.test.DatabaseDrivenTestRule;
import org.gbif.utils.file.properties.PropertiesUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

/**
 * Provides a JUnit {@link org.junit.rules.TestRule} to allow database driven integration tests in registry. This will
 * simply initialize an empty database using liquibase before each test.
 * See {@link NetworkEntityTest} to see how to use this class.
 */
class DatabaseInitializer<T> extends DatabaseDrivenTestRule<T> {

  public DatabaseInitializer() {
    super(
      new RegistryServiceMyBatisModule(loadProperties()),
      RegistryServiceMyBatisModule.InternalRegistryServiceMyBatisModule.DATASOURCE_BINDING_NAME,
      null, // serviceClass is not used
      null, // dbUnitFileName,
      ImmutableMap.<String, Object>of() // dbUnitProperties
    );
  }

  // Only to handle the checked exception
  private static Properties loadProperties() {
    try {
      return PropertiesUtil.loadProperties("registry-test.properties");
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }


  /**
   * Truncates the tables.
   */
  @Override
  protected void runFinally() {
    try {
      connection.createStatement().execute("TRUNCATE TABLE " +
        "node, node_tag, node_contact, " +
        "organization, organization_tag, organization_contact, " +
        "tag, contact");
      connection.commit();
    } catch (SQLException e) {
      Throwables.propagate(e);
    }
  }

  @Override
  protected void runLiquibase(Connection connection, String... fileNames) throws LiquibaseException {
    log.debug("Updating database with liquibase");
    for (String fileName : fileNames) {
      Liquibase liquibase =
        new Liquibase("liquibase" + File.separatorChar + fileName, new ClassLoaderResourceAccessor(),
          new JdbcConnection(connection));
      liquibase.forceReleaseLocks(); // often happens when tests are aborted for example
      // liquibase.dropAll();
      liquibase.update(null);
    }
  }
}
