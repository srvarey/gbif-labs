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
import org.gbif.api.model.registry2.Installation;
import org.gbif.api.service.registry2.InstallationService;
import org.gbif.registry2.ws.client.guice.RegistryWs;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.inject.Inject;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * Client-side implementation to the InstallationService.
 */
public class InstallationWsClient extends BaseNetworkEntityClient<Installation> implements InstallationService {

  @Inject
  public InstallationWsClient(@RegistryWs WebResource resource, @Nullable ClientFilter authFilter) {
    super(Installation.class, resource.path("installation"), authFilter, GenericTypes.PAGING_INSTALLATION);
  }

  @Override
  public PagingResponse<Dataset> getHostedDatasets(UUID installationKey, Pageable page) {
    return get(GenericTypes.PAGING_DATASET, null, null, page, String.valueOf(installationKey), "dataset");
  }

  @Override
  public PagingResponse<Installation> listDeleted(Pageable page) {
    return get(GenericTypes.PAGING_INSTALLATION, null, null, page, "deleted");
  }

  @Override
  public PagingResponse<Installation> listNonPublishing(Pageable page) {
    return get(GenericTypes.PAGING_INSTALLATION, null, null, page, "nonPublishing");
  }
}
