package org.gbif.api.registry.model;

import org.gbif.api.vocabulary.Country;
import org.gbif.api.vocabulary.Language;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;


/**
 * A GBIF data publisher.
 */
public class Organization implements NetworkEntity, Contactable, Accessible, MachineTaggable, Taggable, Identifiable,
  Commentable {

  private UUID key;
  private UUID endorsingNodeKey;
  private boolean endorsementApproved;
  private String password;
  private String title;
  private String abbreviation;
  private String description;
  private Language language;
  private String email;
  private String phone;
  private URI homepage;
  private URI logoUrl;
  private String address;
  private String city;
  private String province;
  private Country country;
  private String postalCode;
  private BigDecimal latitude;
  private BigDecimal longitude;
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

  @NotNull
  public UUID getEndorsingNodeKey() {
    return endorsingNodeKey;
  }

  public void setEndorsingNodeKey(UUID endorsingNodeKey) {
    this.endorsingNodeKey = endorsingNodeKey;
  }

  public boolean isEndorsementApproved() {
    return endorsementApproved;
  }

  public void setEndorsementApproved(boolean endorsementApproved) {
    this.endorsementApproved = endorsementApproved;
  }

  @Nullable
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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
  @Size(min = 1, max = 10)
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
  @Size(min = 5)
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Nullable
  @Size(min = 5)
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
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
  @Size(min = 1)
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Nullable
  @Size(min = 1)
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  @Nullable
  @Size(min = 1)
  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  @Nullable
  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  @Nullable
  @Size(min = 1)
  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  @Nullable
  @Min(-90)
  @Max(90)
  public BigDecimal getLatitude() {
    return latitude;
  }

  public void setLatitude(BigDecimal latitude) {
    this.latitude = latitude;
  }

  @Nullable
  @Min(-180)
  @Max(180)
  public BigDecimal getLongitude() {
    return longitude;
  }

  public void setLongitude(BigDecimal longitude) {
    this.longitude = longitude;
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
    return Objects.toStringHelper(this).add("key", key).add("endorsingNodeKey", endorsingNodeKey)
      .add("endorsementApproved", endorsementApproved).add("password", password).add("title", title)
      .add("abbreviation", abbreviation).add("description", description).add("language", language).add("email", email)
      .add("phone", phone).add("homepage", homepage).add("logoUrl", logoUrl).add("address", address).add("city", city)
      .add("province", province).add("country", country).add("postalCode", postalCode).add("latitude", latitude)
      .add("longitude", longitude).add("createdBy", createdBy).add("modifiedBy", modifiedBy).add("created", created)
      .add("modified", modified).add("deleted", deleted).add("contacts", contacts).add("endpoints", endpoints)
      .add("machineTags", machineTags).add("tags", tags).add("identifiers", identifiers).add("comments", comments)
      .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key, endorsingNodeKey, endorsementApproved, password, title, abbreviation, description,
      language, email, phone, homepage, logoUrl, address, city, province, country, postalCode, latitude, longitude,
      createdBy, modifiedBy, created, modified, deleted, contacts, endpoints, machineTags, tags, identifiers, comments);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Organization) {
      Organization that = (Organization) object;
      return Objects.equal(this.key, that.key) && Objects.equal(this.endorsingNodeKey, that.endorsingNodeKey)
        && Objects.equal(this.endorsementApproved, that.endorsementApproved)
        && Objects.equal(this.password, that.password) && Objects.equal(this.title, that.title)
        && Objects.equal(this.abbreviation, that.abbreviation) && Objects.equal(this.description, that.description)
        && Objects.equal(this.language, that.language) && Objects.equal(this.email, that.email)
        && Objects.equal(this.phone, that.phone) && Objects.equal(this.homepage, that.homepage)
        && Objects.equal(this.logoUrl, that.logoUrl) && Objects.equal(this.address, that.address)
        && Objects.equal(this.city, that.city) && Objects.equal(this.province, that.province)
        && Objects.equal(this.country, that.country) && Objects.equal(this.postalCode, that.postalCode)
        && Objects.equal(this.latitude, that.latitude) && Objects.equal(this.longitude, that.longitude)
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
