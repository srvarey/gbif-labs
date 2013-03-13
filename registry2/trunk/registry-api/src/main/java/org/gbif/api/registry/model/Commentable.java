package org.gbif.api.registry.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Generic comment interface for entities.
 */
interface Commentable {

  @Valid
  @NotNull
  List<Comment> getComments();

  void setComments(List<Comment> comments);
}
