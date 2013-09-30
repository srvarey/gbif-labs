package org.gbif.registry.persistence.guice;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.model.Node;
import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.model.Tag;
import org.gbif.mybatis.guice.MyBatisModule;
import org.gbif.mybatis.type.UriTypeHandler;
import org.gbif.mybatis.type.UuidTypeHandler;
import org.gbif.registry.persistence.mapper.ContactMapper;
import org.gbif.registry.persistence.mapper.NodeMapper;
import org.gbif.registry.persistence.mapper.OrganizationMapper;
import org.gbif.registry.persistence.mapper.TagMapper;
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
    expose(OrganizationMapper.class);
    expose(NodeMapper.class);
    expose(ContactMapper.class);
    expose(TagMapper.class);
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
      addAlias("Node").to(Node.class);
      addAlias("Organization").to(Organization.class);
      addAlias("Pageable").to(Pageable.class);
      addAlias("Tag").to(Tag.class);
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
