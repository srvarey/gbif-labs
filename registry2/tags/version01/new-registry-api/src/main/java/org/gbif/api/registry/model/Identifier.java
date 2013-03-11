package org.gbif.api.registry.model;

import org.gbif.api.registry.vocabulary.IdentifierType;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.common.base.Objects;


public class Identifier {

  private Integer key;
  private IdentifierType type;
  private String identifier;
  private String createdBy;
  private Date created;

  @NotNull
  public Integer getKey() {
    return key;
  }

  public void setKey(Integer key) {
    this.key = key;
  }

  @NotNull
  public IdentifierType getType() {
    return type;
  }

  public void setType(IdentifierType type) {
    this.type = type;
  }

  @NotNull
  @Size(min = 1)
  public String getIdentifier() {
    return identifier;
  }


  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  @NotNull
  @Size(min = 3)
  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  @NotNull
  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("key", key).add("type", type).add("identifier", identifier)
      .add("createdBy", createdBy).add("created", created).toString();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key, type, identifier, createdBy, created);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Identifier) {
      Identifier that = (Identifier) object;
      return Objects.equal(this.key, that.key) && Objects.equal(this.type, that.type)
        && Objects.equal(this.identifier, that.identifier) && Objects.equal(this.createdBy, that.createdBy)
        && Objects.equal(this.created, that.created);
    }
    return false;
  }

}
