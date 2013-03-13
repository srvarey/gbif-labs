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
