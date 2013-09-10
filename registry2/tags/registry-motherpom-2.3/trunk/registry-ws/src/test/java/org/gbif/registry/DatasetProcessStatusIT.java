/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry;

import org.gbif.api.model.common.paging.PagingRequest;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.crawler.CrawlJob;
import org.gbif.api.model.crawler.DatasetProcessStatus;
import org.gbif.api.model.crawler.FinishReason;
import org.gbif.api.model.registry.Dataset;
import org.gbif.api.model.registry.Installation;
import org.gbif.api.model.registry.Organization;
import org.gbif.api.service.registry.DatasetProcessStatusService;
import org.gbif.api.service.registry.DatasetService;
import org.gbif.api.service.registry.InstallationService;
import org.gbif.api.service.registry.NodeService;
import org.gbif.api.service.registry.OrganizationService;
import org.gbif.api.vocabulary.EndpointType;
import org.gbif.registry.database.DatabaseInitializer;
import org.gbif.registry.database.LiquibaseInitializer;
import org.gbif.registry.grizzly.RegistryServer;
import org.gbif.registry.guice.RegistryTestModules;
import org.gbif.registry.utils.Datasets;
import org.gbif.registry.utils.Installations;
import org.gbif.registry.utils.Nodes;
import org.gbif.registry.utils.Organizations;
import org.gbif.registry.ws.resources.DatasetResource;
import org.gbif.registry.ws.resources.InstallationResource;
import org.gbif.registry.ws.resources.NodeResource;
import org.gbif.registry.ws.resources.OrganizationResource;
import org.gbif.ws.client.filter.SimplePrincipalProvider;

import java.net.URI;
import java.util.Date;
import java.util.UUID;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.gbif.registry.guice.RegistryTestModules.webservice;
import static org.gbif.registry.guice.RegistryTestModules.webserviceClient;

/**
 * Runs tests for the {@link DatasetProcessStatusService} implementations.
 * This is parameterized to run the same test routines for the following:
 * <ol>
 * <li>The persistence layer</li>
 * <li>The WS service layer</li>
 * <li>The WS service client layer</li>
 * </ol>
 */
@RunWith(Parameterized.class)
public class DatasetProcessStatusIT {

  // Flushes the database on each run
  @ClassRule
  public static final LiquibaseInitializer liquibaseRule = new LiquibaseInitializer(RegistryTestModules.database());

  @ClassRule
  public static final RegistryServer registryServer = RegistryServer.INSTANCE;

  // Tests user
  private static String TEST_USER = "admin";

  @Rule
  public final DatabaseInitializer databaseRule = new DatabaseInitializer(RegistryTestModules.database());

  private final DatasetProcessStatusService datasetProcessStatusService;

  private final SimplePrincipalProvider simplePrincipalProvider;

  // The following services are required to create dataset instances
  private final DatasetService datasetService;

  private final OrganizationService organizationService;
  private final NodeService nodeService;
  private final InstallationService installationService;

  public DatasetProcessStatusIT(
    DatasetProcessStatusService datasetProcessStatusService,
    DatasetService datasetService,
    OrganizationService organizationService, NodeService nodeService,
    InstallationService installationService,
    SimplePrincipalProvider simplePrincipalProvider) {
    this.datasetProcessStatusService = datasetProcessStatusService;
    this.datasetService = datasetService;
    this.organizationService = organizationService;
    this.nodeService = nodeService;
    this.installationService = installationService;
    this.simplePrincipalProvider = simplePrincipalProvider;
  }

  @Parameters
  public static Iterable<Object[]> data() {
    final Injector webservice = webservice();
    final Injector client = webserviceClient();
    return ImmutableList.<Object[]>of(new Object[] {webservice.getInstance(DatasetResource.class),
      webservice.getInstance(DatasetResource.class), webservice.getInstance(OrganizationResource.class),
      webservice.getInstance(NodeResource.class), webservice.getInstance(InstallationResource.class), null},
      new Object[] {client.getInstance(DatasetProcessStatusService.class), client.getInstance(DatasetService.class),
        client.getInstance(OrganizationService.class), client.getInstance(NodeService.class),
        client.getInstance(InstallationService.class), client.getInstance(SimplePrincipalProvider.class)});
  }


