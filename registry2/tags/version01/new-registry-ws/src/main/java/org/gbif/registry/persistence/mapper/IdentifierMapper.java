package org.gbif.registry.persistence.mapper;

import org.gbif.api.registry.model.Identifier;

public interface IdentifierMapper {

  int createIdentifier(Identifier identifier);
}
