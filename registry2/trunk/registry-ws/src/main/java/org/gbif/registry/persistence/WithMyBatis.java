package org.gbif.registry.persistence;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.model.NetworkEntity;
import org.gbif.api.registry.model.Tag;
import org.gbif.registry.persistence.mapper.ContactMapper;
import org.gbif.registry.persistence.mapper.ContactableMapper;
import org.gbif.registry.persistence.mapper.NetworkEntityMapper;
import org.gbif.registry.persistence.mapper.TagMapper;
import org.gbif.registry.persistence.mapper.TaggableMapper;

import java.util.List;
import java.util.UUID;

import com.google.common.base.Preconditions;
import org.mybatis.guice.transactional.Transactional;

/**
 * Static utility methods for common MyBatis operations supporting fluent style coding.
 */
public class WithMyBatis {

  @Transactional
  public static <T extends NetworkEntity> UUID create(NetworkEntityMapper<T> mapper, T entity) {
    Preconditions.checkArgument(entity.getKey() == null, "Unable to create an entity which already has a key");
    entity.setKey(UUID.randomUUID());
    mapper.create(entity);
    return entity.getKey();
  }

  public static <T extends NetworkEntity> T get(NetworkEntityMapper<T> mapper, UUID key) {
    return mapper.get(key);
  }

  @Transactional
  public static <T extends NetworkEntity> void update(NetworkEntityMapper<T> mapper, T entity) {
    Preconditions.checkNotNull(entity, "Unable to update an entity when it is not provided");
    T existing = mapper.get(entity.getKey());
    Preconditions.checkNotNull(existing, "Unable to update a non existing entity");
    Preconditions.checkArgument(existing.getDeleted() == null, "Unable to update a previously deleted entity");
    mapper.update(entity);
  }

  @Transactional
  public static <T extends NetworkEntity> void delete(NetworkEntityMapper<T> mapper, UUID key) {
    mapper.delete(key);
  }

  public static <T extends NetworkEntity> PagingResponse<T> list(NetworkEntityMapper<T> mapper, Pageable page) {
    long total = mapper.count();
    return new PagingResponse<T>(page.getOffset(), page.getLimit(), total, mapper.list(page));
  }

  @Transactional
  public static int addTag(TagMapper tagMapper, TaggableMapper taggableMapper, UUID targetEntityKey, String value) {
    // Mybatis needs an object to set the key on
    Tag t = new Tag(value, "TODO: Implement with Apache shiro?");
    tagMapper.createTag(t);
    taggableMapper.addTag(targetEntityKey, t.getKey());
    return t.getKey();
  }

  public static void deleteTag(TaggableMapper taggableMapper, UUID targetEntityKey, int tagKey) {
    taggableMapper.deleteTag(targetEntityKey, tagKey);
  }

  public static List<Tag> listTags(TaggableMapper taggableMapper, UUID targetEntityKey, String owner) {
    // TODO: support the owner
    return taggableMapper.listTags(targetEntityKey);
  }

  @Transactional
  public static int addContact(ContactMapper contactMapper, ContactableMapper contactableMapper, UUID targetEntityKey,
    Contact contact) {
    Preconditions.checkArgument(contact.getKey() == null, "Unable to create an entity which already has a key");
    contactMapper.createContact(contact);
    contactableMapper.addContact(targetEntityKey, contact.getKey());
    return contact.getKey();
  }

  public static void deleteContact(ContactableMapper contactableMapper, UUID targetEntityKey, int contactKey) {
    contactableMapper.deleteContact(targetEntityKey, contactKey);
  }

  public static List<Contact> listContacts(ContactableMapper contactableMapper, UUID targetEntityKey) {
    return contactableMapper.listContacts(targetEntityKey);
  }

}
