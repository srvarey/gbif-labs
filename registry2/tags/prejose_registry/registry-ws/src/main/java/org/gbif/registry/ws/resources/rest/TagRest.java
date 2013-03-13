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
  public int addTag(@PathParam("key") UUID targetEntityKey, @NotNull String value);

  @DELETE
  @Path("{key}/tag/{tagKey}")
  @Override
  public void deleteTag(@PathParam("key") UUID targetEntityKey, @PathParam("tagKey") int tagKey);

  @GET
  @Path("{key}/tag")
  @Override
  public List<Tag> listTags(@PathParam("key") UUID targetEntityKey, @QueryParam("owner") String owner);
}
