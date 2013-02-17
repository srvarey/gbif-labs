package org.gbif.api.registry.model;

import java.util.List;

/**
 * A package visible interface to provide the commonality for addresses, including the constraint validations.
 */
interface Taggable {

  List<Tag> getTags();

  public void setTags(List<Tag> tags);
}
