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
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.service.registry2.OrganizationService;
import org.gbif.registry2.persistence.mapper.CommentMapper;
import org.gbif.registry2.persistence.mapper.ContactMapper;
import org.gbif.registry2.persistence.mapper.DatasetMapper;
import org.gbif.registry2.persistence.mapper.EndpointMapper;
import org.gbif.registry2.persistence.mapper.IdentifierMapper;
import org.gbif.registry2.persistence.mapper.MachineTagMapper;
import org.gbif.registry2.persistence.mapper.OrganizationMapper;
import org.gbif.registry2.persistence.mapper.TagMapper;

import java.util.UUID;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * A MyBATIS implementation of the service.
 */
@Path("organization")
@Singleton
public class OrganizationResource extends BaseNetworkEntityResource3<Organization> implements OrganizationService {

  private final DatasetMapper datasetMapper;

  @Inject
  public OrganizationResource(
    OrganizationMapper organizationMapper,
    ContactMapper contactMapper,
    EndpointMapper endpointMapper,
    MachineTagMapper machineTagMapper,
    TagMapper tagMapper,
    IdentifierMapper identifierMapper,
    CommentMapper commentMapper,
    DatasetMapper datasetMapper,
    EventBus eventBus) {
    super(organizationMapper,
      commentMapper,
      contactMapper,
      endpointMapper,
      identifierMapper,
      machineTagMapper,
      tagMapper,
      Organization.class,
      eventBus);
    this.datasetMapper = datasetMapper;
  }

  @GET
  @Path("{key}/hostedDataset")
  @Override
  public PagingResponse<Dataset> hostedDatasets(@PathParam("key") UUID organizationKey, @Context Pageable page) {
    return new PagingResponse<Dataset>(page, null, datasetMapper.listDatasetsHostedBy(organizationKey, page));
  }

  @GET
  @Path("{key}/ownedDataset")
  @Override
  public PagingResponse<Dataset> ownedDatasets(@PathParam("key") UUID organizationKey, @Context Pageable page) {
    return new PagingResponse<Dataset>(page, null, datasetMapper.listDatasetsOwnedBy(organizationKey, page));
  }
}
