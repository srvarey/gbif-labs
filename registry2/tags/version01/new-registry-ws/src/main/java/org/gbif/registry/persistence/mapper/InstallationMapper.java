package org.gbif.registry.persistence.mapper;

import org.gbif.api.registry.model.Installation;

public interface InstallationMapper extends NetworkEntityMapper<Installation>, ContactableMapper, AccessibleMapper,
  MachineTaggableMapper, TaggableMapper, CommentableMapper {

}
