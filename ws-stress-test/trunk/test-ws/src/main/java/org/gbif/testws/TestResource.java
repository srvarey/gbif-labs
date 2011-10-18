package org.gbif.testws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("test")
public class TestResource {

  @GET
  public String test() {
    return "TEST";
  }

}
