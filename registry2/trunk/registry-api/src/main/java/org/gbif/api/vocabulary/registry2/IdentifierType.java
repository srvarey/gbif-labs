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
package org.gbif.api.vocabulary.registry2;

import org.gbif.api.util.VocabularyUtils;

/**
 * Enumeration for all possible identifier types.
 */
public enum IdentifierType {

  SOURCE_ID,
  URL,
  LSID,
  HANDLER,
  DOI,
  UUID,
  FTP,
  URI,
  UNKNOWN,
  GBIF_PORTAL,
  GBIF_NODE,

  /**
   * Participant identifier from the GBIF IMS Filemaker system.
   */
  GBIF_PARTICIPANT;

  /**
   * @return the matching IdentifierType or null
   */
  public static IdentifierType fromString(String identifierType) {
    return (IdentifierType) VocabularyUtils.lookupEnum(identifierType, IdentifierType.class);
  }

}
