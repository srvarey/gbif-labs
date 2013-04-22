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
package org.gbif.api.service.registry2;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry2.Node;
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.vocabulary.Country;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Actions on a GBIF node.
 */
public interface NodeService extends NetworkEntityService<Node>, MachineTagService, TagService, CommentService,
  IdentifierService {

  /**
   * Provides access to the organizations endorsed by a single node.
   */
  PagingResponse<Organization> organizationsEndorsedBy(@NotNull UUID nodeKey, @Nullable Pageable page);

  /**
   * Provides access to the organizations that are awaiting an endorsement approval.
   */
  PagingResponse<Organization> pendingEndorsements(@Nullable Pageable page);

  /**
   * Returns a node for a given country.
   *
   * @return the countries node or null if none exists
   */
  Node getByCountry(Country country);

  /**
   * Returns a list of all countries which do have a GBIF node.
   *
   * @return list of distinct countries having a GBIF node
   */
  List<Country> listNodeCountries();

}
