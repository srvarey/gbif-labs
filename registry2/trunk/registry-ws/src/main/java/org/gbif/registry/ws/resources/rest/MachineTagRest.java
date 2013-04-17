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
