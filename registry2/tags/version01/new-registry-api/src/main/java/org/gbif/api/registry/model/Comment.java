package org.gbif.api.registry.model;

import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.common.base.Objects;


public class Comment {

  private Integer key;
  private String content;
  private String createdBy;
  private String modifiedBy;
  private Date created;
  private Date modified;

  @Min(1)
  public Integer getKey() {
    return key;
  }

  public void setKey(Integer key) {
    this.key = key;
  }

  @NotNull
  @Size(min = 1)
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
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
  @Size(min = 3)
  public String getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  @NotNull
  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  @NotNull
  public Date getModified() {
    return modified;
  }

  public void setModified(Date modified) {
    this.modified = modified;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("key", key).add("content", content).add("createdBy", createdBy)
      .add("modifiedBy", modifiedBy).add("created", created).add("modified", modified).toString();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key, content, createdBy, modifiedBy, created, modified);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Comment) {
      Comment that = (Comment) object;
      return Objects.equal(this.key, that.key) && Objects.equal(this.content, that.content)
        && Objects.equal(this.createdBy, that.createdBy) && Objects.equal(this.modifiedBy, that.modifiedBy)
        && Objects.equal(this.created, that.created) && Objects.equal(this.modified, that.modified);
    }
    return false;
  }


}
