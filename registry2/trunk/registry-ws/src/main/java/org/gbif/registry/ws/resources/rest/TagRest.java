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
package org.gbif.registry.ws.resources.rest;

import org.gbif.api.registry.model.Tag;
import org.gbif.api.registry.service.TagService;

import java.util.List;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.bval.guice.Validate;

public interface TagRest extends TagService {

  @POST
  @Path("{key}/tag")
  @Validate
  @Override
  int addTag(@PathParam("key") UUID targetEntityKey, @NotNull String value);

  @DELETE
  @Path("{key}/tag/{tagKey}")
  @Override
  void deleteTag(@PathParam("key") UUID taggedEntityKey, @PathParam("tagKey") int tagKey);

  @GET
  @Path("{key}/tag")
  @Override
  List<Tag> listTags(@PathParam("key") UUID taggedEntityKey, @QueryParam("owner") String owner);

}
