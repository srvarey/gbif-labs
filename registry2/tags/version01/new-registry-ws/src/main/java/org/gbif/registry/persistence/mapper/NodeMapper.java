package org.gbif.registry.persistence.mapper;

import org.gbif.api.registry.model.Node;

public interface NodeMapper extends NetworkEntityMapper<Node>, ContactableMapper, MachineTaggableMapper,
  TaggableMapper, CommentableMapper {
}
