package org.gbif.api.registry.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Generic endpoint interface for entities.
 */
interface Accessible {

  @Valid
  @NotNull
  List<Endpoint> getEndpoints();

  public void setEndpoints(List<Endpoint> endpoints);
}
