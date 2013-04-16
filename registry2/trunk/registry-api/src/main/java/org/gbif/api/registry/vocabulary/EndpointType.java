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
 * Enumeration for all endpoint types.
 */
public enum EndpointType {

  EML,
  FEED,
  WFS,
  WMS,
  TCS_RDF,
  TCS_XML,
  DWC_ARCHIVE,
  DIGIR,
  DIGIR_MANIS,
  TAPIR,
  BIOCASE,
  OAI_PMH,
  DWC_ARCHIVE_CHECKLIST,
  DWC_ARCHIVE_OCCURRENCE,
  OTHER;

  /**
   * @return the matching EndpointType or null
   */
  public static EndpointType fromString(String endpointType) {
    return (EndpointType) VocabularyUtils.lookupEnum(endpointType, EndpointType.class);
  }
}
