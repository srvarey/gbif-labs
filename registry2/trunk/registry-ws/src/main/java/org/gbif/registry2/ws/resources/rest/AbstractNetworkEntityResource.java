/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry2.ws.resources.rest;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingRequest;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry2.NetworkEntity;
import org.gbif.registry2.persistence.WithMyBatis;
import org.gbif.registry2.persistence.mapper.NetworkEntityMapper;
import org.gbif.registry2.ws.guice.Trim;
import org.gbif.ws.util.ExtraMediaTypes;

import java.util.UUID;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Preconditions;
import org.apache.bval.guice.Validate;
import org.mybatis.guice.transactional.Transactional;

/**
 * Provides a skeleton implementation of the core CRUD operations.
 *
 * @param <T> The type of resource that is under CRUD
 */
@Produces({MediaType.APPLICATION_JSON, ExtraMediaTypes.APPLICATION_JAVASCRIPT})
@Consumes(MediaType.APPLICATION_JSON)
public class AbstractNetworkEntityResource<T extends NetworkEntity> implements NetworkEntityRest<T> {

  private final NetworkEntityMapper<T> mapper;

  protected AbstractNetworkEntityResource(NetworkEntityMapper<T> mapper) {
    this.mapper = mapper;
  }

  @Validate
  @Transactional
  @Override
  public UUID create(@NotNull @Valid @Trim T entity) {
    return WithMyBatis.create(mapper, entity);
  }

  @Nullable
  @Override
  public T get(UUID key) {
    return WithMyBatis.get(mapper, key);
  }

  /**
   * Verifies that the path variable for the key matches the entity and then updates the entity.
   */
  @Validate
  @Transactional
  @Override
  public void update(UUID key, @NotNull @Valid @Trim T entity) {
    Preconditions.checkArgument(key.equals(entity.getKey()),
                                "Provided entity must have the same key as the resource URL");
    update(entity);
  }

  @Validate
  @Transactional
  @Override
  public void update(@NotNull @Valid @Trim T entity) {
    WithMyBatis.update(mapper, entity);
  }

  @Transactional
  @Override
  public void delete(UUID key) {
    WithMyBatis.delete(mapper, key);
  }

  @Override
  public PagingResponse<T> list(@Nullable Pageable page) {
    page = page == null ? new PagingRequest() : page;
    return WithMyBatis.list(mapper, page);
  }

}
