/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry2.ws.resources;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingRequest;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry2.Comment;
import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.Endpoint;
import org.gbif.api.model.registry2.Identifier;
import org.gbif.api.model.registry2.MachineTag;
import org.gbif.api.model.registry2.NetworkEntity;
import org.gbif.api.model.registry2.Tag;
import org.gbif.api.service.registry2.NetworkEntityService;
import org.gbif.registry2.events.CreateEvent;
import org.gbif.registry2.events.DeleteEvent;
import org.gbif.registry2.events.UpdateEvent;
import org.gbif.registry2.persistence.WithMyBatis;
import org.gbif.registry2.persistence.mapper.BaseNetworkEntityMapper;
import org.gbif.registry2.persistence.mapper.CommentMapper;
import org.gbif.registry2.persistence.mapper.ContactMapper;
import org.gbif.registry2.persistence.mapper.EndpointMapper;
import org.gbif.registry2.persistence.mapper.IdentifierMapper;
import org.gbif.registry2.persistence.mapper.MachineTagMapper;
import org.gbif.registry2.persistence.mapper.TagMapper;
import org.gbif.registry2.ws.guice.Trim;
import org.gbif.ws.server.interceptor.NullToNotFound;
import org.gbif.ws.util.ExtraMediaTypes;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import org.apache.bval.guice.Validate;
import org.mybatis.guice.transactional.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Provides a skeleton implementation of the following.
 * <ul>
 * <li>Base CRUD operations for a network entity</li>
 * <li>Comment operations</li>
 * <li>Contact operations (in addition to BaseNetworkEntityResource)</li>
 * <li>Endpoint operations (in addition to BaseNetworkEntityResource)</li>
 * <li>Identifier operations (in addition to BaseNetworkEntityResource2)</li>
 * <li>MachineTag operations</li>
 * <li>Tag operations</li>
 * </ul>
 *
 * @param <T> The type of resource that is under CRUD
 */
@Produces({MediaType.APPLICATION_JSON, ExtraMediaTypes.APPLICATION_JAVASCRIPT})
@Consumes(MediaType.APPLICATION_JSON)
public class BaseNetworkEntityResource<T extends NetworkEntity> implements NetworkEntityService<T> {

  private static final Logger LOG = LoggerFactory.getLogger(BaseNetworkEntityResource.class);
  private final BaseNetworkEntityMapper<T> mapper;
  private final CommentMapper commentMapper;
  private final MachineTagMapper machineTagMapper;
  private final TagMapper tagMapper;
  private final ContactMapper contactMapper;
  private final EndpointMapper endpointMapper;
  private final IdentifierMapper identifierMapper;
  private final Class<T> objectClass;
  private final EventBus eventBus;

  protected BaseNetworkEntityResource(
    BaseNetworkEntityMapper<T> mapper,
    CommentMapper commentMapper,
    ContactMapper contactMapper,
    EndpointMapper endpointMapper,
    IdentifierMapper identifierMapper,
    MachineTagMapper machineTagMapper,
    TagMapper tagMapper,
    Class<T> objectClass,
    EventBus eventBus) {
    this.mapper = mapper;
    this.commentMapper = commentMapper;
    this.machineTagMapper = machineTagMapper;
    this.tagMapper = tagMapper;
    this.contactMapper = contactMapper;
    this.endpointMapper = endpointMapper;
    this.identifierMapper = identifierMapper;
    this.objectClass = objectClass;
    this.eventBus = eventBus;
  }

  @POST
  @Validate
  @Trim
  @Transactional
  @Override
  public UUID create(@NotNull @Valid @Trim T entity) {
    WithMyBatis.create(mapper, entity);
    eventBus.post(CreateEvent.newInstance(entity, objectClass));
    return entity.getKey();
  }

  // relax content-type to wildcard to allow angularjs
  @DELETE
  @Path("{key}")
  @Transactional
  @Override
  @Consumes(MediaType.WILDCARD)
  public void delete(@PathParam("key") UUID key) {
    T objectToDelete = get(key);
    if (objectToDelete == null || objectToDelete.getDeleted() != null) {
      LOG.debug("Tried to delete [{}] with id [{}] but it doesn't exist or is already marked as deleted.",
        objectClass.getSimpleName(),
        key);
      return;
    }

    WithMyBatis.delete(mapper, key);
    eventBus.post(DeleteEvent.newInstance(objectToDelete, objectClass));
  }

  @GET
  @Path("{key}")
  @Nullable
  @NullToNotFound
  @Override
  public T get(@PathParam("key") UUID key) {
    return WithMyBatis.get(mapper, key);
  }

