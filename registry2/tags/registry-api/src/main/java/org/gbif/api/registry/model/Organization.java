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
 * A GBIF voting participant node.
 */
public class Organization implements NetworkEntity, Taggable, Contactable {

  // TODO move all validations to getters
  @Nullable
  private UUID key;
  @Nullable
  private UUID endorsingNodeKey;
  private boolean endorsementApproved;
  @NotNull
  @Size(min = 2, max = 255)
  private String title;
  @Nullable
  private String alias;
  @NotNull
  @Size(min = 10)
  private String description;
  @NotNull
  private Language language; // for things like description
  // @email ?
  @Nullable
  @Size(min = 5, max = 100)
  private String email;
  @Nullable
  @Size(min = 5, max = 50)
  private String phone;
  // @URL
  @Nullable
// @Size(min = 10, max = 100) // TODO
  private URI homepage;
  // @URL
  @Nullable
  // @Size(min = 10, max = 100) // TODO
  private URI logoUrl;
  @Nullable
  @Size(min = 0, max = 255)
  private String address;
  @Nullable
  @Size(min = 0, max = 100)
  private String city;
  @Nullable
  @Size(min = 0, max = 100)
  private String province;
  @NotNull
  // @Size(min = 2, max = 100) // TODO
  private Country country;
  @Nullable
  @Size(min = 0, max = 50)
  private String postalCode;
  @Nullable
  @Min(-90)
  @Max(90)
  private BigDecimal latitude;
  @Nullable
  @Min(-180)
  @Max(180)
  private BigDecimal longitude;
  private Date created;
  private Date modified;
  private Date deleted;
  private List<Tag> tags = Lists.newArrayList();
  private List<Contact> contacts = Lists.newArrayList();

  @Override
  public UUID getKey() {
    return key;
  }

  @Override
  public void setKey(UUID key) {
    this.key = key;
  }

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

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public void setTitle(String title) {
    this.title = title;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  public Language getLanguage() {
    return language;
  }

  public void setLanguage(Language language) {
    this.language = language;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public URI getHomepage() {
    return homepage;
  }

  public void setHomepage(URI homepage) {
    this.homepage = homepage;
  }

  public URI getLogoUrl() {
    return logoUrl;
  }

  public void setLogoUrl(URI logoUrl) {
    this.logoUrl = logoUrl;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public BigDecimal getLatitude() {
    return latitude;
  }

  public void setLatitude(BigDecimal latitude) {
    this.latitude = latitude;
  }

  public BigDecimal getLongitude() {
    return longitude;
  }

  public void setLongitude(BigDecimal longitude) {
    this.longitude = longitude;
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
    return Objects.hashCode(key, endorsingNodeKey, endorsementApproved, title, alias, description,
      language, email, phone, homepage, logoUrl, address, city, province, country, postalCode, latitude, longitude,
      created, modified, deleted, tags, contacts);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Organization) {
      Organization that = (Organization) object;
      return Objects.equal(this.key, that.key)
        && Objects.equal(this.endorsingNodeKey, that.endorsingNodeKey)
        && Objects.equal(this.endorsementApproved, that.endorsementApproved)
        && Objects.equal(this.title, that.title)
        && Objects.equal(this.alias, that.alias)
        && Objects.equal(this.description, that.description)
        && Objects.equal(this.language, that.language)
        && Objects.equal(this.email, that.email)
        && Objects.equal(this.phone, that.phone)
        && Objects.equal(this.homepage, that.homepage)
        && Objects.equal(this.logoUrl, that.logoUrl)
        && Objects.equal(this.address, that.address)
        && Objects.equal(this.city, that.city)
        && Objects.equal(this.province, that.province)
        && Objects.equal(this.country, that.country)
        && Objects.equal(this.postalCode, that.postalCode)
        && Objects.equal(this.latitude, that.latitude)
        && Objects.equal(this.longitude, that.longitude)
        && Objects.equal(this.created, that.created)
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
      .add("key", key)
      .add("endorsingNodeKey", endorsingNodeKey)
      .add("endorsementApproved", endorsementApproved)
      .add("title", title)
      .add("alias", alias)
      .add("description", description)
      .add("language", language)
      .add("email", email)
      .add("phone", phone)
      .add("homepage", homepage)
      .add("logoUrl", logoUrl)
      .add("address", address)
      .add("city", city)
      .add("province", province)
      .add("country", country)
      .add("postalCode", postalCode)
      .add("latitude", latitude)
      .add("longitude", longitude)
      .add("created", created)
      .add("modified", modified)
      .add("deleted", deleted)
      .add("tags", tags)
      .add("contacts", contacts)
      .toString();
  }
}
