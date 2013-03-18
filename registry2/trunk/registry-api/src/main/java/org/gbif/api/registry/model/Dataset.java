package org.gbif.api.registry.model;

import org.gbif.api.registry.vocabulary.DatasetSubType;
import org.gbif.api.registry.vocabulary.DatasetType;
import org.gbif.api.vocabulary.Language;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;


/**
 * A GBIF dataset which provides occurrence data, checklist data or metadata.
 */
public class Dataset implements NetworkEntity, Contactable, Endpointable, MachineTaggable, Taggable, Identifiable,
  Commentable {

  private UUID key;
  private UUID parentDatasetKey;
  private UUID duplicateOfDatasetKey;
  private UUID installationKey;
  private UUID owningOrganizationKey;
  private boolean external;
  private DatasetType type;
  private DatasetSubType subType;
  private String title;
  private String alias;
  private String abbreviation;
  private String description;
  private Language language;
  private URI homepage;
  private URI logoUrl;
  private String citation;
  private String citationIdentifier;
  private String rights;
  private boolean lockedForAutoUpdate;
  private String createdBy;
  private String modifiedBy;
  private Date created;
  private Date modified;
  private Date deleted;
  private List<Contact> contacts = Lists.newArrayList();
  private List<Endpoint> endpoints = Lists.newArrayList();
  private List<MachineTag> machineTags = Lists.newArrayList();
  private List<Tag> tags = Lists.newArrayList();
  private List<Identifier> identifiers = Lists.newArrayList();
  private List<Comment> comments = Lists.newArrayList();

  @Override
  public UUID getKey() {
    return key;
  }

  @Override
  public void setKey(UUID key) {
    this.key = key;
  }

  @Nullable
  public UUID getParentDatasetKey() {
    return parentDatasetKey;
  }

  public void setParentDatasetKey(UUID parentDatasetKey) {
    this.parentDatasetKey = parentDatasetKey;
  }

  @Nullable
  public UUID getDuplicateOfDatasetKey() {
    return duplicateOfDatasetKey;
  }

  public void setDuplicateOfDatasetKey(UUID duplicateOfDatasetKey) {
    this.duplicateOfDatasetKey = duplicateOfDatasetKey;
  }

  @NotNull
  public UUID getInstallationKey() {
    return installationKey;
  }

  public void setInstallationKey(UUID installationKey) {
    this.installationKey = installationKey;
  }

  @NotNull
  public UUID getOwningOrganizationKey() {
    return owningOrganizationKey;
  }

  public void setOwningOrganizationKey(UUID owningOrganizationKey) {
    this.owningOrganizationKey = owningOrganizationKey;
  }

  public boolean isExternal() {
    return external;
  }

  public void setExternal(boolean external) {
    this.external = external;
  }

  @NotNull
  public DatasetType getType() {
    return type;
  }

  public void setType(DatasetType type) {
    this.type = type;
  }

  @Nullable
  public DatasetSubType getSubType() {
    return subType;
  }

  public void setSubType(DatasetSubType subType) {
    this.subType = subType;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public void setTitle(String title) {
    this.title = title;
  }

  @Nullable
  @Size(min = 2, max = 50)
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Nullable
  @Size(min = 1, max = 50)
  public String getAbbreviation() {
    return abbreviation;
  }

  public void setAbbreviation(String abbreviation) {
    this.abbreviation = abbreviation;
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
  public Language getLanguage() {
    return language;
  }

  public void setLanguage(Language language) {
    this.language = language;
  }

  @Nullable
  public URI getHomepage() {
    return homepage;
  }

  public void setHomepage(URI homepage) {
    this.homepage = homepage;
  }

  @Nullable
  public URI getLogoUrl() {
    return logoUrl;
  }

  public void setLogoUrl(URI logoUrl) {
    this.logoUrl = logoUrl;
  }

  @Nullable
  @Size(min = 10)
  public String getCitation() {
    return citation;
  }

  public void setCitation(String citation) {
    this.citation = citation;
  }

  @Nullable
  @Size(min = 10, max = 100)
  public String getCitationIdentifier() {
    return citationIdentifier;
  }

  public void setCitationIdentifier(String citationIdentifier) {
    this.citationIdentifier = citationIdentifier;
  }

  @Nullable
  @Size(min = 1)
  public String getRights() {
    return rights;
  }

  public void setRights(String rights) {
    this.rights = rights;
  }

  public boolean isLockedForAutoUpdate() {
    return lockedForAutoUpdate;
  }

  public void setLockedForAutoUpdate(boolean lockedForAutoUpdate) {
    this.lockedForAutoUpdate = lockedForAutoUpdate;
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
  public List<Identifier> getIdentifiers() {
    return identifiers;
  }

  @Override
  public void setIdentifiers(List<Identifier> identifiers) {
    this.identifiers = identifiers;
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
    return Objects.toStringHelper(this).add("key", key).add("parentDatasetKey", parentDatasetKey)
      .add("duplicateOfDatasetKey", duplicateOfDatasetKey).add("installationKey", installationKey)
      .add("owningOrganizationKey", owningOrganizationKey).add("external", external).add("type", type)
      .add("subType", subType).add("title", title).add("alias", alias).add("abbreviation", abbreviation)
      .add("description", description).add("language", language).add("homepage", homepage).add("logoUrl", logoUrl)
      .add("citation", citation).add("citationIdentifier", citationIdentifier).add("rights", rights)
      .add("lockedForAutoUpdate", lockedForAutoUpdate).add("createdBy", createdBy).add("modifiedBy", modifiedBy)
      .add("created", created).add("modified", modified).add("deleted", deleted).add("contacts", contacts)
      .add("endpoints", endpoints).add("machineTags", machineTags).add("tags", tags).add("identifiers", identifiers)
      .add("comments", comments).toString();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key, parentDatasetKey, duplicateOfDatasetKey, installationKey, owningOrganizationKey,
      external, type, subType, title, alias, abbreviation, description, language, homepage, logoUrl, citation,
      citationIdentifier, rights, lockedForAutoUpdate, createdBy, modifiedBy, created, modified, deleted, contacts,
      endpoints, machineTags, tags, identifiers, comments);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Dataset) {
      Dataset that = (Dataset) object;
      return Objects.equal(this.key, that.key) && Objects.equal(this.parentDatasetKey, that.parentDatasetKey)
        && Objects.equal(this.duplicateOfDatasetKey, that.duplicateOfDatasetKey)
        && Objects.equal(this.installationKey, that.installationKey)
        && Objects.equal(this.owningOrganizationKey, that.owningOrganizationKey)
        && Objects.equal(this.external, that.external) && Objects.equal(this.type, that.type)
        && Objects.equal(this.subType, that.subType) && Objects.equal(this.title, that.title)
        && Objects.equal(this.alias, that.alias) && Objects.equal(this.abbreviation, that.abbreviation)
        && Objects.equal(this.description, that.description) && Objects.equal(this.language, that.language)
        && Objects.equal(this.homepage, that.homepage) && Objects.equal(this.logoUrl, that.logoUrl)
        && Objects.equal(this.citation, that.citation)
        && Objects.equal(this.citationIdentifier, that.citationIdentifier) && Objects.equal(this.rights, that.rights)
        && Objects.equal(this.lockedForAutoUpdate, that.lockedForAutoUpdate)
        && Objects.equal(this.createdBy, that.createdBy) && Objects.equal(this.modifiedBy, that.modifiedBy)
        && Objects.equal(this.created, that.created) && Objects.equal(this.modified, that.modified)
        && Objects.equal(this.deleted, that.deleted) && Objects.equal(this.contacts, that.contacts)
        && Objects.equal(this.endpoints, that.endpoints) && Objects.equal(this.machineTags, that.machineTags)
        && Objects.equal(this.tags, that.tags) && Objects.equal(this.identifiers, that.identifiers)
        && Objects.equal(this.comments, that.comments);
    }
    return false;
  }

}
