package org.gbif.registry;

import org.gbif.api.registry.model.Installation;
import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.service.InstallationService;
import org.gbif.api.registry.service.NodeService;
import org.gbif.api.registry.service.OrganizationService;
import org.gbif.registry.utils.Installations;
import org.gbif.registry.utils.Nodes;
import org.gbif.registry.utils.Organizations;
import org.gbif.registry.ws.resources.InstallationResource;
import org.gbif.registry.ws.resources.NodeResource;
import org.gbif.registry.ws.resources.OrganizationResource;

import java.util.UUID;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.gbif.registry.guice.RegistryTestModules.webservice;
import static org.gbif.registry.guice.RegistryTestModules.webserviceClient;


/**
 * This is parameterized to run the same test routines for the following:
 * <ol>
 * <li>The persistence layer</li>
 * <li>The WS service layer</li>
 * <li>The WS service client layer</li>
 * </ol>
 */
@RunWith(value = Parameterized.class)
public class InstallationTest extends NetworkEntityTest<Installation> {

  private final InstallationService service;
  private final OrganizationService organizationService;
  private final NodeService nodeService;

  public InstallationTest(InstallationService service, OrganizationService organizationService, NodeService nodeService) {
    super(service);
    this.service = service;
    this.organizationService = organizationService;
    this.nodeService = nodeService;
  }

  @Parameters
  public static Iterable<Object[]> data() {
    final Injector webservice = webservice();
    final Injector client = webserviceClient();
    return ImmutableList.<Object[]>of(
      new Object[] {webservice.getInstance(InstallationResource.class),
        webservice.getInstance(OrganizationResource.class), webservice.getInstance(NodeResource.class)},
      new Object[] {client.getInstance(InstallationService.class), client.getInstance(OrganizationService.class),
        client.getInstance(NodeService.class)});
  }

  @Test
  public void testContacts() {
    Installation installation = create(newEntity(), 1);
    ContactTests.testAddDelete(service, installation);
  }

  @Test
  public void testEndpoints() {
    Installation installation = create(newEntity(), 1);
    EndpointTests.testAddDelete(service, installation);
  }

  @Test
  public void testMachineTags() {
    Installation installation = create(newEntity(), 1);
    MachineTagTests.testAddDelete(service, installation);
  }

  @Test
  public void testTags() {
    Installation installation = create(newEntity(), 1);
    TagTests.testAddDelete(service, installation);
    installation = create(newEntity(), 2);
    TagTests.testTagErroneousDelete(service, installation);
  }

  @Test
  public void testComments() {
    Installation installation = create(newEntity(), 1);
    CommentTests.testAddDelete(service, installation);
  }

  @Override
  protected Installation newEntity() {
    UUID nodeKey = nodeService.create(Nodes.newInstance());
    Organization o = Organizations.newInstance();
    o.setEndorsingNodeKey(nodeKey);
    UUID key = organizationService.create(o);
    Organization organization = organizationService.get(key);
    Installation i = Installations.newInstance();
    i.setOrganizationKey(organization.getKey());
    return i;
  }
}
