package org.gbif.registry;

import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.model.NetworkEntity;
import org.gbif.api.registry.service.ContactService;
import org.gbif.registry.utils.Contacts;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ContactTests {

  @Test
  public static <T extends NetworkEntity> void testAddDelete(ContactService service, T entity) {

    // check there are none on a newly created entity
    List<Contact> contacts = service.listContacts(entity.getKey());
    assertNotNull("Contact list should be empty, not null when no contacts exist", contacts);
    assertTrue("Contact should be empty when none added", contacts.isEmpty());

    // test additions
    service.addContact(entity.getKey(), Contacts.newInstance());
    service.addContact(entity.getKey(), Contacts.newInstance());
    contacts = service.listContacts(entity.getKey());
    assertNotNull(contacts);
    assertEquals("2 contacts have been added", 2, contacts.size());

    // test deletion, ensuring correct one is deleted
    service.deleteContact(entity.getKey(), contacts.get(0).getKey());
    contacts = service.listContacts(entity.getKey());
    assertNotNull(contacts);
    assertEquals("1 contact should remain after the deletion", 1, contacts.size());
    Contact expected = Contacts.newInstance();
    Contact created = contacts.get(0);
    expected.setKey(created.getKey());
    expected.setCreated(created.getCreated());
    expected.setModified(created.getModified());
    assertEquals("Created contact does not read as expected", expected, created);
  }
}
