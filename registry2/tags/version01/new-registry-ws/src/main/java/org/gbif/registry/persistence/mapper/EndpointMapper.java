package org.gbif.registry.persistence.mapper;

import org.gbif.api.registry.model.Endpoint;

public interface EndpointMapper {

  int createEndpoint(Endpoint endpoint);
}
