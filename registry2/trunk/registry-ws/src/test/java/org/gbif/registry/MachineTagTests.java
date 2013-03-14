package org.gbif.registry;

import org.gbif.api.registry.model.MachineTag;
import org.gbif.api.registry.model.NetworkEntity;
import org.gbif.api.registry.service.MachineTagService;
import org.gbif.registry.utils.MachineTags;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MachineTagTests {

  public static <T extends NetworkEntity> void testAddDelete(MachineTagService service, T entity) {

    // check there are none on a newly created entity
    List<MachineTag> machineTags = service.listMachineTags(entity.getKey());
    assertNotNull("Machine tag list should be empty, not null when no machine tags exist", machineTags);
    assertTrue("Machine Tag should be empty when none added", machineTags.isEmpty());

    // test additions
    service.addMachineTag(entity.getKey(), MachineTags.newInstance());
    service.addMachineTag(entity.getKey(), MachineTags.newInstance());
    machineTags = service.listMachineTags(entity.getKey());
    assertNotNull(machineTags);
    assertEquals("2 machine tags have been added", 2, machineTags.size());

    // test deletion, ensuring correct one is deleted
    service.deleteMachineTag(entity.getKey(), machineTags.get(0).getKey());
    machineTags = service.listMachineTags(entity.getKey());
    assertNotNull(machineTags);
    assertEquals("1 machine tag should remain after the deletion", 1, machineTags.size());
    MachineTag expected = MachineTags.newInstance();
    MachineTag created = machineTags.get(0);
    expected.setKey(created.getKey());
    expected.setCreated(created.getCreated());
    assertEquals("Created machine tag does not read as expected", expected, created);
  }
}
