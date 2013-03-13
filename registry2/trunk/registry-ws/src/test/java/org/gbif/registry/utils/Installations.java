package org.gbif.registry.utils;

import org.gbif.api.registry.model.Installation;

import org.codehaus.jackson.type.TypeReference;


public class Installations extends JsonBackedData<Installation> {

  private static final Installations INSTANCE = new Installations();

  private Installations() {
    super("data/installation.json", new TypeReference<Installation>() {
    });
  }

  public static Installation newInstance() {
    return INSTANCE.newTypedInstance();
  }
}
