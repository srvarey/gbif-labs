package org.gbif.registry.utils;

import org.gbif.api.registry.model.Dataset;

import org.codehaus.jackson.type.TypeReference;


public class Datasets extends JsonBackedData<Dataset> {

  private static final Datasets INSTANCE = new Datasets();

  private Datasets() {
    super("data/dataset.json", new TypeReference<Dataset>() {
    });
  }

  public static Dataset newInstance() {
    return INSTANCE.newTypedInstance();
  }
}
