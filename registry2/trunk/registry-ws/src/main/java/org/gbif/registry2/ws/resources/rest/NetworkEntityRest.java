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
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.service.registry2.NetworkEntityService;
import org.gbif.registry2.ws.guice.Trim;

import java.util.UUID;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.bval.guice.Validate;
import org.mybatis.guice.transactional.Transactional;

public interface NetworkEntityRest<T> extends NetworkEntityService<T> {

  @POST
  @Validate
  @Transactional
  @Override
  UUID create(@NotNull @Valid @Trim T entity);

  @DELETE
  @Path("{key}")
  @Transactional
  @Override
  void delete(@PathParam("key") UUID key);

  @GET
  @Path("{key}")
  @Nullable
  @Override
  T get(@PathParam("key") UUID key);

  @GET
  @Override
  PagingResponse<T> list(@Nullable @Context Pageable page);

  @Validate
  @Transactional
  @Override
  void update(@Valid @Trim T entity);

  /**
   * Method exists only to allow validation that the path key equals the entity key.
   */
  @PUT
  @Path("{key}")
  @Validate
  void update(@PathParam("key") UUID key, @NotNull @Valid @Trim T entity);

}
