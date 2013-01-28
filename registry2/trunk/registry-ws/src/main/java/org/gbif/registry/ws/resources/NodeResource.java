package org.gbif.registry.ws.resources;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.registry.model.Node;
import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.model.WritableNode;
import org.gbif.api.registry.service.NodeService;

import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import com.google.inject.Inject;

@Path("node")
public class NodeResource extends NetworkEntityResource<Node, WritableNode> implements NodeService {

  private final NodeService nodeService;

  @Inject
  public NodeResource(NodeService nodeService) {
    super(nodeService);
    this.nodeService = nodeService;
  }

  @GET
  @Path("{key}/organization")
  @Override
  public PagingResponse<Organization> organizationsEndorsedBy(@PathParam("key") UUID nodeKey, @Context Pageable page) {
    return nodeService.organizationsEndorsedBy(nodeKey, page);
  }

  @GET
  @Path("pendingEndorsement")
  @Override
  public PagingResponse<Organization> pendingEndorsements(@Context Pageable page) {
    return nodeService.pendingEndorsements(page);
  }
}
