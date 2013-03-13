package org.gbif.registry;

import org.gbif.api.registry.model.Identifier;
import org.gbif.api.registry.model.NetworkEntity;
import org.gbif.api.registry.service.IdentifierService;
import org.gbif.registry.utils.Identifiers;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class IdentifierTests {

  @Test
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
