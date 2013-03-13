/*
 * Copyright 2012 Global Biodiversity Information Facility (GBIF)
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
package org.gbif.api.registry.vocabulary;

import org.gbif.api.util.VocabularyUtils;

/**
 * Enumeration for all contact types.
 */
public enum ContactType {

  TECHNICAL_POINT_OF_CONTACT,
  ADMINISTRATIVE_POINT_OF_CONTACT,
  POINT_OF_CONTACT,
  ORIGINATOR,
  METADATA_AUTHOR,
  PRINCIPAL_INVESTIGATOR,
  AUTHOR,
  CONTENT_PROVIDER,
  CUSTODIAN_STEWARD,
  DISTRIBUTOR,
  EDITOR,
  OWNER,
  PROCESSOR,
  PUBLISHER,
  USER,
  PROGRAMMER,
  DATA_ADMINISTRATOR,
  SYSTEM_ADMINISTRATOR;

  /**
   * @param contact type
   *
   * @return the matching {@link ContactType} or null
   */
  public static ContactType fromString(String contactType) {
    return (ContactType) VocabularyUtils.lookupEnum(contactType, ContactType.class);
  }
}
