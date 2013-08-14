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
package org.gbif.registry.ws.resources;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry.Dataset;
import org.gbif.api.model.registry.Installation;
import org.gbif.api.model.registry.Organization;
import org.gbif.api.model.registry.metasync.MetasyncHistory;
import org.gbif.api.service.registry.InstallationService;
import org.gbif.api.service.registry.MetasyncHistoryService;
import org.gbif.api.vocabulary.InstallationType;
import org.gbif.common.messaging.api.MessagePublisher;
import org.gbif.common.messaging.api.messages.StartMetasyncMessage;
import org.gbif.registry.persistence.mapper.CommentMapper;
import org.gbif.registry.persistence.mapper.ContactMapper;
import org.gbif.registry.persistence.mapper.DatasetMapper;
import org.gbif.registry.persistence.mapper.EndpointMapper;
import org.gbif.registry.persistence.mapper.IdentifierMapper;
import org.gbif.registry.persistence.mapper.InstallationMapper;
import org.gbif.registry.persistence.mapper.MachineTagMapper;
import org.gbif.registry.persistence.mapper.MetasyncHistoryMapper;
import org.gbif.registry.persistence.mapper.OrganizationMapper;
import org.gbif.registry.persistence.mapper.TagMapper;
import org.gbif.registry.ws.guice.Trim;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.Point;
import org.mybatis.guice.transactional.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A MyBATIS implementation of the service.
 */
