package org.gbif.registry2.ws.client;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Utilities to help construct parameters since Jersey classes are not very fluent.
 */
public class Params {

  public static MultivaluedMap<String, String> of(String key, String value) {
    MultivaluedMap<String, String> params = new MultivaluedMapImpl();
    params.putSingle(key, value);
    return params;
  }
}
