package org.gbif.registry;

import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.model.Node;
import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.service.NodeService;
import org.gbif.api.registry.service.OrganizationService;
import org.gbif.registry.database.DatabaseInitializer;
import org.gbif.registry.guice.RegistryTestModules;
import org.gbif.registry.utils.Contacts;
import org.gbif.registry.utils.Nodes;
import org.gbif.registry.utils.Organizations;
import org.gbif.registry.ws.resources.NodeResource;
import org.gbif.registry.ws.resources.OrganizationResource;

import com.google.inject.Injector;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * A test that will populate a sample registry database.
 * This class should be removed when development progresses.
 * This is only used to help those developing the web console.
 */
public class BootstrapTest {

  private final NodeService nodeService;
  private final OrganizationService organizationService;

  /**
   * Truncates the tables
   */
  @Rule
  public final DatabaseInitializer initializer = new DatabaseInitializer(RegistryTestModules.database());

  public BootstrapTest() {
    Injector i = RegistryTestModules.webservice();
    this.nodeService = i.getInstance(NodeResource.class);
    this.organizationService = i.getInstance(OrganizationResource.class);
  }

  @Test
  @Ignore
  public void run() {
    Node n1 = Nodes.newInstance();
    n1.setKey(nodeService.create(n1));
    Organization o1 = Organizations.newInstance(n1.getKey());
    organizationService.create(o1);

    Node n2 = Nodes.newInstance();
    n2.setTitle("The US Node");
    n2.setKey(nodeService.create(n2));
    Organization o2 = Organizations.newInstance(n2.getKey());
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
  @Ignore
  public void lots() {
    for (int n = 0; n < 100; n++) {
      Node n1 = Nodes.newInstance();
      n1.setTitle((n + 1) + ": " + n1.getTitle());
      n1.setKey(nodeService.create(n1));
      Organization o1 = Organizations.newInstance(n1.getKey());
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
