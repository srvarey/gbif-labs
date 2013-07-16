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
package org.gbif.registry.metasync.protocols.biocase;

import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Endpoint;
import org.gbif.api.model.registry2.Installation;
import org.gbif.api.vocabulary.registry2.InstallationType;
import org.gbif.registry.metasync.SyncResult;
import org.gbif.registry.metasync.api.ErrorCode;
import org.gbif.registry.metasync.api.MetadataException;
import org.gbif.registry.metasync.protocols.BaseProtocolHandler;
import org.gbif.registry.metasync.protocols.biocase.model.InventoryDataset;
import org.gbif.registry.metasync.protocols.biocase.model.NewDatasetInventory;
import org.gbif.registry.metasync.protocols.biocase.model.OldDatasetInventory;
import org.gbif.registry.metasync.protocols.biocase.model.capabilities.Capabilities;
import org.gbif.registry.metasync.util.TemplateUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Synchronises metadata from a BioCASe Installation.
 * <p/>
 * Every BioCASe Installation can have multiple Endpoints, each of those Endpoints can have multiple Datasets and each
 * of those Datasets can have multiple Endpoints (Web service, ABCD Archive, DwC-A).
 * <p/>
 * BioCASe synchronisation happens in the following steps:
 * <ul>
 * <li>For all endpoints of an Installation make a {@code capabilities} request, followed by a dataset inventory
 * request, then a metadata and count (to get the number of records) request for each dataset</li>
 * <li>The type of inventory request depends on the capabilities. Newer version of BioCASe (3.4 and greater) support a
 * separate inventory request.</li>
 * </ul>
 * <p/>
 * Unfortunately BioCASe does not have good (stable) identifiers for Datasets so we need to rely on the Dataset title
 * (TODO what's it really called?).
 */
public class BiocaseMetadataSynchroniser extends BaseProtocolHandler {

  protected BiocaseMetadataSynchroniser(HttpClient httpClient) {
    super(httpClient);
  }

  @Override
  public boolean canHandle(Installation installation) {
    return installation.getType() == InstallationType.BIOCASE_INSTALLATION;
  }

  @Override
  public SyncResult syncInstallation(
    Installation installation, List<Dataset> datasets
  ) throws MetadataException {
    checkArgument(installation.getType() == InstallationType.BIOCASE_INSTALLATION,
                  "Only supports BioCASe Installations");

    List<Dataset> added = Lists.newArrayList();
    List<Dataset> deleted = Lists.newArrayList();
    Map<Dataset, Dataset> updated = Maps.newHashMap();

    for (Endpoint endpoint : installation.getEndpoints()) {
      Capabilities capabilities = getCapabilities(endpoint);
      List<String> datasetInventory = getDatasetInventory(capabilities, endpoint);
      for (String datasetTitle : datasetInventory) {
        SimpleAbcd206Metadata biocaseDataSet = getMetadata(endpoint, datasetTitle, capabilities);
        Dataset newDataset = convertToDataset(biocaseDataSet);

        Dataset existingDataset = findDataset(datasetTitle, datasets);
        if (existingDataset == null) {
          added.add(newDataset);
        } else {
          updated.put(existingDataset, newDataset);
        }
      }
    }

    // All Datasets that weren't updated must have been deleted
    for (Dataset dataset : datasets) {
      if (!updated.containsKey(dataset)) {
        deleted.add(dataset);
      }
    }

    return new SyncResult(updated, added, deleted);
  }

  /**
   * Does a Capabilities request against the Endpoint.
   */
  private Capabilities getCapabilities(Endpoint endpoint) throws MetadataException {
    return doHttpRequest(URI.create(endpoint.getUrl()), newDigester(Capabilities.class));
  }

  /**
   * Tries to get an inventory (list) of Datasets for this BioCASe Endpoint. Depending on the version of the
   * Installation there are two ways to do this.
   */
  private List<String> getDatasetInventory(Capabilities capabilities, Endpoint endpoint) throws MetadataException {
    String version = capabilities.getVersions().get("pywrapper");
    if (checkIfSupportsNewInventory(version)) {
      return doNewStyleInventory(endpoint);
    } else {
      return doOldStyleInventory(endpoint, capabilities);
    }
  }

  /**
   * Given a version string in the normal "major.minor.patch" format evaluates whether this version of BioCASe supports
   * the new style inventory request or not. All versions of BioCASe 3.4 and above do support this.
   */
  private boolean checkIfSupportsNewInventory(String version) {
    if (version == null) {
      return false;
    }

    String[] versionParts = version.split("\\.");
    try {
      // Trying to parse the "major" part first, if we succeed and it is greater than 3 we do support the nev inventory
      // style, if it is less than three we don't support it. If it is equal to 3 we need to check the "minor" component
      int majorVersion = Integer.valueOf(versionParts[0]);
      if (majorVersion < 3) {
        return false;
      }
      if (majorVersion > 3) {
        return true;
      }

      // "major" version is 3 but there is no "minor" part
      if (versionParts.length < 2) {
        return false;
      }

      // Check whether the "minor" version is greater than or equal to 4
      int minorVersion = Integer.valueOf(versionParts[1]);
      return minorVersion >= 4;
    } catch (NumberFormatException ignored) {
      return false;
    }
  }

  /**
   * Does a request against the dedicated {@code inventory} endpoint which lists all Datasets that are available as well
   * as all Archives.
   */
  // TODO: Need to return information about archives
  private List<String> doNewStyleInventory(Endpoint endpoint) throws MetadataException {
    URI uri = buildUri(endpoint.getUrl(), "inventory", "1");
    NewDatasetInventory inventory = doHttpRequest(uri, newDigester(NewDatasetInventory.class));
    List<String> datasets = Lists.newArrayList();
    for (InventoryDataset inventoryDataset : inventory.getDatasets()) {
      datasets.add(inventoryDataset.getTitle());
    }
    return datasets;
  }

  /**
   * Does a search request against this Endpoint specially crafted to only find all Dataset titles.
   */
  private List<String> doOldStyleInventory(Endpoint endpoint, Capabilities capabilities) throws MetadataException {
    String requestParameter = TemplateUtils.getBiocaseInventoryRequest(capabilities.getPreferredSchema());
    URI uri = buildUri(endpoint.getUrl(), "request", requestParameter);
    OldDatasetInventory inventory = doHttpRequest(uri, newDigester(OldDatasetInventory.class));
    return inventory.getDatasets();
  }

  /**
   * Does a search request against this Endpoint to get all the Metadata for a single Dataset.
   */
  private SimpleAbcd206Metadata getMetadata(Endpoint endpoint, String dataset, Capabilities capabilities) throws MetadataException {
    String requestParameter = TemplateUtils.getBiocaseMetadataRequest(capabilities.getPreferredSchema(), dataset);
    URI uri = buildUri(endpoint.getUrl(), "request", requestParameter);
    return doHttpRequest(uri, newDigester(SimpleAbcd206Metadata.class));
  }

  public URI buildUri(String url, String parameter, String value) throws MetadataException {
    try {
      return new URIBuilder(url).addParameter(parameter, value).build();
    } catch (URISyntaxException e) {
      throw new MetadataException(ErrorCode.OTHER_ERROR);
    }
  }

  private Dataset convertToDataset(SimpleAbcd206Metadata biocaseDataSet) {
    // TODO: Implement and take care to properly create the Endpoints
    // Open question is how to map those endpoints...
    return null;
  }

  /**
   * Tries to find a matching Dataset in the list of provided Datasets by looking at the title.
   *
   * @return the matching Dataset or {@code null} if it could not be found
   */
  @Nullable
  private Dataset findDataset(String datasetTitle, Iterable<Dataset> datasets) {
    for (Dataset dataset : datasets) {
      if (dataset.getTitle().equals(datasetTitle)) {
        return dataset;
      }
    }

    return null;
  }

}
