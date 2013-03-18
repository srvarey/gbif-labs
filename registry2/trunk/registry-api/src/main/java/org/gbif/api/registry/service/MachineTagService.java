package org.gbif.api.registry.service;

import org.gbif.api.registry.model.MachineTag;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;


public interface MachineTagService {

  int addMachineTag(@NotNull UUID targetEntityKey, @NotNull MachineTag machineTag);

  void deleteMachineTag(@NotNull UUID targetEntityKey, int machineTagKey);

  List<MachineTag> listMachineTags(@NotNull UUID targetEntityKey);
}
