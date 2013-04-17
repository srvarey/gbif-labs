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

import org.gbif.api.model.common.paging.PagingRequest;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry2.Node;
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.service.registry2.NodeService;
import org.gbif.api.service.registry2.OrganizationService;
import org.gbif.registry2.utils.Nodes;
import org.gbif.registry2.utils.Organizations;
import org.gbif.registry2.ws.resources.NodeResource;
import org.gbif.registry2.ws.resources.OrganizationResource;

import java.util.UUID;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.gbif.registry2.guice.RegistryTestModules.webservice;
import static org.gbif.registry2.guice.RegistryTestModules.webserviceClient;

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
@RunWith(Parameterized.class)
public class OrganizationTest extends NetworkEntityTest<Organization> {

  private final OrganizationService service;
  private final NodeService nodeService;

  @Parameters
  public static Iterable<Object[]> data() {
    final Injector webservice = webservice();
    final Injector client = webserviceClient();
    return ImmutableList.<Object[]>of(new Object[] {webservice.getInstance(OrganizationResource.class),
      webservice.getInstance(NodeResource.class)},
                                      new Object[] {client.getInstance(OrganizationService.class),
                                        client.getInstance(NodeService.class)});
  }

  public OrganizationTest(OrganizationService service, NodeService nodeService) {
    super(service);
    this.service = service;
    this.nodeService = nodeService;
  }

  @Test
  public void testContacts() {
    Organization organization = create(newEntity(), 1);
    ContactTests.testAddDelete(service, organization);
  }

  @Test
  public void testEndpoints() {
    Organization organization = create(newEntity(), 1);
    EndpointTests.testAddDelete(service, organization);
  }

  @Test
  public void testMachineTags() {
    Organization organization = create(newEntity(), 1);
    MachineTagTests.testAddDelete(service, organization);
  }

  @Test
  public void testTags() {
    Organization organization = create(newEntity(), 1);
    TagTests.testAddDelete(service, organization);
    organization = create(newEntity(), 2);
    TagTests.testTagErroneousDelete(service, organization);
  }

  @Test
  public void testIdentifiers() {
    Organization organization = create(newEntity(), 1);
    IdentifierTests.testAddDelete(service, organization);
  }

  @Test
  public void testComment() {
    Organization organization = create(newEntity(), 1);
    CommentTests.testAddDelete(service, organization);
  }

  @Test
  public void testEndorsements() {
    Node node = Nodes.newInstance();
    nodeService.create(node);
    node = nodeService.list(new PagingRequest()).getResults().get(0);

    assertResultsOfSize(nodeService.organizationsEndorsedBy(node.getKey(), new PagingRequest()), 0);
    assertResultsOfSize(nodeService.pendingEndorsements(new PagingRequest()), 0);

    Organization o = Organizations.newInstance(node.getKey());
    o.setKey(this.getService().create(o));
    assertResultsOfSize(nodeService.organizationsEndorsedBy(node.getKey(), new PagingRequest()), 0);
    assertResultsOfSize(nodeService.pendingEndorsements(new PagingRequest()), 1);

    o.setEndorsementApproved(true);
    this.getService().update(o);
    assertResultsOfSize(nodeService.pendingEndorsements(new PagingRequest()), 0);
    assertResultsOfSize(nodeService.organizationsEndorsedBy(node.getKey(), new PagingRequest()), 1);
  }

  @Override
  protected Organization newEntity() {
    UUID key = nodeService.create(Nodes.newInstance());
    Node node = nodeService.get(key);
    Organization o = Organizations.newInstance(node.getKey());
    return o;
  }

  private void assertResultsOfSize(PagingResponse<Organization> results, int size) {
    assertNotNull(results);
    assertNotNull(results.getResults());
    assertEquals("Unexpected result size for current test state", size, results.getResults().size());
  }

}
