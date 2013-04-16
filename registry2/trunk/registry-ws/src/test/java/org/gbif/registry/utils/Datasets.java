package org.gbif.registry.utils;

import org.gbif.api.registry.model.Dataset;

import java.util.UUID;

import org.codehaus.jackson.type.TypeReference;


public class Datasets extends JsonBackedData<Dataset> {

  private static final Datasets INSTANCE = new Datasets();

  public Datasets() {
    super("data/dataset.json", new TypeReference<Dataset>() {
    });
  }

  public static Dataset newInstance(UUID owningOrganizationKey) {
    Dataset d = INSTANCE.newTypedInstance();
    d.setOwningOrganizationKey(owningOrganizationKey);
    return d;
  }
}
