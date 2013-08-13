package org.gbif.registry.ws.resources;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.crawler.DatasetProcessStatus;
import org.gbif.api.service.registry.DatasetProcessStatusService;
import org.gbif.registry.persistence.mapper.DatasetProcessStatusMapper;
import org.gbif.registry.ws.guice.Trim;
import org.gbif.ws.server.interceptor.NullToNotFound;
import org.gbif.ws.util.ExtraMediaTypes;

import java.util.UUID;

import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.mybatis.guice.transactional.Transactional;

/**
 * Dataset processing status resource/web service.
 */
@Singleton
@Path("dataset/process")
@Produces({MediaType.APPLICATION_JSON, ExtraMediaTypes.APPLICATION_JAVASCRIPT})
@Consumes(MediaType.APPLICATION_JSON)
public class DatasetProcessStatusResource implements DatasetProcessStatusService {

  private final DatasetProcessStatusMapper datasetProcessStatusMapper;
  private static final String ADMIN_ROLE = "ADMIN";

  @Inject
  public DatasetProcessStatusResource(DatasetProcessStatusMapper datasetProcessStatusMapper) {
    this.datasetProcessStatusMapper = datasetProcessStatusMapper;
  }

  @POST
  @Trim
  @Transactional
  @RolesAllowed(ADMIN_ROLE)
  @Override
  public void create(@Valid @NotNull @Trim DatasetProcessStatus datasetProcessStatus) {
    datasetProcessStatusMapper.create(datasetProcessStatus);
  }

  @GET
  @Path("{datasetKey}/{attempt}")
  @Nullable
  @NullToNotFound
  @Override
  public DatasetProcessStatus get(@PathParam("datasetKey") UUID datasetKey, @PathParam("attempt") int attempt) {
    return datasetProcessStatusMapper.get(datasetKey, attempt);
  }

  @GET
  @Override
  public PagingResponse<DatasetProcessStatus> list(@Context Pageable page) {
    return new PagingResponse<DatasetProcessStatus>(page, (long) datasetProcessStatusMapper.count(),
      datasetProcessStatusMapper.list(page));
  }

  @GET
  @Path("{datasetKey}")
  @Override
  public PagingResponse<DatasetProcessStatus> listByDataset(@PathParam("datasetKey") UUID datasetKey,
    @Context Pageable page) {
    return new PagingResponse<DatasetProcessStatus>(page, (long) datasetProcessStatusMapper.countByDataset(datasetKey),
      datasetProcessStatusMapper.listByDataset(datasetKey, page));
  }

}
