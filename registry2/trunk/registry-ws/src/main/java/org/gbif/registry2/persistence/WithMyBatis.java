package org.gbif.registry2.persistence;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry2.Comment;
import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.Endpoint;
import org.gbif.api.model.registry2.Identifier;
import org.gbif.api.model.registry2.MachineTag;
import org.gbif.api.model.registry2.NetworkEntity;
import org.gbif.api.model.registry2.Tag;
import org.gbif.registry2.persistence.mapper.CommentMapper;
import org.gbif.registry2.persistence.mapper.CommentableMapper;
import org.gbif.registry2.persistence.mapper.ContactMapper;
import org.gbif.registry2.persistence.mapper.ContactableMapper;
import org.gbif.registry2.persistence.mapper.EndpointMapper;
import org.gbif.registry2.persistence.mapper.EndpointableMapper;
import org.gbif.registry2.persistence.mapper.IdentifiableMapper;
import org.gbif.registry2.persistence.mapper.IdentifierMapper;
import org.gbif.registry2.persistence.mapper.MachineTagMapper;
import org.gbif.registry2.persistence.mapper.MachineTaggableMapper;
import org.gbif.registry2.persistence.mapper.NetworkEntityMapper;
import org.gbif.registry2.persistence.mapper.TagMapper;
import org.gbif.registry2.persistence.mapper.TaggableMapper;

import java.util.List;
import java.util.UUID;

