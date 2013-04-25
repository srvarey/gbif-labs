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
package org.gbif.registry2.persistence.mapper;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Metadata;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.ibatis.annotations.Param;

public interface DatasetMapper extends BaseNetworkEntityMapper3<Dataset> {

  /**
   * Obtains the metadata associated to a single dataset.
   */
  Metadata getMetadata(@Param("datasetKey") UUID datasetKey);

  /**
   * Obtains a list of all the constituent datasets that are part of this network.
   */
  List<Dataset> listDatasetsInNetwork(@Param("networkKey") UUID networkKey, @Nullable @Param("page") Pageable page);

  /**
   * Obtains a list of all the datasets owned by the given organization.
   */
  List<Dataset> listDatasetsOwnedBy(@Param("organizationKey") UUID organizationKey,
    @Nullable @Param("page") Pageable page);

  /**
   * Obtains a list of all the datasets hosted by the given organization.
   */
  List<Dataset> listDatasetsHostedBy(@Param("organizationKey") UUID organizationKey,
    @Nullable @Param("page") Pageable page);

  /**
   * Obtains a list of all the datasets hosted by the given installation.
   */
  List<Dataset> listDatasetsByInstallation(@Param("installationKey") UUID installationKey,
    @Nullable @Param("page") Pageable page);


}
