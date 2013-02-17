package org.gbif.api.registry.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * A package visible interface to provide the commonality for addresses, including the constraint validations.
 */
interface Taggable {

  @NotNull
  @Valid
  List<Tag> getTags();

  public void setTags(List<Tag> tags);
}
