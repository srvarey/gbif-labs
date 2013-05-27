package org.gbif.registry2.search;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingRequest;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry2.Comment;
import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Endpoint;
import org.gbif.api.model.registry2.Identifier;
import org.gbif.api.model.registry2.Installation;
import org.gbif.api.model.registry2.MachineTag;
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.model.registry2.Tag;
import org.gbif.api.service.registry2.DatasetService;
import org.gbif.api.service.registry2.InstallationService;
import org.gbif.api.service.registry2.NetworkEntityService;
import org.gbif.api.service.registry2.OrganizationService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import com.google.common.base.Stopwatch;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
    InstallationService installationService, OrganizationService organizationService) {
    this.solrServer = solrServer;
    this.datasetService = datasetService;
    // We can use a cache at startup
    this.sadBuilder =
      new SolrAnnotatedDatasetBuilder(new CachingNetworkEntityService<Organization>(organizationService),
        new CachingNetworkEntityService<Installation>(installationService));
  }

  public void build() throws SolrServerException, IOException {
    LOG.info("Building a new Dataset index");
    Stopwatch stopwatch = new Stopwatch().start();
    pageAndIndex();
    solrServer.commit();
    solrServer.optimize();
    LOG.info("Finished building Dataset index in {} secs", stopwatch.elapsed(TimeUnit.SECONDS));
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

  /**
   * A lightweight cache to help improve performance of the builder.
   * 
   * @param <T> The type of entity being wrapped
   */
  private static class CachingNetworkEntityService<T> implements NetworkEntityService<T> {

    private final NetworkEntityService<T> service;
    private final LoadingCache<UUID, T> cache = CacheBuilder.newBuilder()
      .maximumSize(1000)
      .expireAfterWrite(10, TimeUnit.MINUTES)
      .build(
        new CacheLoader<UUID, T>() {

          @Override
          public T load(UUID key) throws Exception {
            return service.get(key);
          }
        });

    public CachingNetworkEntityService(NetworkEntityService<T> service) {
      this.service = service;
    }

    @Override
    public UUID create(T entity) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public void delete(UUID key) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public T get(UUID key) {
      try {
        return cache.get(key);
      } catch (ExecutionException e) {
        return null;
      }
    }

    @Override
    public PagingResponse<T> list(Pageable page) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public void update(T entity) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public PagingResponse<T> search(String query, Pageable page) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public int addComment(@NotNull UUID targetEntityKey, @NotNull Comment comment) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public void deleteComment(@NotNull UUID targetEntityKey, int commentKey) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public List<Comment> listComments(@NotNull UUID targetEntityKey) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public int addContact(@NotNull UUID targetEntityKey, @NotNull Contact contact) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public void deleteContact(@NotNull UUID targetEntityKey, int contactKey) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public List<Contact> listContacts(@NotNull UUID targetEntityKey) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public int addEndpoint(@NotNull UUID targetEntityKey, @NotNull Endpoint endpoint) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public void deleteEndpoint(@NotNull UUID targetEntityKey, int endpointKey) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public List<Endpoint> listEndpoints(@NotNull UUID targetEntityKey) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public int addIdentifier(@NotNull UUID targetEntityKey, @NotNull Identifier identifier) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public void deleteIdentifier(@NotNull UUID targetEntityKey, int identifierKey) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public List<Identifier> listIdentifiers(@NotNull UUID targetEntityKey) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public int addMachineTag(@NotNull UUID targetEntityKey, @NotNull MachineTag machineTag) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public void deleteMachineTag(@NotNull UUID targetEntityKey, int machineTagKey) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public List<MachineTag> listMachineTags(@NotNull UUID targetEntityKey) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public int addTag(@NotNull UUID targetEntityKey, @NotNull String value) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public void deleteTag(@NotNull UUID taggedEntityKey, int tagKey) {
      throw new IllegalStateException("Method not supported in caching service");
    }

    @Override
    public List<Tag> listTags(@NotNull UUID taggedEntityKey, @Nullable String owner) {
      throw new IllegalStateException("Method not supported in caching service");
    }
  }
}
