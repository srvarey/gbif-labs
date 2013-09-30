package org.gbif.registry2.ws.client;

import org.gbif.api.model.common.search.SearchResponse;
import org.gbif.api.model.registry2.search.DatasetSearchParameter;
import org.gbif.api.model.registry2.search.DatasetSearchRequest;
import org.gbif.api.model.registry2.search.DatasetSearchResult;
import org.gbif.api.model.registry2.search.DatasetSuggestRequest;
import org.gbif.api.service.registry2.DatasetSearchService;
import org.gbif.registry2.ws.client.guice.RegistryWs;
import org.gbif.ws.client.BaseWsSuggestClient;

import java.util.List;

import com.google.inject.Inject;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

public class DatasetSearchWsClient
  extends BaseWsSuggestClient<DatasetSearchResult, DatasetSearchParameter, DatasetSearchRequest, DatasetSuggestRequest>
  implements DatasetSearchService {

  private static final GenericType<SearchResponse<DatasetSearchResult, DatasetSearchParameter>> SEARCH_TYPE =
    new GenericType<SearchResponse<DatasetSearchResult, DatasetSearchParameter>>() {
    };

  private static final GenericType<List<DatasetSearchResult>> SUGGEST_TYPE =
    new GenericType<List<DatasetSearchResult>>() {
    };

  @Inject
  public DatasetSearchWsClient(@RegistryWs WebResource resource) {
    // Note: /search is appended in the parent
    super(resource.path("dataset"), SEARCH_TYPE, SUGGEST_TYPE);
  }

  @Override
  public List<DatasetSearchResult> suggest(DatasetSuggestRequest suggestRequest) {
    return super.suggest(suggestRequest);
  }

  @Override
  public SearchResponse<DatasetSearchResult, DatasetSearchParameter> search(DatasetSearchRequest searchRequest) {
    return super.search(searchRequest);
  }


}