import com.google.common.base.Preconditions;
import org.mybatis.guice.transactional.Transactional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class WithMyBatis {

  @Transactional
  public static <T extends NetworkEntity> UUID create(NetworkEntityMapper<T> mapper, T entity) {
    checkArgument(entity.getKey() == null, "Unable to create an entity which already has a key");
    // REVIEW: If this call fails the entity will have been modified anyway! We could make a copy and return that
// instead
    entity.setKey(UUID.randomUUID());
    mapper.create(entity);
    return entity.getKey();
  }

  public static <T extends NetworkEntity> T get(NetworkEntityMapper<T> mapper, UUID key) {
    return mapper.get(key);
  }

  @Transactional
  public static <T extends NetworkEntity> void update(NetworkEntityMapper<T> mapper, T entity) {
    checkNotNull(entity, "Unable to update an entity when it is not provided");
    T existing = mapper.get(entity.getKey());
    checkNotNull(existing, "Unable to update a non existing entity");

    if (existing.getDeleted() != null) {
      // allow updates ONLY if they are undeleting too
      checkArgument(entity.getDeleted() == null,
        "Unable to update a previously deleted entity unless you clear the deletion timestamp");
    } else {
      // do not allow deletion here (for safety) - we have an explicity deletion service
      checkArgument(entity.getDeleted() == null, "Cannot delete using the update service.  Use the deletion service");
    }

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

  /**
   * The simple search option of the list.
   * 
   * @param mapper To use for the search
   * @param query A simple query string such as "Pontaurus"
   * @param page To support paging
   * @return A paging response
   */
  public static <T extends NetworkEntity> PagingResponse<T> search(NetworkEntityMapper<T> mapper, String query,
    Pageable page) {
    Preconditions.checkNotNull(page, "To search you must supply a page");
    long total = mapper.count(query);
    return new PagingResponse<T>(page.getOffset(), page.getLimit(), total, mapper.search(query, page));
  }

  @Transactional
  public static int addContact(
    ContactMapper contactMapper, ContactableMapper contactableMapper, UUID targetEntityKey, Contact contact
    ) {
    checkArgument(contact.getKey() == null, "Unable to create an entity which already has a key");
    contactMapper.createContact(contact);
    // is this a primary contact? We need to make sure it only exists once per type
    if (contact.isPrimary()) {
      contactableMapper.updatePrimaryContacts(targetEntityKey, contact.getType());
    }
    contactableMapper.addContact(targetEntityKey, contact.getKey(), contact.getType(), contact.isPrimary());
    return contact.getKey();
  }

  @Transactional
  public static int updateContact(ContactMapper contactMapper, ContactableMapper contactableMapper,
    UUID targetEntityKey, Contact contact) {
    checkArgument(contact.getKey() != null, "Unable to update an entity with no key");
    // null safe checking follows
    checkArgument(Boolean.TRUE.equals(contactableMapper.areRelated(targetEntityKey, contact.getKey())),
      "The provided contact is not connected to the given entity");
    // is this a primary contact? We need to make sure it only exists once per type
    if (contact.isPrimary()) {
      contactableMapper.updatePrimaryContacts(targetEntityKey, contact.getType());
    }
    contactMapper.updateContact(contact);
    // update the type and is_primary (is_primary will have been set to false by updatePrimaryContacts above)
    contactableMapper.updateContact(targetEntityKey, contact.getKey(), contact.getType(), contact.isPrimary());
    return contact.getKey();
  }

  public static void deleteContact(ContactableMapper contactableMapper, UUID targetEntityKey, int contactKey) {
    contactableMapper.deleteContact(targetEntityKey, contactKey);
  }

  public static List<Contact> listContacts(ContactableMapper contactableMapper, UUID targetEntityKey) {
    return contactableMapper.listContacts(targetEntityKey);
  }

  @Transactional
  public static int addEndpoint(
    EndpointMapper endpointMapper, EndpointableMapper endpointableMapper, UUID targetEntityKey, Endpoint endpoint
    ) {
    checkArgument(endpoint.getKey() == null, "Unable to create an entity which already has a key");
    endpointMapper.createEndpoint(endpoint);
    endpointableMapper.addEndpoint(targetEntityKey, endpoint.getKey());
    return endpoint.getKey();
  }

  public static void deleteEndpoint(EndpointableMapper endpointableMapper, UUID targetEntityKey, int endpointKey) {
    endpointableMapper.deleteEndpoint(targetEntityKey, endpointKey);
  }

  public static List<Endpoint> listEndpoints(EndpointableMapper endpointableMapper, UUID targetEntityKey) {
    return endpointableMapper.listEndpoints(targetEntityKey);
  }

  public static int addMachineTag(
    MachineTagMapper machineTagMapper,
    MachineTaggableMapper machineTaggableMapper,
    UUID targetEntityKey,
    MachineTag machineTag
    ) {
    checkArgument(machineTag.getKey() == null, "Unable to create an entity which already has a key");
    machineTagMapper.createMachineTag(machineTag);
    machineTaggableMapper.addMachineTag(targetEntityKey, machineTag.getKey());
    return machineTag.getKey();
  }

  public static void deleteMachineTag(
    MachineTaggableMapper machineTaggableMapper, UUID targetEntityKey, int machineTagKey
    ) {
    machineTaggableMapper.deleteMachineTag(targetEntityKey, machineTagKey);
  }

  public static List<MachineTag> listMachineTags(MachineTaggableMapper machineTaggableMapper, UUID targetEntityKey) {
    return machineTaggableMapper.listMachineTags(targetEntityKey);
  }

  @Transactional
  public static int addTag(TagMapper tagMapper, TaggableMapper taggableMapper, UUID targetEntityKey, Tag tag) {
    // Mybatis needs an object to set the key on
    tagMapper.createTag(tag);
    taggableMapper.addTag(targetEntityKey, tag.getKey());
    return tag.getKey();
  }

  public static void deleteTag(TaggableMapper taggableMapper, UUID targetEntityKey, int tagKey) {
    taggableMapper.deleteTag(targetEntityKey, tagKey);
  }

  public static List<Tag> listTags(TaggableMapper taggableMapper, UUID targetEntityKey, String owner) {
    // TODO: support the owner
    return taggableMapper.listTags(targetEntityKey);
  }

  @Transactional
  public static int addIdentifier(
    IdentifierMapper identifierMapper,
    IdentifiableMapper identifiableMapper,
    UUID targetEntityKey,
    Identifier identifier
    ) {
    checkArgument(identifier.getKey() == null, "Unable to create an entity which already has a key");
    identifierMapper.createIdentifier(identifier);
    identifiableMapper.addIdentifier(targetEntityKey, identifier.getKey());
    return identifier.getKey();
  }

  public static void deleteIdentifier(IdentifiableMapper identifiableMapper, UUID targetEntityKey, int identifierKey) {
    identifiableMapper.deleteIdentifier(targetEntityKey, identifierKey);
  }

  public static List<Identifier> listIdentifiers(IdentifiableMapper identifiableMapper, UUID targetEntityKey) {
    return identifiableMapper.listIdentifiers(targetEntityKey);
  }

  @Transactional
  public static int addComment(
    CommentMapper commentMapper, CommentableMapper commentableMapper, UUID targetEntityKey, Comment comment
    ) {
    checkArgument(comment.getKey() == null, "Unable to create an entity which already has a key");
    commentMapper.createComment(comment);
    commentableMapper.addComment(targetEntityKey, comment.getKey());
    return comment.getKey();
  }

  public static void deleteComment(CommentableMapper commentableMapper, UUID targetEntityKey, int commentKey) {
    commentableMapper.deleteComment(targetEntityKey, commentKey);
  }

  public static List<Comment> listComments(CommentableMapper commentableMapper, UUID targetEntityKey) {
    return commentableMapper.listComments(targetEntityKey);
  }

  private WithMyBatis() {
    throw new UnsupportedOperationException("Can't initialize class");
  }

}
