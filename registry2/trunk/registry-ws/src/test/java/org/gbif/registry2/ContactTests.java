/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry2;

import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.NetworkEntity;
import org.gbif.api.service.registry2.ContactService;
import org.gbif.api.service.registry2.NetworkEntityService;
import org.gbif.registry2.utils.Contacts;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ContactTests {

  public static <T extends NetworkEntity> void testAddDelete(ContactService service, T entity) {

    // check there are none on a newly created entity
    List<Contact> contacts = service.listContacts(entity.getKey());
    assertNotNull("Contact list should be empty, not null when no contacts exist", contacts);
    assertTrue("Contact should be empty when none added", contacts.isEmpty());

    // test additions, both being primary
    service.addContact(entity.getKey(), Contacts.newInstance());
    service.addContact(entity.getKey(), Contacts.newInstance());
    contacts = service.listContacts(entity.getKey());
    assertNotNull(contacts);
    assertEquals("2 contacts have been added", 2, contacts.size());
    assertFalse("Older contact should not be primary anymore", contacts.get(0).isPrimary());
    assertTrue("Newer contact should be primary", contacts.get(1).isPrimary());


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

  /**
   * Tests that adding a contact means the entity is found in the search.
   */
  public static <T extends NetworkEntity> void testSimpleSearch(ContactService service,
    NetworkEntityService<T> networkService, T entity) {
    assertEquals("There should be no results for this search", Long.valueOf(0), networkService.search("Frankie", null)
      .getCount());
    Contact c = Contacts.newInstance();
    c.setLastName("Frankie");
    service.addContact(entity.getKey(), c);
    assertEquals("There should a search result for Frankie", Long.valueOf(1), networkService.search("Frankie", null)
      .getCount());

  }
}
