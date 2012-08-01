package org.gbif.cube.gmap.density.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class OccurrenceWritableTest {

  @Test
  public void testSerDe() {
    OccurrenceWritable o = new OccurrenceWritable(1, null, null, null, null, null, null, 1, 0, null, "1", null, "UK", 0.89, null, 1);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutput out = new DataOutputStream(baos);
    try {
      o.write(out);
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      o = new OccurrenceWritable();
      o.readFields(new DataInputStream(bais));
      assertEquals(new Integer(1), o.getKingdomID());
      assertNull(o.getPhylumID());
      assertNull(o.getLongitude());
      assertNull(o.getLongitude());

    } catch (IOException e) {
      fail(e.getMessage());
    }

  }

}
