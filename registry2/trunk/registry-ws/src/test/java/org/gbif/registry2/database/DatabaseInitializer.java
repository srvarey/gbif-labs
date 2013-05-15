/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry2.database;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.google.common.base.Throwables;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Rule that will truncate the tables ready for a new test. It is expected to do this before each test by using the
 * following:
 * 
 * <pre>
 * @Rule
 * public DatabaseInitializer = new DatabaseInitializer(getDatasource()); // developer required to provide datasource
 * </pre>
 */
public class DatabaseInitializer extends ExternalResource {

  private static final Logger LOG = LoggerFactory.getLogger(DatabaseInitializer.class);
  private final DataSource dataSource;

  public DatabaseInitializer(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  protected void before() throws Throwable {
    LOG.info("Truncating registry tables");
    Connection connection = dataSource.getConnection();
    try {
      connection.setAutoCommit(true);
      connection.createStatement().execute("TRUNCATE TABLE " +
        "node, node_identifier, node_machine_tag, node_tag, node_comment, " +
        "organization, organization_contact, organization_endpoint, organization_machine_tag, " +
        "organization_tag, organization_identifier, organization_comment, " +
        "installation, installation_contact, installation_endpoint, installation_machine_tag, " +
        "installation_tag, installation_comment, " +
        "dataset, dataset_contact, dataset_endpoint, dataset_machine_tag, dataset_tag, " +
        "dataset_identifier, dataset_comment, " +
        "network, network_contact, network_endpoint, network_machine_tag, network_tag, network_comment, " +
        "contact, endpoint, machine_tag, tag, identifier, comment, metadata CASCADE");
    } catch (SQLException e) {
      Throwables.propagate(e);
    } finally {
      if (connection != null) {
        connection.close();
      }
    }
    LOG.info("Registry tables truncated");
  }
}