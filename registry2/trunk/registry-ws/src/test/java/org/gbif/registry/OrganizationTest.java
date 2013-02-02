package org.gbif.registry;

import org.gbif.api.model.common.paging.PagingRequest;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.model.WritableNode;
import org.gbif.api.registry.model.WritableOrganization;
import org.gbif.api.registry.service.NodeService;
import org.gbif.api.registry.service.OrganizationService;
import org.gbif.registry.data.Nodes;
import org.gbif.registry.data.Organizations;
import org.gbif.registry.data.Organizations.TYPE;
import org.gbif.registry.ws.resources.NodeResource;
import org.gbif.registry.ws.resources.OrganizationResource;

import com.google.common.collect.ImmutableList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.gbif.registry.guice.RegistryTestModules.webservice;
import static org.gbif.registry.guice.RegistryTestModules.webserviceClient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * This is parameterized to run the same test routines for the following:
 * <ol>
 * <li>The persistence layer</li>
 * <li>The WS service layer</li>
 * <li>The WS service client layer</li>
 * </ol>
 */
@RunWith(value = Parameterized.class)
public class OrganizationTest extends NetworkEntityTest<WritableOrganization, Organization> {

  private static RegistryServer server = new RegistryServer();
  private final NodeService nodeService;

  public OrganizationTest(OrganizationService service, NodeService nodeService) {
    super(service);
    this.nodeService = nodeService;
  }

  @BeforeClass
  public static void startServer() {
    server.start();
  }

  @AfterClass
  public static void stopServer() {
    server.stop();
  }

  @Parameters
  public static Iterable<Object[]> data() {
    return ImmutableList
      .<Object[]>of(
        new Object[] {
          webservice().getInstance(OrganizationResource.class),
          webservice().getInstance(NodeResource.class)},
        new Object[] {
          webserviceClient().getInstance(OrganizationService.class),
          webserviceClient().getInstance(NodeService.class)}
      );
  }


  @Override
  protected WritableOrganization newWritable() {
    WritableOrganization o = Organizations.writableInstanceOf(TYPE.BGBM);
    WritableNode node = Nodes.writableInstanceOf(org.gbif.registry.data.Nodes.TYPE.UK);
    nodeService.create(node);
    node = nodeService.list(new PagingRequest()).getResults().get(0);
    o.setEndorsingNodeKey(node.getKey());
    return o;
  }

  @Test
  public void testEndorsements() {
    WritableNode node = Nodes.writableInstanceOf(org.gbif.registry.data.Nodes.TYPE.UK);
    nodeService.create(node);
    node = nodeService.list(new PagingRequest()).getResults().get(0);

    assertResultsOfSize(nodeService.organizationsEndorsedBy(node.getKey(), new PagingRequest()), 0);
    assertResultsOfSize(nodeService.pendingEndorsements(new PagingRequest()), 0);

    WritableOrganization o = Organizations.writableInstanceOf(TYPE.BGBM);
    o.setEndorsingNodeKey(node.getKey());
    o.setKey(this.getService().create(o));
    assertResultsOfSize(nodeService.organizationsEndorsedBy(node.getKey(), new PagingRequest()), 0);
    assertResultsOfSize(nodeService.pendingEndorsements(new PagingRequest()), 1);

    o.setEndorsementApproved(true);
    this.getService().update(o);
    assertResultsOfSize(nodeService.pendingEndorsements(new PagingRequest()), 0);
    assertResultsOfSize(nodeService.organizationsEndorsedBy(node.getKey(), new PagingRequest()), 1);
  }

  private void assertResultsOfSize(PagingResponse<Organization> pendingEndorsements, int size) {
    assertNotNull(pendingEndorsements);
    assertNotNull(pendingEndorsements.getResults());
    assertEquals("Unexpected result size for current test state", size, pendingEndorsements.getResults().size());
  }
}
