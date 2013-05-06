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
package org.gbif.registry2;

import org.gbif.api.model.common.paging.PagingRequest;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.common.search.SearchResponse;
import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Installation;
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.model.registry2.search.DatasetSearchParameter;
import org.gbif.api.model.registry2.search.DatasetSearchRequest;
import org.gbif.api.model.registry2.search.DatasetSearchResult;
import org.gbif.api.service.registry2.DatasetSearchService;
import org.gbif.api.service.registry2.DatasetService;
import org.gbif.api.service.registry2.InstallationService;
import org.gbif.api.service.registry2.NodeService;
import org.gbif.api.service.registry2.OrganizationService;
import org.gbif.registry2.grizzly.RegistryServer;
import org.gbif.registry2.search.DatasetIndexUpdateListener;
import org.gbif.registry2.search.SolrInitializer;
import org.gbif.registry2.utils.Datasets;
import org.gbif.registry2.utils.Installations;
import org.gbif.registry2.utils.Nodes;
import org.gbif.registry2.utils.Organizations;
import org.gbif.registry2.ws.resources.DatasetResource;
import org.gbif.registry2.ws.resources.InstallationResource;
import org.gbif.registry2.ws.resources.NodeResource;
import org.gbif.registry2.ws.resources.OrganizationResource;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.apache.solr.client.solrj.SolrServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class DatasetIT extends NetworkEntityTest<Dataset> {

  // how often to poll and wait for SOLR to update
  private static final int SOLR_UPDATE_TIMEOUT_SECS = 10;
  private static final int SOLR_UPDATE_POLL_MSECS = 10;

  private static final Logger LOG = LoggerFactory.getLogger(DatasetIT.class);

  // Resets SOLR between each method
  @Rule
  public final SolrInitializer solrRule;

  private final DatasetService service;
  private final DatasetSearchService searchService;
  private final OrganizationService organizationService;
  private final NodeService nodeService;
  private final InstallationService installationService;
  private final DatasetIndexUpdateListener datasetIndexUpdater;

  @Parameters
  public static Iterable<Object[]> data() {
    final Injector client = webserviceClient();
    final Injector webservice = webservice();

    return ImmutableList.<Object[]>of(
      new Object[] {
        webservice.getInstance(DatasetResource.class),
        webservice.getInstance(DatasetResource.class),
        webservice.getInstance(OrganizationResource.class),
        webservice.getInstance(NodeResource.class),
        webservice.getInstance(InstallationResource.class),
        webservice.getInstance(Key.get(SolrServer.class, Names.named("Dataset"))),
        webservice.getInstance(DatasetIndexUpdateListener.class)},
      new Object[] {
        client.getInstance(DatasetService.class),
        client.getInstance(DatasetSearchService.class),
        client.getInstance(OrganizationService.class),
        client.getInstance(NodeService.class),
        client.getInstance(InstallationService.class),
        null, // use the SOLR in Grizzly
        null // use the SOLR in Grizzly
      }
      );
  }

  public DatasetIT(
    DatasetService service,
    DatasetSearchService searchService,
    OrganizationService organizationService,
    NodeService nodeService,
    InstallationService installationService,
    @Nullable SolrServer solrServer,
    @Nullable DatasetIndexUpdateListener datasetIndexUpdater) {
    super(service);
    this.service = service;
    this.searchService = searchService;
    this.organizationService = organizationService;
    this.nodeService = nodeService;
    this.installationService = installationService;
    // if no SOLR are given for the test, use the SOLR in Grizzly
    solrServer = solrServer == null ? RegistryServer.INSTANCE.getSolrServer() : solrServer;
    this.datasetIndexUpdater =
      datasetIndexUpdater == null ? RegistryServer.INSTANCE.getDatasetUpdater() : datasetIndexUpdater;
    this.solrRule = new SolrInitializer(solrServer);
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

  @Test
  public void testConstituents() {
    Dataset parent = create(newEntity(), 1);
    for (int id = 0; id < 10; id++) {
      Dataset constituent = newEntity();
      constituent.setParentDatasetKey(parent.getKey());
      constituent.setType(parent.getType());
      create(constituent, id + 2);
    }

    assertEquals(10, service.get(parent.getKey()).getNumConstituents());
  }

  // Easier to test this here than OrganizationIT due to our utility dataset factory
  @Test
  public void testHostedByList() {
    Dataset dataset = create(newEntity(), 1);
    Installation i = installationService.get(dataset.getInstallationKey());
    assertNotNull("Dataset should have an installation", i);
    PagingResponse<Dataset> hosted = organizationService.hostedDatasets(i.getOrganizationKey(), new PagingRequest());
    assertEquals("This installation should have only 1 hosted dataset", 1, hosted.getResults().size());
    assertEquals("The hosted installation should serve the dataset created", hosted.getResults().get(0).getKey(),
      dataset.getKey());
  }

  // Easier to test this here than OrganizationIT due to our utility dataset factory
  @Test
  public void testOwnedByList() {
    Dataset dataset = create(newEntity(), 1);
    PagingResponse<Dataset> owned =
      organizationService.ownedDatasets(dataset.getOwningOrganizationKey(), new PagingRequest());
    assertEquals("The organization should have only 1 dataset", 1, owned.getResults().size());
    assertEquals("The organization should own the dataset created", owned.getResults().get(0).getKey(),
      dataset.getKey());
  }

  // Easier to test this here than InstallationIT due to our utility dataset factory
  @Test
  public void testHostedByInstallationList() {
    Dataset dataset = create(newEntity(), 1);
    Installation i = installationService.get(dataset.getInstallationKey());
    assertNotNull("Dataset should have an installation", i);
    PagingResponse<Dataset> hosted =
      installationService.hostedDatasets(dataset.getInstallationKey(), new PagingRequest());
    assertEquals("This installation should have only 1 hosted dataset", 1, hosted.getResults().size());
    assertEquals("The hosted installation should serve the dataset created", hosted.getResults().get(0).getKey(),
      dataset.getKey());
  }

  // Check that simple search covers contacts
  @Test
  public void testSimpleSearchContact() {
    ContactTests.testSimpleSearch(service, service, create(newEntity(), 1));
  }

  @Test
  public void testSearchListener() {
    Dataset d = newEntity();
    d = create(d, 1);
    assertSearch(d.getTitle(), 1); // 1 result expected

    // update
    String oldTitle = d.getTitle();
    d.setTitle("NEW-DATASET-TITLE");
    service.update(d);
    assertSearch("*", 1);
    assertSearch(oldTitle, 0);
    assertSearch(d.getTitle(), 1);

    // update owning organization title should be captured
    Organization owner = organizationService.get(d.getOwningOrganizationKey());
    assertSearch(owner.getTitle(), 1);
    oldTitle = owner.getTitle();
    owner.setTitle("NEW-OWNER-TITLE");
    organizationService.update(owner);
    assertSearch(oldTitle, 0);
    assertSearch(owner.getTitle(), 1);

    // update hosting organization title should be captured
    Installation installation = installationService.get(d.getInstallationKey());
    assertNotNull("Installation should be present", installation);
    Organization host = organizationService.get(installation.getOrganizationKey());
    assertSearch(host.getTitle(), 1);
    oldTitle = host.getTitle();
    host.setTitle("NEW-HOST-TITLE");
    organizationService.update(host);
    assertSearch(oldTitle, 0);
    assertSearch(host.getTitle(), 1);

    // check a deletion removes the dataset for search
    service.delete(d.getKey());
    assertSearch(host.getTitle(), 0);
  }

  @Test
  public void testInstallationMove() {
    Dataset d = newEntity();
    d = create(d, 1);
    assertSearch(d.getTitle(), 1); // 1 result expected

    UUID nodeKey = nodeService.create(Nodes.newInstance());
    Organization o = Organizations.newInstance(nodeKey);
    o.setTitle("A-NEW-ORG");
    UUID organizationKey = organizationService.create(o);
    assertSearch(o.getTitle(), 0); // No datasets hosted by that organization yet

    Installation installation = installationService.get(d.getInstallationKey());
    installation.setOrganizationKey(organizationKey);
    installationService.update(installation); // we just moved the installation to a new organization

    assertSearch(o.getTitle(), 1); // The dataset moved with the organization!
    assertSearch("*", 1);
  }

  /**
   * Utility to verify that after waiting for SOLR to update, the given query returns the expected count of results.
   */
  private void assertSearch(String query, int expected) {
    awaitUpdates(); // SOLR updates are asynchronous
    DatasetSearchRequest req = new DatasetSearchRequest();
    req.setQ(query);
    SearchResponse<DatasetSearchResult, DatasetSearchParameter> resp = searchService.search(req);
    assertNotNull(resp.getCount());
    assertEquals("SOLR does not have the expected number of results for query[" + query + "]", Long.valueOf(expected),
      resp.getCount());

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

  /**
   * Waits for SOLR update threads to finish.
   */
  public void awaitUpdates() {
    try {
      Stopwatch stopWatch = new Stopwatch().start();
      while (datasetIndexUpdater.queuedUpdates() > 0) {
        Thread.sleep(TimeUnit.MILLISECONDS.toMillis(SOLR_UPDATE_POLL_MSECS));
        if (stopWatch.elapsed(TimeUnit.SECONDS) > SOLR_UPDATE_TIMEOUT_SECS) {
          throw new IllegalStateException("Failing test due to unreasonable timeout on SOLR update");
        }
      }
      LOG.debug("Waited {} msecs for SOLR update backlog to clear successfully",
        stopWatch.elapsed(TimeUnit.MILLISECONDS));

    } catch (InterruptedException e) {
      throw Throwables.propagate(e);
    }
  }


  @Test
  public void testCitation() {
    Dataset dataset = create(newEntity(), 1);

    // empty when new
    Dataset dRead = service.get(dataset.getKey());
    assertNotNull("Citation should never be null", dRead.getCitation());
    assertEquals("ABC", dRead.getCitation().getIdentifier());
    assertEquals("This is a citation text", dRead.getCitation().getText());

    dataset.getCitation().setIdentifier("doi:123");
    dataset.getCitation().setText("GOD publishing, volume 123");
    assertCitationChange(dataset, "doi:123", "GOD publishing, volume 123");

    dataset.getCitation().setText(null);
    assertCitationChange(dataset, "doi:123", null);
  }

  private void assertCitationChange(Dataset dataset, String identifier, String text) {
    service.update(dataset);

    Dataset dRead = service.get(dataset.getKey());
    assertNotNull("Citation should never be null", dRead.getCitation());
    assertEquals(identifier, dRead.getCitation().getIdentifier());
    assertEquals(text, dRead.getCitation().getText());
  }
}
