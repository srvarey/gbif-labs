package org.gbif.registry2.ws.model;

import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.Endpoint;
import org.gbif.api.model.registry2.Installation;
import org.gbif.api.vocabulary.registry2.ContactType;
import org.gbif.api.vocabulary.registry2.EndpointType;
import org.gbif.api.vocabulary.registry2.InstallationType;

import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Strings;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to create or update an Installation of type IPT_INSTALLATION from a set of HTTP Form parameters coming
 * from an IPT POST request.
 * </br>
 * Its fields are injected using the @FormParam. It is assumed the following parameters exist in the HTTP request:
 * 'organisationKey', 'name', 'description', 'primaryContactName', 'primaryContactEmail', 'primaryContactType',
 * 'serviceTypes', 'serviceURLs', and 'wsPassword'.
 * </br>
 * JAXB annotations allow the class to be converted into an XML document, that gets included in the Response following
 * a successful registration or update. @XmlElement is used to specify element names that an IPT expects to find.
 */
@XmlRootElement(name = "IptInstallation")
public class IptInstallation extends Installation {

  private static final Logger LOG = LoggerFactory.getLogger(IptInstallation.class);

  // injected from HTTP form parameters
  private ContactType primaryContactType;
  private String primaryContactEmail;
  private String primaryContactName;
  private EndpointType endpointType;
  private String endpointUrl;

  // created from combination of fields after injection
  private Contact primaryContact;
  private Endpoint feedEndpoint;

  // IPT constants
  private static final String RSS_ENDPOINT_TYPE = "RSS";
  private static final String ADMINISTRATIVE_CONTACT_TYPE = "administrative";
  public static final String USER = "GBIF Registry Web Services";
  // TODO: remove once security implemented
  public static final String ORGANIZATION_KEY_PARAM = "organisationKey";

  /**
   * Default constructor always sets type (IPT_INSTALLATION), created, createdBy, modified, modifiedBy.
   */
  public IptInstallation() {
    setType(InstallationType.IPT_INSTALLATION);
    // TODO: remove, will be set by authenticated account
    setCreatedBy(USER);
    setModifiedBy(USER);
  }

  /**
   * Set the hosting organization key. Mandatory field, injected on both register and update requests.
   *
   * @param organizationKey organization key as UUID
   */
  @FormParam(ORGANIZATION_KEY_PARAM)
  public void setHostingOrganizationKey(String organizationKey) {
    try {
      this.setOrganizationKey(UUID.fromString(Strings.nullToEmpty(organizationKey)));
    } catch (IllegalArgumentException e) {
      LOG.error("Hosting organization key is not a valid UUID: " + Strings.nullToEmpty(organizationKey));
    }
  }

  @XmlElement(name = ORGANIZATION_KEY_PARAM)
  @NotNull
  public String getHostingOrganizationKey() {
    return (getOrganizationKey() != null) ? getOrganizationKey().toString() : null;
  }

  @XmlElement(name = "key")
  @Nullable
  public String getIptInstallationKey() {
    return (getKey() != null) ? getKey().toString() : null;
  }

  /**
   * Set the title.
   *
   * @param name title of the installation
   */
  @FormParam("name")
  public void setIptName(String name) {
    this.setTitle(name);
  }

  /**
   * Get the title of the installation. This method is not used but it is needed otherwise this Object
   * can't be converted into an XML document via JAXB.
   *
   * @return title of the installation
   */
  @XmlTransient
  @Nullable
  public String getIptName() {
    return getTitle();
  }

  /**
   * Set the description.
   *
   * @param description of the installation
   */
  @FormParam("description")
  public void setIptDescription(String description) {
    this.setDescription(description);
  }

  /**
   * Get the description of the installation. This method is not used but it is needed otherwise this Object
   * can't be converted into an XML document via JAXB.
   *
   * @return description of the installation
   */
  @XmlTransient
  @Nullable
  public String getIptDescription() {
    return getDescription();
  }

  /**
   * Get the endpoint type.
   *
   * @return the endpoint type
   */
  @XmlTransient
  @Nullable
  public EndpointType getEndpointType() {
    return endpointType;
  }

  /**
   * Set the endpoint type. IPT endpoint type RSS gets converted to type FEED.
   *
   * @param endpointType endpoint type
   */
  @FormParam("serviceTypes")
  public void setEndpointType(String endpointType) {
    this.endpointType =
      (endpointType.equalsIgnoreCase(RSS_ENDPOINT_TYPE)) ? EndpointType.FEED : EndpointType.fromString(endpointType);
  }

  /**
   * Get the endpoint URL.
   *
   * @return the endpoint URL
   */
  @XmlTransient
  @Nullable
  public String getEndpointUrl() {
    return endpointUrl;
  }

  /**
   * Set the endpoint URL.
   *
   * @param endpointUrl endpoint URL
   */
  @FormParam("serviceURLs")
  public void setEndpointUrl(String endpointUrl) {
    this.endpointUrl = endpointUrl;
  }

  @XmlTransient
  @Nullable
  public String getWsPassword() {
    return getPassword();
  }

  @FormParam("wsPassword")
  public void setWsPassword(String wsPassword) {
    setPassword(Strings.nullToEmpty(wsPassword));
  }

  /**
   * Get primary contact name.
   *
   * @return primary contact name
   */
  @XmlTransient
  @Nullable
  public String getPrimaryContactName() {
    return primaryContactName;
  }

  /**
   * Set primary contact name.
   * Note: this is not a required field.
   *
   * @param primaryContactName primary contact name
   */
  @FormParam("primaryContactName")
  public void setPrimaryContactName(String primaryContactName) {
    this.primaryContactName = primaryContactName;
  }

