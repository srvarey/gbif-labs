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
package org.gbif.biovel.locality.guice;

import org.gbif.biovel.locality.model.Location;
import org.gbif.biovel.locality.persistence.LocationMapper;
import org.gbif.mybatis.guice.MyBatisModule;
import org.gbif.service.guice.PrivateServiceModule;

import java.util.Properties;

/**
 * Sets up the persistence layer using the properties supplied.
 */
public class LocalityMyBatisModule extends PrivateServiceModule {

  /**
   * Sets up the MyBatis structure. Note that MyBatis Guice uses named injection parameters (e.g. JDBC.url), and they
   * are filtered and bound in the enclosing class.
   */
  public static class InternalLocationServiceMyBatisModule extends MyBatisModule {

    public static final String DATASOURCE_BINDING_NAME = "location";

    public InternalLocationServiceMyBatisModule() {
      super(DATASOURCE_BINDING_NAME);
    }

    @Override
    protected void bindManagers() {
    }

    @Override
    protected void bindMappers() {
      addMapperClass(LocationMapper.class);

      // reduce mapper verboseness with aliases
      addAlias("Location").to(Location.class);
    }

    @Override
    protected void bindTypeHandlers() {
      // We have no custom type handlers

    }
  }

  private static final String PREFIX = "locality.db.";

  public LocalityMyBatisModule(Properties properties) {
    super(PREFIX, properties);
  }

  @Override
  protected void configureService() {
    MyBatisModule internalModule = new InternalLocationServiceMyBatisModule();
    install(internalModule); // the named parameters are already configured at this stage
    expose(internalModule.getDatasourceKey()); // to avoid clashes between multiple datasources
    // The Mappers are exposed to be injected in the ws resources
    expose(LocationMapper.class);
  }
}
