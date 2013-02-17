package org.gbif.registry.search;

import org.gbif.api.registry.model.Node;

import java.util.UUID;

import com.google.common.base.Objects;
import org.apache.solr.client.solrj.beans.Field;

/**
 * The model object mapped to the SOLR schema.
 */
public class SearchableNode {

  // @Field is set on the custom String setter
  private UUID key;
  @Field
  private String title;

  // Required by SOLR
  public SearchableNode() {
  }

  public SearchableNode(Node n) {
    this.key = n.getKey();
    this.title = n.getTitle();
  }

  public UUID getKey() {
    return key;
  }

  public void setKey(UUID key) {
    this.key = key;
  }

  // String setter required for SOLR (seems uuidfield always comes as string)
  @Field
  public void setKey(String key) {
    this.key = UUID.fromString(key);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("key", key)
      .add("title", title)
      .toString();
  }
}
