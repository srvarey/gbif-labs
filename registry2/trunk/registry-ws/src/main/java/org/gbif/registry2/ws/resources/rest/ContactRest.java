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

import org.gbif.api.model.registry2.Contact;
import org.gbif.api.service.registry2.ContactService;
import org.gbif.registry2.ws.guice.Trim;

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

public interface ContactRest extends ContactService {

  @POST
  @Path("{key}/contact")
  @Validate
  @Transactional
  @Override
  int addContact(@PathParam("key") UUID targetEntityKey, @NotNull @Valid @Trim Contact contact);

  @DELETE
  @Path("{key}/contact/{contactKey}")
  @Override
  void deleteContact(@PathParam("key") UUID targetEntityKey, @PathParam("contactKey") int contactKey);

  @GET
  @Path("{key}/contact")
  @Override
  List<Contact> listContacts(@PathParam("key") UUID targetEntityKey);

}
