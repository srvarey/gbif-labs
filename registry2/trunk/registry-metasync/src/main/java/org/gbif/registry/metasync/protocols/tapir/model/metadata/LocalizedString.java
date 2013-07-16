package org.gbif.registry.metasync.protocols.tapir.model.metadata;

import org.gbif.api.vocabulary.Language;

import java.util.Map;

import com.google.common.collect.Maps;

public class LocalizedString {

  private final Map<Language, String> values = Maps.newHashMap();

  public void addValue(Language language, String value) {
    values.put(language, value);
  }

  public Map<Language, String> getValues() {
    return values;
  }

  @Override
  public String toString() {
    if (values.isEmpty()) {
      return null;
    }

    StringBuilder sb = new StringBuilder();
    for (Map.Entry<Language, String> entry : values.entrySet()) {
      sb.append(entry.getKey());
      sb.append(entry.getValue());
    }
    return sb.toString();
  }
}
