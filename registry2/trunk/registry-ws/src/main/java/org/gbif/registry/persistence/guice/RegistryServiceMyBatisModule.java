package org.gbif.registry.persistence.guice;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.model.Node;
import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.model.Tag;
import org.gbif.api.registry.model.WritableContact;
import org.gbif.api.registry.model.WritableNode;
import org.gbif.api.registry.model.WritableOrganization;
import org.gbif.api.registry.service.NodeService;
import org.gbif.api.registry.service.OrganizationService;
import org.gbif.mybatis.guice.MyBatisModule;
import org.gbif.mybatis.type.UriTypeHandler;
import org.gbif.mybatis.type.UuidTypeHandler;
import org.gbif.registry.persistence.NodeServiceMybatis;
import org.gbif.registry.persistence.OrganizationServiceMybatis;
import org.gbif.registry.persistence.mapper.ContactMapper;
import org.gbif.registry.persistence.mapper.NodeMapper;
import org.gbif.registry.persistence.mapper.OrganizationMapper;
import org.gbif.registry.persistence.mapper.TagMapper;
import org.gbif.service.guice.PrivateServiceModule;

import java.net.URI;
import java.util.Properties;
import java.util.UUID;

import com.google.inject.Scopes;
import com.google.inject.name.Names;

/**
 * Sets up the persistence layer using the properties supplied.
 */
public class RegistryServiceMyBatisModule extends PrivateServiceModule {

  private static final String PREFIX = "registry.db.";

  public RegistryServiceMyBatisModule(Properties properties) {
    super(PREFIX, properties);
  }

  @Override
  protected void configureService() {
    MyBatisModule internalModule = new InternalRegistryServiceMyBatisModule();
    install(internalModule); // the named parameters are already configured at this stage
    expose(internalModule.getDatasourceKey()); // to avoid clashes between multiple datasources
    expose(NodeService.class);
    expose(OrganizationService.class);
  }

  /**
   * Sets up the MyBatis structure. Note that MyBatis Guice uses named injection parameters (e.g. JDBC.url), and they
   * are filtered and bound in the enclosing class.
   */
  public class InternalRegistryServiceMyBatisModule extends MyBatisModule {

    public static final String DATASOURCE_BINDING_NAME = "registry";

    public InternalRegistryServiceMyBatisModule() {
      super(DATASOURCE_BINDING_NAME);
    }

    @Override
    protected void bindManagers() {
      bind(NodeService.class).to(NodeServiceMybatis.class).in(Scopes.SINGLETON);
      bind(OrganizationService.class).to(OrganizationServiceMybatis.class).in(Scopes.SINGLETON);
    }

    @Override
    protected void initialize() {
      // makes things like logo_url map to logoUrl
      bindConstant().annotatedWith(Names.named("mybatis.configuration.mapUnderscoreToCamelCase")).to(true);
      super.initialize();
    }

    @Override
    protected void bindMappers() {
      addMapperClass(OrganizationMapper.class);
      addMapperClass(NodeMapper.class);
      addMapperClass(ContactMapper.class);
      addMapperClass(TagMapper.class);

      // reduce mapper verboseness with aliases
      addAlias("WritableNode").to(WritableNode.class);
      addAlias("Node").to(Node.class);
      addAlias("WritableOrganization").to(WritableOrganization.class);
      addAlias("Organization").to(Organization.class);
      addAlias("Pageable").to(Pageable.class);
      addAlias("Tag").to(Tag.class);
      addAlias("WritableContact").to(WritableContact.class);
      addAlias("Contact").to(Contact.class);
    }

    @Override
    protected void bindTypeHandlers() {
      handleType(UUID.class).with(UuidTypeHandler.class);
      handleType(URI.class).with(UriTypeHandler.class);
      // TODO: change to use proper typehandler and use char(2) in DB
      // handleType(Language.class).with(LanguageTypeHandler.java)
    }
  }
}
