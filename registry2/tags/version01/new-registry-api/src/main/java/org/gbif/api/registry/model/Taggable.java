package org.gbif.api.registry.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Generic tag interface for entities.
 */
interface Taggable {

  @Valid
  @NotNull
  List<Tag> getTags();

  void setTags(List<Tag> tags);
}
