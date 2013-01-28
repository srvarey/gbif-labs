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
public class NodeResource extends NetworkEntityResource<Node, WritableNode, NodeService> implements NodeService {

  @Inject
  public NodeResource(NodeService nodeService) {
    super(nodeService);
  }

  @GET
  @Path("{key}/organization")
  @Override
  public PagingResponse<Organization> organizationsEndorsedBy(@PathParam("key") UUID nodeKey, @Context Pageable page) {
    return this.getService().organizationsEndorsedBy(nodeKey, page);
  }

  @GET
  @Path("pendingEndorsement")
  @Override
  public PagingResponse<Organization> pendingEndorsements(@Context Pageable page) {
    return this.getService().pendingEndorsements(page);
  }
}
