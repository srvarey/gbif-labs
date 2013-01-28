package org.gbif.registry.ws.client;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.model.Tag;
import org.gbif.api.registry.model.WritableContact;
import org.gbif.api.registry.model.WritableOrganization;
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
  public UUID create(WritableOrganization entity) {
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
  public void update(WritableOrganization entity) {
    super.put(entity, entity.getKey().toString());
  }

  @Override
  public int addTag(UUID targetEntityKey, String value) {
    // post the value to .../uuid/tag and expect an int back
    return super.post(Integer.class, (Object) value, targetEntityKey.toString(), "tag");
  }

  @Override
  public void deleteTag(UUID targetEntityKey, int tagKey) {
    super.delete(targetEntityKey.toString(), "tag", String.valueOf(tagKey));
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
  public int addContact(UUID targetEntityKey, WritableContact contact) {
    // post the contact to .../uuid/contact and expect an int back
    return super.post(Integer.class, contact, targetEntityKey.toString(), "contact");
  }

  @Override
  public void deleteContact(UUID targetEntityKey, int contactKey) {
    super.delete(targetEntityKey.toString(), "contact", String.valueOf(contactKey));
  }

  @Override
  public List<Contact> listContacts(UUID targetEntityKey) {
    return super.get(GenericTypes.LIST_CONTACT,
      (Locale) null,
      (MultivaluedMap<String, String>) null, // TODO: type on contact?
      (Pageable) null,
      targetEntityKey.toString(), "contact");
  }
}
