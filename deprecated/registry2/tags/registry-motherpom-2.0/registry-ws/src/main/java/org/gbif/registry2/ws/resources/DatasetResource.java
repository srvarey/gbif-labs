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
package org.gbif.registry2.ws.resources;

import org.gbif.api.exception.ServiceUnavailableException;
import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.common.search.SearchResponse;
import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Metadata;
import org.gbif.api.model.registry2.search.DatasetSearchParameter;
import org.gbif.api.model.registry2.search.DatasetSearchRequest;
import org.gbif.api.model.registry2.search.DatasetSearchResult;
import org.gbif.api.model.registry2.search.DatasetSuggestRequest;
import org.gbif.api.service.registry2.DatasetSearchService;
import org.gbif.api.service.registry2.DatasetService;
import org.gbif.api.vocabulary.Country;
import org.gbif.api.vocabulary.registry2.DatasetType;
import org.gbif.api.vocabulary.registry2.MetadataType;
import org.gbif.registry2.metadata.EMLWriter;
import org.gbif.registry2.metadata.parse.DatasetParser;
import org.gbif.registry2.persistence.WithMyBatis;
import org.gbif.registry2.persistence.mapper.CommentMapper;
import org.gbif.registry2.persistence.mapper.ContactMapper;
import org.gbif.registry2.persistence.mapper.DatasetMapper;
import org.gbif.registry2.persistence.mapper.EndpointMapper;
import org.gbif.registry2.persistence.mapper.IdentifierMapper;
import org.gbif.registry2.persistence.mapper.MachineTagMapper;
import org.gbif.registry2.persistence.mapper.MetadataMapper;
import org.gbif.registry2.persistence.mapper.TagMapper;
import org.gbif.ws.server.interceptor.NullToNotFound;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A MyBATIS implementation of the service.
 */
