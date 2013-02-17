package org.gbif.registry;

import org.gbif.api.registry.model.Node;
import org.gbif.api.registry.service.NodeService;
import org.gbif.registry.guice.RegistryTestModules;
import org.gbif.registry.utils.Nodes;
import org.gbif.registry.ws.resources.NodeResource;

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
public class NodeTest extends NetworkEntityTest<Node> {

  private final NodeService service;

  public NodeTest(NodeService service) {
    super(service);
    this.service = service;
  }

  @Parameters
  public static Iterable<Object[]> data() {
    return ImmutableList.<Object[]>of(
      new Object[] {RegistryTestModules.webservice().getInstance(NodeResource.class)},
      new Object[] {RegistryTestModules.webserviceClient().getInstance(NodeService.class)}
      );
  }

  @Test
  public void testContacts() {
    Node node = create(newEntity(), 1);
    ContactTests.testAddDelete(service, node);
  }

  @Test
  public void testTags() {
    Node node = create(newEntity(), 1);
    TagTests.testAddDelete(service, node);
    node = create(newEntity(), 2);
    TagTests.testTagErroneousDelete(service, node);
  }


  @Override
  protected Node newEntity() {
    return Nodes.newInstance();
  }
}
