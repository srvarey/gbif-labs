package org.gbif.registry.utils;

import org.gbif.api.registry.model.Identifier;

import org.codehaus.jackson.type.TypeReference;


public class Identifiers extends JsonBackedData<Identifier> {

  private static final Identifiers INSTANCE = new Identifiers();

  private Identifiers() {
    super("data/identifier.json", new TypeReference<Identifier>() {
    });
  }

  public static Identifier newInstance() {
    return INSTANCE.newTypedInstance();
  }
}
