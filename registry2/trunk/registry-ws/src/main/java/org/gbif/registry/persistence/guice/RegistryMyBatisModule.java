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
package org.gbif.registry.persistence.guice;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.registry.Citation;
import org.gbif.api.model.registry.Comment;
import org.gbif.api.model.registry.Contact;
import org.gbif.api.model.registry.Dataset;
import org.gbif.api.model.registry.Endpoint;
import org.gbif.api.model.registry.Identifier;
import org.gbif.api.model.registry.Installation;
import org.gbif.api.model.registry.MachineTag;
import org.gbif.api.model.registry.Metadata;
import org.gbif.api.model.registry.Network;
import org.gbif.api.model.registry.Node;
import org.gbif.api.model.registry.Organization;
import org.gbif.api.model.registry.Tag;
import org.gbif.api.vocabulary.Country;
import org.gbif.api.vocabulary.Language;
import org.gbif.mybatis.guice.MyBatisModule;
import org.gbif.mybatis.type.CountryTypeHandler;
import org.gbif.mybatis.type.LanguageTypeHandler;
import org.gbif.mybatis.type.UriTypeHandler;
import org.gbif.registry.persistence.mapper.CommentMapper;
import org.gbif.registry.persistence.mapper.ContactMapper;
import org.gbif.registry.persistence.mapper.DatasetMapper;
import org.gbif.registry.persistence.mapper.EndpointMapper;
import org.gbif.registry.persistence.mapper.IdentifierMapper;
import org.gbif.registry.persistence.mapper.InstallationMapper;
import org.gbif.registry.persistence.mapper.MachineTagMapper;
import org.gbif.registry.persistence.mapper.MetadataMapper;
import org.gbif.registry.persistence.mapper.NetworkMapper;
import org.gbif.registry.persistence.mapper.NodeMapper;
import org.gbif.registry.persistence.mapper.OrganizationMapper;
import org.gbif.registry.persistence.mapper.TagMapper;
import org.gbif.registry.persistence.mapper.handler.UuidTypeHandler;
import org.gbif.service.guice.PrivateServiceModule;

import java.net.URI;
import java.util.Properties;
import java.util.UUID;

//import org.gbif.registry.persistence.mapper.handler.CountryTypeHandler;
//import org.gbif.registry.persistence.mapper.handler.LanguageTypeHandler;

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

      addAlias("Citation").to(Citation.class);
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
    }

    @Override
    protected void bindManagers() {
    }
  }
}
