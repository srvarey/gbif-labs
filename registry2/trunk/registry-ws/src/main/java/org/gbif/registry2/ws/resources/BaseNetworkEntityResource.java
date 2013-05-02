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
import org.gbif.api.model.registry2.MachineTag;
import org.gbif.api.model.registry2.NetworkEntity;
import org.gbif.api.model.registry2.Tag;
import org.gbif.api.service.registry2.CommentService;
import org.gbif.api.service.registry2.MachineTagService;
import org.gbif.api.service.registry2.NetworkEntityService;
import org.gbif.api.service.registry2.TagService;
import org.gbif.registry2.events.CreateEvent;
import org.gbif.registry2.events.DeleteEvent;
import org.gbif.registry2.events.UpdateEvent;
import org.gbif.registry2.persistence.WithMyBatis;
import org.gbif.registry2.persistence.mapper.BaseNetworkEntityMapper;
import org.gbif.registry2.persistence.mapper.CommentMapper;
import org.gbif.registry2.persistence.mapper.MachineTagMapper;
import org.gbif.registry2.persistence.mapper.TagMapper;
import org.gbif.registry2.ws.guice.Trim;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import org.apache.bval.guice.Validate;
import org.mybatis.guice.transactional.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Provides a skeleton implementation of the core CRUD operations.
 * 
 * @param <T> The type of resource that is under CRUD
 */
@Produces({MediaType.APPLICATION_JSON, ExtraMediaTypes.APPLICATION_JAVASCRIPT})
@Consumes(MediaType.APPLICATION_JSON)
public class BaseNetworkEntityResource<T extends NetworkEntity>
  implements NetworkEntityService<T>, CommentService, MachineTagService, TagService {

  private static final Logger LOG = LoggerFactory.getLogger(BaseNetworkEntityResource.class);
  private final BaseNetworkEntityMapper<T> mapper;
  private final CommentMapper commentMapper;
  private final MachineTagMapper machineTagMapper;
  private final TagMapper tagMapper;
  private final Class<T> objectClass;
  private final EventBus eventBus;

  protected BaseNetworkEntityResource(
    BaseNetworkEntityMapper<T> mapper,
    CommentMapper commentMapper,
    MachineTagMapper machineTagMapper,
    TagMapper tagMapper,
    Class<T> objectClass,
    EventBus eventBus) {
    this.mapper = mapper;
    this.commentMapper = commentMapper;
    this.machineTagMapper = machineTagMapper;
    this.tagMapper = tagMapper;
    this.objectClass = objectClass;
    this.eventBus = eventBus;
  }

  @POST
  @Validate
  @Transactional
  @Override
  public UUID create(@NotNull @Valid @Trim T entity) {
    WithMyBatis.create(mapper, entity);
    eventBus.post(CreateEvent.newInstance(entity, objectClass));
    return entity.getKey();
  }

  @DELETE
  @Path("{key}")
  @Transactional
  @Override
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
  @Override
  public T get(@PathParam("key") UUID key) {
    return WithMyBatis.get(mapper, key);
  }

  /**
   * All network entities support simple (!) search with "&q=".
   * This is to support the console user interface, and is in addition to any complex, faceted search that might
   * additionally be supported, such as dataset search.
   */
  @GET
  public PagingResponse<T> list(@Nullable @QueryParam("q") String query, @Nullable @Context Pageable page) {
    if (Strings.isNullOrEmpty(query)) {
      return WithMyBatis.list(mapper, page);
    } else {
      return search(query, page);
    }
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
  public PagingResponse<T> list(@Nullable @Context Pageable page) {
    page = page == null ? new PagingRequest() : page;
    return WithMyBatis.list(mapper, page);
  }

  @Validate
  @Transactional
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
  @Transactional
  public void update(@PathParam("key") UUID key, @NotNull @Valid @Trim T entity) {
    checkArgument(key.equals(entity.getKey()), "Provided entity must have the same key as the resource URL");
    update(entity);
  }

  @POST
  @Path("{key}/comment")
  @Validate
  @Transactional
  @Override
  public int addComment(@NotNull @PathParam("key") UUID targetEntityKey, @NotNull @Valid @Trim Comment comment) {
    return WithMyBatis.addComment(commentMapper, mapper, targetEntityKey, comment);
  }

  @DELETE
  @Path("{key}/comment/{commentKey}")
  @Override
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
  @Transactional
  @Override
  public int addMachineTag(@PathParam("key") UUID targetEntityKey, @NotNull @Valid @Trim MachineTag machineTag) {
    return WithMyBatis.addMachineTag(machineTagMapper, mapper, targetEntityKey, machineTag);
  }

  @DELETE
  @Path("{key}/machinetag/{machinetagKey}")
  @Override
  public void deleteMachineTag(@PathParam("key") UUID targetEntityKey, @PathParam("machinetagKey") int machineTagKey) {
    WithMyBatis.deleteMachineTag(mapper, targetEntityKey, machineTagKey);
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

  @DELETE
  @Path("{key}/tag/{tagKey}")
  @Override
  public void deleteTag(@PathParam("key") UUID taggedEntityKey, @PathParam("tagKey") int tagKey) {
    WithMyBatis.deleteTag(mapper, taggedEntityKey, tagKey);
  }

  @GET
  @Path("{key}/tag")
  @Override
  public List<Tag> listTags(@PathParam("key") UUID taggedEntityKey, @QueryParam("owner") String owner) {
    return WithMyBatis.listTags(mapper, taggedEntityKey, owner);
  }
}
