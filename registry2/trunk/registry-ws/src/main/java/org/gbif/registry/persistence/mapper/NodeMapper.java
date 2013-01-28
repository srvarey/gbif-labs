package org.gbif.registry.persistence.mapper;

import org.gbif.api.registry.model.Node;
import org.gbif.api.registry.model.WritableNode;

public interface NodeMapper
  extends NetworkEntityMapper<Node, WritableNode> {
}
