package org.gbif.registry.ws.resources.rest;

import org.gbif.api.registry.model.MachineTag;
import org.gbif.api.registry.service.MachineTagService;
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

public interface MachineTagRest extends MachineTagService {

  @POST
  @Path("{key}/machinetag")
  @Validate
  @Transactional
  @Override
  int addMachineTag(@PathParam("key") UUID targetEntityKey, @NotNull @Valid @Trim MachineTag machineTag);

  @DELETE
  @Path("{key}/machinetag/{machinetagKey}")
  @Override
  void deleteMachineTag(@PathParam("key") UUID targetEntityKey, @PathParam("machinetagKey") int machinetagKey);

  @GET
  @Path("{key}/machinetag")
  @Override
  List<MachineTag> listMachineTags(@PathParam("key") UUID targetEntityKey);
}
