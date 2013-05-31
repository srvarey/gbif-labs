package org.gbif.registry2.ws.util;

import org.gbif.api.model.registry2.Installation;
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.service.registry2.InstallationService;
import org.gbif.api.service.registry2.OrganizationService;
import org.gbif.registry2.ws.model.IptInstallation;

import java.util.UUID;
import javax.ws.rs.core.MultivaluedMap;

import com.google.common.base.Strings;
import com.sun.jersey.api.core.HttpContext;
import org.apache.commons.codec.binary.Base64;

/**
 * Class providing temporary authorization for requests coming from an IPT.
 */
public class IptRequestAuthorization {

  private static final String AUTHORIZATION_HEADER_NAME = "Authorization";

  private HttpContext httpContext;
  private OrganizationService organizationService;
  private InstallationService installationService;

  public IptRequestAuthorization(OrganizationService organizationService, HttpContext httpContext) {
    this.organizationService = organizationService;
    this.httpContext = httpContext;
  }

  public IptRequestAuthorization(InstallationService installationService, HttpContext httpContext) {
    this.installationService = installationService;
    this.httpContext = httpContext;
  }

  /**
   * Determine if HTTP request is authorized to modify Organization.
   *
   * @return true if the HTTP request is authorized to modify Organization
   */
  public boolean isAuthorizedToModifyOrganization() {
    MultivaluedMap<String, String> params = httpContext.getRequest().getFormParameters();
    // retrieve HTTP param for hosting organization key
    String organizationKeyParam = params.getFirst(IptInstallation.ORGANIZATION_KEY_PARAM);
    if (Strings.isNullOrEmpty(organizationKeyParam)) {
      return false;
    }
    // convert incoming key into UUID
    UUID organizationKey;
    try {
      organizationKey = UUID.fromString(organizationKeyParam);
    } catch (IllegalArgumentException e) {
      return false;
    }
    // validate organization key belongs to an existing organization
    Organization organization = organizationService.get(organizationKey);
    if (organization == null) {
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

    // get the user from the BASE-64 encoded authorization key, equal to organization UUID
    String user = getUserFromAuthenticationKey(decryptedAuthorizationKey);
    // get the password from the BASE-64 encoded authorization key, equal to organization password
    String password = getPasswordFromAuthenticationKey(decryptedAuthorizationKey);

    return (user != null && password != null) && user.equalsIgnoreCase(organizationKey.toString()) && password
      .equals(organization.getPassword());
  }

  /**
   * Determine if HTTP request is authorized to modify Installation.
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
    // TODO replace hard coded "password" with installation.getWsPassword
    return (user != null && password != null) && user.equalsIgnoreCase(installation.getKey().toString()) && password
      .equals("password");
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
