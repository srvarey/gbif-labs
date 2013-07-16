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
package org.gbif.registry.metasync.api;

/**
 * Any exception happening during synchronisation will be converted to this exception. It currently does not retain any
 * information about the source exception except a category the error belonged to.
 */
public class MetadataException extends Exception {

  private final ErrorCode error;

  public MetadataException(ErrorCode error) {
    this.error = error;
  }

  public ErrorCode getError() {
    return error;
  }
}
