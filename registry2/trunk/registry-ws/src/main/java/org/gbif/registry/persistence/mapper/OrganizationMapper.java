package org.gbif.registry.persistence.mapper;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.model.WritableOrganization;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.ibatis.annotations.Param;

public interface OrganizationMapper
  extends NetworkEntityMapper<Organization, WritableOrganization> {

  /**
   * At higher levels this appears on the NodeService, but it makes a cleaner MyBatis implementation on this mapper.
   */
  List<Organization> organizationsEndorsedBy(@Param("nodeKey") UUID nodeKey, @Nullable @Param("page") Pageable page);

  /**
   * At higher levels this appears on the NodeService, but it makes a cleaner MyBatis implementation on this mapper.
   */
  List<Organization> pendingEndorsements(@Nullable @Param("page") Pageable page);
}
