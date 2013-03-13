package org.gbif.registry;

import org.gbif.api.registry.model.Network;
import org.gbif.api.registry.service.NetworkService;
import org.gbif.registry.guice.RegistryTestModules;
import org.gbif.registry.utils.Networks;
import org.gbif.registry.ws.resources.NetworkResource;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


/**
 * This is parameterized to run the same test routines for the following:
 * <ol>
 * <li>The persistence layer</li>
 * <li>The WS service layer</li>
 * <li>The WS service client layer</li>
 * </ol>
 */
@RunWith(value = Parameterized.class)
public class NetworkTest extends NetworkEntityTest<Network> {

  private final NetworkService service;

  public NetworkTest(NetworkService service) {
    super(service);
    this.service = service;
  }

  @Parameters
  public static Iterable<Object[]> data() {
    return ImmutableList.<Object[]>of(
      new Object[] {RegistryTestModules.webservice().getInstance(NetworkResource.class)},
      new Object[] {RegistryTestModules.webserviceClient().getInstance(NetworkService.class)});
  }

  @Test
  public void testContacts() {
    Network network = create(newEntity(), 1);
    ContactTests.testAddDelete(service, network);
  }

  @Test
  public void testEndpoints() {
    Network network = create(newEntity(), 1);
    EndpointTests.testAddDelete(service, network);
  }

  @Test
  public void testMachineTags() {
    Network network = create(newEntity(), 1);
    MachineTagTests.testAddDelete(service, network);
  }

  @Test
  public void testTags() {
    Network network = create(newEntity(), 1);
    TagTests.testAddDelete(service, network);
    network = create(newEntity(), 2);
    TagTests.testTagErroneousDelete(service, network);
  }

  @Test
  public void testComments() {
    Network network = create(newEntity(), 1);
    CommentTests.testAddDelete(service, network);
  }

  @Override
  protected Network newEntity() {
    return Networks.newInstance();
  }
}
