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
package org.gbif.registry.ws.client;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry.Dataset;
import org.gbif.api.model.registry.Metadata;
import org.gbif.api.service.registry.DatasetService;
import org.gbif.api.vocabulary.Country;
import org.gbif.api.vocabulary.DatasetType;
import org.gbif.api.vocabulary.MetadataType;
import org.gbif.registry.ws.client.guice.RegistryWs;
import org.gbif.ws.client.QueryParamBuilder;
import org.gbif.ws.util.InputStreamUtils;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * Client-side implementation to the DatasetService.
 */
public class DatasetWsClient extends BaseNetworkEntityClient<Dataset> implements DatasetService {

  @Inject
  public DatasetWsClient(@RegistryWs WebResource resource, @Nullable ClientFilter authFilter) {
    super(Dataset.class, resource.path("dataset"), authFilter, GenericTypes.PAGING_DATASET);
  }

  @Override
  public InputStream getMetadataDocument(UUID datasetKey) {
    return InputStreamUtils.wrapStream(getResource(datasetKey.toString(), "document"));
  }

  @Override
  public Metadata insertMetadata(UUID datasetKey, InputStream document) {
    return getResource(datasetKey.toString(), "document")
      .type(MediaType.APPLICATION_XML)
      .entity(document)
      .post(Metadata.class);
  }

  @Override
  public PagingResponse<Dataset> listByCountry(Country country, @Nullable DatasetType type, @Nullable Pageable page) {
    return get(GenericTypes.PAGING_DATASET, null, QueryParamBuilder.create("country", country, "type", type)
      .build(), page);
  }

  @Override
  public PagingResponse<Dataset> listConstituents(UUID datasetKey, @Nullable Pageable page) {
    return get(GenericTypes.PAGING_DATASET, page, datasetKey.toString(), "constituents");
  }

  @Override
  public List<Metadata> listMetadata(UUID datasetKey, @Nullable MetadataType type) {
    return get(GenericTypes.LIST_METADATA, QueryParamBuilder.create("type", type).build(), datasetKey.toString(),
      "metadata");
  }

  @Override
  public Metadata getMetadata(int metadataKey) {
    return get(GenericTypes.METADATA, "metadata", String.valueOf(metadataKey));
  }

  @Override
  public InputStream getMetadataDocument(int metadataKey) {
    return InputStreamUtils.wrapStream(getResource("metadata", String.valueOf(metadataKey)));
  }

  @Override
  public void deleteMetadata(int metadataKey) {
    delete(String.valueOf(metadataKey));
  }

  @Override
  public PagingResponse<Dataset> listDeleted(Pageable page) {
    return get(GenericTypes.PAGING_DATASET, null, null, page, "deleted");
  }

  @Override
  public PagingResponse<Dataset> listDuplicates(Pageable page) {
    return get(GenericTypes.PAGING_DATASET, null, null, page, "duplicate");
  }

  @Override
  public PagingResponse<Dataset> listSubdatasets(Pageable page) {
    return get(GenericTypes.PAGING_DATASET, null, null, page, "subDataset");
  }

  @Override
  public PagingResponse<Dataset> listDatasetsWithNoEndpoint(Pageable page) {
    return get(GenericTypes.PAGING_DATASET, null, null, page, "withNoEndpoint");
  }
}