  @Before
  public void setup() {
    // reset SimplePrincipleProvider, configured for web service client tests only
    if (simplePrincipalProvider != null) {
      simplePrincipalProvider.setPrincipal(TEST_USER);
    }
  }

  /**
   * Tests the create and get of a {@link DatasetProcessStatus}.
   */
  @Test
  public void testCreateAndGet() {
    DatasetProcessStatus datasetProcessStatus = getTestInstance();
    datasetProcessStatusService.createDatasetProcessStatus(datasetProcessStatus);
    DatasetProcessStatus datasetProcessStatus2 =
      datasetProcessStatusService.getDatasetProcessStatus(datasetProcessStatus.getDatasetUuid(), datasetProcessStatus
        .getCrawlJob()
        .getAttempt());
    Assert.assertNotNull(datasetProcessStatus2);
  }


  /**
   * Tests the {@link DatasetProcessStatusService#list(org.gbif.api.model.common.paging.Pageable)} operation.
   */
  @Test
  public void testListAndListByDataset() {
    DatasetProcessStatus datasetProcessStatus = getTestInstance();
    datasetProcessStatusService.createDatasetProcessStatus(datasetProcessStatus);
    PagingResponse<DatasetProcessStatus> statuses =
      datasetProcessStatusService.listDatasetProcessStatus(new PagingRequest(0, 10));
    Assert.assertTrue("List operation should return at least 1 element", statuses.getResults().size() > 0);

    PagingResponse<DatasetProcessStatus> statusesByDataset =
      datasetProcessStatusService.listDatasetProcessStatus(datasetProcessStatus.getDatasetUuid(), new PagingRequest());
    Assert.assertTrue("List operation should return at least 1 element", statusesByDataset.getResults().size() > 0);

  }

  /**
   * Creates a test dataset. The dataset is persisted in the data base.
   * The installation and organization related to the dataset are created too.
   */
  private Dataset createTestDataset() {
    // endorsing node for the organization
    UUID nodeKey = nodeService.create(Nodes.newInstance());

    // owning organization (required field)
    Organization o = Organizations.newInstance(nodeKey);
    UUID organizationKey = organizationService.create(o);

    Installation i = Installations.newInstance(organizationKey);
    UUID installationKey = installationService.create(i);
    Dataset dataset = Datasets.newInstance(organizationKey, installationKey);
    dataset.setKey(datasetService.create(dataset));
    return dataset;
  }

  private DatasetProcessStatus getTestInstance() {
    DatasetProcessStatus.Builder builder = new DatasetProcessStatus.Builder();
    Dataset dataset = createTestDataset();
    CrawlJob crawlJob = new CrawlJob(dataset.getKey(), EndpointType.DIGIR, URI.create("http://gbif.org.test"), 1, null);
    builder.crawlJob(crawlJob);
    builder.crawlContext("testcontext");
    builder.startedCrawling(new Date());
    builder.finishedCrawling(new Date());
    builder.fragmentsEmitted(144);
    builder.fragmentsProcessed(144);
    builder.fragmentsReceived(144);
    builder.finishReason(FinishReason.NORMAL);
    builder.interpretedOccurrencesPersistedError(0);
    builder.interpretedOccurrencesPersistedSuccessful(144);
    builder.pagesCrawled(2);
    builder.pagesFragmentedError(0);
    builder.pagesFragmentedSuccessful(2);
    builder.rawOccurrencesPersistedError(0);
    builder.rawOccurrencesPersistedNew(144);
    builder.rawOccurrencesPersistedUnchanged(0);
    builder.rawOccurrencesPersistedUpdated(0);
    builder.verbatimOccurrencesPersistedError(0);
    builder.verbatimOccurrencesPersistedSuccessful(144);
    builder.datasetUuid(crawlJob.getDatasetUuid());
    return builder.build();
  }
}
