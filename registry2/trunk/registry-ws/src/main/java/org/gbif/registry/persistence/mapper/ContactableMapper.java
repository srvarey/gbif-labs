package org.gbif.registry.persistence.mapper;

import org.gbif.api.registry.model.Contact;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Param;


public interface ContactableMapper {

  int addContact(@Param("targetEntityKey") UUID entityKey, @Param("contactKey") int contactKey);

  int deleteContact(@Param("targetEntityKey") UUID entityKey, @Param("contactKey") int contactKey);

  List<Contact> listContacts(@Param("targetEntityKey") UUID targetEntityKey);

}
