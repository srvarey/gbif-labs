package org.gbif.api.registry.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Generic machine tag interface for entities.
 */
interface MachineTaggable {

  @Valid
  @NotNull
  List<MachineTag> getMachineTags();

  void setMachineTags(List<MachineTag> machineTags);
}
