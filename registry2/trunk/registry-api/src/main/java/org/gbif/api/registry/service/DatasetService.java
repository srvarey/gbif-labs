package org.gbif.api.registry.service;

import org.gbif.api.registry.model.Dataset;

/**
 * 
 */
public interface DatasetService extends NetworkEntityService<Dataset>, ContactService, EndpointService,
  MachineTagService, TagService, IdentifierService, CommentService {
}
