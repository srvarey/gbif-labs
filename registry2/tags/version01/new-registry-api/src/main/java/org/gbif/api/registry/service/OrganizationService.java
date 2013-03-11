package org.gbif.api.registry.service;

import org.gbif.api.registry.model.Organization;

/**
 * 
 */
public interface OrganizationService extends NetworkEntityService<Organization>, ContactService, EndpointService,
  MachineTagService, TagService, IdentifierService, CommentService {
}
