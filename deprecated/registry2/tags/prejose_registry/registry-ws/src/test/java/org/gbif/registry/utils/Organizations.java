package org.gbif.registry.utils;

import org.gbif.api.registry.model.Organization;

import org.codehaus.jackson.type.TypeReference;


public class Organizations extends JsonBackedData<Organization> {

  private static final Organizations INSTANCE = new Organizations();

  private Organizations() {
    super("data/organization.json", new TypeReference<Organization>() {
    });
  }

  public static Organization newInstance() {
    return INSTANCE.newTypedInstance();
  }
}
