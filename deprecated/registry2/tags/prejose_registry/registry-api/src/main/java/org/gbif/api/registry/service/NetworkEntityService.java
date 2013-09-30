package org.gbif.api.registry.service;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;

import java.util.UUID;

import javax.annotation.Nullable;


public interface NetworkEntityService<T> {

  UUID create(T entity);

  void delete(UUID key);

  T get(UUID key);

  PagingResponse<T> list(@Nullable Pageable page);

  void update(T entity);
}
