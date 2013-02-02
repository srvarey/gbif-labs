package org.gbif.registry.guice;

import org.gbif.registry.RegistryServer;
import org.gbif.registry.persistence.guice.RegistryMyBatisModule;
import org.gbif.registry.ws.client.guice.RegistryWsClientModule;
import org.gbif.registry.ws.resources.NodeResource;
import org.gbif.registry.ws.resources.OrganizationResource;

import java.io.IOException;
import java.util.Properties;

import com.google.common.base.Throwables;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.bval.guice.ValidationModule;
import org.apache.ibatis.io.Resources;

/**
 * Utility to provide the different Guice configurations for:
 * <ol>
 * <li>The persistence layer</li>
 * <li>The WS service layer</li>
 * <li>The WS service client layer</li>
 * </ol>
 */
public class RegistryTestModules {

  /**
   * @return An injector that is bound for the webservice layer.
   */
  public static Injector webservice() {
    try {
      Properties p = new Properties();
      p.load(Resources.getResourceAsStream("registry-test.properties"));
      return Guice.createInjector(
        new AbstractModule() {

          @Override
          protected void configure() {
            bind(NodeResource.class);
            bind(OrganizationResource.class);
          }
        }, new RegistryMyBatisModule(p), new ValidationModule());
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  /**
   * @return An injector that is bound for the webservice client layer.
   */
  public static Injector webserviceClient() {
    Properties props = new Properties();
    props.put("registry.ws.url", "http://localhost:" + RegistryServer.getPort());
    return Guice.createInjector(new RegistryWsClientModule(props));
  }
}
