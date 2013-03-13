package org.gbif.registry.persistence.mapper;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.registry.model.Dataset;
import org.gbif.api.registry.model.Metadata;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.ibatis.annotations.Param;

public interface DatasetMapper extends NetworkEntityMapper<Dataset>, ContactableMapper, AccessibleMapper,
  MachineTaggableMapper, TaggableMapper, IdentifiableMapper, CommentableMapper {

  /**
   * Obtains the metadata associated to a single dataset.
   */
  Metadata getMetadata(@Param("datasetKey") UUID datasetKey);

  /**
   * Obtains a list of all the constituent datasets that are part of this network.
   */
  List<Dataset> listDatasetsInNetwork(@Param("networkKey") UUID networkKey, @Nullable @Param("page") Pageable page);
}
