package org.gbif.registry.ws.resources.rest;

import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.service.ContactService;
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
