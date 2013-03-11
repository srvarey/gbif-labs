package org.gbif.api.registry.model;

import java.util.List;

import javax.validation.Valid;

/**
 * Generic identifier interface for entities.
 */
interface Identifiable {

  @Valid
  List<Identifier> getIdentifiers();

  public void setIdentifiers(List<Identifier> identifiers);
}
