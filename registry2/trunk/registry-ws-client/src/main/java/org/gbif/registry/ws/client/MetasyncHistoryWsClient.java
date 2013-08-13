package org.gbif.registry.ws.client;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry.metasync.MetasyncHistory;
import org.gbif.api.service.registry.MetasyncHistoryService;
import org.gbif.registry.ws.client.guice.RegistryWs;
import org.gbif.ws.client.BaseWsGetClient;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.inject.Inject;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * MetasyncHistoryService web service client.
 */
public class MetasyncHistoryWsClient extends BaseWsGetClient<MetasyncHistory, UUID> implements
  MetasyncHistoryService {

  @Inject
  public MetasyncHistoryWsClient(@RegistryWs WebResource resource, @Nullable ClientFilter authFilter) {
    super(MetasyncHistory.class, resource.path("registry/metasync/history"), authFilter);
  }

  @Override
  public void create(MetasyncHistory metasyncHistory) {
    post(metasyncHistory, "/");
  }

  @Override
  public PagingResponse<MetasyncHistory> list(Pageable page) {
    return get(GenericTypes.METASYNC_HISTORY, page);
  }

  @Override
  public PagingResponse<MetasyncHistory> listByInstallation(UUID installationKey, Pageable page) {
    return get(GenericTypes.METASYNC_HISTORY, page, installationKey.toString());
  }

}
