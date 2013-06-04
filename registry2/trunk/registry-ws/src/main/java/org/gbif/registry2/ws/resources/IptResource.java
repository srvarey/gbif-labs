package org.gbif.registry2.ws.resources;

import org.gbif.api.service.registry2.InstallationService;
import org.gbif.api.service.registry2.OrganizationService;
import org.gbif.registry2.ws.model.IptInstallation;
import org.gbif.registry2.ws.util.IptRequestAuthorization;

import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.InjectParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle all requests coming from IPT installations, providing backwards support for the earlier web services hosted
 * by the GBRDS.
 */
@Singleton
@Path("ipt")
public class IptResource {

  private static final Logger LOG = LoggerFactory.getLogger(IptResource.class);

  private final InstallationService installationService;
  private final OrganizationService organizationService;
  // used to ensure Response is not cached, forcing the IPT to make a new request
  private static final CacheControl CACHE_CONTROL_DISABLED = CacheControl.valueOf("no-cache");

  @Inject
  public IptResource(InstallationService installationService, OrganizationService organizationService) {
    this.installationService = installationService;
    this.organizationService = organizationService;
  }

  /**
   * Register IPT installation, handling incoming request with path /ipt/register. The primary contact and hosting
   * organization key are mandatory. Only after both the installation and primary contact have been persisted is a
   * Response with Status.CREATED returned.
   *
   * @param installation IptInstallation with HTTP form parameters having been injected from Jersey
   * @param request      HttpContext to access HTTP Headers during authorization
   *
   * @return response as expected by IPT
   */
  @POST
  @Path("register")
  @Produces(MediaType.APPLICATION_XML)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response register(@InjectParam IptInstallation installation, @Context HttpContext request) {

    // TODO remove once authorization interceptor implemented
    IptRequestAuthorization authorization = new IptRequestAuthorization(organizationService, request);
    if (!authorization.isAuthorizedToModifyOrganization()) {
      LOG.error("Request to registry IPT not authorized!");
      return Response.status(Response.Status.UNAUTHORIZED).cacheControl(CACHE_CONTROL_DISABLED).build();
    }

    if (installation != null) {
      // add contact and endpoint to installation
      installation.prepare();
      // primary contact and hosting organization key key are mandatory
      if (installation.getPrimaryContact() != null && installation.getHostingOrganizationKey() != null) {
        // persist installation
        UUID key = installationService.create(installation);
        // persist contact
        if (key != null) {
          // retrieve same primary contact if possible
          installationService.addContact(key, installation.getPrimaryContact());
          // try to persist FEED endpoint (non-mandatory)
          if (installation.getFeedEndpoint() != null) {
            installationService.addEndpoint(key, installation.getFeedEndpoint());
          }
          LOG.info("IPT installation registered successfully, key=%s", key.toString());
          return Response.status(Response.Status.CREATED).cacheControl(CACHE_CONTROL_DISABLED).entity(installation)
            .build();
        } else {
          LOG.error("IPT installation could not be persisted!");
        }
      } else {
        LOG.error("Mandatory primary contact and/or hosting organization key missing or incomplete!");
      }
    }
    LOG.error("IPT installation registration failed");
    return Response.status(Response.Status.BAD_REQUEST).cacheControl(CACHE_CONTROL_DISABLED).build();
  }


  /**
   * Update IPT installation, handling incoming request with path /ipt/update/{key}. The primary contact and hosting
   * organization key are mandatory. Only after both the installation and primary contact have been updated is a
   * Response with Status.OK returned.
   *
   * @param installationKey installation key (UUID) coming in as path param
   * @param installation    IptInstallation with HTTP form parameters having been injected from Jersey
   * @param request         HttpContext to access HTTP Headers during authorization
   *
   * @return response as expected by IPT
   */
  @POST
  @Path("update/{key}")
  @Produces(MediaType.APPLICATION_XML)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response update(@PathParam("key") UUID installationKey, @InjectParam IptInstallation installation,
    @Context HttpContext request) {

    // TODO remove once authorization interceptor implemented
    IptRequestAuthorization authorization = new IptRequestAuthorization(installationService, request);
    if (!authorization.isAuthorizedToModifyInstallation(installationKey)) {
      LOG.error("Request to registry IPT not authorized!");
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    if (installation != null) {
      // set key from path parameter
      installation.setKey(installationKey);
      // add contact and endpoint to installation
      installation.prepare();
      // primary contact and hosting organization key key are mandatory
      if (installation.getPrimaryContact() != null && installation.getHostingOrganizationKey() != null) {
        // update installation
        installationService.update(installation);
        // update primary contact
        installationService.addContact(installationKey, installation.getPrimaryContact());
        // update FEED endpoint (non-mandatory)
        if (installation.getFeedEndpoint() != null) {
          installationService.addEndpoint(installationKey, installation.getFeedEndpoint());
        }
        LOG.info("IPT installation updated successfully, key=%s", installationKey.toString());
        return Response.status(Response.Status.OK).entity(installation).build();

      } else {
        LOG.error("Mandatory primary contact and/or hosting organization key missing or incomplete!");
      }
    }
    LOG.error("IPT installation update failed");
    return Response.status(Response.Status.BAD_REQUEST).build();
  }
}
