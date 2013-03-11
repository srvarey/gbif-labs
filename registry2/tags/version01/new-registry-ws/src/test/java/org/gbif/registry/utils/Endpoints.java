package org.gbif.registry.utils;

import org.gbif.api.registry.model.Endpoint;

import org.codehaus.jackson.type.TypeReference;


public class Endpoints extends JsonBackedData<Endpoint> {

  private static final Endpoints INSTANCE = new Endpoints();

  private Endpoints() {
    super("data/endpoint.json", new TypeReference<Endpoint>() {
    });
  }

  public static Endpoint newInstance() {
    return INSTANCE.newTypedInstance();
  }
}
