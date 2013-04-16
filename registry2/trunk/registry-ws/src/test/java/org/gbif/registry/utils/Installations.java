package org.gbif.registry.utils;

import org.gbif.api.registry.model.Installation;

import java.util.UUID;

import org.codehaus.jackson.type.TypeReference;


public class Installations extends JsonBackedData<Installation> {

  private static final Installations INSTANCE = new Installations();

  private Installations() {
    super("data/installation.json", new TypeReference<Installation>() {
    });
  }

  public static Installation newInstance(UUID organizationKey) {
    Installation i = INSTANCE.newTypedInstance();
    i.setOrganizationKey(organizationKey);
    return i;
  }
}
