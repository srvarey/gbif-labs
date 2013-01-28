package org.gbif.registry.data;

import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.model.WritableOrganization;

import java.util.Map;

import org.codehaus.jackson.type.TypeReference;


public class Organizations extends
  JsonBackedData<Map<Organizations.TYPE, Organization>, Map<Organizations.TYPE, WritableOrganization>> {

  public enum TYPE {
    BGBM, KEW
  };

  private final static Organizations INSTANCE = new Organizations();

  private Organizations() {
    super("data/organizations.json", new TypeReference<Map<TYPE, Organization>>() {
    }, new TypeReference<Map<TYPE, WritableOrganization>>() {
    });
  };

  public static WritableOrganization writableInstanceOf(TYPE type) {
    return INSTANCE.writable().get(type);
  }

  public static Organization instanceOf(TYPE type) {
    return INSTANCE.readable().get(type);
  }
}
