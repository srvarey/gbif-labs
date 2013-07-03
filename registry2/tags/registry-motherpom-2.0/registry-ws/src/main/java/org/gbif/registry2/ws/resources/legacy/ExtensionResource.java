package org.gbif.registry2.ws.resources.legacy;

import org.gbif.registry2.ws.util.LegacyResourceConstants;

import java.io.IOException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle all legacy web service Extension requests, previously handled by the GBRDS.
 */
@Singleton
@Path("registry/extensions")
public class ExtensionResource {

  private static final Logger LOG = LoggerFactory.getLogger(ExtensionResource.class);

  /**
   * Get a list of all extensions, handling incoming request with path /extensions.json.
   *
   * @return list of all extensions
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.TEXT_PLAIN)
  public Response getExtensions() {
    // TODO: replace static list http://dev.gbif.org/issues/browse/REG-394
    try {
      String content = Resources.toString(Resources.getResource("legacy/extensions.json"), Charsets.UTF_8);
      LOG.debug("Get extensions finished");
      return Response.status(Response.Status.OK).entity(content)
        .cacheControl(LegacyResourceConstants.CACHE_CONTROL_DISABLED).build();
    } catch (IOException e) {
      LOG.error("An error occurred retrieving extensions");
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .cacheControl(LegacyResourceConstants.CACHE_CONTROL_DISABLED).build();
    }
  }

}
