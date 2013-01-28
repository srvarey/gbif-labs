package org.gbif.api.registry.model;

import java.util.Date;

import com.google.common.base.Objects;


public class Tag {

  private int key; // primary key
  private String value;
  private String creator;
  private Date created;

  public Tag() {
  }

  public Tag(String value, String creator) {
    this.value = value;
    this.creator = creator;
  }

  public int getKey() {
    return key;
  }

  public void setKey(int key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key, value, creator, created);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Tag) {
      Tag that = (Tag) object;
      return Objects.equal(this.key, that.key)
        && Objects.equal(this.value, that.value)
        && Objects.equal(this.creator, that.creator)
        && Objects.equal(this.created, that.created);
    }
    return false;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("key", key)
      .add("value", value)
      .add("creator", creator)
      .add("created", created)
      .toString();
  }
}
