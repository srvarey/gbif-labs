package org.gbif.resource.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.api.Responses;

/**
 * This class maps the ResourceNotFoundException class to a Response with status Responses.NOT_FOUND (HTTP 404).
 * The @Provider annotation allows registering this class as an extension interface
 */
@Provider
public class ResourceNotFoundMapper implements ExceptionMapper<ResourceNotFoundException> {

  public Response toResponse(ResourceNotFoundException ex) {
    return Response.status(Responses.NOT_FOUND).
      entity(ex.getMessage()).
      type("text/plain").
      build();
  }
}
