package org.gbif.registry;

import org.gbif.api.registry.model.Dataset;
import org.gbif.api.registry.model.Installation;
import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.service.DatasetService;
import org.gbif.api.registry.service.InstallationService;
import org.gbif.api.registry.service.NodeService;
import org.gbif.api.registry.service.OrganizationService;
import org.gbif.registry.utils.Datasets;
import org.gbif.registry.utils.Installations;
import org.gbif.registry.utils.Nodes;
import org.gbif.registry.utils.Organizations;
import org.gbif.registry.ws.resources.DatasetResource;
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
public class DatasetTest extends NetworkEntityTest<Dataset> {

  private final DatasetService service;
  private final OrganizationService organizationService;
  private final NodeService nodeService;
  private final InstallationService installationService;

  public DatasetTest(DatasetService service, OrganizationService organizationService, NodeService nodeService,
    InstallationService installationService) {
    super(service);
    this.service = service;
    this.organizationService = organizationService;
    this.nodeService = nodeService;
    this.installationService = installationService;
  }

  @Parameters
  public static Iterable<Object[]> data() {
    final Injector webservice = webservice();
    final Injector client = webserviceClient();
    return ImmutableList.<Object[]>of(
      new Object[] {webservice.getInstance(DatasetResource.class), webservice.getInstance(OrganizationResource.class),
        webservice.getInstance(NodeResource.class), webservice.getInstance(InstallationResource.class)},
      new Object[] {client.getInstance(DatasetService.class), client.getInstance(OrganizationService.class),
        client.getInstance(NodeService.class), client.getInstance(InstallationService.class)});
  }

  @Test
  public void testContacts() {
    Dataset dataset = create(newEntity(), 1);
    ContactTests.testAddDelete(service, dataset);
  }

  @Test
  public void testEndpoints() {
    Dataset dataset = create(newEntity(), 1);
    EndpointTests.testAddDelete(service, dataset);
  }  
  
  @Test
  public void testMachineTags() {
    Dataset dataset = create(newEntity(), 1);
    MachineTagTests.testAddDelete(service, dataset);
  }  
  
  @Test
  public void testTags() {
    Dataset dataset = create(newEntity(), 1);
    TagTests.testAddDelete(service, dataset);
    dataset = create(newEntity(), 2);
    TagTests.testTagErroneousDelete(service, dataset);
  }
  
  @Test
  public void testIdentifiers() {
    Dataset dataset = create(newEntity(), 1);
    IdentifierTests.testAddDelete(service, dataset);
  }  
  
  @Test
  public void testComments() {
    Dataset dataset = create(newEntity(), 1);
    CommentTests.testAddDelete(service, dataset);
  }  

  @Override
  protected Dataset newEntity() {
    // endorsing node for the organization
    UUID nodeKey = nodeService.create(Nodes.newInstance());
    // owning organization (required field)
    Organization o = Organizations.newInstance();
    o.setEndorsingNodeKey(nodeKey);
    UUID organizationKey = organizationService.create(o);
    // hosting technical installation (required field)
    Installation i = Installations.newInstance();
    i.setOrganizationKey(organizationKey);
    UUID installationKey = installationService.create(i);

    // the dataset
    Dataset d = Datasets.newInstance();
    d.setOwningOrganizationKey(organizationKey);
    d.setInstallationKey(installationKey);
    return d;
  }
}
