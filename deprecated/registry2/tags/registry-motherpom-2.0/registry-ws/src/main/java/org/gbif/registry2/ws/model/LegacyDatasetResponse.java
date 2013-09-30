package org.gbif.registry2.ws.model;

import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.vocabulary.registry2.ContactType;
import org.gbif.registry2.ws.util.LegacyResourceConstants;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Strings;

/**
 * Class used to generate responses for legacy (GBRDS/IPT) API.
 * </br>
 * JAXB annotations allow the class to be converted into an XML document or JSON response. @XmlElement is used to
 * specify element names that consumers of legacy services expect to find.
 */
@XmlRootElement(name = "resource")
public class LegacyDatasetResponse {

  private String key;
  private String organisationKey;
  private String name;
  private String nameLanguage;
  private String description;
  private String descriptionLanguage;
  private String homepageURL;
  private String primaryContactName;
  private String primaryContactAddress;
  private String primaryContactEmail;
  private String primaryContactPhone;
  private String primaryContactDescription;
  private String primaryContactType;

  public LegacyDatasetResponse(Dataset dataset, Contact contact) {
    setKey((dataset.getKey() == null) ? "" : dataset.getKey().toString());
    setOrganisationKey(
      (dataset.getOwningOrganizationKey() == null) ? "" : dataset.getOwningOrganizationKey().toString());
    setName((dataset.getTitle() == null) ? "" : dataset.getTitle());
    setDescription((dataset.getDescription() == null) ? "" : dataset.getDescription());
    setDescriptionLanguage((dataset.getLanguage() == null) ? "" : dataset.getLanguage().getIso2LetterCode());
    setNameLanguage((dataset.getLanguage() == null) ? "" : dataset.getLanguage().getIso2LetterCode());
    setHomepageURL((dataset.getHomepage() == null) ? "" : dataset.getHomepage().toString());
    setPrimaryContactAddress((contact == null) ? "" : Strings.nullToEmpty(contact.getAddress()));
    setPrimaryContactDescription((contact == null) ? "" : Strings.nullToEmpty(contact.getDescription()));
    setPrimaryContactEmail((contact == null) ? "" : Strings.nullToEmpty(contact.getEmail()));
    setPrimaryContactPhone((contact == null) ? "" : Strings.nullToEmpty(contact.getPhone()));
    setPrimaryContactName((contact == null) ? ""
      : Strings.nullToEmpty(contact.getFirstName()) + " " + Strings.nullToEmpty(contact.getLastName()));
    // conversion of contact type
    if (contact != null && contact.getType().compareTo(ContactType.ADMINISTRATIVE_POINT_OF_CONTACT) == 0) {
      setPrimaryContactType(LegacyResourceConstants.ADMINISTRATIVE_CONTACT_TYPE);
    } else if (contact != null && contact.getType().compareTo(ContactType.TECHNICAL_POINT_OF_CONTACT) == 0) {
      setPrimaryContactType(LegacyResourceConstants.TECHNICAL_CONTACT_TYPE);
    } else {
      setPrimaryContactType("");
    }
  }

  /**
   * No argument, default constructor needed by JAXB.
   */
  public LegacyDatasetResponse() {
  }

  @XmlElement(name = LegacyResourceConstants.KEY_PARAM)
  @NotNull
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @XmlElement(name = LegacyResourceConstants.NAME_PARAM)
  @NotNull
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  @XmlElement(name = LegacyResourceConstants.NAME_LANGUAGE_PARAM)
  @NotNull
  public String getNameLanguage() {
    return nameLanguage;
  }

  public void setNameLanguage(String nameLanguage) {
    this.nameLanguage = nameLanguage;
  }

  @XmlElement(name = LegacyResourceConstants.ORGANIZATION_KEY_PARAM)
  @NotNull
  public String getOrganisationKey() {
    return organisationKey;
  }

  public void setOrganisationKey(String organisationKey) {
    this.organisationKey = organisationKey;
  }

  @XmlElement(name = LegacyResourceConstants.DESCRIPTION_PARAM)
  @NotNull
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @XmlElement(name = LegacyResourceConstants.DESCRIPTION_LANGUAGE_PARAM)
  @NotNull
  public String getDescriptionLanguage() {
    return descriptionLanguage;
  }

  public void setDescriptionLanguage(String descriptionLanguage) {
    this.descriptionLanguage = descriptionLanguage;
  }

  @XmlElement(name = LegacyResourceConstants.HOMEPAGE_URL_PARAM)
  @NotNull
  public String getHomepageURL() {
    return homepageURL;
  }

  public void setHomepageURL(String homepageURL) {
    this.homepageURL = homepageURL;
  }

  @XmlElement(name = LegacyResourceConstants.PRIMARY_CONTACT_NAME_PARAM)
  @NotNull
  public String getPrimaryContactName() {
    return primaryContactName;
  }

  public void setPrimaryContactName(String primaryContactName) {
    this.primaryContactName = primaryContactName;
  }

  @XmlElement(name = LegacyResourceConstants.PRIMARY_CONTACT_ADDRESS_PARAM)
  @NotNull
  public String getPrimaryContactAddress() {
    return primaryContactAddress;
  }

  public void setPrimaryContactAddress(String primaryContactAddress) {
    this.primaryContactAddress = primaryContactAddress;
  }

  @XmlElement(name = LegacyResourceConstants.PRIMARY_CONTACT_EMAIL_PARAM)
  @NotNull
  public String getPrimaryContactEmail() {
    return primaryContactEmail;
  }

  public void setPrimaryContactEmail(String primaryContactEmail) {
    this.primaryContactEmail = primaryContactEmail;
  }

  @XmlElement(name = LegacyResourceConstants.PRIMARY_CONTACT_PHONE_PARAM)
  @NotNull
  public String getPrimaryContactPhone() {
    return primaryContactPhone;
  }

  public void setPrimaryContactPhone(String primaryContactPhone) {
    this.primaryContactPhone = primaryContactPhone;
  }

  @XmlElement(name = LegacyResourceConstants.PRIMARY_CONTACT_DESCRIPTION_PARAM)
  @NotNull
  public String getPrimaryContactDescription() {
    return primaryContactDescription;
  }

  public void setPrimaryContactDescription(String primaryContactDescription) {
    this.primaryContactDescription = primaryContactDescription;
  }

  @XmlElement(name = LegacyResourceConstants.PRIMARY_CONTACT_TYPE_PARAM)
  @NotNull
  public String getPrimaryContactType() {
    return primaryContactType;
  }

  public void setPrimaryContactType(String primaryContactType) {
    this.primaryContactType = primaryContactType;
  }
}
