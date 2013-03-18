package org.gbif.api.registry.service;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;

import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;


public interface NetworkEntityService<T> {

  UUID create(@NotNull T entity);

  void delete(@NotNull UUID key);

  T get(@NotNull UUID key);

  PagingResponse<T> list(@Nullable Pageable page);

  void update(@NotNull T entity);
}
