package org.gbif.registry.utils;

import org.gbif.api.registry.model.Contact;

import org.codehaus.jackson.type.TypeReference;


public class Contacts extends JsonBackedData<Contact> {

  private static final Contacts INSTANCE = new Contacts();

  private Contacts() {
    super("data/contact.json", new TypeReference<Contact>() {
    });
  }

  public static Contact newInstance() {
    return INSTANCE.newTypedInstance();
  }
}
