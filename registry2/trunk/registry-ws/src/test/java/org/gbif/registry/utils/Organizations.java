package org.gbif.registry.utils;

import org.gbif.api.registry.model.Organization;

import java.util.UUID;

import org.codehaus.jackson.type.TypeReference;


public class Organizations extends JsonBackedData<Organization> {

  private static final Organizations INSTANCE = new Organizations();

  private Organizations() {
    super("data/organization.json", new TypeReference<Organization>() {
    });
  }

  public static Organization newInstance(UUID endorsingNodeKey) {
    Organization o = INSTANCE.newTypedInstance();
    o.setEndorsingNodeKey(endorsingNodeKey);
    return o;
  }
}
