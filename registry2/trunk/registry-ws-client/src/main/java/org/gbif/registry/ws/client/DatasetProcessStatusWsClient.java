package org.gbif.registry.ws.client;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.crawler.DatasetProcessStatus;
import org.gbif.api.service.registry.DatasetProcessStatusService;
import org.gbif.registry.ws.client.guice.RegistryWs;
import org.gbif.ws.client.BaseWsGetClient;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.inject.Inject;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * DatasetProcessStatusService web service client.
 */
public class DatasetProcessStatusWsClient extends BaseWsGetClient<DatasetProcessStatus, UUID> implements
  DatasetProcessStatusService {

  @Inject
  public DatasetProcessStatusWsClient(@RegistryWs WebResource resource, @Nullable ClientFilter authFilter) {
    super(DatasetProcessStatus.class, resource.path("dataset/process"), authFilter);
  }

  @Override
  public void create(DatasetProcessStatus datasetProcessStatus) {
    post(datasetProcessStatus, "/");
  }

  @Override
  public DatasetProcessStatus get(UUID datasetKey, int attempt) {
    return get(datasetKey.toString() + '/' + Integer.toString(attempt));
  }


  @Override
  public PagingResponse<DatasetProcessStatus> list(Pageable page) {
    return get(GenericTypes.PAGING_DATASET_PROCESS_STATUS, page);
  }

  @Override
  public PagingResponse<DatasetProcessStatus> listByDataset(UUID datasetKey, Pageable page) {
    return get(GenericTypes.PAGING_DATASET_PROCESS_STATUS, page, datasetKey.toString());
  }

}
