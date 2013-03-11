package org.gbif.registry.persistence.mapper;

import org.gbif.api.registry.model.MachineTag;

public interface MachineTagMapper {

  int createMachineTag(MachineTag machineTag);
}
