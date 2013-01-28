package org.gbif.registry.data;

import org.gbif.api.registry.model.Contact;

import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import org.codehaus.jackson.map.ObjectMapper;

// TODO: refactor a common parent for this and Nodes
public class Contacts {

  private final String json;
  private final ObjectMapper MAPPER = new ObjectMapper();
  private final static Contacts INSTANCE = new Contacts();

  private Contacts() {
    json = getJson("data/contacts.json");
  };

  private String getJson(String file) {
    try {
      return Resources.toString(Resources.getResource(file), Charsets.UTF_8);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  public static Contact newInstance() {
    try {
      return INSTANCE.MAPPER.readValue(INSTANCE.json, Contact.class);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }
}
