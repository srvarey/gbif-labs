/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry2;

import org.gbif.api.model.registry2.Identifier;
import org.gbif.api.model.registry2.NetworkEntity;
import org.gbif.api.service.registry2.IdentifierService;
import org.gbif.registry2.utils.Identifiers;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class IdentifierTests {

  public static <T extends NetworkEntity> void testAddDelete(IdentifierService service, T entity) {

    // check there are none on a newly created entity
    List<Identifier> identifiers = service.listIdentifiers(entity.getKey());
    assertNotNull("Identifier list should be empty, not null when no identifiers exist", identifiers);
    assertTrue("Identifiers should be empty when none added", identifiers.isEmpty());

    // test additions
    service.addIdentifier(entity.getKey(), Identifiers.newInstance());
    service.addIdentifier(entity.getKey(), Identifiers.newInstance());
    identifiers = service.listIdentifiers(entity.getKey());
    assertNotNull(identifiers);
    assertEquals("2 identifiers have been added", 2, identifiers.size());

    // test deletion, ensuring correct one is deleted
    service.deleteIdentifier(entity.getKey(), identifiers.get(0).getKey());
    identifiers = service.listIdentifiers(entity.getKey());
    assertNotNull(identifiers);
    assertEquals("1 identifier should remain after the deletion", 1, identifiers.size());
    Identifier expected = Identifiers.newInstance();
    Identifier created = identifiers.get(0);
    expected.setKey(created.getKey());
    expected.setCreated(created.getCreated());
    assertEquals("Created identifier does not read as expected", expected, created);
  }

}
