package org.gbif.registry.persistence.mapper;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.registry.model.NetworkEntity;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.ibatis.annotations.Param;


/**
 * Mappers that perform operations on network entities.
 * 
 * @param <T>
 */
public interface NetworkEntityMapper<T extends NetworkEntity> {

  T get(@Param("key") UUID key);

  void create(T entity);

  void delete(@Param("key") UUID key);

  void update(T entity);

  List<T> list(@Nullable @Param("page") Pageable page);

  int count();
}
