package org.gbif.api.registry.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * A package visible providing the commonality for addresses, including the constraint validations.
 */
interface Contactable {

  @NotNull
  @Valid
  public List<Contact> getContacts();

  public void setContacts(List<Contact> contacts);
}
