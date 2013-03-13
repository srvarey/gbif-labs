package org.gbif.registry.persistence.mapper;

import org.gbif.api.registry.model.MachineTag;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Param;

public interface MachineTaggableMapper {

  int addMachineTag(@Param("targetEntityKey") UUID entityKey, @Param("machineTagKey") int machineTagKey);

  int deleteMachineTag(@Param("targetEntityKey") UUID entityKey, @Param("machineTagKey") int machineTagKey);

  List<MachineTag> listMachineTags(@Param("targetEntityKey") UUID targetEntityKey);

}
