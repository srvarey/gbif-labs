package org.gbif.registry.ws.client.guice;

import org.gbif.api.registry.service.DatasetService;
import org.gbif.api.registry.service.InstallationService;
import org.gbif.api.registry.service.NetworkService;
import org.gbif.api.registry.service.NodeService;
import org.gbif.api.registry.service.OrganizationService;
import org.gbif.registry.ws.client.DatasetWsClient;
import org.gbif.registry.ws.client.InstallationWsClient;
import org.gbif.registry.ws.client.NetworkWsClient;
import org.gbif.registry.ws.client.NodeWsClient;
import org.gbif.registry.ws.client.OrganizationWsClient;
import org.gbif.service.guice.PrivateServiceModule;
import org.gbif.ws.client.guice.AnonymousAuthModule;
import org.gbif.ws.client.guice.GbifApplicationAuthModule;
import org.gbif.ws.client.guice.GbifWsClientModule;

import java.util.Properties;

import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.name.Named;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;


/**
 * A Module for Guice doing all the necessary wiring with the exception of the authentication filters.
 * In order to use this module an authentication module needs to be installed first, such as {@link AnonymousAuthModule}
 * for anonymous access or {@link GbifApplicationAuthModule} for trusted applications.
 * With an authentication module installed, the only thing left for clients to do is to provide a property
 * to be bound to each of {@code registry.ws.url} and {@code registry.search.ws.url}.
 * If you want to use this module remember to also depend on Guice, jersey-apache-client4 and jersey-json in your
 * pom.xml file as they are declared as optional dependencies in this project.
 */
public class RegistryWsClientModule extends GbifWsClientModule {

  public RegistryWsClientModule(Properties properties) {
    super(properties, NodeWsClient.class.getPackage());
  }

  @Override
  protected void configureClient() {
    install(new InternalRegistryWsClientModule(getProperties()));
    expose(NodeService.class);
    expose(OrganizationService.class);
    expose(InstallationService.class);
    expose(DatasetService.class);
    expose(NetworkService.class);

  }

// @Override
// protected Map<Class<?>, Class<?>> getPolymorphicClassMap() {
// return PolymorphicSerializable.CLASS_MAP;
// }

  // To allow the prefixing of the properties
  private class InternalRegistryWsClientModule extends PrivateServiceModule {

    private static final String PREFIX = "registry.";

    public InternalRegistryWsClientModule(Properties properties) {
      super(PREFIX, properties);
    }

    @Override
    protected void configureService() {

      bind(NodeService.class).to(NodeWsClient.class).in(Scopes.SINGLETON);
      bind(OrganizationService.class).to(OrganizationWsClient.class).in(Scopes.SINGLETON);
      bind(InstallationService.class).to(InstallationWsClient.class).in(Scopes.SINGLETON);
      bind(DatasetService.class).to(DatasetWsClient.class).in(Scopes.SINGLETON);
      bind(NetworkService.class).to(NetworkWsClient.class).in(Scopes.SINGLETON);

      expose(NodeService.class);
      expose(OrganizationService.class);
      expose(InstallationService.class);
      expose(DatasetService.class);
      expose(NetworkService.class);
    }

    @Provides
    @RegistryWs
    private WebResource provideBaseWsWebResource(Client client, @Named("ws.url") String url) {
      return client.resource(url);
    }
  }
}
