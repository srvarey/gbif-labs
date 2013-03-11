package org.gbif.api.registry.model;

import org.gbif.api.vocabulary.Country;

import javax.annotation.Nullable;
import javax.validation.constraints.Size;

/**
 * A package visible providing the commonality for addresses, including the constraint validations.
 */
interface Address {

  @Nullable
  @Size(min = 5)
  String getEmail();

  void setEmail(String email);

  @Nullable
  @Size(min = 5)
  String getPhone();

  void setPhone(String phone);

  @Nullable
  @Size(min = 1)
  String getAddress();

  void setAddress(String address);

  @Nullable
  @Size(min = 1)
  String getCity();

  void setCity(String city);

  @Nullable
  @Size(min = 1)
  String getProvince();

  void setProvince(String province);

  @Nullable
  Country getCountry();

  void setCountry(Country country);

  @Nullable
  @Size(min = 1)
  String getPostalCode();

  void setPostalCode(String postalCode);
}
