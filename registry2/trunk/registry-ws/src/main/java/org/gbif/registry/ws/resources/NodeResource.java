package org.gbif.registry.ws.resources;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.registry.model.Node;
import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.model.WritableNode;
import org.gbif.api.registry.service.NodeService;
import org.gbif.registry.persistence.mapper.ContactMapper;
import org.gbif.registry.persistence.mapper.NodeMapper;
import org.gbif.registry.persistence.mapper.OrganizationMapper;
import org.gbif.registry.persistence.mapper.TagMapper;

import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * A MyBATIS implementation of the service.
 */
@Path("node")
@Singleton
public class NodeResource extends NetworkEntityResource<Node, WritableNode> implements
  NodeService {

  private final OrganizationMapper organizationMapper;

  @Inject
  public NodeResource(NodeMapper nodeMapper, TagMapper tagMapper, ContactMapper contactMapper,
    OrganizationMapper organizationMapper) {
    super(nodeMapper, tagMapper, contactMapper);
    this.organizationMapper = organizationMapper;
  }

  @GET
  @Path("{key}/organization")
  @Override
  public PagingResponse<Organization> organizationsEndorsedBy(@PathParam("key") UUID nodeKey, @Context Pageable page) {
    return PagingResponse.of(page, organizationMapper.organizationsEndorsedBy(nodeKey, page));
  }

  @GET
  @Path("pendingEndorsement")
  @Override
  public PagingResponse<Organization> pendingEndorsements(@Context Pageable page) {
    return PagingResponse.of(page, organizationMapper.pendingEndorsements(page));
  }
}
