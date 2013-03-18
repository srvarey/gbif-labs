package org.gbif.api.registry.service;

import org.gbif.api.registry.model.Network;

public interface NetworkService extends NetworkEntityService<Network>, ContactService, EndpointService,
  MachineTagService, TagService, CommentService {
}
