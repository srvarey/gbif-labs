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

import org.gbif.api.registry.model.Comment;
import org.gbif.api.registry.service.CommentService;
import org.gbif.registry.ws.guice.Trim;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.bval.guice.Validate;
import org.mybatis.guice.transactional.Transactional;

/**
 * Defines the URL structure for all comment APIS.
 * Note: while validation annotations are included here, they will not be inherited unless used in a framework that
 * supports this (Guice validation module does not). Therefore implementations must repeat the validation annotations,
 * as shown here. Jackson annotations are inherited.
 */
public interface CommentRest extends CommentService {

  @POST
  @Path("{key}/comment")
  @Validate
  @Transactional
  @Override
  int addComment(@PathParam("key") UUID targetEntityKey, @NotNull @Valid @Trim Comment comment);

  @DELETE
  @Path("{key}/comment/{commentKey}")
  @Override
  void deleteComment(@PathParam("key") UUID targetEntityKey, @PathParam("commentKey") int commentKey);

  @GET
  @Path("{key}/comment")
  @Override
  List<Comment> listComments(@PathParam("key") UUID targetEntityKey);

}
