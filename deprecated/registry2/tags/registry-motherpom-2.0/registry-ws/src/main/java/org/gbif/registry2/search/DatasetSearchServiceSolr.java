package org.gbif.registry2.search;

import org.gbif.api.model.registry2.search.DatasetSearchParameter;
import org.gbif.api.model.registry2.search.DatasetSearchRequest;
import org.gbif.api.model.registry2.search.DatasetSearchResult;
import org.gbif.api.model.registry2.search.DatasetSuggestRequest;
import org.gbif.api.service.registry2.DatasetSearchService;
import org.gbif.common.search.service.SolrSearchSuggestService;

import java.util.Map;

import com.google.common.collect.ImmutableSortedMap;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;

/**
 * Dataset search implementation using the provided SOLR instance.
 */
public class DatasetSearchServiceSolr extends SolrSearchSuggestService
  <DatasetSearchResult, DatasetSearchParameter, SolrAnnotatedDataset, DatasetSearchRequest, DatasetSuggestRequest>
  implements DatasetSearchService {

  // Order by best score, then alphabetically by title
  private static final Map<String, SolrQuery.ORDER> PRIMARY_SORT_ORDER = ImmutableSortedMap.of(
    "score", SolrQuery.ORDER.desc,
    "dataset_title", SolrQuery.ORDER.asc);

  @Inject
  public DatasetSearchServiceSolr(@Named("Dataset") SolrServer server) {
    super(server, DatasetSearchResult.class, SolrAnnotatedDataset.class, DatasetSearchParameter.class,
      PRIMARY_SORT_ORDER, true // use the enum value, not the ordinal, since the index was built this way
    );
  }
}
