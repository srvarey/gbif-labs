package org.gbif.api.registry.service;

import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.model.Tag;
import org.gbif.api.registry.model.WritableContact;
import org.gbif.api.service.common.CrudService;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;


public interface NetworkEntityService<READABLE, WRITABLE> extends CrudService<READABLE, WRITABLE, UUID> {

  int addTag(@NotNull UUID targetEntityKey, String value);

  void deleteTag(@NotNull UUID taggedEntityKey, @NotNull int tagKey);

  List<Tag> listTags(@NotNull UUID taggedEntityKey, @Nullable String owner);

  int addContact(@NotNull UUID targetEntityKey, WritableContact contact);

  void deleteContact(@NotNull UUID targetEntityKey, @NotNull int contactKey);

  List<Contact> listContacts(@NotNull UUID targetEntityKey);

}
