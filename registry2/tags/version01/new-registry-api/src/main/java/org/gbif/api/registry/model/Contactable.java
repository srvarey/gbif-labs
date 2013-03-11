package org.gbif.api.registry.model;

import java.util.List;

import javax.validation.Valid;

/**
 * Generic contact interface for entities.
 */
interface Contactable {

  @Valid
  public List<Contact> getContacts();

  public void setContacts(List<Contact> contacts);
}
