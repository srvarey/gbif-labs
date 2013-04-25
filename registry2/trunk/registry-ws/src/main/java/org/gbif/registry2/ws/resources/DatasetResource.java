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
package org.gbif.registry2.ws.resources;

import org.gbif.api.model.common.search.SearchResponse;
import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.search.DatasetSearchParameter;
import org.gbif.api.model.registry2.search.DatasetSearchRequest;
import org.gbif.api.model.registry2.search.DatasetSearchResult;
import org.gbif.api.model.registry2.search.DatasetSuggestRequest;
import org.gbif.api.service.registry2.DatasetSearchService;
import org.gbif.api.service.registry2.DatasetService;
import org.gbif.registry2.persistence.mapper.CommentMapper;
import org.gbif.registry2.persistence.mapper.ContactMapper;
import org.gbif.registry2.persistence.mapper.DatasetMapper;
import org.gbif.registry2.persistence.mapper.EndpointMapper;
import org.gbif.registry2.persistence.mapper.IdentifierMapper;
import org.gbif.registry2.persistence.mapper.MachineTagMapper;
import org.gbif.registry2.persistence.mapper.TagMapper;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A MyBATIS implementation of the service.
 */
@Path("dataset")
@Singleton
public class DatasetResource extends BaseNetworkEntityResource3<Dataset> implements DatasetService,
  DatasetSearchService {

  private static final Logger LOG = LoggerFactory.getLogger(DatasetResource.class);

  private final DatasetSearchService searchService;

  @Inject
  public DatasetResource(DatasetMapper datasetMapper, ContactMapper contactMapper, EndpointMapper endpointMapper,
    MachineTagMapper machineTagMapper, TagMapper tagMapper, IdentifierMapper identifierMapper,
    CommentMapper commentMapper, EventBus eventBus, DatasetSearchService searchService) {
    super(datasetMapper,
      commentMapper,
      contactMapper,
      endpointMapper,
      identifierMapper,
      machineTagMapper,
      tagMapper,
      Dataset.class,
      eventBus);
    this.searchService = searchService;
  }

  @GET
  @Path("search")
  @Override
  public SearchResponse<DatasetSearchResult, DatasetSearchParameter>
    search(@Context DatasetSearchRequest searchRequest) {
    LOG.debug("Search operation received {}", searchRequest);
    return searchService.search(searchRequest);
  }

  @Path("suggest")
  @GET
  @Override
  public List<DatasetSearchResult> suggest(@Context DatasetSuggestRequest suggestRequest) {
    LOG.debug("Suggest operation received {}", suggestRequest);
    return searchService.suggest(suggestRequest);
  }
}
