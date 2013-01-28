package org.gbif.registry.ws.resources;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.model.NetworkEntity;
import org.gbif.api.registry.model.Tag;
import org.gbif.api.registry.model.WritableContact;
import org.gbif.api.registry.model.WritableNetworkEntity;
import org.gbif.api.registry.service.NetworkEntityService;
import org.gbif.registry.ws.guice.Trim;
import org.gbif.ws.annotation.NullForNotFound;
import org.gbif.ws.util.ExtraMediaTypes;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Preconditions;
import org.apache.bval.guice.Validate;

@Produces({MediaType.APPLICATION_JSON, ExtraMediaTypes.APPLICATION_JAVASCRIPT})
@Consumes({MediaType.APPLICATION_JSON})
public class NetworkEntityResource<READABLE extends NetworkEntity, WRITABLE extends WritableNetworkEntity>
  implements NetworkEntityService<READABLE, WRITABLE> {

  private final NetworkEntityService<READABLE, WRITABLE> service;

  protected NetworkEntityResource(NetworkEntityService<READABLE, WRITABLE> service) {
    this.service = service;
  }

  protected final NetworkEntityService<READABLE, WRITABLE> getService() {
    return service;
  }

  @POST
  @Trim
  @Validate
  @Override
  public UUID create(@org.apache.bval.Validate @Trim WRITABLE entity) {
    return service.create(entity);
  }

  @GET
  @Path("/{key}")
  @Validate
  @NullForNotFound
  @Override
  public READABLE get(@NotNull @PathParam("key") UUID key) {
    return service.get(key);
  }

  @GET
  @Override
  public PagingResponse<READABLE> list(@Nullable @Context Pageable pageable) {
    return service.list(pageable);
  }

  @PUT
  @Path("/{key}")
  @Trim
  @Validate
  public void update(@NotNull @PathParam("key") UUID key, @org.apache.bval.Validate @Trim WRITABLE entity) {
    Preconditions.checkArgument(key.equals(entity.getKey()),
      "Provided entity must have the same key as the resource URL");
    service.update(entity);
  }

  @Validate
  @Override
  public void update(@org.apache.bval.Validate @Trim WRITABLE entity) {
    service.update(entity);
  }

  @DELETE
  @Path("/{key}")
  @Validate
  @Override
  public void delete(@NotNull @PathParam("key") UUID key) {
    service.delete(key);
  }

  @POST
  @Path("/{key}/tag")
  @Validate
  @Override
  public int addTag(@NotNull @PathParam("key") UUID targetEntityKey, @NotNull String value) {
    return service.addTag(targetEntityKey, value);
  }

  @DELETE
  @Path("/{key}/tag/{tagKey}")
  @Override
  public void deleteTag(@PathParam("key") UUID targetEntityKey, @PathParam("tagKey") int tagKey) {
    service.deleteTag(targetEntityKey, tagKey);
  }

  @GET
  @Path("/{key}/tag")
  @Override
  public List<Tag> listTags(@PathParam("key") UUID targetEntityKey, @QueryParam("owner") String owner) {
    return service.listTags(targetEntityKey, owner);
  }

  @POST
  @Path("/{key}/contact")
  @Override
  public int addContact(@PathParam("key") UUID targetEntityKey, WritableContact contact) {
    return service.addContact(targetEntityKey, contact);
  }

  @DELETE
  @Path("/{key}/contact/{contactKey}")
  @Override
  public void deleteContact(@PathParam("key") UUID targetEntityKey, @PathParam("contactKey") int contactKey) {
    service.deleteContact(targetEntityKey, contactKey);
  }

  @GET
  @Path("/{key}/contact")
  @Override
  public List<Contact> listContacts(@PathParam("key") UUID targetEntityKey) {
    return service.listContacts(targetEntityKey);
  }
}
