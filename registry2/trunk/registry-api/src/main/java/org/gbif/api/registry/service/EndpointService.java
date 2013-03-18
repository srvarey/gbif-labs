package org.gbif.api.registry.service;

import org.gbif.api.registry.model.Endpoint;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;


public interface EndpointService {

  int addEndpoint(@NotNull UUID targetEntityKey, @NotNull Endpoint endpoint);

  void deleteEndpoint(@NotNull UUID targetEntityKey, int endpointKey);

  List<Endpoint> listEndpoints(@NotNull UUID targetEntityKey);
}
