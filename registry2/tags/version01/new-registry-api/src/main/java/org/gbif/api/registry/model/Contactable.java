package org.gbif.api.registry.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Generic contact interface for entities.
 */
interface Contactable {

  @Valid
  @NotNull
  List<Contact> getContacts();

  void setContacts(List<Contact> contacts);
}
