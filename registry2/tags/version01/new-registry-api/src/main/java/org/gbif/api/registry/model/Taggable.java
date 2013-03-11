package org.gbif.api.registry.model;

import java.util.List;

import javax.validation.Valid;

/**
 * Generic tag interface for entities.
 */
interface Taggable {

  @Valid
  List<Tag> getTags();

  public void setTags(List<Tag> tags);
}
