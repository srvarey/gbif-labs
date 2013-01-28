package org.gbif.registry.ws.resources;

import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.model.WritableOrganization;
import org.gbif.api.registry.service.OrganizationService;

import javax.ws.rs.Path;

import com.google.inject.Inject;

@Path("organization")
public class OrganizationResource extends
  NetworkEntityResource<Organization, WritableOrganization> implements OrganizationService {

  @Inject
  public OrganizationResource(OrganizationService organizationService) {
    super(organizationService);
  }
}
