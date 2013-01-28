package org.gbif.registry.persistence.mapper;

import org.gbif.api.registry.model.WritableContact;


public interface ContactMapper {

  int createContact(WritableContact contact);
}
