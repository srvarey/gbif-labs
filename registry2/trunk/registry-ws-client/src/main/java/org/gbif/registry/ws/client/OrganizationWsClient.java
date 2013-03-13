package org.gbif.registry.ws.client;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.registry.model.Comment;
import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.model.Endpoint;
import org.gbif.api.registry.model.Identifier;
import org.gbif.api.registry.model.MachineTag;
import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.model.Tag;
import org.gbif.api.registry.service.OrganizationService;
import org.gbif.registry.ws.client.guice.RegistryWs;
import org.gbif.ws.client.BaseWsGetClient;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.ws.rs.core.MultivaluedMap;

import com.google.inject.Inject;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;


/**
 * Client-side implementation to the OrganizationService.
 */
public class OrganizationWsClient extends BaseWsGetClient<Organization, UUID> implements OrganizationService {

  @Inject
  public OrganizationWsClient(@RegistryWs WebResource resource) {
    super(Organization.class, resource.path("organization"), (ClientFilter) null);
  }

  @Override
  public UUID create(Organization entity) {
    return super.post(UUID.class, entity, "/");
  }

  @Override
  public void delete(UUID key) {
    super.delete(key.toString());
  }

  @Override
  public Organization get(UUID key) {
    return super.get(key.toString());
  }

  @Override
  public PagingResponse<Organization> list(Pageable page) {
    return super.get(GenericTypes.PAGING_ORGANIZATION,
      (Locale) null,
      (MultivaluedMap<String, String>) null,
      page);
  }

  @Override
  public void update(Organization entity) {
    super.put(entity, entity.getKey().toString());
  }

  @Override
  public int addTag(UUID targetEntityKey, String value) {
    // post the value to .../uuid/tag and expect an int back
    return super.post(Integer.class, (Object) value, targetEntityKey.toString(), "tag");
  }

  @Override
  public void deleteTag(UUID targetEntityKey, int componentKey) {
    super.delete(targetEntityKey.toString(), "tag", String.valueOf(componentKey));
  }

  @Override
  public List<Tag> listTags(UUID targetEntityKey, String owner) {
    return super.get(GenericTypes.LIST_TAG,
      (Locale) null,
      (MultivaluedMap<String, String>) null, // TODO add owner here
      (Pageable) null,
      targetEntityKey.toString(), "tag");
  }

  @Override
  public int addContact(UUID targetEntityKey, Contact component) {
    // post the contact to .../uuid/contact and expect an int back
    return super.post(Integer.class, component, targetEntityKey.toString(), "contact");
  }

  @Override
  public void deleteContact(UUID targetEntityKey, int componentKey) {
    super.delete(targetEntityKey.toString(), "contact", String.valueOf(componentKey));
  }

  @Override
  public List<Contact> listContacts(UUID targetEntityKey) {
    return super.get(GenericTypes.LIST_CONTACT,
      (Locale) null,
      (MultivaluedMap<String, String>) null, // TODO: type on contact?
      (Pageable) null,
      targetEntityKey.toString(), "contact");
  }

  @Override
  public int addEndpoint(UUID targetEntityKey, Endpoint component) {
    return super.post(Integer.class, component, targetEntityKey.toString(), "endpoint");
  }

  @Override
  public void deleteEndpoint(UUID targetEntityKey, int componentKey) {
    super.delete(targetEntityKey.toString(), "endpoint", String.valueOf(componentKey));
  }

  @Override
  public List<Endpoint> listEndpoints(UUID targetEntityKey) {
    return super.get(GenericTypes.LIST_ENDPOINT,
      (Locale) null,
      (MultivaluedMap<String, String>) null, // TODO: type on endpoint?
      (Pageable) null,
      targetEntityKey.toString(), "endpoint");
  }

  @Override
  public int addMachineTag(UUID targetEntityKey, MachineTag component) {
    return super.post(Integer.class, component, targetEntityKey.toString(), "machinetag");
  }

  @Override
  public void deleteMachineTag(UUID targetEntityKey, int componentKey) {
    super.delete(targetEntityKey.toString(), "machinetag", String.valueOf(componentKey));
  }

  @Override
  public List<MachineTag> listMachineTags(UUID targetEntityKey) {
    return super.get(GenericTypes.LIST_MACHINETAG,
      (Locale) null,
      (MultivaluedMap<String, String>) null,
      (Pageable) null,
      targetEntityKey.toString(), "machinetag");
  }

  @Override
  public int addIdentifier(UUID targetEntityKey, Identifier component) {
    return super.post(Integer.class, component, targetEntityKey.toString(), "identifier");
  }

  @Override
  public void deleteIdentifier(UUID targetEntityKey, int componentKey) {
    super.delete(targetEntityKey.toString(), "identifier", String.valueOf(componentKey));
  }

  @Override
  public List<Identifier> listIdentifiers(UUID targetEntityKey) {
    return super.get(GenericTypes.LIST_IDENTIFIER,
      (Locale) null,
      (MultivaluedMap<String, String>) null, // TODO: identifier type?
      (Pageable) null,
      targetEntityKey.toString(), "identifier");
  }

  @Override
  public int addComment(UUID targetEntityKey, Comment component) {
    return super.post(Integer.class, component, targetEntityKey.toString(), "comment");
  }

  @Override
  public void deleteComment(UUID targetEntityKey, int componentKey) {
    super.delete(targetEntityKey.toString(), "comment", String.valueOf(componentKey));
  }

  @Override
  public List<Comment> listComments(UUID targetEntityKey) {
    return super.get(GenericTypes.LIST_COMMENT,
      (Locale) null,
      (MultivaluedMap<String, String>) null,
      (Pageable) null,
      targetEntityKey.toString(), "comment");
  }
}
