package org.gbif.registry.persistence.mapper;

import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.vocabulary.ContactType;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Param;


public interface ContactableMapper {

  int addContact(@Param("targetEntityKey") UUID entityKey, @Param("contactKey") int contactKey,
    @Param("type") ContactType contactType, @Param("isPrimary") boolean isPrimary);

  int deleteContact(@Param("targetEntityKey") UUID entityKey, @Param("contactKey") int contactKey);

  List<Contact> listContacts(@Param("targetEntityKey") UUID targetEntityKey);

}
