package org.gbif.biovel.locality.database;

import org.gbif.biovel.locality.guice.LocalityTestModules;

import org.junit.Rule;
import org.junit.Test;

/**
 * A test that will ensure the DB schema is up to date.
 */
public class BootstrapTest {

  /**
   * Truncates the tables
   */
  @Rule
  public final LiquibaseInitializer initializer = new LiquibaseInitializer(LocalityTestModules.database());

  @Test
  public void proceed() {
    // empty test, just to ensure the Liquibase is fired.
  }
}
