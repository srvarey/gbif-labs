package org.gbif.registry.utils;

import org.gbif.api.registry.model.Network;

import org.codehaus.jackson.type.TypeReference;


public class Networks extends JsonBackedData<Network> {

  private static final Networks INSTANCE = new Networks();

  private Networks() {
    super("data/network.json", new TypeReference<Network>() {
    });
  }

  public static Network newInstance() {
    return INSTANCE.newTypedInstance();
  }
}
