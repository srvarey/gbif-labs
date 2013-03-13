package org.gbif.registry.ws.resources;

import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.model.Tag;
import org.gbif.api.registry.service.OrganizationService;
import org.gbif.registry.persistence.WithMyBatis;
import org.gbif.registry.persistence.mapper.ContactMapper;
import org.gbif.registry.persistence.mapper.OrganizationMapper;
import org.gbif.registry.persistence.mapper.TagMapper;
import org.gbif.registry.ws.resources.rest.AbstractNetworkEntityResource;
import org.gbif.registry.ws.resources.rest.ContactRest;
import org.gbif.registry.ws.resources.rest.TagRest;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * A MyBATIS implementation of the service.
 */
@Path("organization")
@Singleton
public class OrganizationResource extends AbstractNetworkEntityResource<Organization> implements OrganizationService,
  ContactRest, TagRest {

  private final OrganizationMapper organizationMapper;
  private final ContactMapper contactMapper;
  private final TagMapper tagMapper;

  @Inject
  public OrganizationResource(OrganizationMapper organizationMapper, TagMapper tagMapper, ContactMapper contactMapper) {
    super(organizationMapper);
    this.organizationMapper = organizationMapper;
    this.contactMapper = contactMapper;
    this.tagMapper = tagMapper;
  }

  @Override
  public int addContact(UUID targetEntityKey, Contact contact) {
    return WithMyBatis.addContact(contactMapper, organizationMapper, targetEntityKey, contact);
  }

  @Override
  public void deleteContact(UUID targetEntityKey, int contactKey) {
    WithMyBatis.deleteContact(organizationMapper, targetEntityKey, contactKey);
  }

  @Override
  public List<Contact> listContacts(UUID targetEntityKey) {
    return WithMyBatis.listContacts(organizationMapper, targetEntityKey);
  }

  @Override
  public int addTag(UUID targetEntityKey, String value) {
    return WithMyBatis.addTag(tagMapper, organizationMapper, targetEntityKey, value);
  }

  @Override
  public void deleteTag(UUID targetEntityKey, int tagKey) {
    WithMyBatis.deleteTag(organizationMapper, targetEntityKey, tagKey);
  }

  @Override
  public List<Tag> listTags(UUID targetEntityKey, String owner) {
    return WithMyBatis.listTags(organizationMapper, targetEntityKey, owner);
  }
}
