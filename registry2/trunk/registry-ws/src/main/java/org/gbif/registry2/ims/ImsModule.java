/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry2.ims;

import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.Endpoint;
import org.gbif.api.model.registry2.Node;
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.vocabulary.Country;
import org.gbif.api.vocabulary.Language;
import org.gbif.mybatis.guice.MyBatisModule;
import org.gbif.mybatis.type.UriTypeHandler;
import org.gbif.registry2.persistence.mapper.handler.CountryTypeHandler;
import org.gbif.registry2.persistence.mapper.handler.LanguageTypeHandler;
import org.gbif.registry2.persistence.mapper.handler.UuidTypeHandler;
import org.gbif.service.guice.PrivateServiceModule;

import java.net.URI;
import java.util.Properties;
import java.util.UUID;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sets up the persistence layer using the properties supplied.
 */
public class ImsModule extends PrivateServiceModule {
  private static Logger LOG = LoggerFactory.getLogger(ImsModule.class);

  private static final String PREFIX = "ims.db.";

  public ImsModule(Properties properties) {
    super(PREFIX, properties);
  }

  @Override
  protected void configureService() {
    // is any real IMS database configured?
    if (getVerbatimProperties().getProperty("ims.db.JDBC.url").startsWith("$")) {
      // no, use an empty module that injects mocks
      LOG.info("IMS not configured");
      install( new ImsEmptyModule() );

    } else {
      LOG.info("IMS configured, connecting to Filemaker at {}", getVerbatimProperties().getProperty("ims.db.JDBC.url"));
      MyBatisModule internalModule = new InternalModule();
      install(internalModule); // the named parameters are already configured at this stage
    }

    // expose only the augmenter
    expose(Augmenter.class);
  }

  /**
   * Sets up the MyBatis structure. Note that MyBatis Guice uses named injection parameters (e.g. JDBC.url), and they
   * are filtered and bound in the enclosing class.
   */
  static class InternalModule extends MyBatisModule {

    public static final String DATASOURCE_BINDING_NAME = "ims";

    public InternalModule() {
      super(DATASOURCE_BINDING_NAME);
    }

    @Override
    protected void initialize() {
      // makes things like logo_url map to logoUrl
      bindConstant().annotatedWith(Names.named("mybatis.configuration.mapUnderscoreToCamelCase")).to(true);
      super.initialize();
    }

    @Override
    protected void bindMappers() {
      // network entities
      addMapperClass(ImsNodeMapper.class);

      // reduce mapper verboseness with aliases
      addAlias("Node").to(Node.class);
      addAlias("Organization").to(Organization.class);
      addAlias("Contact").to(Contact.class);
      addAlias("Endpoint").to(Endpoint.class);

      addAlias("Country").to(Country.class);
      addAlias("Language").to(Language.class);
      addAlias("LanguageTypeHandler").to(LanguageTypeHandler.class);
      addAlias("CountryTypeHandler").to(CountryTypeHandler.class);
    }

    @Override
    protected void bindTypeHandlers() {
      handleType(UUID.class).with(UuidTypeHandler.class);
      handleType(URI.class).with(UriTypeHandler.class);
      handleType(Country.class).with(CountryTypeHandler.class);
      handleType(Language.class).with(LanguageTypeHandler.class);
    }

    @Override
    protected void bindManagers() {
      bind(Augmenter.class).to(AugmenterImpl.class).in(Scopes.SINGLETON);
    }

  }


  static class ImsEmptyModule extends AbstractModule {

    @Override
    protected void configure() {
      bind(Augmenter.class).to(AugmenterPassThru.class).in(Scopes.SINGLETON);
    }

    static class AugmenterPassThru implements Augmenter {

      @Override
      public Node augment(Node node) {
        return node;
      }
    }
  }
}
