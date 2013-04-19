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
package org.gbif.registry2.events;

public class DeleteEvent<T> {

  private final T oldObject;
  private final Class<T> objectClass;

  public static <T> DeleteEvent<T> newInstance(T oldObject, Class<T> objectClass) {
    return new DeleteEvent<T>(oldObject, objectClass);
  }

  public DeleteEvent(T oldObject, Class<T> objectClass) {
    this.oldObject = oldObject;
    this.objectClass = objectClass;
  }

  public T getOldObject() {
    return oldObject;
  }

  public Class<T> getObjectClass() {
    return objectClass;
  }

}
