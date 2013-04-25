/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
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

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Organization;

import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

public interface OrganizationService
  extends NetworkEntityService<Organization>, ContactService, EndpointService, MachineTagService, TagService,
  IdentifierService, CommentService {


  /**
   * Provides paging service to list datasets hosted by a specific organization.
   */
  PagingResponse<Dataset> hostedDatasets(@NotNull UUID organizationKey, @Nullable Pageable page);

  /**
   * Provides paging service to list datasets owned by a specific organization.
   */
  PagingResponse<Dataset> ownedDatasets(@NotNull UUID organizationKey, @Nullable Pageable page);
}