  /**
   * The simple search is not mapped to a URL, but called from the root path (e.g. /dataset) when the optional query
   * parameter is given.
   */
  @Override
  public PagingResponse<T> search(String query, @Nullable Pageable page) {
    page = page == null ? new PagingRequest() : page;
    // trim and handle null from given input
    String q = Strings.nullToEmpty(CharMatcher.WHITESPACE.trimFrom(query));
    return WithMyBatis.search(mapper, q, page);
  }

  @Override
  public PagingResponse<T> list(@Nullable Pageable page) {
    page = page == null ? new PagingRequest() : page;
    return WithMyBatis.list(mapper, page);
  }

  @Validate
  @Transactional
  @Trim
  @Override
  public void update(@NotNull @Valid @Trim T entity) {
    T oldEntity = get(entity.getKey());
    WithMyBatis.update(mapper, entity);
    eventBus.post(UpdateEvent.newInstance(entity, oldEntity, objectClass));
  }

  /**
   * Verifies that the path variable for the key matches the entity and then updates the entity.
   */
  @PUT
  @Path("{key}")
  @Validate
  @Trim
  @Transactional
  public void update(@PathParam("key") UUID key, @NotNull @Valid @Trim T entity) {
    checkArgument(key.equals(entity.getKey()), "Provided entity must have the same key as the resource URL");
    update(entity);
  }

  @POST
  @Path("{key}/comment")
  @Validate
  @Trim
  @Transactional
  @Override
  public int addComment(@NotNull @PathParam("key") UUID targetEntityKey, @NotNull @Valid @Trim Comment comment) {
    return WithMyBatis.addComment(commentMapper, mapper, targetEntityKey, comment);
  }

  // relax content-type to wildcard to allow angularjs
  @DELETE
  @Path("{key}/comment/{commentKey}")
  @Override
  @Consumes(MediaType.WILDCARD)
  public void deleteComment(@NotNull @PathParam("key") UUID targetEntityKey, @PathParam("commentKey") int commentKey) {
    WithMyBatis.deleteComment(mapper, targetEntityKey, commentKey);
  }

  @GET
  @Path("{key}/comment")
  @Override
  public List<Comment> listComments(@NotNull @PathParam("key") UUID targetEntityKey) {
    return WithMyBatis.listComments(mapper, targetEntityKey);
  }

  @POST
  @Path("{key}/machinetag")
  @Validate
  @Trim
  @Transactional
  @Override
  public int addMachineTag(@PathParam("key") UUID targetEntityKey, @NotNull @Valid @Trim MachineTag machineTag) {
    // TODO: Fix this!
    machineTag.setCreatedBy("TODO: FIXME");
    return WithMyBatis.addMachineTag(machineTagMapper, mapper, targetEntityKey, machineTag);
  }

  @Override
  public int addMachineTag(
    @NotNull UUID targetEntityKey, @NotNull String namespace, @NotNull String name, @NotNull String value
  ) {
    MachineTag machineTag = new MachineTag();
    machineTag.setNamespace(namespace);
    machineTag.setName(name);
    machineTag.setValue(value);
    return addMachineTag(targetEntityKey, machineTag);
  }

  // relax content-type to wildcard to allow angularjs
  @DELETE
  @Path("{key}/machinetag/{machinetagKey}")
  @Override
  @Consumes(MediaType.WILDCARD)
  public void deleteMachineTag(@PathParam("key") UUID targetEntityKey, @PathParam("machinetagKey") int machineTagKey) {
    WithMyBatis.deleteMachineTag(mapper, targetEntityKey, machineTagKey);
  }

