package org.gbif.registry2.ws.servlet;

import org.gbif.api.model.common.User;
import org.gbif.api.model.common.UserPrincipal;
import org.gbif.api.service.common.UserService;
import org.gbif.api.service.registry2.DatasetService;
import org.gbif.api.service.registry2.InstallationService;
import org.gbif.api.service.registry2.OrganizationService;
import org.gbif.registry2.ws.util.LegacyRequestAuthorization;
import org.gbif.registry2.ws.util.LegacyResourceConstants;

import java.security.Principal;
import java.util.UUID;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import com.google.inject.Inject;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A filter that will intercept legacy web service requests to /registry/* and perform authentication setting
 * a security context on the request in advance of the the GBIF AuthFilter.
 */
public class LegacyWsFilter implements ContainerRequestFilter {

  private static final Logger LOG = LoggerFactory.getLogger(LegacyWsFilter.class);

  private final InstallationService installationService;
  private final OrganizationService organizationService;
  private final DatasetService datasetService;
  private final Authorizer authorizer;

  // request HttpContext to access HTTP Headers during authorization
  @Context
  private HttpContext httpContext;
  @Context
  private UriInfo uriInfo;

  @Inject
  public LegacyWsFilter(InstallationService installationService, OrganizationService organizationService,
    DatasetService datasetService, UserService userService) {
    this.installationService = installationService;
    this.organizationService = organizationService;
    this.datasetService = datasetService;
    // create Authorizer storing the security information (ie principal provider)
    User user = userService.get("admin");
    this.authorizer = new Authorizer(user, SecurityContext.BASIC_AUTH);
  }

  @Override
  public ContainerRequest filter(ContainerRequest request) {
    String path = request.getPath();
    // is it a legacy web service POST, PUT, DELETE request requiring authorization?
    if (path.contains("registry/") && !"GET".equalsIgnoreCase(request.getMethod())) {
      // legacy installation request
      if (path.contains("/ipt")) {
        // register installation?
        if (path.endsWith("/register")) {
          return authorizeOrganizationChange(request);
        }
        // update installation?
        else if (path.contains("/update/")) {
          UUID installationKey = retrieveKeyFromRequestPath(request);
          return authorizeInstallationChange(request, installationKey);
        }
        // register dataset?
        else if (path.endsWith("/resource")) {
          return authorizeOrganizationChange(request);
        }
        // update dataset, delete dataset?
        else if (path.contains("/resource/")) {
          UUID datasetKey = retrieveKeyFromRequestPath(request);
          return authorizeOrganizationDatasetChange(request, datasetKey);
        }
      }
      // legacy dataset request
      else if (path.contains("/resource")) {
        // register dataset?
        if (path.endsWith("/resource")) {
          return authorizeOrganizationChange(request);
        }
        // update dataset, delete dataset?
        else if (path.contains("/resource/")) {
          UUID datasetKey = retrieveKeyFromRequestPath(request);
          return authorizeOrganizationDatasetChange(request, datasetKey);
        }
      }
      // legacy endpoint request
      else if (path.endsWith("/service")) {
         // add endpoint?
         if (request.getQueryParameters().isEmpty()) {
           UUID datasetKey = retrieveDatasetKeyFromFormParameters(request);
           return authorizeOrganizationDatasetChange(request, datasetKey);
         }
         // delete all dataset's enpoints?
         else if (uriInfo.getRequestUri().toString().contains("?resourceKey=")) {
           UUID datasetKey = retrieveDatasetKeyFromQueryParameters(request);
           return authorizeOrganizationDatasetChange(request, datasetKey);
         }
      }
    }
    // otherwise return request unchanged
    return request;
  }

  /**
   * Authorize request can make a change to an organization, setting the request security context specifying the
   * principal provider. Called for example, when adding a new dataset.
   *
   * @param request request
   *
   * @return request
   *
   * @throws WebApplicationException if request isn't authorized
   */
  private ContainerRequest authorizeOrganizationChange(ContainerRequest request) throws WebApplicationException {
    LegacyRequestAuthorization authorization = new LegacyRequestAuthorization(organizationService, httpContext);
    if (authorization.isAuthorizedToModifyOrganization()) {
      request.setSecurityContext(authorizer);
    } else {
      LOG.error("Request to register not authorized!");
      throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }
    return request;
  }

