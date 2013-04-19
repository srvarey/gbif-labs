package org.gbif.registry2.events;

import static com.google.common.base.Preconditions.checkNotNull;

public class CreateEvent<T> {

  private final T newObject;
  private final Class<T> objectClass;

  public static <T> CreateEvent<T> newInstance(T newObject, Class<T> objectClass) {
    return new CreateEvent<T>(newObject, objectClass);
  }

  public CreateEvent(T newObject, Class<T> objectClass) {
    this.newObject = checkNotNull(newObject, "newObject can't be null");
    this.objectClass = checkNotNull(objectClass, "objectClass can't be null");
  }

  public T getNewObject() {
    return newObject;
  }

  public Class<T> getObjectClass() {
    return objectClass;
  }

}
