/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry2.ws.resources;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry2.Node;
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.service.registry2.NodeService;
import org.gbif.api.vocabulary.Country;
import org.gbif.registry2.ims.Augmenter;
import org.gbif.registry2.persistence.mapper.CommentMapper;
import org.gbif.registry2.persistence.mapper.MachineTagMapper;
import org.gbif.registry2.persistence.mapper.NodeMapper;
import org.gbif.registry2.persistence.mapper.OrganizationMapper;
import org.gbif.registry2.persistence.mapper.TagMapper;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@Path("node")
public class NodeResource extends BaseNetworkEntityResource<Node> implements NodeService {

  private final NodeMapper nodeMapper;
  private final OrganizationMapper organizationMapper;
  private final Augmenter nodeAugmenter;

  @Inject
  public NodeResource(
    NodeMapper nodeMapper,
    OrganizationMapper organizationMapper,
    MachineTagMapper machineTagMapper,
    TagMapper tagMapper,
    CommentMapper commentMapper,
    EventBus eventBus,
    Augmenter nodeAugmenter
  ) {
    super(nodeMapper, commentMapper, machineTagMapper, tagMapper, Node.class, eventBus);
    this.nodeMapper = nodeMapper;
    this.organizationMapper = organizationMapper;
    this.nodeAugmenter = nodeAugmenter;
  }

  @Nullable
  @Override
  public Node get(UUID key) {
    return nodeAugmenter.augment(super.get(key));
  }

  @Override
  public PagingResponse<Node> list(@Nullable Pageable page) {
    PagingResponse<Node> resp = super.list(page);
    for (Node n : resp.getResults()) {
      nodeAugmenter.augment(n);
    }
    return resp;
  }

  @GET
  @Path("{key}/organization")
  @Override
  public PagingResponse<Organization> organizationsEndorsedBy(@PathParam("key") UUID nodeKey, @Context Pageable page) {
    return new PagingResponse<Organization>(page, null, organizationMapper.organizationsEndorsedBy(nodeKey, page));
  }

  @GET
  @Path("pendingEndorsement")
  @Override
  public PagingResponse<Organization> pendingEndorsements(@Context Pageable page) {
    return new PagingResponse<Organization>(page, null, organizationMapper.pendingEndorsements(page));
  }

  @GET
  @Path("country/{key}")
  @Nullable
  public Node getByCountry(@PathParam("key") String isoCode) {
    return getByCountry(Country .fromIsoCode(isoCode));
  }

  @Nullable
  @Override
  public Node getByCountry(Country country) {
    return nodeAugmenter.augment(nodeMapper.getByCountry(country));
  }

  @GET
  @Path("country")
  @Override
  public List<Country> listNodeCountries() {
    return nodeMapper.listNodeCountries();
  }

}
