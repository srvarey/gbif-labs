package org.gbif.resource.interceptors;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This filter will process all the responses of Jersey services
 * Every response will be analyzed to evaluate if the response content is null,
 * if it is null a NotFoundException is thrown
 */
public class NullResponseFilter implements ContainerResponseFilter {

  private static Logger log = LoggerFactory.getLogger(NullResponseFilter.class);

  @Override
  public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
    log.debug("NullResponseFilter invoked");
    //response doesn't contain content
    if(response.getResponse().getEntity() == null){
      log.debug("NullResponseFilter throwing NotFoundException");
      throw new NotFoundException();
    }
    return response;
  }
}