@Path("dataset")
@Singleton
public class DatasetResource extends BaseNetworkEntityResource<Dataset>
  implements DatasetService, DatasetSearchService {

  private static final Logger LOG = LoggerFactory.getLogger(DatasetResource.class);

  private final DatasetSearchService searchService;
  private final MetadataMapper metadataMapper;
  private final DatasetMapper datasetMapper;
  private final ContactMapper contactMapper;

  @Inject
  public DatasetResource(DatasetMapper datasetMapper, ContactMapper contactMapper, EndpointMapper endpointMapper,
    MachineTagMapper machineTagMapper, TagMapper tagMapper, IdentifierMapper identifierMapper,
    CommentMapper commentMapper, EventBus eventBus, DatasetSearchService searchService, MetadataMapper metadataMapper) {
    super(datasetMapper, commentMapper, contactMapper, endpointMapper, identifierMapper, machineTagMapper, tagMapper,
      Dataset.class, eventBus);
    this.searchService = searchService;
    this.metadataMapper = metadataMapper;
    this.datasetMapper = datasetMapper;
    this.contactMapper = contactMapper;
  }

  @GET
  @Path("search")
  @Override
  public SearchResponse<DatasetSearchResult, DatasetSearchParameter> search(
    @Context DatasetSearchRequest searchRequest) {
    LOG.debug("Search operation received {}", searchRequest);
    return searchService.search(searchRequest);
  }

  @Path("suggest")
  @GET
  @Override
  public List<DatasetSearchResult> suggest(@Context DatasetSuggestRequest suggestRequest) {
    LOG.debug("Suggest operation received {}", suggestRequest);
    return searchService.suggest(suggestRequest);
  }

  @GET
  @Path("{key}")
  @Nullable
  @NullToNotFound
  @Override
  public Dataset get(@PathParam("key") UUID key) {
    return merge(getPreferredMetadataDataset(key), super.get(key));
  }


  /**
   * All network entities support simple (!) search with "&q=".
   * This is to support the console user interface, and is in addition to any complex, faceted search that might
   * additionally be supported, such as dataset search.
   */
  @GET
  public PagingResponse<Dataset> list(@Nullable @QueryParam("country") Country country,
    @Nullable @QueryParam("type") DatasetType type,
    @Nullable @QueryParam("q") String query,
    @Nullable @Context Pageable page) {
    if (country == null && type != null) {
      return listByType(type, page);
    } else if (country != null) {
      return listByCountry(country, type, page);
    } else if (!Strings.isNullOrEmpty(query)) {
      return search(query, page);
    } else {
      return list(page);
    }
  }

  @Override
  public PagingResponse<Dataset> listByCountry(Country country, DatasetType type, Pageable page) {
    long total = datasetMapper.countWithFilter(country, type);
    return pagingResponse(page, total, datasetMapper.listWithFilter(country, type, page));
  }

  public PagingResponse<Dataset> listByType(DatasetType type, Pageable page) {
    long total = datasetMapper.countWithFilter(null, type);
    return pagingResponse(page, total, datasetMapper.listWithFilter(null, type, page));
  }


  @Override
  public PagingResponse<Dataset> search(String query, @Nullable Pageable page) {
    return augmentWithMetadata(super.search(query, page));
  }

  @Override
  public PagingResponse<Dataset> list(@Nullable Pageable page) {
    return augmentWithMetadata(super.list(page));
  }

  /**
   * Returns the parsed, preferred metadata document as a dataset.
   */
  private Dataset getPreferredMetadataDataset(UUID key) {
    List<Metadata> docs = listMetadata(key, null);
    if (!docs.isEmpty()) {
      InputStream stream = null;
      try {
        // the list is sorted by priority already, just pick the first!
        stream = getMetadataDocument(docs.get(0).getKey());
        return DatasetParser.build(stream);
      } catch (IOException e) {
        LOG.error("Stored metadata document {} cannot be read", docs.get(0).getKey(), e);
      } finally {
        Closeables.closeQuietly(stream);
      }
    }

    return null;
  }

  /**
   * Augments a list of datasets with information from their preferred metadata document.
   * 
   * @return a the same paging response with a new list of augmented dataset instances
   */
  private PagingResponse<Dataset> augmentWithMetadata(PagingResponse<Dataset> resp) {
    List<Dataset> augmented = Lists.newArrayList();
    for (Dataset d : resp.getResults()) {
      augmented.add(merge(getPreferredMetadataDataset(d.getKey()), d));
    }
    resp.setResults(augmented);
    return resp;
  }

  /**
   * Merges an original dataset with another dataset, overwriting all persisted properties, i.e. excluding all
   * extended EML properties. If the second dataset contains null values, these will replace any existing values
   * in the original dataset.
   * 
   * @param d original dataset, if null a new instance will be created
   * @param d2 the dataset that is used to update the original d
   * @return the orignal dataset instance with merged information from d2
   */
  private Dataset merge(@Nullable Dataset d, @Nullable Dataset d2) {
    // if the original is missing dont do anything
    if (d2 == null) {
      return null;
    }

    if (d == null) {
      d = new Dataset();
    }
    d.setKey(d2.getKey());
    d.setParentDatasetKey(d2.getParentDatasetKey());
    d.setDuplicateOfDatasetKey(d2.getDuplicateOfDatasetKey());
    d.setInstallationKey(d2.getInstallationKey());
    d.setOwningOrganizationKey(d2.getOwningOrganizationKey());
    d.setExternal(d2.isExternal());
    d.setNumConstituents(d2.getNumConstituents());
    d.setType(d2.getType());
    d.setSubtype(d2.getSubtype());
    d.setTitle(d2.getTitle());
    d.setAlias(d2.getAlias());
    d.setAbbreviation(d2.getAbbreviation());
    d.setDescription(d2.getDescription());
    d.setLanguage(d2.getLanguage());
    d.setHomepage(d2.getHomepage());
    d.setLogoUrl(d2.getLogoUrl());
    d.setCitation(d2.getCitation());
    d.setRights(d2.getRights());
    d.setLockedForAutoUpdate(d2.isLockedForAutoUpdate());
    d.setCreated(d2.getCreated());
    d.setCreatedBy(d2.getCreatedBy());
    d.setModified(d2.getModified());
    d.setModifiedBy(d2.getModifiedBy());
    d.setDeleted(d2.getDeleted());
    // copy all related
    d.setComments(d2.getComments());
    d.setContacts(d2.getContacts());
    d.setEndpoints(d2.getEndpoints());
    d.setIdentifiers(d2.getIdentifiers());

    return d;
  }

  @Path("{key}/document")
  @GET
  @Produces(MediaType.APPLICATION_XML)
  @Override
  public InputStream getMetadataDocument(@PathParam("key") UUID datasetKey) {
    // the fully augmented dataset
    Dataset dataset = get(datasetKey);
    if (dataset != null) {
      // generate new EML
      try {
        StringWriter eml = new StringWriter();
        EMLWriter.write(dataset, eml);
        return new ByteArrayInputStream(eml.toString().getBytes("UTF-8"));

      } catch (Exception e) {
        throw new ServiceUnavailableException("Failed to serialize dataset " + datasetKey, e);
      }
    }
    return null;
  }

  @Path("{key}/document")
  @POST
  @Consumes(MediaType.APPLICATION_XML)
  public Metadata insertMetadata(@PathParam("key") UUID datasetKey, @Context HttpServletRequest request,
    @Context SecurityContext security) {
    // TODO: temporary until we implement the security bit
    String user = (security != null && security.getUserPrincipal() != null) ? security.getUserPrincipal().getName()
      : "GBIF Document Upload";
    try {
      return insertMetadata(datasetKey, request.getInputStream(), user);
    } catch (IOException e) {
      return null;
    }
  }

  private Metadata insertMetadata(UUID datasetKey, InputStream document, String user) {
    // check if the dataset actually exists
    Dataset dataset = super.get(datasetKey);
    if (dataset == null) {
      throw new IllegalArgumentException("Dataset " + datasetKey + " not existing");
    }

    // first keep document as byte array so we can analyze it as much as we want and store it later
    final byte[] data;
    try {
      data = ByteStreams.toByteArray(document);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unreadable document", e);
    }

    // now detect type and create a new metadata record
    MetadataType type;
    InputStream in = null;
    try {
      in = new ByteArrayInputStream(data);
      type = DatasetParser.detectParserType(in);
    } finally {
      Closeables.closeQuietly(in);
    }

    if (type == null) {
      throw new IllegalArgumentException("Document format not supported");
    }

    Metadata metadata = new Metadata();
    metadata.setDatasetKey(datasetKey);
    metadata.setType(type);
    metadata.setCreatedBy(user);
    metadata.setModifiedBy(user);

    // persist metadata & data
    // first remove all existing metadata of the same type (so we end up storing only one document per type)
    for (Metadata exist : listMetadata(datasetKey, type)) {
      deleteMetadata(exist.getKey());
    }
    final int metaKey = metadataMapper.create(metadata, data);
    metadata.setKey(metaKey);

    // check if we should update our registered base information
    if (dataset.isLockedForAutoUpdate()) {
      LOG
        .info(
          "Dataset {} locked for automatic updates. Uploaded metadata document not does not modify registered dataset information",
          datasetKey);

    } else {
      // we retrieve the preferred document and only update if this new metadata is the preferred one
      // e.g. we could put a DC document while an EML document exists that takes preference
      Dataset updDataset = getPreferredMetadataDataset(datasetKey);
      // keep some of the original properties
      updDataset.setKey(dataset.getKey());
      updDataset.setParentDatasetKey(dataset.getParentDatasetKey());
      updDataset.setDuplicateOfDatasetKey(dataset.getDuplicateOfDatasetKey());
      updDataset.setInstallationKey(dataset.getInstallationKey());
      updDataset.setOwningOrganizationKey(dataset.getOwningOrganizationKey());
      updDataset.setExternal(dataset.isExternal());
      updDataset.setNumConstituents(dataset.getNumConstituents());
      updDataset.setType(dataset.getType());
      updDataset.setSubtype(dataset.getSubtype());
      updDataset.setLockedForAutoUpdate(dataset.isLockedForAutoUpdate());
      updDataset.setCreatedBy(dataset.getCreatedBy());
      updDataset.setCreated(dataset.getCreated());
      updDataset.setModifiedBy(user);
      updDataset.setModified(new Date());
      // persist contacts, overwriting any existing ones
      for (Contact c : dataset.getContacts()) {
        datasetMapper.deleteContact(datasetKey, c.getKey());
      }
      for (Contact c : updDataset.getContacts()) {
        c.setCreatedBy(user);
        c.setCreated(new Date());
        c.setModifiedBy(user);
        c.setModified(new Date());
        WithMyBatis.addContact(contactMapper, datasetMapper, datasetKey, c);
      }
      // now update the core dataset only, remove associated data which could break validation
      updDataset.getContacts().clear();
      updDataset.getIdentifiers().clear();
      updDataset.getTags().clear();
      updDataset.getMachineTags().clear();
      update(updDataset);

      LOG.info("Dataset {} updated with base information from metadata document {}", datasetKey, metaKey);
    }

    return metadata;
  }

  /**
   * We need to implement this interface method here, but there is no way to retrieve the actual user
   * as we cannot access any http request. The real server method does this correctly but has more parameters.
   */
  @Override
  public Metadata insertMetadata(@PathParam("key") UUID datasetKey, InputStream document) {
    // this method should never be called but from tests
    return insertMetadata(datasetKey, document, "UNKNOWN USER");
  }

  @Path("{key}/constituents")
  @GET
  @Override
  public PagingResponse<Dataset> listConstituents(@PathParam("key") UUID datasetKey, @Context Pageable page) {
    return pagingResponse(page, (long) datasetMapper.countConstituents(datasetKey),
      datasetMapper.listConstituents(datasetKey, page));
  }

  @Path("{key}/metadata")
  @GET
  @Override
  public List<Metadata> listMetadata(@PathParam("key") UUID datasetKey, @QueryParam("type") MetadataType type) {
    return metadataMapper.list(datasetKey, type);
  }

  @Path("metadata/{key}")
  @GET
  @Override
  @NullToNotFound
  public Metadata getMetadata(@PathParam("key") int metadataKey) {
    return metadataMapper.get(metadataKey);
  }

  @Path("metadata/{key}/document")
  @GET
  @Produces(MediaType.APPLICATION_XML)
  @Override
  public InputStream getMetadataDocument(@PathParam("key") int metadataKey) {
    return new ByteArrayInputStream(metadataMapper.getDocument(metadataKey).getData());
  }

  @Path("metadata/{key}")
  @DELETE
  @Override
  public void deleteMetadata(@PathParam("key") int metadataKey) {
    metadataMapper.delete(metadataKey);
  }

  @GET
  @Path("deleted")
  @Override
  public PagingResponse<Dataset> listDeleted(@Context Pageable page) {
    return pagingResponse(page, datasetMapper.countDeleted(), datasetMapper.deleted(page));
  }

  @GET
  @Path("duplicate")
  @Override
  public PagingResponse<Dataset> listDuplicates(@Context Pageable page) {
    return pagingResponse(page, datasetMapper.countDuplicates(), datasetMapper.duplicates(page));
  }

  @GET
  @Path("subDataset")
  @Override
  public PagingResponse<Dataset> listSubdatasets(@Context Pageable page) {
    return pagingResponse(page, datasetMapper.countSubdatasets(), datasetMapper.subdatasets(page));
  }

  @GET
  @Path("withNoEndpoint")
  @Override
  public PagingResponse<Dataset> listDatasetsWithNoEndpoint(@Context Pageable page) {
    return pagingResponse(page, datasetMapper.countWithNoEndpoint(), datasetMapper.withNoEndpoint(page));
  }

}
