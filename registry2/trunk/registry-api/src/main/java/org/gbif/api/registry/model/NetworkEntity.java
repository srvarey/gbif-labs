package org.gbif.api.registry.model;

import java.util.Date;
import java.util.List;

/**
 * 
 */
public interface NetworkEntity extends WritableNetworkEntity {

  Date getCreated();

  void setCreated(Date created);

  Date getModified();

  void setModified(Date modified);

  Date getDeleted();

  void setDeleted(Date deleted);

  List<Tag> getTags();

  void setTags(List<Tag> tag);

  List<Contact> getContacts();

  void setContacts(List<Contact> contacts);
}
