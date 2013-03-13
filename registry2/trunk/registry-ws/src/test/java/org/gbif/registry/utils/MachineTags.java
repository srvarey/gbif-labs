package org.gbif.registry.utils;

import org.gbif.api.registry.model.MachineTag;

import org.codehaus.jackson.type.TypeReference;


public class MachineTags extends JsonBackedData<MachineTag> {

  private static final MachineTags INSTANCE = new MachineTags();

  private MachineTags() {
    super("data/machine_tag.json", new TypeReference<MachineTag>() {
    });
  }

  public static MachineTag newInstance() {
    return INSTANCE.newTypedInstance();
  }
}
