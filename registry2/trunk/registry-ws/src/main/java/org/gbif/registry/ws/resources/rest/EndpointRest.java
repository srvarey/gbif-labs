package org.gbif.registry.ws.resources.rest;

import org.gbif.api.registry.model.Endpoint;
import org.gbif.api.registry.service.EndpointService;
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

public interface EndpointRest extends EndpointService {

  @POST
  @Path("{key}/endpoint")
  @Validate
  @Transactional
  @Override
  int addEndpoint(@PathParam("key") UUID targetEntityKey, @NotNull @Valid @Trim Endpoint endpoint);

  @DELETE
  @Path("{key}/endpoint/{endpointKey}")
  @Override
  void deleteEndpoint(@PathParam("key") UUID targetEntityKey, @PathParam("endpointKey") int endpointKey);

  @GET
  @Path("{key}/endpoint")
  @Override
  List<Endpoint> listEndpoints(@PathParam("key") UUID targetEntityKey);
}
