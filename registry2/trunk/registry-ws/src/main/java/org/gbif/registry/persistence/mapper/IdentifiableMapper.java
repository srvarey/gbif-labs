package org.gbif.registry.persistence.mapper;

import org.gbif.api.registry.model.Identifier;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Param;

public interface IdentifiableMapper {

  int addIdentifier(@Param("targetEntityKey") UUID entityKey, @Param("identifierKey") int identifierKey);

  int deleteIdentifier(@Param("targetEntityKey") UUID entityKey, @Param("identifierKey") int identifierKey);

  List<Identifier> listIdentifiers(@Param("targetEntityKey") UUID identifierKey);

}
