package org.gbif.registry.ws.resources;

import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.model.WritableOrganization;
import org.gbif.api.registry.service.OrganizationService;
import org.gbif.registry.persistence.mapper.ContactMapper;
import org.gbif.registry.persistence.mapper.OrganizationMapper;
import org.gbif.registry.persistence.mapper.TagMapper;

import javax.ws.rs.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * A MyBATIS implementation of the service.
 */
@Path("organization")
@Singleton
public class OrganizationResource extends NetworkEntityResource<Organization, WritableOrganization>
  implements OrganizationService {

  @Inject
  public OrganizationResource(OrganizationMapper organizationMapper, TagMapper tagMapper,
    ContactMapper contactMapper) {
    super(organizationMapper, tagMapper, contactMapper);
  }
}