  /**
   * Authorize request can make a change to an organization's dataset, setting the request security context specifying
   * the principal provider. Called for example, when updating or deleting a dataset.
   *
   * @param request    request
   * @param datasetKey dataset key
   *
   * @return request
   *
   * @throws WebApplicationException if request isn't authorized
   */
  private ContainerRequest authorizeOrganizationDatasetChange(ContainerRequest request, UUID datasetKey)
    throws WebApplicationException {
    LegacyRequestAuthorization authorization =
      new LegacyRequestAuthorization(organizationService, httpContext, datasetService);
    if (authorization.isAuthorizedToModifyOrganizationsDataset(datasetKey)) {
      request.setSecurityContext(authorizer);
    } else {
      LOG.error("Request to update Dataset not authorized!");
      throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }
    return request;
  }

  /**
   * Authorize request can make a change to an installation, setting the request security context specifying the
   * principal provider. Called for example, when adding a new dataset.
   *
   * @param request request
   * @param installationKey installation key
   * @return request
   *
   * @throws WebApplicationException if request isn't authorized
   */
  private ContainerRequest authorizeInstallationChange(ContainerRequest request, UUID installationKey) throws WebApplicationException {
    LegacyRequestAuthorization authorization = new LegacyRequestAuthorization(installationService, httpContext);
    if (authorization.isAuthorizedToModifyInstallation(installationKey)) {
      request.setSecurityContext(authorizer);
    } else {
      LOG.error("Request to update IPT not authorized!");
      throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }
    return request;
  }

  /**
   * Retrieve key from request path, where the key is the last path segment, e.g. /registry/resource/{key}
   *
   * @param request request
   *
   * @return dataset key
   *
   * @throws WebApplicationException if incoming string key isn't a valid UUID
   */
  private UUID retrieveKeyFromRequestPath(ContainerRequest request) throws WebApplicationException {
    String path = request.getPath();
    String key = path.substring(path.lastIndexOf("/") + 1);
    try {
      return UUID.fromString(key);
    } catch (IllegalArgumentException e) {
      throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }
  }

  /**
   * Retrieve dataset key from form parameters.
   *
   * @param request request
   *
   * @return dataset key
   *
   * @throws WebApplicationException if incoming string key isn't a valid UUID
   */
  private UUID retrieveDatasetKeyFromFormParameters(ContainerRequest request) throws WebApplicationException {
    MultivaluedMap<String, String> params = request.getFormParameters();
    String key = params.getFirst(LegacyResourceConstants.RESOURCE_KEY_PARAM);
    try {
      return UUID.fromString(key);
    } catch (IllegalArgumentException e) {
      throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }
  }

  /**
   * Retrieve dataset key from query parameters.
   *
   * @param request request
   *
   * @return dataset key
   *
   * @throws WebApplicationException if incoming string key isn't a valid UUID
   */
  private UUID retrieveDatasetKeyFromQueryParameters(ContainerRequest request) throws WebApplicationException {
    MultivaluedMap<String, String> params = request.getQueryParameters();
    String key = params.getFirst(LegacyResourceConstants.RESOURCE_KEY_PARAM);
    try {
      return UUID.fromString(key);
    } catch (IllegalArgumentException e) {
      throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }
  }

  /**
   * Class providing access to request's security related information.
   */
  public class Authorizer implements SecurityContext {

    private final UserPrincipal principal;
    private final String authenticationScheme;

    public Authorizer(User user, String scheme) {
      this.principal = new UserPrincipal(user);
      this.authenticationScheme = scheme;
    }

    @Override
    public Principal getUserPrincipal() {
      return principal;
    }

    @Override
    public boolean isUserInRole(String role) {
      return principal != null && principal.hasRole(role);
    }

    @Override
    public boolean isSecure() {
      return "https".equals(uriInfo.getRequestUri().getScheme());
    }

    @Override
    public String getAuthenticationScheme() {
      return authenticationScheme;
    }
  }
}
