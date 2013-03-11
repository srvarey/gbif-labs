package org.gbif.api.registry.model;

import java.util.List;

import javax.validation.Valid;

/**
 * Generic machine tag interface for entities.
 */
interface MachineTaggable {

  @Valid
  List<MachineTag> getMachineTags();

  public void setMachineTags(List<MachineTag> machineTags);
}
