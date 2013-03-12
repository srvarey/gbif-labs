package org.gbif.api.registry.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Generic identifier interface for entities.
 */
interface Identifiable {

  @Valid
  @NotNull
  List<Identifier> getIdentifiers();

  void setIdentifiers(List<Identifier> identifiers);
}
