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

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.registry2.NetworkEntity;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

import org.apache.ibatis.annotations.Param;

/**
 * Mappers that perform operations on network entities.
 */
public interface NetworkEntityMapper<T extends NetworkEntity> {

  T get(@Param("key") UUID key);

  void create(T entity);

  void delete(@Param("key") UUID key);

  void update(T entity);

  List<T> list(@Nullable @Param("page") Pageable page);

  int count();

}
