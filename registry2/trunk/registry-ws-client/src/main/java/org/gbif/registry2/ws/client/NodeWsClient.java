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
package org.gbif.registry2.ws.client;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Node;
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.service.registry2.NodeService;
import org.gbif.api.vocabulary.Country;
import org.gbif.registry2.ws.client.guice.RegistryWs;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.sun.jersey.api.client.WebResource;

/**
 * Client-side implementation to the NodeService.
 */
public class NodeWsClient extends BaseNetworkEntityClient<Node> implements NodeService {

  @Inject
  public NodeWsClient(@RegistryWs WebResource resource) {
    super(Node.class, resource.path("node"), null, GenericTypes.PAGING_NODE);
  }

  @Override
  public PagingResponse<Organization> organizationsEndorsedBy(UUID nodeKey, Pageable page) {
    return get(GenericTypes.PAGING_ORGANIZATION, null, null, page, nodeKey.toString(), "organization");
  }

  @Override
  public PagingResponse<Organization> pendingEndorsements(Pageable page) {
    return get(GenericTypes.PAGING_ORGANIZATION, null, null, page, "pendingEndorsement");
  }

  @Override
  public Node getByCountry(Country country) {
    return get("country", country.getIso2LetterCode());
  }

  @Override
  public List<Country> listNodeCountries() {
    return get(GenericTypes.LIST_COUNTRY, "country");
  }

  @Override
  public PagingResponse<Dataset> endorsedDatasets(@NotNull UUID nodeKey, @Nullable Pageable page) {
    return get(GenericTypes.PAGING_DATASET, page, nodeKey.toString(), "dataset");
  }
}
