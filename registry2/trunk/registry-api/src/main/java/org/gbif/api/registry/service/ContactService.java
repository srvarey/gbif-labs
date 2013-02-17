package org.gbif.api.registry.service;

import org.gbif.api.registry.model.Contact;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;


public interface ContactService {

  int addContact(@NotNull UUID targetEntityKey, Contact contact);

  void deleteContact(@NotNull UUID targetEntityKey, @NotNull int contactKey);

  List<Contact> listContacts(@NotNull UUID targetEntityKey);
}