@Path("installation")
@Singleton
public class InstallationResource extends BaseNetworkEntityResource<Installation> implements InstallationService,
  MetasyncHistoryService {

  private static final String ADMIN_ROLE = "ADMIN";

  private static final Logger LOG = LoggerFactory.getLogger(InstallationResource.class);
  private final DatasetMapper datasetMapper;
  private final InstallationMapper installationMapper;
  private final OrganizationMapper organizationMapper;
  private final MetasyncHistoryMapper metasyncHistoryMapper;


  /**
   * The messagePublisher can be optional, and optional is not supported in constructor injection.
   */
  @Inject(optional = true)
  private final MessagePublisher messagePublisher = null;

  @Inject
  public InstallationResource(
    InstallationMapper installationMapper,
    ContactMapper contactMapper,
    EndpointMapper endpointMapper,
    IdentifierMapper identifierMapper,
    MachineTagMapper machineTagMapper,
    TagMapper tagMapper,
    CommentMapper commentMapper,
    DatasetMapper datasetMapper,
    OrganizationMapper organizationMapper,
    MetasyncHistoryMapper metasyncHistoryMapper,
    EventBus eventBus) {
    super(installationMapper,
      commentMapper,
      contactMapper,
      endpointMapper,
      identifierMapper,
      machineTagMapper,
      tagMapper,
      Installation.class,
      eventBus);
    this.datasetMapper = datasetMapper;
    this.installationMapper = installationMapper;
    this.organizationMapper = organizationMapper;
    this.metasyncHistoryMapper = metasyncHistoryMapper;
  }


  /**
   * All network entities support simple (!) search with "&q=".
   * This is to support the console user interface, and is in addition to any complex, faceted search that might
   * additionally be supported, such as dataset search.
   */
  @GET
  public PagingResponse<Installation> list(@Nullable @QueryParam("q") String query, @Nullable @Context Pageable page) {
    if (Strings.isNullOrEmpty(query)) {
      return list(page);
    } else {
      return search(query, page);
    }
  }

  @GET
  @Path("{key}/dataset")
  @Override
  public PagingResponse<Dataset> getHostedDatasets(@PathParam("key") UUID installationKey, @Context Pageable page) {
    return new PagingResponse<Dataset>(page, datasetMapper.countDatasetsByInstallation(installationKey),
      datasetMapper.listDatasetsByInstallation(installationKey, page));
  }

  @GET
  @Path("deleted")
  @Override
  public PagingResponse<Installation> listDeleted(@Context Pageable page) {
    return pagingResponse(page, installationMapper.countDeleted(), installationMapper.deleted(page));
  }

  @GET
  @Path("nonPublishing")
  @Override
  public PagingResponse<Installation> listNonPublishing(@Context Pageable page) {
    return pagingResponse(page, installationMapper.countNonPublishing(), installationMapper.nonPublishing(page));
  }

  /**
   * This is a REST only (e.g. not part of the Java API) method that allows the registry console to trigger the
   * synchronization of the installation. This simply emits a message to rabbitmq requesting the sync, and applies
   * necessary security.
   */
  @POST
  @Path("{key}/synchronize")
  @RolesAllowed(ADMIN_ROLE)
  public void synchronize(@PathParam("key") UUID installationKey) {
    if (messagePublisher != null) {
      LOG.info("Requesting synchronizing installation[{}]", installationKey);
      try {
        messagePublisher.send(new StartMetasyncMessage(installationKey));
      } catch (IOException e) {
        LOG.error("Unable to send message requesting synchronization", e);
      }

    } else {
      LOG.warn("Registry is configured to run without messaging capabilities.  Unable to synchronize installation[{}]",
        installationKey);
    }
  }

  /**
   * This is a REST only (e.g. not part of the Java API) method that allows you to get the locations of installations as
   * GeoJSON. This method exists primarily to produce the content for the "locations of organizations hosting an IPT".
   * The response holds the distinct organizations running the installations of the specified type.
   */
  @GET
  @Path("location/{type}")
  public FeatureCollection organizationsAsGeoJSON(@PathParam("type") InstallationType type) {
    List<Organization> orgs = organizationMapper.hostingInstallationsOf(type, true);
    FeatureCollection fc = new FeatureCollection();

    // to increment the count on duplicates
    Map<UUID, Feature> index = Maps.newHashMap();

    for (Organization o : orgs) {
      Feature f = (index.containsKey(o.getKey())) ? index.get(o.getKey()) : new Feature();

      if (index.containsKey(o.getKey())) {
        f.setProperty("count", ((Integer) f.getProperty("count")) + 1);
      } else {
        f.setProperty("key", o.getKey());
        f.setProperty("title", o.getTitle());
        f.setProperty("count", Integer.valueOf(1));
        // we ensured that georeferenced only orgs were returned above, so this should never throw NPE
        f.setGeometry(new Point(o.getLatitude().doubleValue(), o.getLongitude().doubleValue()));
        index.put(o.getKey(), f);
        fc.add(f);
      }
    }
    return fc;
  }

  @POST
  @Path("{installationKey}/metasync")
  @Trim
  @Transactional
  @RolesAllowed(ADMIN_ROLE)
  public void createMetasync(@PathParam("installationKey") UUID installationKey,
    @Valid @NotNull @Trim MetasyncHistory metasyncHistory) {
    checkArgument(installationKey.equals(metasyncHistory.getInstallationKey()),
      "Metasync must have the same key as the installation");
    this.createMetasync(metasyncHistory);
  }

  @Trim
  @Transactional
  @RolesAllowed(ADMIN_ROLE)
  @Override
  public void createMetasync(@Valid @NotNull @Trim MetasyncHistory metasyncHistory) {
    metasyncHistoryMapper.create(metasyncHistory);
  }

  @Path("metasync")
  @GET
  @Override
  public PagingResponse<MetasyncHistory> listMetasync(@Context Pageable page) {
    return new PagingResponse<MetasyncHistory>(page, (long) metasyncHistoryMapper.count(),
      metasyncHistoryMapper.list(page));
  }


  @GET
  @Path("{installationKey}/metasync")
  @Override
  public PagingResponse<MetasyncHistory> listMetasync(@PathParam("installationKey") UUID installationKey,
    @Context Pageable page) {
    return new PagingResponse<MetasyncHistory>(page, (long) metasyncHistoryMapper.countByInstallation(installationKey),
      metasyncHistoryMapper.listByInstallation(installationKey, page));
  }
}
