package org.gbif.registry.ws.resources;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry.metasync.MetasyncHistory;
import org.gbif.api.service.registry.MetasyncHistoryService;
import org.gbif.registry.persistence.mapper.MetasyncHistoryMapper;
import org.gbif.registry.ws.guice.Trim;
import org.gbif.ws.util.ExtraMediaTypes;

import java.util.UUID;

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
 * Metasync history resource/web service.
 */
@Singleton
@Path("registry/metasync/history")
@Produces({MediaType.APPLICATION_JSON, ExtraMediaTypes.APPLICATION_JAVASCRIPT})
@Consumes(MediaType.APPLICATION_JSON)
public class MetasyncHistoryResource implements MetasyncHistoryService {

  private final MetasyncHistoryMapper metasyncHistoryMapper;
  private static final String ADMIN_ROLE = "ADMIN";

  @Inject
  public MetasyncHistoryResource(MetasyncHistoryMapper metasyncHistoryMapper) {
    this.metasyncHistoryMapper = metasyncHistoryMapper;
  }

  @POST
  @Trim
  @Transactional
  @RolesAllowed(ADMIN_ROLE)
  @Override
  public void create(@Valid @NotNull @Trim MetasyncHistory metasyncHistory) {
    metasyncHistoryMapper.create(metasyncHistory);
  }

  @GET
  @Override
  public PagingResponse<MetasyncHistory> list(@Context Pageable page) {
    return new PagingResponse<MetasyncHistory>(page, (long) metasyncHistoryMapper.count(),
      metasyncHistoryMapper.list(page));
  }

  @GET
  @Path("{installationKey}")
  @Override
  public PagingResponse<MetasyncHistory> listByInstallation(@PathParam("installationKey") UUID installationKey,
    @Context Pageable page) {
    return new PagingResponse<MetasyncHistory>(page, (long) metasyncHistoryMapper.countByInstallation(installationKey),
      metasyncHistoryMapper.listByInstallation(installationKey, page));
  }

}
