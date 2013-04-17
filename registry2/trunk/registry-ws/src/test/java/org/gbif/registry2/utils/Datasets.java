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
package org.gbif.registry2.utils;

import org.gbif.api.model.registry2.Dataset;

import java.util.UUID;

import org.codehaus.jackson.type.TypeReference;

public class Datasets extends JsonBackedData<Dataset> {

  private static final Datasets INSTANCE = new Datasets();

  public static Dataset newInstance(UUID owningOrganizationKey) {
    Dataset d = INSTANCE.newTypedInstance();
    d.setOwningOrganizationKey(owningOrganizationKey);
    return d;
  }

  public Datasets() {
    super("data/dataset.json", new TypeReference<Dataset>() {});
  }

}
