package org.gbif.registry2.search;

import org.gbif.api.model.common.paging.PagingRequest;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.service.registry2.DatasetService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A builder that will clear and build a new dataset index by paging over the given service.
 */
@Singleton
public class DatasetIndexBuilder {

  // controls how many results we request while paging over the WS
  private static final int WS_PAGE_SIZE = 100;
  private static final Logger LOG = LoggerFactory.getLogger(DatasetIndexBuilder.class);
  private final SolrServer solrServer;
  private final DatasetService datasetService;
  private final SolrAnnotatedDatasetBuilder sadBuilder;

  @Inject
  public DatasetIndexBuilder(@Named("Dataset") SolrServer solrServer, DatasetService datasetService,
    SolrAnnotatedDatasetBuilder sadBuilder) {
    this.solrServer = solrServer;
    this.datasetService = datasetService;
    this.sadBuilder = sadBuilder;
  }

  public void build() throws SolrServerException, IOException {
    LOG.info("Building a new Dataset index");
    Stopwatch stopwatch = new Stopwatch().start();
    solrServer.deleteByQuery("*:*");
    pageAndIndex();
    solrServer.commit();
    solrServer.optimize();
    LOG.info("Finished building Dataset index in " + stopwatch.elapsed(TimeUnit.SECONDS) + " secs");
  }

  /**
   * Pages over all datasets and adds to SOLR.
   */
  private void pageAndIndex() throws IOException, SolrServerException {
    PagingRequest page = new PagingRequest(0, WS_PAGE_SIZE);
    PagingResponse<Dataset> response = null;
    do {
      LOG.debug("Requesting {} datasets starting at offset {}", page.getLimit(), page.getOffset());
      response = datasetService.list(page);
      // Batching updates to SOLR proves quicker with batches of 100 - 1000 showing similar performance
      List<SolrAnnotatedDataset> batch = Lists.newArrayList();
      for (Dataset ds : response.getResults()) {
        batch.add(sadBuilder.build(ds));
      }
      if (!batch.isEmpty()) {
        solrServer.addBeans(batch);
        solrServer.commit(); // to allow eager users (or impatient developers) to see search data on startup quickly
      }
      page.nextPage();

    } while (!response.isEndOfRecords());
  }
}
