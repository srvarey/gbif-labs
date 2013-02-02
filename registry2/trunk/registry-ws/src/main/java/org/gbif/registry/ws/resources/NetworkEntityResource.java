package org.gbif.registry.ws.resources;

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
import org.gbif.registry.ws.guice.Trim;
import org.gbif.ws.annotation.NullForNotFound;
import org.gbif.ws.util.ExtraMediaTypes;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Preconditions;
import com.google.inject.Singleton;
import org.apache.bval.guice.Validate;
import org.mybatis.guice.transactional.Transactional;

/**
 * A MyBATIS implementation of the service.
 */
@Produces({MediaType.APPLICATION_JSON, ExtraMediaTypes.APPLICATION_JAVASCRIPT})
@Consumes({MediaType.APPLICATION_JSON})
@Singleton
public class NetworkEntityResource<READABLE extends NetworkEntity, WRITABLE extends WritableNetworkEntity>
  implements NetworkEntityService<READABLE, WRITABLE> {

  private final NetworkEntityMapper<READABLE, WRITABLE> entityMapper;
  private final TagMapper tagMapper;
  private final ContactMapper contactMapper;

  public NetworkEntityResource(NetworkEntityMapper<READABLE, WRITABLE> entityMapper, TagMapper tagMapper,
    ContactMapper contactMapper) {
    this.entityMapper = entityMapper;
    this.tagMapper = tagMapper;
    this.contactMapper = contactMapper;
  }

  @POST
  @Validate
  @Transactional
  @Override
  public UUID create(@NotNull @Valid @Trim WRITABLE entity) {
    Preconditions.checkArgument(entity.getKey() == null, "Unable to create an entity which already has a key");
    entity.setKey(UUID.randomUUID());
    entityMapper.create(entity);
    return entity.getKey();
  }

  @GET
  @Path("{key}")
  @NullForNotFound
  @Override
  public READABLE get(@PathParam("key") UUID key) {
    return entityMapper.get(key);
  }

  /**
   * Method exists only to map to the URL structure we desire.
   */
  @PUT
  @Path("{key}")
  @Validate
  public void update(@PathParam("key") UUID key, @NotNull @Valid @Trim WRITABLE entity) {
    Preconditions.checkArgument(key.equals(entity.getKey()),
      "Provided entity must have the same key as the resource URL");
    update(entity);
  }

  @Validate
  @Transactional
  @Override
  public void update(@Valid @Trim WRITABLE entity) {
    Preconditions.checkNotNull(entity, "Unable to update an entity when it is not provided");
    READABLE existing = entityMapper.get(entity.getKey());
    Preconditions.checkNotNull(existing, "Unable to update a non existing entity");
    Preconditions.checkArgument(existing.getDeleted() == null, "Unable to update a previously deleted entity");
    entityMapper.update(entity);
  }

  @DELETE
  @Path("{key}")
  @Transactional
  @Override
  public void delete(@PathParam("key") UUID key) {
    entityMapper.delete(key);
  }

  @GET
  @Override
  public PagingResponse<READABLE> list(@Nullable @Context Pageable page) {
    long total = entityMapper.count();
    return new PagingResponse<READABLE>(page.getOffset(), page.getLimit(), total, entityMapper.list(page));
  }

  @POST
  @Path("{key}/tag")
  @Validate
  @Transactional
  @Override
  public int addTag(@PathParam("key") UUID targetEntityKey, @NotNull String value) {
    // Mybatis needs an object to set the key on
    Tag t = new Tag(value, "TODO: Implement with Apache shiro?");
    tagMapper.createTag(t);
    entityMapper.addTag(targetEntityKey, t.getKey());
    return t.getKey();
  }

  @DELETE
  @Path("{key}/tag/{tagKey}")
  @Override
  public void deleteTag(@PathParam("key") UUID targetEntityKey, @PathParam("tagKey") int tagKey) {
    entityMapper.deleteTag(targetEntityKey, tagKey);
  }

  @GET
  @Path("{key}/tag")
  @Override
  public List<Tag> listTags(@PathParam("key") UUID targetEntityKey, @QueryParam("owner") String owner) {
    // TODO: support the owner
    return entityMapper.listTags(targetEntityKey);
  }

  @POST
  @Path("{key}/contact")
  @Validate
  @Transactional
  @Override
  public int addContact(@PathParam("key") UUID targetEntityKey, @NotNull @Valid @Trim WritableContact contact) {
    Preconditions.checkArgument(contact.getKey() == null, "Unable to create an entity which already has a key");
    contactMapper.createContact(contact);
    entityMapper.addContact(targetEntityKey, contact.getKey());
    return contact.getKey();
  }

  @DELETE
  @Path("{key}/contact/{contactKey}")
  @Override
  public void deleteContact(@PathParam("key") UUID targetEntityKey, @PathParam("contactKey") int contactKey) {
    entityMapper.deleteContact(targetEntityKey, contactKey);
  }

  @GET
  @Path("{key}/contact")
  @Override
  public List<Contact> listContacts(@PathParam("key") UUID targetEntityKey) {
    return entityMapper.listContacts(targetEntityKey);
  }
}
