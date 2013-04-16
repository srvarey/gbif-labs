package org.gbif.api.registry.service;

import org.gbif.api.registry.model.Installation;

public interface InstallationService extends NetworkEntityService<Installation>, ContactService, EndpointService,
  MachineTagService, TagService, CommentService {
}
