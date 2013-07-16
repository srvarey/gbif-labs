package org.gbif.registry.metasync;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ContextTest {

  @Test
  public void testGetCounters() {
    Context context = new Context();
    context.incrementCounter("B");
    context.incrementCounter("C");
    context.incrementCounter("A");
    context.incrementCounter("B");
    context.incrementCounter("C");

    ImmutableMap<String, Integer> counters = context.getCounters();
    assertEquals(3, counters.size());
    assertEquals("A", counters.keySet().asList().get(0));
    assertEquals("B", counters.keySet().asList().get(1));
    assertEquals("C", counters.keySet().asList().get(2));
    assertEquals(Integer.valueOf(1), counters.get("A"));
    assertEquals(Integer.valueOf(2), counters.get("B"));
    assertEquals(Integer.valueOf(2), counters.get("C"));
  }
}
