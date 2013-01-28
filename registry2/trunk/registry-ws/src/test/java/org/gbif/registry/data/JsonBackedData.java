package org.gbif.registry.data;

import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;


public abstract class JsonBackedData<R, W> {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private final TypeReference<R> readableType;
  private final TypeReference<W> writableType;
  private final String json; // for reuse

  protected JsonBackedData(String file, TypeReference<R> readableType, TypeReference<W> writableType) {
    json = getJson(file);
    this.readableType = readableType;
    this.writableType = writableType;
  }

  // utility method to read the file, and throw RTE if there is a problem
  private String getJson(String file) {
    try {
      return Resources.toString(Resources.getResource(file), Charsets.UTF_8);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  protected R readable() {
    try {
      return MAPPER.readValue(json, readableType);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  protected R writable() {
    try {
      return MAPPER.readValue(json, writableType);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }
}
