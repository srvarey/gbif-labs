package org.gbif.registry.persistence;

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

import com.google.inject.Inject;

/**
 * A MyBATIS implementation of the service.
 */
public class NodeServiceMybatis extends NetworkEntityServiceMybatis<Node, WritableNode, NodeMapper> implements
  NodeService {

  private final OrganizationMapper organizationMapper;

  @Inject
  public NodeServiceMybatis(NodeMapper nodeMapper, TagMapper tagMapper, ContactMapper contactMapper,
    OrganizationMapper organizationMapper) {
    super(nodeMapper, tagMapper, contactMapper);
    this.organizationMapper = organizationMapper;
  }

  @Override
  public PagingResponse<Organization> organizationsEndorsedBy(UUID nodeKey, Pageable page) {
    return PagingResponse.of(page, organizationMapper.organizationsEndorsedBy(nodeKey, page));
  }

  @Override
  public PagingResponse<Organization> pendingEndorsements(Pageable page) {
    return PagingResponse.of(page, organizationMapper.pendingEndorsements(page));
  }
}
