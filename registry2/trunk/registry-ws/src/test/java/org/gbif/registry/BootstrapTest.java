package org.gbif.registry;

import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.model.Node;
import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.service.NodeService;
import org.gbif.api.registry.service.OrganizationService;
import org.gbif.registry.data.Contacts;
import org.gbif.registry.data.Nodes;
import org.gbif.registry.data.Organizations;
import org.gbif.registry.guice.RegistryTestModules;

import com.google.inject.Injector;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static org.gbif.registry.data.Nodes.TYPE.DK;
import static org.gbif.registry.data.Nodes.TYPE.UK;
import static org.gbif.registry.data.Organizations.TYPE.BGBM;
import static org.gbif.registry.data.Organizations.TYPE.KEW;

/**
 * A test that will populate a sample registry database.
 */
public class BootstrapTest {

  private final NodeService nodeService;
  private final OrganizationService organizationService;

  @Rule
  public final DatabaseInitializer<Void> initializer = new DatabaseInitializer<Void>();

  public BootstrapTest() {
    Injector i = RegistryTestModules.webservice();
    this.nodeService = i.getInstance(NodeService.class);
    this.organizationService = i.getInstance(OrganizationService.class);
  }

  @Test
  @Ignore
  public void run() {
    Node n1 = Nodes.instanceOf(UK);
    n1.setKey(nodeService.create(n1));
    Organization o1 = Organizations.instanceOf(KEW);
    o1.setEndorsingNodeKey(n1.getKey());
    organizationService.create(o1);

    Node n2 = Nodes.instanceOf(DK);
    n2.setKey(nodeService.create(n2));
    Organization o2 = Organizations.instanceOf(BGBM);
    o2.setEndorsingNodeKey(n2.getKey());
    o2.setEndorsementApproved(true);
    organizationService.create(o2);

    for (int i = 0; i < 5; i++) {
      nodeService.addContact(n1.getKey(), newContact(i + ": "));
      nodeService.addContact(n2.getKey(), newContact(i + ": "));
    }
    String[] tags = {"Abies", "Georeferenced", "Images", "Dubious", "DataPaper"};
    for (String tag : tags) {
      nodeService.addTag(n1.getKey(), tag);
      nodeService.addTag(n2.getKey(), tag);
    }
  }

  @Test
  public void lots() {
    for (int n = 0; n < 1000; n++) {
      Node n1 = Nodes.instanceOf(UK);
      n1.setTitle((n + 1) + ": " + n1.getTitle());
      n1.setKey(nodeService.create(n1));
      Organization o1 = Organizations.instanceOf(KEW);
      o1.setEndorsingNodeKey(n1.getKey());
      organizationService.create(o1);
      for (int i = 0; i < 5; i++) {
        nodeService.addContact(n1.getKey(), newContact(i + ": "));
      }
      String[] tags = {"Abies", "Georeferenced", "Images", "Dubious", "DataPaper"};
      for (String tag : tags) {
        nodeService.addTag(n1.getKey(), tag);
      }
    }
  }

  private Contact newContact(String namePrefix) {
    Contact c = Contacts.newInstance();
    c.setName(namePrefix + c.getName());
    return c;

  }
}
