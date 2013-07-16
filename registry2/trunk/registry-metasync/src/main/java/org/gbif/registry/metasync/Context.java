package org.gbif.registry.metasync;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;

/**
 * A context for which the metadata synchronization is running.
 * This allows, (e.g.) multithreaded clients to maintain a shared job state.
 */
@ThreadSafe
public class Context {

  private final Map<String, AtomicInteger> counters = Maps.newHashMap();
  private final Object counterLock = new Object();

  /**
   * Increment the named counter by 1, creating it if necessary.
   * 
   * @param name Of the counter to increment
   */
  public void incrementCounter(String name) {
    synchronized (counterLock) {
      if (!counters.containsKey(name)) {
        counters.put(name, new AtomicInteger(1));
        return;
      }
    }
    counters.get(name).incrementAndGet();
  }

  /**
   * @return a copy of the counters sorted by keys alphabetically
   */
  public ImmutableSortedMap<String, Integer> getCounters() {
    ImmutableSortedMap.Builder<String, Integer> builder = ImmutableSortedMap.naturalOrder();
    for (Entry<String, AtomicInteger> e : counters.entrySet()) {
      builder.put(e.getKey(), e.getValue().get());
    }
    return builder.build();
  }
}
