/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry2;

import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Identifier;
import org.gbif.api.model.registry2.Installation;
import org.gbif.api.model.registry2.Node;
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.service.registry2.DatasetService;
import org.gbif.api.service.registry2.InstallationService;
import org.gbif.api.service.registry2.NodeService;
import org.gbif.api.service.registry2.OrganizationService;
import org.gbif.api.vocabulary.Country;
import org.gbif.api.vocabulary.registry2.IdentifierType;
import org.gbif.registry2.guice.RegistryTestModules;
import org.gbif.registry2.utils.Datasets;
import org.gbif.registry2.utils.Installations;
import org.gbif.registry2.utils.Nodes;
import org.gbif.registry2.utils.Organizations;
import org.gbif.registry2.ws.resources.DatasetResource;
import org.gbif.registry2.ws.resources.InstallationResource;
import org.gbif.registry2.ws.resources.NodeResource;
import org.gbif.registry2.ws.resources.OrganizationResource;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * This is parameterized to run the same test routines for the following:
 * <ol>
 * <li>The persistence layer</li>
 * <li>The WS service layer</li>
 * <li>The WS service client layer</li>
 * </ol>
 */
@RunWith(Parameterized.class)
public class NodeIT extends NetworkEntityTest<Node> {

  private final NodeService nodeService;
  private final OrganizationService organizationService;
  private final InstallationService installationService;
  private final DatasetService datasetService;

  private static final Map<Country, Integer> TEST_COUNTRIES = ImmutableMap.<Country, Integer>builder()
    .put(Country.AFGHANISTAN, 6)
    .put(Country.ARGENTINA, 16)
    .put(Country.DENMARK, 2)
    .put(Country.SPAIN, 1)
    .build();

  @Parameters
  public static Iterable<Object[]> data() {
    return ImmutableList.<Object[]>of(
                    new Object[] {
                      RegistryTestModules.webservice().getInstance(NodeResource.class),
                      RegistryTestModules.webservice().getInstance(OrganizationResource.class),
                      RegistryTestModules.webservice().getInstance(InstallationResource.class),
                      RegistryTestModules.webservice().getInstance(DatasetResource.class)
                    },
                    new Object[] {
                      RegistryTestModules.webserviceClient().getInstance(NodeService.class),
                      RegistryTestModules.webserviceClient().getInstance(OrganizationService.class),
                      RegistryTestModules.webserviceClient().getInstance(InstallationService.class),
                      RegistryTestModules.webserviceClient().getInstance(DatasetService.class)
                    });
  }

  public NodeIT(NodeService nodeService, OrganizationService organizationService,
    InstallationService installationService, DatasetService datasetService) {
    super(nodeService);
    this.nodeService = nodeService;
    this.organizationService = organizationService;
    this.installationService = installationService;
    this.datasetService = datasetService;
  }


  @Test
  public void testGetByCountry() {
    initCountryNodes();
    Node n = nodeService.getByCountry(Country.ANGOLA);
    assertNull(n);

    for (Country c : TEST_COUNTRIES.keySet()) {
      n = nodeService.getByCountry(c);
      assertEquals(c, n.getCountry());
    }
  }

  private void initCountryNodes() {
    int count = 0;
    for (Country c : TEST_COUNTRIES.keySet()) {
      Node n = newEntity();
      n.setCountry(c);
      n.setTitle("GBIF Node " + c.getTitle());
      n = create(n, count + 1);
      count++;

      // create IMS identifiers
      Identifier id = new Identifier();
      id.setType(IdentifierType.GBIF_PARTICIPANT);
      id.setIdentifier(TEST_COUNTRIES.get(c).toString());
      id.setCreatedBy("NodeIT");
      nodeService.addIdentifier(n.getKey(), id);
    }
  }

  @Override
  protected Node asWritable(Node source) {
    Node node = super.asWritable(source);
    // remove all IMS augmented properties
    node.getContacts().clear();
    node.setDescription(null);
    node.setParticipantSince(null);
    node.setAddress(null);
    node.setPostalCode(null);
    node.setCity(null);
    node.setProvince(null);
    node.setEmail(null);
    node.setPhone(null);
    node.setHomepage(null);

    return node;
  }

  @Test
  public void testCountries() {
    initCountryNodes();
    List<Country> countries = nodeService.listNodeCountries();
    assertEquals(TEST_COUNTRIES.size(), countries.size());
    for (Country c : countries) {
      assertTrue("Unexpected node country" + c, TEST_COUNTRIES.containsKey(c));
    }
  }


  @Test
  public void testDatasets() {
    // endorsing node for the organization
    Node node = create(newEntity(), 1);
    // owning organization (required field)
    Organization o = Organizations.newInstance(node.getKey());
    o.setEndorsementApproved(true);
    o.setEndorsingNodeKey(node.getKey());
    UUID organizationKey = organizationService.create(o);
    // hosting technical installation (required field)
    Installation i = Installations.newInstance(organizationKey);
    UUID installationKey = installationService.create(i);
    // 2 datasets
    Dataset d1 = Datasets.newInstance(organizationKey);
    d1.setInstallationKey(installationKey);
    datasetService.create(d1);
    Dataset d2 = Datasets.newInstance(organizationKey);
    d2.setInstallationKey(installationKey);
    UUID d2Key = datasetService.create(d2);

    // test node service
    PagingResponse<Dataset> resp = nodeService.publishedDatasets(node.getKey(), null);
    assertEquals(2, resp.getResults().size());
    // the last created dataset should be the first in the list
    assertEquals(d2Key, resp.getResults().get(0).getKey());
  }


  @Test
  @Ignore("A manual test requiring a local filemaker IMS copy")
  public void testIms() throws Exception {
    initCountryNodes();
    Node es = nodeService.getByCountry(Country.SPAIN);
    assertEquals("Madrid", es.getCity());
    assertEquals("28014", es.getPostalCode());
    assertNotNull(es.getAddress());
    assertTrue(es.getContacts().size() > 5);

    Node notInIms = nodeService.getByCountry(Country.AFGHANISTAN);
    assertNotNull(notInIms);
  }

  /**
   * Node contacts are IMS managed and the service throws exceptions
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testContacts() {
    Node n = create(newEntity(), 1);
    nodeService.listContacts(n.getKey());
  }

  /**
   * Node contacts are IMS managed and the service throws exceptions
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testAddContact() {
    Node n = create(newEntity(), 1);
    nodeService.addContact(n.getKey(), new Contact());
  }

  /**
   * Node contacts are IMS managed and the service throws exceptions
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testDeleteContact() {
    Node n = create(newEntity(), 1);
    nodeService.deleteContact(n.getKey(), 1);
  }

  @Test(expected = UnsupportedOperationException.class)
  @Override
  public void testSimpleSearchContact() {
    super.testSimpleSearchContact();
  }


  @Override
  protected Node newEntity() {
    return Nodes.newInstance();
  }

}