  @Override
  public void deleteMachineTags(@NotNull UUID targetEntityKey, @NotNull String namespace) {
    // TODO: Write implementation
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void deleteMachineTags(
    @NotNull UUID targetEntityKey, @NotNull String namespace, @NotNull String name
  ) {
    // TODO: Write implementation
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @GET
  @Path("{key}/machinetag")
  @Override
  public List<MachineTag> listMachineTags(@PathParam("key") UUID targetEntityKey) {
    return WithMyBatis.listMachineTags(mapper, targetEntityKey);
  }

  @POST
  @Path("{key}/tag")
  @Validate
  @Override
  public int addTag(@PathParam("key") UUID targetEntityKey, @NotNull @Size(min = 1) String value) {
    return WithMyBatis.addTag(tagMapper, mapper, targetEntityKey, value);
  }

  // relax content-type to wildcard to allow angularjs
  @DELETE
  @Path("{key}/tag/{tagKey}")
  @Override
  @Consumes(MediaType.WILDCARD)
  public void deleteTag(@PathParam("key") UUID taggedEntityKey, @PathParam("tagKey") int tagKey) {
    WithMyBatis.deleteTag(mapper, taggedEntityKey, tagKey);
  }

  @GET
  @Path("{key}/tag")
  @Override
  public List<Tag> listTags(@PathParam("key") UUID taggedEntityKey, @QueryParam("owner") String owner) {
    return WithMyBatis.listTags(mapper, taggedEntityKey, owner);
  }


  @POST
  @Path("{key}/contact")
  @Validate
  @Trim
  @Transactional
  @Override
  public int addContact(@PathParam("key") UUID targetEntityKey, @NotNull @Valid @Trim Contact contact) {
    return WithMyBatis.addContact(contactMapper, mapper, targetEntityKey, contact);
  }

  @PUT
  @Path("{key}/contact/{contactKey}")
  @Validate
  @Trim
  @Transactional
  public void updateContact(@PathParam("key") UUID targetEntityKey, @PathParam("contactKey") int contactKey,
    @NotNull @Valid @Trim Contact contact) {
    // for safety, and to match a nice RESTful URL structure
    Preconditions.checkArgument(Integer.valueOf(contactKey).equals(contact.getKey()),
      "Provided contact (key) does not match the path provided");
    updateContact(targetEntityKey, contact);
  }

  @Validate
  @Trim
  @Transactional
  @Override
  public void updateContact(@PathParam("key") UUID targetEntityKey, @NotNull @Valid @Trim Contact contact) {
    Preconditions.checkNotNull(contact, "The contactkey must be provided");
    Preconditions.checkNotNull(targetEntityKey, "The target entity key must be provided");
    WithMyBatis.updateContact(contactMapper, mapper, targetEntityKey, contact);
  }

  @DELETE
  @Path("{key}/contact/{contactKey}")
  @Override
  public void deleteContact(@PathParam("key") UUID targetEntityKey, @PathParam("contactKey") int contactKey) {
    WithMyBatis.deleteContact(mapper, targetEntityKey, contactKey);
  }

  @GET
  @Path("{key}/contact")
  @Override
  public List<Contact> listContacts(@PathParam("key") UUID targetEntityKey) {
    return WithMyBatis.listContacts(mapper, targetEntityKey);
  }

  @POST
  @Path("{key}/endpoint")
  @Validate
  @Trim
  @Transactional
  @Override
  public int addEndpoint(@PathParam("key") UUID targetEntityKey, @NotNull @Valid @Trim Endpoint endpoint) {
    return WithMyBatis.addEndpoint(endpointMapper, mapper, targetEntityKey, endpoint);
  }

  @DELETE
  @Path("{key}/endpoint/{endpointKey}")
  @Override
  public void deleteEndpoint(@PathParam("key") UUID targetEntityKey, @PathParam("endpointKey") int endpointKey) {
    WithMyBatis.deleteEndpoint(mapper, targetEntityKey, endpointKey);
  }

  @GET
  @Path("{key}/endpoint")
  @Override
  public List<Endpoint> listEndpoints(@PathParam("key") UUID targetEntityKey) {
    return WithMyBatis.listEndpoints(mapper, targetEntityKey);
  }


  @POST
  @Path("{key}/identifier")
  @Validate
  @Trim
  @Transactional
  @Override
  public int addIdentifier(@PathParam("key") UUID targetEntityKey, @NotNull @Valid @Trim Identifier identifier) {
    return WithMyBatis.addIdentifier(identifierMapper, mapper, targetEntityKey, identifier);
  }

  @DELETE
  @Path("{key}/identifier/{identifierKey}")
  @Override
  public void deleteIdentifier(@PathParam("key") UUID targetEntityKey, @PathParam("identifierKey") int identifierKey) {
    WithMyBatis.deleteIdentifier(mapper, targetEntityKey, identifierKey);
  }

  @GET
  @Path("{key}/identifier")
  @Override
  public List<Identifier> listIdentifiers(@PathParam("key") UUID targetEntityKey) {
    return WithMyBatis.listIdentifiers(mapper, targetEntityKey);
  }

  /**
   * Null safe builder to construct a paging response.
   *
   * @param page page to create response for, can be null
   */
  protected static <T> PagingResponse<T> pagingResponse(@Nullable Pageable page, Long count, List<T> result) {
    if (page == null) {
      // use default request
      page = new PagingRequest();
    }
    return new PagingResponse<T>(page, count, result);
  }

}
