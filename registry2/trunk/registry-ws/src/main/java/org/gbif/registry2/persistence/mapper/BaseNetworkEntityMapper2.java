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
package org.gbif.registry2.persistence.mapper;

import org.gbif.api.model.registry2.NetworkEntity;

/**
 * These mappers (BaseNetworkEntityMapper*) define a common interface for all our Network entities. We have five
 * different ones (Datasets, Installations, Networks, Nodes and Organizations) and they can be grouped in three
 * different categories:
 * <ul>
 * <li>BaseNetworkEntityMapper: Comments, Machine tags and tags (Node)</li>
 * <li>BaseNetworkEntityMapper2: Comments, Machine tags, tags, contacts and endpoints (Installation and Network)</li>
 * <li>BaseNetworkEntityMapper3: Comments, Machine tags, tags, contacts, identifiers and endpoints (Datasets and
 * Organizations)</li>
 * </ul>
 */
public interface BaseNetworkEntityMapper2<T extends NetworkEntity>
  extends BaseNetworkEntityMapper<T>, ContactableMapper, EndpointableMapper {}
