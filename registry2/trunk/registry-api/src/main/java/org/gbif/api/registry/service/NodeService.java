package org.gbif.api.registry.service;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.registry.model.Node;
import org.gbif.api.registry.model.Organization;

import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Actions on a GBIF node.
 */
public interface NodeService extends NetworkEntityService<Node>, ContactService, MachineTagService, TagService,
  CommentService {

  /**
   * Provides access to the organizations endorsed by a single node.
   */
  PagingResponse<Organization> organizationsEndorsedBy(@NotNull UUID nodeKey, @Nullable Pageable page);

  /**
   * Provides access to the organizations that are awaiting an endorsement approval.
   */
  PagingResponse<Organization> pendingEndorsements(@Nullable Pageable page);
}
