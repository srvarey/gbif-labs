package org.gbif.api.registry.model;

import org.gbif.api.vocabulary.Country;

import com.google.common.base.Objects;


public class WritableContact {

  // TODO validations
  private Integer key; // primary key
  private String name;
  private String description;
  private String email;
  private String phone;
  private String organization;
  private String address;
  private String city;
  private String province;
  private Country country;
  private String postalCode;

  public Integer getKey() {
    return key;
  }

  public void setKey(Integer key) {
    this.key = key;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
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


  @Override
  public int hashCode() {
    return Objects.hashCode(key, name, description, email, phone, organization, address, city, province, country,
      postalCode);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof WritableContact) {
      WritableContact that = (WritableContact) object;
      return Objects.equal(this.key, that.key)
        && Objects.equal(this.name, that.name)
        && Objects.equal(this.description, that.description)
        && Objects.equal(this.email, that.email)
        && Objects.equal(this.phone, that.phone)
        && Objects.equal(this.organization, that.organization)
        && Objects.equal(this.address, that.address)
        && Objects.equal(this.city, that.city)
        && Objects.equal(this.province, that.province)
        && Objects.equal(this.country, that.country)
        && Objects.equal(this.postalCode, that.postalCode);
    }
    return false;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("key", key)
      .add("name", name)
      .add("description", description)
      .add("email", email)
      .add("phone", phone)
      .add("organization", organization)
      .add("address", address)
      .add("city", city)
      .add("province", province)
      .add("country", country)
      .add("postalCode", postalCode)
      .toString();
  }
}
