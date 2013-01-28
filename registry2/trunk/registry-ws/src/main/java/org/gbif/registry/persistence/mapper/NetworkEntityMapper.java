package org.gbif.registry.persistence.mapper;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.model.Tag;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.ibatis.annotations.Param;


/**
 * Mappers that perform operations on network entities.
 * 
 * @param <READABLE> The object type returned on reads
 * @param <WRITABLE> The object type used on writes
 */
public interface NetworkEntityMapper<READABLE, WRITABLE> {

  READABLE get(@Param("key") UUID key);

  void create(WRITABLE entity);

  void delete(@Param("key") UUID key);

  void update(WRITABLE entity);

  List<READABLE> list(@Nullable @Param("page") Pageable page);

  int count();

  int addTag(@Param("targetEntityKey") UUID entityKey, @Param("tagKey") int tagKey);

  int deleteTag(@Param("targetEntityKey") UUID entityKey, @Param("tagKey") int tagKey);

  List<Tag> listTags(@Param("targetEntityKey") UUID targetEntityKey);

  int addContact(@Param("targetEntityKey") UUID entityKey, @Param("contactKey") int contactKey);

  int deleteContact(@Param("targetEntityKey") UUID entityKey, @Param("contactKey") int contactKey);

  List<Contact> listContacts(@Param("targetEntityKey") UUID targetEntityKey);
}
