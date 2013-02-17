package org.gbif.api.registry.model;

import org.gbif.api.vocabulary.Country;

/**
 * A package visible providing the commonality for addresses, including the constraint validations.
 */
interface Address {

  // TODO add validations on the Getters
  String getEmail();

  void setEmail(String email);

  String getPhone();

  void setPhone(String phone);

  String getAddress();

  void setAddress(String address);

  String getCity();

  void setCity(String city);

  String getProvince();

  void setProvince(String province);

  Country getCountry();

  void setCountry(Country country);

  String getPostalCode();

  void setPostalCode(String postalCode);
}
