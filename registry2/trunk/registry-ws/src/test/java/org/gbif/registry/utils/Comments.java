package org.gbif.registry.utils;

import org.gbif.api.registry.model.Comment;

import org.codehaus.jackson.type.TypeReference;


public class Comments extends JsonBackedData<Comment> {

  private static final Comments INSTANCE = new Comments();

  private Comments() {
    super("data/comment.json", new TypeReference<Comment>() {
    });
  }

  public static Comment newInstance() {
    return INSTANCE.newTypedInstance();
  }
}
