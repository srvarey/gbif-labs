package org.gbif.registry.utils;

import org.gbif.api.registry.model.Node;

import org.codehaus.jackson.type.TypeReference;


public class Nodes extends JsonBackedData<Node> {

  private static final Nodes INSTANCE = new Nodes();

  private Nodes() {
    super("data/node.json", new TypeReference<Node>() {
    });
  }

  public static Node newInstance() {
    return INSTANCE.newTypedInstance();
  }
}
