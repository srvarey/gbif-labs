package org.gbif.registry.ws.resources.legacy;

import org.gbif.api.model.common.paging.PagingRequest;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry.Contact;
import org.gbif.api.model.registry.Node;
import org.gbif.api.model.registry.Organization;
import org.gbif.api.service.registry.NodeService;
import org.gbif.api.service.registry.OrganizationService;
import org.gbif.registry.ws.model.ErrorResponse;
import org.gbif.registry.ws.model.LegacyOrganizationBriefResponse;
import org.gbif.registry.ws.model.LegacyOrganizationResponse;
import org.gbif.registry.ws.util.LegacyRequestAuthorization;
import org.gbif.registry.ws.util.LegacyResourceConstants;
import org.gbif.registry.ws.util.LegacyResourceUtils;

import java.util.List;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.api.core.HttpContext;
import org.codehaus.jackson.map.util.JSONPObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle all legacy web service Organization requests, previously handled by the GBRDS.
 */
@Singleton
@Path("registry/organisation")
public class LegacyOrganizationResource {

  private static final Logger LOG = LoggerFactory.getLogger(LegacyOrganizationResource.class);

  private final OrganizationService organizationService;
  private final NodeService nodeService;

  @Inject
  public LegacyOrganizationResource(OrganizationService organizationService, NodeService nodeService) {
    this.organizationService = organizationService;
    this.nodeService = nodeService;
  }

  /**
   * This sub-resource can be called for various reasons:
   * </br>
   * 1. Get an Organization, handling incoming request with path /organization/{key}.json?callback=?, signifying
   * that the response must be JSONP. This request is made in order to verify that an organization exists.
   * No authorization is required for this request.
   * </br>
   * 2. Validate the organization credentials sent with incoming GET request. Handling request with path
   * /organization/{key}.json?op=login. Only after the credentials have been verified, is the
   * Response with Status.OK returned.
   *
   * @param organisationKey organization key (UUID) coming in as path param
   * @param callback        parameter
   *
   * @return 1. Organization, wrapped with callback parameter in JSONP, or null if organization with key does not
   *         exist.
   *         2. Response with Status.OK if credentials were verified, or Response with Status.UNAUTHORIZED if they
   *         weren't
   */
  @GET
  @Path("{key}")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON,
    "application/x-javascript", "application/javascriptx-javascript"})
  @Consumes(
    {MediaType.TEXT_PLAIN, MediaType.APPLICATION_FORM_URLENCODED, "application/x-javascript", "application/javascript"})
  public Object getOrganization(@PathParam("key") UUID organisationKey, @QueryParam("callback") String callback,
    @QueryParam("op") String op, @Context HttpContext request) {

    // incoming path parameter for organization key required
    if (organisationKey == null) {
      return Response.status(Response.Status.BAD_REQUEST).cacheControl(LegacyResourceConstants.CACHE_CONTROL_DISABLED)
        .build();
    }
    LOG.info("Get Organization with key={}", organisationKey.toString());

    Organization organization = organizationService.get(organisationKey);
    if (organization == null) {
      // the organization didn't exist, and expected response is "{Error: "No organisation matches the key provided}"
      return Response.status(Response.Status.OK).entity(new ErrorResponse("No organisation matches the key provided"))
        .cacheControl(LegacyResourceConstants.CACHE_CONTROL_DISABLED).build();
    }

    // login?
    if (op != null && op.equalsIgnoreCase("login")) {
      // are the organization credentials correct, and does the organization exist?
      LegacyRequestAuthorization authorization = new LegacyRequestAuthorization(organizationService, request);
      if (!authorization.isAuthorizedToModifyOrganization(organisationKey)) {
        LOG.error("Authorization failed for organization with key={}", organisationKey.toString());
        return Response.status(Response.Status.UNAUTHORIZED)
          .cacheControl(LegacyResourceConstants.CACHE_CONTROL_DISABLED).build();
      }
      return Response.status(Response.Status.OK).cacheControl(LegacyResourceConstants.CACHE_CONTROL_DISABLED).build();
    }

    // construct organization response object
    Contact contact = LegacyResourceUtils.getPrimaryContact(organization);
    Node node = nodeService.get(organization.getEndorsingNodeKey());
    LegacyOrganizationResponse o = new LegacyOrganizationResponse(organization, contact, node);

    // callback?
    if ("?".equals(callback)) {
      return new JSONPObject(callback, o);
    }
    // simple read?
    else {
      return Response.status(Response.Status.OK).entity(o).cacheControl(LegacyResourceConstants.CACHE_CONTROL_DISABLED)
        .build();
    }
  }

  /**
   * Get a list of all Organizations, handling incoming request with path /organization.json. For each Organization,
   * only the key and title(name) fields are required. No authorization is required for this request.
   *
   * @return list of all Organizations
   */
  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Consumes(MediaType.TEXT_PLAIN)
  public Response getOrganizations() {
    LOG.debug("Get all Organizations started");
    List<LegacyOrganizationBriefResponse> organizations = Lists.newArrayList();
    PagingRequest page = new PagingRequest(0, LegacyResourceConstants.WS_PAGE_SIZE);
    PagingResponse<Organization> response;
    do {
      LOG.debug("Requesting {} organizations starting at offset {}", page.getLimit(), page.getOffset());
      response = organizationService.list(page);
      for (Organization o : response.getResults()) {
        organizations.add(new LegacyOrganizationBriefResponse(o));
      }
      page.nextPage();
    } while (!response.isEndOfRecords());
    LOG.debug("Get all Organizations finished");
    // return array, required for serialization otherwise get com.sun.jersey.api.MessageException: A message body
    // writer for Java class java.util.ArrayList
    LegacyOrganizationBriefResponse[] array =
      organizations.toArray(new LegacyOrganizationBriefResponse[organizations.size()]);
    return Response.status(Response.Status.OK).entity(array)
      .cacheControl(LegacyResourceConstants.CACHE_CONTROL_DISABLED).build();
  }

}
