package org.gbif.resource.exceptions;

/**
 *  This class represent any exception that is not managed directly by the resource layer, could be an exception thrown by
 *  the service (business)  layer notifying the absence of expected response
 */
public class ResourceNotFoundException extends RuntimeException{

  public ResourceNotFoundException() {
    super();
  }

  public ResourceNotFoundException(String s) {
    super(s);
  }

  public ResourceNotFoundException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public ResourceNotFoundException(Throwable throwable) {
    super(throwable);
  }
}
