package org.gbif.api.registry.model;

import java.util.List;

import javax.validation.Valid;

/**
 * Generic comment interface for entities.
 */
interface Commentable {

  @Valid
  List<Comment> getComments();

  public void setComments(List<Comment> comments);
}
