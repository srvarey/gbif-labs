package org.gbif.registry2.ws.resources;

import org.gbif.api.model.registry2.Node;
import org.gbif.api.service.registry2.NodeService;
import org.gbif.api.vocabulary.Country;
import org.gbif.registry2.grizzly.RegistryServer;
import org.gbif.registry2.guice.RegistryTestModules;

import org.junit.ClassRule;
import org.junit.Test;

/**
 * Manual test class for trying filemaker pro which we don't simulate in integration tests.
 * Uncomment the tests to try with a local filemaker server configured in the registry-test.properties.
 */
public class NodeResourceIT {
  @ClassRule
  public static final RegistryServer registryServer = new RegistryServer();
  private final NodeService service;

  public NodeResourceIT() {
    this.service = RegistryTestModules.webservice().getInstance(NodeResource.class);
  }

  @Test
  public void testGet() throws Exception {
    Node es = service.getByCountry(Country.SPAIN);
    System.out.println(es);
  }

}
