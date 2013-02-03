package org.gbif.registry;

import org.gbif.api.registry.model.Node;
import org.gbif.api.registry.model.WritableNode;
import org.gbif.api.registry.service.NodeService;
import org.gbif.registry.data.Nodes;
import org.gbif.registry.data.Nodes.TYPE;
import org.gbif.registry.guice.RegistryTestModules;
import org.gbif.registry.ws.resources.NodeResource;

import com.google.common.collect.ImmutableList;
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
public class NodeTest extends NetworkEntityTest<WritableNode, Node> {

  public NodeTest(NodeService service) {
    super(service);
  }

  @Parameters
  public static Iterable<Object[]> data() {
    return ImmutableList.<Object[]>of(
      new Object[] {RegistryTestModules.webservice().getInstance(NodeResource.class)},
      new Object[] {RegistryTestModules.webserviceClient().getInstance(NodeService.class)}
      );
  }

  @Override
  protected WritableNode newWritable() {
    return Nodes.writableInstanceOf(TYPE.UK);
  }
}
