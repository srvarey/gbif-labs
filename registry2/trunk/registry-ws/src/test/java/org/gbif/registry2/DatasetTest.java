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

import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Installation;
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.service.registry2.DatasetService;
import org.gbif.api.service.registry2.InstallationService;
import org.gbif.api.service.registry2.NodeService;
import org.gbif.api.service.registry2.OrganizationService;
import org.gbif.registry2.utils.Datasets;
import org.gbif.registry2.utils.Installations;
import org.gbif.registry2.utils.Nodes;
import org.gbif.registry2.utils.Organizations;
import org.gbif.registry2.ws.resources.DatasetResource;
import org.gbif.registry2.ws.resources.InstallationResource;
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

/**
 * This is parameterized to run the same test routines for the following:
 * <ol>
 * <li>The persistence layer</li>
 * <li>The WS service layer</li>
 * <li>The WS service client layer</li>
 * </ol>
 */
@RunWith(Parameterized.class)
public class DatasetTest extends NetworkEntityTest<Dataset> {

  private final DatasetService service;
  private final OrganizationService organizationService;
  private final NodeService nodeService;
  private final InstallationService installationService;

  @Parameters
  public static Iterable<Object[]> data() {
    final Injector webservice = webservice();
    final Injector client = webserviceClient();
    return ImmutableList.<Object[]>of(new Object[] {webservice.getInstance(DatasetResource.class),
      webservice.getInstance(OrganizationResource.class), webservice.getInstance(NodeResource.class),
      webservice.getInstance(InstallationResource.class)},
                                      new Object[] {client.getInstance(DatasetService.class),
                                        client.getInstance(OrganizationService.class),
                                        client.getInstance(NodeService.class),
                                        client.getInstance(InstallationService.class)});
  }

  public DatasetTest(
    DatasetService service,
    OrganizationService organizationService,
    NodeService nodeService,
    InstallationService installationService
  ) {
    super(service);
    this.service = service;
    this.organizationService = organizationService;
    this.nodeService = nodeService;
    this.installationService = installationService;
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
    Organization o = Organizations.newInstance(nodeKey);
    UUID organizationKey = organizationService.create(o);
    // hosting technical installation (required field)
    Installation i = Installations.newInstance(organizationKey);
    UUID installationKey = installationService.create(i);

    // the dataset
    Dataset d = Datasets.newInstance(organizationKey);
    d.setInstallationKey(installationKey);
    return d;
  }

}
