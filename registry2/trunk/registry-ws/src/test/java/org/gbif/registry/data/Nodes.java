package org.gbif.registry.data;

import org.gbif.api.registry.model.Node;
import org.gbif.api.registry.model.WritableNode;

import java.util.Map;

import org.codehaus.jackson.type.TypeReference;


public class Nodes extends JsonBackedData<Map<Nodes.TYPE, Node>, Map<Nodes.TYPE, WritableNode>> {

  public enum TYPE {
    UK, DK
  };

  private final static Nodes INSTANCE = new Nodes();

  private Nodes() {
    super("data/nodes.json", new TypeReference<Map<TYPE, Node>>() {
    }, new TypeReference<Map<TYPE, WritableNode>>() {
    });
  };

  public static WritableNode writableInstanceOf(TYPE type) {
    return INSTANCE.writable().get(type);
  }

  public static Node instanceOf(TYPE type) {
    return INSTANCE.readable().get(type);
  }
}
