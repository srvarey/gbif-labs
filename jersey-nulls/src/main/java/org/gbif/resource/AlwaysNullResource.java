package org.gbif.resource;

import org.gbif.resource.exceptions.ResourceNotFoundException;
import org.gbif.resource.interceptors.NullableResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.Responses;
import com.sun.jersey.spi.resource.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test class that exercise possible scenarios of ws-resource methods that can return null values
 */
@Singleton
@Path("resource")
public class AlwaysNullResource {

  private static Logger log = LoggerFactory.getLogger(AlwaysNullResource.class);

  /**
   * Returns null and is annotated with @NullableResponse, so the interceptor evaluates the response and throws a
   * NotFoundException that contains a NOT_FOUND(404) status
   * @return always null
   */
  @GET
  @Path("getnull")
  @NullableResponse
  public  Object getNull(){
    log.debug("returning null");
    return null;
  }

  /**
   * The NullResponseFilter gets the null response and throws a NotFoundException
   * @return
   */
  @GET
  @Path("getnullbyfilter")
  public Object getNullExceptionByFilter() {
    return null;
  }

  /**
   * Returns "something" even when is annotated with  @NullableResponse
   * @return
   */
  @GET
  @Path("getsomething")
  @NullableResponse
  public String getSomething() {
    return "something";
  }

  /**
   * Shows a method that throws the Jersey NotFoundException, which is understood as a Responses.NOT_FOUND by the client
   * @return
   */
  @GET
  @Path("getwebexception")
  public Object getWebException() {
    log.debug("returning getWebException");
    throw new NotFoundException();
  }

  /**
   * Always throws a ResourceNotFoundException which is mapped to Responses.NOT_FOUND by the ResourceNotFoundMapper
   * provider
   * @return
   */
  @GET
  @Path("getexception")
  public Object getException() {
    log.debug("returning ResourceNotFoundException");
    throw new ResourceNotFoundException();
  }
}
