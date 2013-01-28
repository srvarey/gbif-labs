package org.gbif.registry.persistence;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.model.NetworkEntity;
import org.gbif.api.registry.model.Tag;
import org.gbif.api.registry.model.WritableContact;
import org.gbif.api.registry.model.WritableNetworkEntity;
import org.gbif.api.registry.service.NetworkEntityService;
import org.gbif.registry.persistence.mapper.ContactMapper;
import org.gbif.registry.persistence.mapper.NetworkEntityMapper;
import org.gbif.registry.persistence.mapper.TagMapper;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;
import org.mybatis.guice.transactional.Transactional;

/**
 * A MyBATIS implementation of the service.
 */
abstract class NetworkEntityServiceMybatis<READABLE extends NetworkEntity, WRITABLE extends WritableNetworkEntity, MAPPER extends NetworkEntityMapper<READABLE, WRITABLE>>
  implements NetworkEntityService<READABLE, WRITABLE> {

  private final MAPPER entityMapper;
  private final TagMapper tagMapper;
  private final ContactMapper contactMapper;

  public NetworkEntityServiceMybatis(MAPPER entityMapper, TagMapper tagMapper,
    ContactMapper contactMapper) {
    this.entityMapper = entityMapper;
    this.tagMapper = tagMapper;
    this.contactMapper = contactMapper;
  }

  @Override
  @Transactional
  public UUID create(WRITABLE entity) {
    Preconditions.checkArgument(entity.getKey() == null, "Unable to create an entity which already has a key");
    entity.setKey(UUID.randomUUID());
    entityMapper.create(entity);
    return entity.getKey();
  }

  @Override
  public READABLE get(UUID key) {
    return entityMapper.get(key);
  }

  @Override
  @Transactional
  public void update(WRITABLE entity) {
    READABLE existing = entityMapper.get(entity.getKey());
    Preconditions.checkNotNull(existing, "Unable to update a non existing entity");
    Preconditions.checkArgument(existing.getDeleted() == null, "Unable to update a previously deleted entity");
    entityMapper.update(entity);
  }

  @Override
  @Transactional
  public void delete(UUID key) {
    READABLE existing = entityMapper.get(key);
    Preconditions.checkArgument(existing.getDeleted() == null, "Unable to delete a previously deleted entity");
    entityMapper.delete(key);
  }

  @Override
  public PagingResponse<READABLE> list(Pageable page) {
    int total = entityMapper.count();
    return new PagingResponse<READABLE>(page.getOffset(), page.getLimit(), (long) total, entityMapper.list(page));
  }

  @Override
  @Transactional
  public int addTag(UUID targetEntityKey, String value) {
    // Mybatis needs an object to set the key on
    Tag t = new Tag(value, "TODO: Implement with Apache shiro?");
    tagMapper.createTag(t);
    entityMapper.addTag(targetEntityKey, t.getKey());
    return t.getKey();
  }

  @Override
  public void deleteTag(@NotNull UUID targetEntityKey, @NotNull int tagKey) {
    entityMapper.deleteTag(targetEntityKey, tagKey);
  }

  @Override
  public List<Tag> listTags(UUID targetEntityKey, String owner) {
    // TODO: support the owner
    return entityMapper.listTags(targetEntityKey);
  }

  @Transactional
  @Override
  public int addContact(UUID targetEntityKey, WritableContact contact) {
    Preconditions.checkNotNull(contact, "Unable to update a non existing entity");
    Preconditions.checkArgument(contact.getKey() == null, "Unable to create an entity which already has a key");
    contactMapper.createContact(contact);
    entityMapper.addContact(targetEntityKey, contact.getKey());
    return contact.getKey();
  }

  @Override
  public void deleteContact(UUID targetEntityKey, int contactKey) {
    entityMapper.deleteContact(targetEntityKey, contactKey);
  }

  @Override
  public List<Contact> listContacts(UUID targetEntityKey) {
    return entityMapper.listContacts(targetEntityKey);
  }


  protected MAPPER getEntityMapper() {
    return entityMapper;
  }
}
