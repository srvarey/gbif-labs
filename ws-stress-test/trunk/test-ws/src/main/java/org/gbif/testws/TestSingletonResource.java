package org.gbif.testws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.google.inject.Singleton;

@Path("singleton")
@Singleton
public class TestSingletonResource {

  @GET
  public String test() {
    return "{\"id\":1}";
  }

}
