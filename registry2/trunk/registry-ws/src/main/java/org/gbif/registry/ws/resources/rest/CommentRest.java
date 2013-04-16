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
