package org.gbif.api.registry.model;

import java.util.Date;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;


/**
 * A GBIF voting participant node.
 */
public class Organization extends WritableOrganization implements NetworkEntity {

  // TODO validations
  private Date created;
  private Date modified;
  private Date deleted;
  private List<Tag> tags = Lists.newArrayList();
  private List<Contact> contacts = Lists.newArrayList();

  @Override
  public Date getCreated() {
    return created;
  }

  @Override
  public void setCreated(Date created) {
    this.created = created;
  }

  @Override
  public Date getModified() {
    return modified;
  }

  @Override
  public void setModified(Date modified) {
    this.modified = modified;
  }

  @Override
  public Date getDeleted() {
    return deleted;
  }

  @Override
  public void setDeleted(Date deleted) {
    this.deleted = deleted;
  }

  @Override
  public List<Tag> getTags() {
    return tags;
  }

  @Override
  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }

  @Override
  public List<Contact> getContacts() {
    return contacts;
  }

  @Override
  public void setContacts(List<Contact> contacts) {
    this.contacts = contacts;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(super.hashCode(), created, modified, deleted, tags, contacts);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Organization) {
      if (!super.equals(object)) {
        return false;
      }
      Organization that = (Organization) object;
      return Objects.equal(this.created, that.created)
        && Objects.equal(this.modified, that.modified)
        && Objects.equal(this.deleted, that.deleted)
        && Objects.equal(this.tags, that.tags)
        && Objects.equal(this.contacts, that.contacts);
    }
    return false;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("super", super.toString())
      .add("created", created)
      .add("modified", modified)
      .add("deleted", deleted)
      .add("tags", tags)
      .add("contacts", contacts)
      .toString();
  }
}