  /**
   * Get primary contact email.
   *
   * @return primary contact email
   */
  @XmlTransient
  @NotNull
  public String getPrimaryContactEmail() {
    return primaryContactEmail;
  }

  /**
   * Set primary contact email and check if it is a valid email address.
   * Note: this field is required, and the old web services would throw 400 response if not valid.
   * TODO: once field validation is working, the validation below can be removed
   *
   * @param primaryContactEmail primary contact email address
   */
  @FormParam("primaryContactEmail")
  public void setPrimaryContactEmail(String primaryContactEmail) {
    EmailValidator validator = EmailValidator.getInstance();
    if (!validator.isValid(primaryContactEmail)) {
      LOG.error("No valid primary contact email has been specified: " + Strings.nullToEmpty(primaryContactEmail));
    } else {
      this.primaryContactEmail = primaryContactEmail;
    }
  }

  /**
   * Get primary contact type.
   *
   * @return primary contact type
   */
  @XmlTransient
  @NotNull
  public ContactType getPrimaryContactType() {
    return primaryContactType;
  }

  /**
   * Set primary contact type. First, check if it is not null or empty. The incoming type is always either
   * administrative or technical, always defaulting to type technical.
   * Note: this field is required, and the old web services would throw 400 response if not found.
   *
   * @param primaryContactType primary contact type
   */
  @FormParam("primaryContactType")
  public void setPrimaryContactType(String primaryContactType) {
    if (Strings.isNullOrEmpty(primaryContactType)) {
      LOG.error("No primary contact type has ben provided");
    }
    this.primaryContactType = (Strings.nullToEmpty(primaryContactType).equalsIgnoreCase(ADMINISTRATIVE_CONTACT_TYPE))
      ? ContactType.ADMINISTRATIVE_POINT_OF_CONTACT : ContactType.TECHNICAL_POINT_OF_CONTACT;
  }

  /**
   * Get the endpoint of type FEED.
   *
   * @return the endpoint of type FEED
   */
  @XmlTransient
  public Endpoint getFeedEndpoint() {
    return feedEndpoint;
  }

  /**
   * Set the endpoint of type FEED. This endpoint will have been created via addEndpoint() that creates the endpoint
   * from the injected HTTP Form parameters.
   *
   * @param feedEndpoint endpoint of type FEED
   */
  public void setFeedEndpoint(Endpoint feedEndpoint) {
    this.feedEndpoint = feedEndpoint;
  }

  /**
   * Get the primary contact.
   *
   * @return the primary contact
   */
  @XmlTransient
  @Nullable
  public Contact getPrimaryContact() {
    return (primaryContact != null && primaryContact.getEmail() != null && primaryContact.getType() != null)
      ? primaryContact : null;
  }

  /**
   * Set the primary contact. This contact will have been created via addContact() that creates the contact from
   * the injected HTTP Form parameters.
   *
   * @param primaryContact primary contact
   */
  public void setPrimaryContact(Contact primaryContact) {
    this.primaryContact = primaryContact;
  }

  /**
   * Prepares the installation for being persisting, ensuring the primary contact and endpoint have been constructed
   * from the injected HTTP parameters.
   */
  public void prepare() {
    addPrimaryContact();
    addEndpoint();
  }

  /**
   * Generates the primary technical contact, and adds it to the installation. This method must be called after all
   * primary contact parameters have been set.
   *
   * @return new primary contact added
   */
  private Contact addPrimaryContact() {
    Contact contact = null;
    if (!Strings.isNullOrEmpty(primaryContactName) && !Strings.isNullOrEmpty(primaryContactEmail)) {

      // check if the primary contact with this type exists already
      for (Contact c : getContacts()) {
        if (c.isPrimary() && c.getType() == getPrimaryContactType()) {
          contact = c;
          break;
        }
      }
      // if it doesn't exist already, create it
      if (contact == null) {
        contact = new Contact();
        contact.setCreated(new Date());
        contact.setCreatedBy(USER);
      }
      // set/update other properties
      contact.setPrimary(true);
      contact.setModified(new Date());
      contact.setModifiedBy(USER);
      contact.setFirstName(getPrimaryContactName());
      contact.setEmail(getPrimaryContactEmail());
      contact.setType(getPrimaryContactType());
      setPrimaryContact(contact);

      // add to installation's list
      getContacts().add(contact);
    }
    return contact;
  }

  /**
   * Generates an Endpoint, and adds it to the installation. This method must be called after all
   * endpoint related parameters have been set.
   *
   * @return new Endpoint added
   */
  private Endpoint addEndpoint() {
    Endpoint endpoint = null;
    if (!Strings.isNullOrEmpty(getEndpointUrl()) && getEndpointType() != null) {
      // check if the endpoint with type FEED exists already
      for (Endpoint e : getEndpoints()) {
        if (e.getType() == EndpointType.FEED) {
          endpoint = e;
          break;
        }
      }
      // if it doesn't exist already, create it
      if (endpoint == null) {
        endpoint = new Endpoint();
        endpoint.setCreated(new Date());
        endpoint.setCreatedBy(USER);
      }
      // set/update other properties
      endpoint.setModified(new Date());
      endpoint.setModifiedBy(USER);
      endpoint.setUrl(getEndpointUrl());
      endpoint.setType(getEndpointType());
      setFeedEndpoint(endpoint);

      // add to installation's list
      getEndpoints().add(endpoint);
    }
    return endpoint;
  }
}
