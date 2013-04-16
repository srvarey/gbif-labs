package org.gbif.api.registry.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Generic endpoint interface for entities.
 */
interface Endpointable {

  @Valid
  @NotNull
  List<Endpoint> getEndpoints();

  void setEndpoints(List<Endpoint> endpoints);
}
