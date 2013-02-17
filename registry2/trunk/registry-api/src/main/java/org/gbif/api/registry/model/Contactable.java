package org.gbif.api.registry.model;

import java.util.List;

/**
 * A package visible interface to provide the commonality for objects that expose contacts.
 */
interface Contactable {

  public List<Contact> getContacts();

  public void setContacts(List<Contact> contacts);
}
