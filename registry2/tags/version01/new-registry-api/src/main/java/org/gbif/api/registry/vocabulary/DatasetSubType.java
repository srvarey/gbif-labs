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
 * Enumeration for all possible dataset subtypes.
 */
public enum DatasetSubType {

  TAXONOMIC_AUTHORITY,
  NOMENCLATOR_AUTHORITY,
  INVENTORY_THEMATIC,
  INVENTORY_REGIONAL,
  GLOBAL_SPECIES_DATASET,
  DERIVED_FROM_OCCURRENCE,
  SPECIMEN,
  OBSERVATION;

  /**
   * @param dataset subtype
   *
   * @return the matching {@link DatasetSubType} or null
   */
  public static DatasetSubType fromString(String datasetSubType) {
    return (DatasetSubType) VocabularyUtils.lookupEnum(datasetSubType, DatasetSubType.class);
  }
}
