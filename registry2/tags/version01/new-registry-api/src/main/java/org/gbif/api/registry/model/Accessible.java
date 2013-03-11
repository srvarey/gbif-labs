package org.gbif.api.registry.model;

import java.util.List;

import javax.validation.Valid;

/**
 * Generic endpoint interface for entities.
 */
interface Accessible {

  @Valid
  List<Endpoint> getEndpoints();

  public void setEndpoints(List<Endpoint> endpoints);
}
