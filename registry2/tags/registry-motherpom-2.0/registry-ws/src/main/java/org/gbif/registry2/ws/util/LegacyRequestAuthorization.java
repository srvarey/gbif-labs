package org.gbif.registry2.ws.util;

import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Installation;
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.service.registry2.DatasetService;
import org.gbif.api.service.registry2.InstallationService;
import org.gbif.api.service.registry2.OrganizationService;

import java.util.UUID;
import javax.annotation.Nullable;
import javax.ws.rs.core.MultivaluedMap;

import com.google.common.base.Strings;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.core.HttpContext;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class providing temporary authorization for legacy web service requests (GBRDS/IPT).
 */
public class LegacyRequestAuthorization {

  private static final Logger LOG = LoggerFactory.getLogger(LegacyRequestAuthorization.class);
  private static final String AUTHORIZATION_HEADER_NAME = "Authorization";

  private final HttpContext httpContext;
  private OrganizationService organizationService;
  private InstallationService installationService;
  private DatasetService datasetService;

  public LegacyRequestAuthorization(OrganizationService organizationService, HttpContext httpContext) {
    this.organizationService = organizationService;
    this.httpContext = httpContext;
  }

  public LegacyRequestAuthorization(InstallationService installationService, HttpContext httpContext) {
    this.installationService = installationService;
    this.httpContext = httpContext;
  }

  public LegacyRequestAuthorization(OrganizationService organizationService, HttpContext httpContext,
    DatasetService datasetService) {
    this.organizationService = organizationService;
    this.httpContext = httpContext;
    this.datasetService = datasetService;
  }

  /**
   * Determine if HTTP request is authorized to modify Organization. The difference between this method and
   * isAuthorizedToModifyOrganization(organizationKey) is that the organizationKey must first be parsed from the
   * form parameters.
   *
   * @return true if the HTTP request is authorized to modify Organization
   *
   * @see LegacyRequestAuthorization#isAuthorizedToModifyOrganization(UUID)
   */
  public boolean isAuthorizedToModifyOrganization() {
    // retrieve HTTP param for hosting organization key and convert incoming key into UUID
    UUID organizationKey = getOrganizationKeyFromParams();

    return isAuthorizedToModifyOrganization(organizationKey);
  }

  /**
   * Determine if HTTP request is authorized to modify Organization.
   *
   * @param organizationKey organization key
   *
   * @return true if the HTTP request is authorized to modify Organization
   */
  public boolean isAuthorizedToModifyOrganization(UUID organizationKey) {
    if (organizationKey == null) {
      LOG.error("The organization key was null");
      return false;
    }

    // get organization key from credentials, verify it exists, and is authorized
    UUID organizationKeyFromCredentials = getOrganizationKeyFromCredentials();
    if (organizationKeyFromCredentials == null) {
      return false;
    }

    if (!organizationKeyFromCredentials.equals(organizationKey)) {
      LOG.error("Different organization keys were specified in the parameters and credentials");
      return false;
    }

    LOG.info("Authorization succeeded for organization with key={}", organizationKey.toString());
    return true;
  }

