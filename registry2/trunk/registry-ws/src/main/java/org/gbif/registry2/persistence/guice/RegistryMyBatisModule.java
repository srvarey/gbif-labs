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
package org.gbif.registry2.persistence.guice;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.registry2.Comment;
import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Endpoint;
import org.gbif.api.model.registry2.Identifier;
import org.gbif.api.model.registry2.Installation;
import org.gbif.api.model.registry2.MachineTag;
import org.gbif.api.model.registry2.Metadata;
import org.gbif.api.model.registry2.Network;
import org.gbif.api.model.registry2.Node;
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.model.registry2.Tag;
import org.gbif.api.vocabulary.Country;
import org.gbif.api.vocabulary.Language;
import org.gbif.mybatis.guice.MyBatisModule;
import org.gbif.mybatis.type.UriTypeHandler;
import org.gbif.registry2.persistence.mapper.CommentMapper;
import org.gbif.registry2.persistence.mapper.ContactMapper;
import org.gbif.registry2.persistence.mapper.DatasetMapper;
import org.gbif.registry2.persistence.mapper.EndpointMapper;
import org.gbif.registry2.persistence.mapper.IdentifierMapper;
import org.gbif.registry2.persistence.mapper.InstallationMapper;
import org.gbif.registry2.persistence.mapper.MachineTagMapper;
import org.gbif.registry2.persistence.mapper.MetadataMapper;
import org.gbif.registry2.persistence.mapper.NetworkMapper;
import org.gbif.registry2.persistence.mapper.NodeMapper;
import org.gbif.registry2.persistence.mapper.OrganizationMapper;
import org.gbif.registry2.persistence.mapper.TagMapper;
import org.gbif.registry2.persistence.mapper.handler.CountryTypeHandler;
import org.gbif.registry2.persistence.mapper.handler.LanguageTypeHandler;
import org.gbif.registry2.persistence.mapper.handler.UuidTypeHandler;
import org.gbif.service.guice.PrivateServiceModule;

import java.net.URI;
import java.util.Properties;
import java.util.UUID;

import com.google.inject.name.Names;

/**
 * Sets up the persistence layer using the properties supplied.
 */
public class RegistryMyBatisModule extends PrivateServiceModule {

  private static final String PREFIX = "registry.db.";

  public RegistryMyBatisModule(Properties properties) {
    super(PREFIX, properties);
  }

  @Override
  protected void configureService() {
    MyBatisModule internalModule = new InternalRegistryServiceMyBatisModule();
    install(internalModule); // the named parameters are already configured at this stage
    expose(internalModule.getDatasourceKey()); // to avoid clashes between multiple datasources
    // The Mappers are exposed to be injected in the ws resources
    expose(NodeMapper.class);
    expose(OrganizationMapper.class);
    expose(InstallationMapper.class);
    expose(DatasetMapper.class);
    expose(NetworkMapper.class);
    expose(ContactMapper.class);
    expose(EndpointMapper.class);
    expose(MachineTagMapper.class);
    expose(TagMapper.class);
    expose(IdentifierMapper.class);
    expose(CommentMapper.class);
    expose(MetadataMapper.class);

  }

  /**
   * Sets up the MyBatis structure. Note that MyBatis Guice uses named injection parameters (e.g. JDBC.url), and they
   * are filtered and bound in the enclosing class.
   */
  public static class InternalRegistryServiceMyBatisModule extends MyBatisModule {

    public static final String DATASOURCE_BINDING_NAME = "registry";

    public InternalRegistryServiceMyBatisModule() {
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
      addMapperClass(NodeMapper.class);
      addMapperClass(OrganizationMapper.class);
      addMapperClass(InstallationMapper.class);
      addMapperClass(DatasetMapper.class);
      addMapperClass(NetworkMapper.class);

      // components
      addMapperClass(ContactMapper.class);
      addMapperClass(EndpointMapper.class);
      addMapperClass(MachineTagMapper.class);
      addMapperClass(TagMapper.class);
      addMapperClass(IdentifierMapper.class);
      addMapperClass(CommentMapper.class);
      addMapperClass(MetadataMapper.class);

      // reduce mapper verboseness with aliases
      addAlias("Node").to(Node.class);
      addAlias("Organization").to(Organization.class);
      addAlias("Installation").to(Installation.class);
      addAlias("Dataset").to(Dataset.class);
      addAlias("Network").to(Network.class);

      addAlias("Contact").to(Contact.class);
      addAlias("Endpoint").to(Endpoint.class);
      addAlias("MachineTag").to(MachineTag.class);
      addAlias("Tag").to(Tag.class);
      addAlias("Identifier").to(Identifier.class);
      addAlias("Comment").to(Comment.class);
      addAlias("Metadata").to(Metadata.class);

      addAlias("Pageable").to(Pageable.class);
      addAlias("UuidTypeHandler").to(UuidTypeHandler.class);
      addAlias("UUID").to(UUID.class);
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
      // TODO: change to use proper typehandler and use char(2) in DB
      // handleType(Language.class).with(LanguageTypeHandler.java)
    }

    @Override
    protected void bindManagers() {
    }
  }
}
