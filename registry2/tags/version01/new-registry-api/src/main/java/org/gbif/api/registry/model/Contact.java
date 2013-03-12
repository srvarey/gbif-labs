package org.gbif.api.registry.model;

import org.gbif.api.vocabulary.Country;

import java.util.Date;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.common.base.Objects;

public class Contact implements Address {

  private Integer key;
  private String name;
  private String position;
  private String description;
  private String email;
  private String phone;
  private String organization;
  private String address;
  private String city;
  private String province;
  private Country country;
  private String postalCode;
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

  @Nullable
  @Size(min = 1)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Nullable
  @Size(min = 2)
  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  @Nullable
  @Size(min = 10)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  @Override
  @Nullable
  @Size(min = 1, max = 254)
  public String getEmail() {
    return email;
  }

  @Override
  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  @Nullable
  @Size(min = 5, max = 50)
  public String getPhone() {
    return phone;
  }

  @Override
  public void setPhone(String phone) {
    this.phone = phone;
  }

  @Nullable
  @Size(min = 2)
  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }

  @Override
  public String getAddress() {
    return address;
  }

  @Override
  public void setAddress(String address) {
    this.address = address;
  }

  @Override
  public String getCity() {
    return city;
  }

  @Override
  public void setCity(String city) {
    this.city = city;
  }

  @Override
  public String getProvince() {
    return province;
  }

  @Override
  public void setProvince(String province) {
    this.province = province;
  }

  @Override
  public Country getCountry() {
    return country;
  }

  @Override
  public void setCountry(Country country) {
    this.country = country;
  }

  @Override
  public String getPostalCode() {
    return postalCode;
  }

  @Override
  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
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
    return Objects.toStringHelper(this).add("key", key).add("name", name).add("position", position)
      .add("description", description).add("email", email).add("phone", phone).add("organization", organization)
      .add("address", address).add("city", city).add("province", province).add("country", country)
      .add("postalCode", postalCode).add("createdBy", createdBy).add("modifiedBy", modifiedBy).add("created", created)
      .add("modified", modified).toString();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key, name, position, description, email, phone, organization, address, city, province,
      country, postalCode, createdBy, modifiedBy, created, modified);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Contact) {
      Contact that = (Contact) object;
      return Objects.equal(this.key, that.key) && Objects.equal(this.name, that.name)
        && Objects.equal(this.position, that.position) && Objects.equal(this.description, that.description)
        && Objects.equal(this.email, that.email) && Objects.equal(this.phone, that.phone)
        && Objects.equal(this.organization, that.organization) && Objects.equal(this.address, that.address)
        && Objects.equal(this.city, that.city) && Objects.equal(this.province, that.province)
        && Objects.equal(this.country, that.country) && Objects.equal(this.postalCode, that.postalCode)
        && Objects.equal(this.createdBy, that.createdBy) && Objects.equal(this.modifiedBy, that.modifiedBy)
        && Objects.equal(this.created, that.created) && Objects.equal(this.modified, that.modified);
    }
    return false;
  }


}
