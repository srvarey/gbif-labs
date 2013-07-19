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

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Installation;
import org.gbif.api.service.registry2.InstallationService;
import org.gbif.registry2.persistence.mapper.CommentMapper;
import org.gbif.registry2.persistence.mapper.ContactMapper;
import org.gbif.registry2.persistence.mapper.DatasetMapper;
import org.gbif.registry2.persistence.mapper.EndpointMapper;
import org.gbif.registry2.persistence.mapper.IdentifierMapper;
import org.gbif.registry2.persistence.mapper.InstallationMapper;
import org.gbif.registry2.persistence.mapper.MachineTagMapper;
import org.gbif.registry2.persistence.mapper.TagMapper;

import java.util.UUID;
import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * A MyBATIS implementation of the service.
 */
@Path("installation")
@Singleton
public class InstallationResource extends BaseNetworkEntityResource<Installation> implements InstallationService {

  private final DatasetMapper datasetMapper;
  private final InstallationMapper installationMapper;

  @Inject
  public InstallationResource(
    InstallationMapper installationMapper,
    ContactMapper contactMapper,
    EndpointMapper endpointMapper,
    IdentifierMapper identifierMapper,
    MachineTagMapper machineTagMapper,
    TagMapper tagMapper,
    CommentMapper commentMapper,
    DatasetMapper datasetMapper,
    EventBus eventBus) {
    super(installationMapper,
      commentMapper,
      contactMapper,
      endpointMapper,
      identifierMapper,
      machineTagMapper,
      tagMapper,
      Installation.class,
      eventBus);
    this.datasetMapper = datasetMapper;
    this.installationMapper = installationMapper;
  }


  /**
   * All network entities support simple (!) search with "&q=".
   * This is to support the console user interface, and is in addition to any complex, faceted search that might
   * additionally be supported, such as dataset search.
   */
  @GET
  public PagingResponse<Installation> list(@Nullable @QueryParam("q") String query, @Nullable @Context Pageable page) {
    if (Strings.isNullOrEmpty(query)) {
      return list(page);
    } else {
      return search(query, page);
    }
  }

  @GET
  @Path("{key}/dataset")
  @Override
  public PagingResponse<Dataset> getHostedDatasets(@PathParam("key") UUID installationKey, @Context Pageable page) {
    return new PagingResponse<Dataset>(page, datasetMapper.countDatasetsByInstallation(installationKey),
      datasetMapper.listDatasetsByInstallation(installationKey, page));
  }

  @GET
  @Path("deleted")
  @Override
  public PagingResponse<Installation> listDeleted(@Context Pageable page) {
    return pagingResponse(page, installationMapper.countDeleted(), installationMapper.deleted(page));
  }

  @GET
  @Path("nonPublishing")
  @Override
  public PagingResponse<Installation> listNonPublishing(@Context Pageable page) {
    return pagingResponse(page, installationMapper.countNonPublishing(), installationMapper.nonPublishing(page));
  }
}
