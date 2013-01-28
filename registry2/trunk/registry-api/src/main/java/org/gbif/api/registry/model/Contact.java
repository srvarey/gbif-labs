package org.gbif.api.registry.model;

import java.util.Date;

import com.google.common.base.Objects;


public class Contact extends WritableContact {

  private Date created;
  private Date modified;

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getModified() {
    return modified;
  }

  public void setModified(Date modified) {
    this.modified = modified;
  }


  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("super", super.toString())
      .add("created", created)
      .add("modified", modified)
      .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(super.hashCode(), created, modified);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Contact) {
      if (!super.equals(object)) {
        return false;
      }
      Contact that = (Contact) object;
      return Objects.equal(this.created, that.created)
        && Objects.equal(this.modified, that.modified);
    }
    return false;
  }
}