  /**
   * Retrieves the organizationKey from the requests' form parameters.
   *
   * @return key or null if not found
   */
  public UUID getOrganizationKeyFromParams() {
    MultivaluedMap<String, String> params = httpContext.getRequest().getFormParameters();
    if (params == null) {
      return null;
    }

    // retrieve HTTP param for hosting organization key
    String organizationKeyFormParam = params.getFirst(LegacyResourceConstants.ORGANIZATION_KEY_PARAM);
    if (Strings.isNullOrEmpty(organizationKeyFormParam)) {
      return null;
    }
    // convert incoming key into UUID
    try {
      return UUID.fromString(organizationKeyFormParam);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  /**
   * Retrieves the organization key from the requests' credentials, and validate that the organization exists and
   * that the credentials are authorized.
   *
   * @return organization key, or null if organization not found, or credentials weren't authorized
   */
  @Nullable
  public UUID getOrganizationKeyFromCredentials() {
    // retrieve authorization Header
    String authorizationHeader = httpContext.getRequest().getHeaderValue(AUTHORIZATION_HEADER_NAME);
    if (Strings.isNullOrEmpty(authorizationHeader)) {
      LOG.error("The authorization header was missing from requests");
      return null;
    }

    // get the key portion of the authorization Header
    String encryptedAuthorizationKey = getAuthorizationKey(authorizationHeader);
    String decryptedAuthorizationKey = decrypt(encryptedAuthorizationKey);

    // get the user from the BASE-64 encoded authorization key, equal to organization UUID
    String user = getUserFromAuthenticationKey(decryptedAuthorizationKey);
    // get the password from the BASE-64 encoded authorization key, equal to organization password
    String password = getPasswordFromAuthenticationKey(decryptedAuthorizationKey);

    if (user == null || password == null) {
      LOG.error("One or both of owning organization key and password were missing from requests credentials");
      return null;
    }

    // convert incoming key into UUID
    UUID organizationKey;
    try {
      organizationKey = UUID.fromString(user);
    } catch (IllegalArgumentException e) {
      LOG.error("The owning organization specified in the credentials is not a valid UUID");
      return null;
    }

    // validate organization key belongs to an existing organization
    Organization organization;
    try {
      organization = organizationService.get(organizationKey);
    } catch (NotFoundException e) {
      LOG.error("The owning organization specified in the credentials does not exist");
      return null;
    }

    if (!password.equals(organization.getPassword())) {
      LOG.error("The wrong password was supplied");
      return null;
    }

    return organizationKey;
  }

  /**
   * Determine if HTTP request is authorized to modify a Dataset belonging to an Organization.
   * This method checks that the same organizationKey is specified in the credentials and HTTP form parameters,
   * that the Organization corresponding to that organizationKey exists, that the Dataset corresponding to the
   * datasetKey exists, that the Dataset is owned by that Organization, and that the correct organization password has
   * been supplied.
   *
   * @param datasetKey Dataset key
   *
   * @return true if the HTTP request is authorized to modify a Dataset belonging to Organization
   */
  public boolean isAuthorizedToModifyOrganizationsDataset(UUID datasetKey) {
    if (datasetKey == null) {
      LOG.error("Dataset key was null");
      return false;
    }
    // retrieve dataset to ensure it exists
    Dataset dataset = datasetService.get(datasetKey);
    if (dataset == null) {
      LOG.error("Dataset with key={} does not exist", datasetKey.toString());
      return false;
    }

    // retrieve organization key from credentials, validate organization exists, and credentials authorized
    UUID organizationKey = getOrganizationKeyFromCredentials();
    if (organizationKey == null) {
      LOG.error("Organization key in credentials was null or could not be authorized");
      return false;
    }

    // check the dataset belongs to organization
    if (dataset.getOwningOrganizationKey().compareTo(organizationKey) != 0) {
      LOG.error("The Dataset is not owned by the organization specified in the credentials");
      return false;
    }

    // check if an organisationKey was included in form params, that the organization keys match
    UUID organizationKeyFromFormParams = getOrganizationKeyFromParams();
    if (organizationKeyFromFormParams != null) {
      if (organizationKeyFromFormParams.compareTo(organizationKey) != 0) {
        LOG.error("Different organization keys were specified in the form parameters and credentials");
        return false;
      }
    }
    LOG.info("Authorization succeeded, can modify dataset with key={} belonging to organization with key={}",
      datasetKey.toString(), organizationKey.toString());
    return true;
  }

  /**
   * Determine if HTTP request is authorized to modify Installation.
   *
   * @param installationKey Installation key
   *
   * @return true if the HTTP request is authorized to modify Installation
   */
  public boolean isAuthorizedToModifyInstallation(UUID installationKey) {
    // retrieve path param for installation key
    if (installationKey == null) {
      return false;
    }
    // validate installation key belongs to an existing installation
    Installation installation = installationService.get(installationKey);
    if (installation == null) {
      return false;
    }
    // retrieve authorization Header
    String authorizationHeader = httpContext.getRequest().getHeaderValue(AUTHORIZATION_HEADER_NAME);
    if (Strings.isNullOrEmpty(authorizationHeader)) {
      return false;
    }

    // get the key portion of the authorization Header
    String encryptedAuthorizationKey = getAuthorizationKey(authorizationHeader);
    String decryptedAuthorizationKey = decrypt(encryptedAuthorizationKey);

    // get the user from the BASE-64 encoded authorization key, equal to installation UUID
    String user = getUserFromAuthenticationKey(decryptedAuthorizationKey);
    // get the password from the BASE-64 encoded authorization key, equal to installation ws password
    String password = getPasswordFromAuthenticationKey(decryptedAuthorizationKey);

    // ensure the installation about to be modified, is the same specified in credentials
    return (user != null && password != null) && user.equalsIgnoreCase(installation.getKey().toString()) && password
      .equals(installation.getPassword());
  }

  /**
   * Extracts the authorization key from the HTTP Authorization header.
   *
   * @param authorizationHeader authorization Header value
   *
   * @return the authorization key
   */
  private String getAuthorizationKey(String authorizationHeader) {
    int positionBlankSpace = authorizationHeader.indexOf(' ');
    return authorizationHeader.substring(positionBlankSpace + 1);
  }

  /**
   * Decrypts an authorization key in Base64 format (Basic Access Authentication).
   *
   * @param encryptedKey encrypted key
   *
   * @return the decrypted key
   */
  public static String decrypt(String encryptedKey) {
    byte[] rawDecryptedKey = Base64.decodeBase64(encryptedKey);
    return new String(rawDecryptedKey);
  }

  /**
   * Extracts the user from the decrypted HTTP Authorization key.
   *
   * @param authorizationKey decrypted authorization Key
   *
   * @return the authorization user
   */
  public static String getUserFromAuthenticationKey(String authorizationKey) {
    int positionColon = authorizationKey.indexOf(':');
    return (positionColon > 0) ? authorizationKey.substring(0, positionColon) : null;
  }

  /**
   * Extracts the password from the decrypted HTTP Authorization key.
   *
   * @param authorizationKey decrypted authorization Key
   *
   * @return the authorization password
   */
  public static String getPasswordFromAuthenticationKey(String authorizationKey) {
    int positionColon = authorizationKey.indexOf(':');
    return (positionColon > 0) ? authorizationKey.substring(positionColon + 1, authorizationKey.length()) : null;
  }
}
