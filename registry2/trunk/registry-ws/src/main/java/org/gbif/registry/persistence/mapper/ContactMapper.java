package org.gbif.registry.persistence.mapper;

import org.gbif.api.registry.model.Contact;


public interface ContactMapper {

  int createContact(Contact contact);
}
