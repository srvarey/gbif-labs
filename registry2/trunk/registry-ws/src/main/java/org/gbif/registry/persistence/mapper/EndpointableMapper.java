package org.gbif.registry.persistence.mapper;

import org.gbif.api.registry.model.Endpoint;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Param;

public interface EndpointableMapper {

  int addEndpoint(@Param("targetEntityKey") UUID entityKey, @Param("endpointKey") int endpointKey);

  int deleteEndpoint(@Param("targetEntityKey") UUID entityKey, @Param("endpointKey") int endpointKey);

  List<Endpoint> listEndpoints(@Param("targetEntityKey") UUID targetEntityKey);

}
