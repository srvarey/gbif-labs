/*
 * Copyright 2012 Global Biodiversity Information Facility (GBIF)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.api.service.registry2;

import org.gbif.api.model.registry2.search.DatasetSearchParameter;
import org.gbif.api.model.registry2.search.DatasetSearchRequest;
import org.gbif.api.model.registry2.search.DatasetSearchResult;
import org.gbif.api.model.registry2.search.DatasetSuggestRequest;
import org.gbif.api.service.common.SearchService;
import org.gbif.api.service.common.SuggestService;

/**
 * Interface that provides search and suggest operations over Datasets.
 */
public interface DatasetSearchService
  extends SearchService<DatasetSearchResult, DatasetSearchParameter, DatasetSearchRequest>,
  SuggestService<DatasetSearchResult, DatasetSearchParameter, DatasetSuggestRequest> {

}
