package org.gbif.resource.interceptors;

import com.sun.jersey.api.NotFoundException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class intercepts a method, execute it and the validates the result.
 * If the result is null a NotFoundException is thrown. This Interceptor should
 * be used in a Jersey web service later since it throws NotFoundException;
 * this can be changed to threw another exception that is mapped to an specific response
 * using a Jersey ExceptionMapper provider class.
 */
public class NullResponseInterceptor implements MethodInterceptor {

  private static Logger log = LoggerFactory.getLogger(NullResponseInterceptor.class);

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    log.debug("Interceptor");
    //the method is invoked to evaluate the response
    Object result = invocation.proceed();
    //is response is null a NotFoundException (which is mapped to Results.NOT_FOUND = 404) is thrown
    if (result == null){
      log.debug("Throwing NotFoundException");
       throw new NotFoundException();
    }
    return result;
  }
}
