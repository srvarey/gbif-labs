package org.gbif.api.registry.model;

import org.gbif.api.registry.vocabulary.InstallationType;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;


/**
 * A technical installation which can serve dataset(s).
 */
public class Installation implements NetworkEntity, Contactable, Endpointable, MachineTaggable, Taggable, Commentable {

  private UUID key;
  private UUID organizationKey;
  private InstallationType type;
  private String title;
  private String description;
  private String createdBy;
  private String modifiedBy;
  private Date created;
  private Date modified;
  private Date deleted;
  private List<Contact> contacts = Lists.newArrayList();
  private List<Endpoint> endpoints = Lists.newArrayList();
  private List<MachineTag> machineTags = Lists.newArrayList();
  private List<Tag> tags = Lists.newArrayList();
  private List<Comment> comments = Lists.newArrayList();

  @Override
  public UUID getKey() {
    return key;
  }

  @Override
  public void setKey(UUID key) {
    this.key = key;
  }

  @NotNull
  public UUID getOrganizationKey() {
    return organizationKey;
  }

  public void setOrganizationKey(UUID organizationKey) {
    this.organizationKey = organizationKey;
  }

  @NotNull
  public InstallationType getType() {
    return type;
  }

  public void setType(InstallationType type) {
    this.type = type;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
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
  public List<Contact> getContacts() {
    return contacts;
  }

  @Override
  public void setContacts(List<Contact> contacts) {
    this.contacts = contacts;
  }

  @Override
  public List<Endpoint> getEndpoints() {
    return endpoints;
  }

  @Override
  public void setEndpoints(List<Endpoint> endpoints) {
    this.endpoints = endpoints;
  }

  @Override
  public List<MachineTag> getMachineTags() {
    return machineTags;
  }

  @Override
  public void setMachineTags(List<MachineTag> machineTags) {
    this.machineTags = machineTags;
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
  public List<Comment> getComments() {
    return comments;
  }

  @Override
  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("key", key).add("organizationKey", organizationKey).add("type", type)
      .add("title", title).add("description", description).add("createdBy", createdBy).add("modifiedBy", modifiedBy)
      .add("created", created).add("modified", modified).add("deleted", deleted).add("contacts", contacts)
      .add("endpoints", endpoints).add("machineTags", machineTags).add("tags", tags).add("comments", comments)
      .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key, organizationKey, type, title, description, createdBy, modifiedBy, created, modified,
      deleted, contacts, endpoints, machineTags, tags, comments);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Installation) {
      Installation that = (Installation) object;
      return Objects.equal(this.key, that.key) && Objects.equal(this.organizationKey, that.organizationKey)
        && Objects.equal(this.type, that.type) && Objects.equal(this.title, that.title)
        && Objects.equal(this.description, that.description) && Objects.equal(this.createdBy, that.createdBy)
        && Objects.equal(this.modifiedBy, that.modifiedBy) && Objects.equal(this.created, that.created)
        && Objects.equal(this.modified, that.modified) && Objects.equal(this.deleted, that.deleted)
        && Objects.equal(this.contacts, that.contacts) && Objects.equal(this.endpoints, that.endpoints)
        && Objects.equal(this.machineTags, that.machineTags) && Objects.equal(this.tags, that.tags)
        && Objects.equal(this.comments, that.comments);
    }
    return false;
  }


}
