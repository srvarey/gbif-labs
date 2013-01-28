package org.gbif.registry.persistence;

import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.model.WritableOrganization;
import org.gbif.api.registry.service.OrganizationService;
import org.gbif.registry.persistence.mapper.ContactMapper;
import org.gbif.registry.persistence.mapper.OrganizationMapper;
import org.gbif.registry.persistence.mapper.TagMapper;

import com.google.inject.Inject;

/**
 * A MyBATIS implementation of the service.
 */
public class OrganizationServiceMybatis extends NetworkEntityServiceMybatis<Organization, WritableOrganization>
  implements OrganizationService {

  @Inject
  public OrganizationServiceMybatis(OrganizationMapper organizationMapper, TagMapper tagMapper,
    ContactMapper contactMapper) {
    super(organizationMapper, tagMapper, contactMapper);
  }
}
