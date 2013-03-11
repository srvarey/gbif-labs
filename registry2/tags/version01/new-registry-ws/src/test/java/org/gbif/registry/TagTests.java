package org.gbif.registry;

import org.gbif.api.registry.model.NetworkEntity;
import org.gbif.api.registry.model.Tag;
import org.gbif.api.registry.service.TagService;

import java.util.List;
import java.util.UUID;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TagTests {

  @Test
  public static <T extends NetworkEntity> void testTagErroneousDelete(TagService service, T entity) {
    int tagKey = service.addTag(entity.getKey(), "tag1");
    service.deleteTag(UUID.randomUUID(), tagKey); // wrong parent UUID
    // nothing happens - expected?
  }

  @Test
  public static <T extends NetworkEntity> void testAddDelete(TagService service, T entity) {
    List<Tag> tags = service.listTags(entity.getKey(), null);
    assertNotNull("Tag list should be empty, not null when no tags exist", tags);
    assertTrue("Tags should be empty when none added", tags.isEmpty());
    service.addTag(entity.getKey(), "tag1");
    service.addTag(entity.getKey(), "tag2");
    tags = service.listTags(entity.getKey(), null);
    assertNotNull(tags);
    assertEquals("2 tags have been added", 2, tags.size());
    service.deleteTag(entity.getKey(), tags.get(0).getKey());
    tags = service.listTags(entity.getKey(), null);
    assertNotNull(tags);
    assertEquals("1 tag should remain after the deletion", 1, tags.size());
  }
}
