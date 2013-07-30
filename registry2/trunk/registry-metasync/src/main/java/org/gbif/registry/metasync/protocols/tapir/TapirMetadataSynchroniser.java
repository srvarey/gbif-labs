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
package org.gbif.registry.metasync.protocols.tapir;

import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Endpoint;
import org.gbif.api.model.registry2.Installation;
import org.gbif.api.model.registry2.MachineTag;
import org.gbif.api.vocabulary.registry2.EndpointType;
import org.gbif.api.vocabulary.registry2.InstallationType;
import org.gbif.registry.metasync.api.ErrorCode;
import org.gbif.registry.metasync.api.MetadataException;
import org.gbif.registry.metasync.api.SyncResult;
import org.gbif.registry.metasync.protocols.BaseProtocolHandler;
import org.gbif.registry.metasync.protocols.tapir.model.capabilities.Capabilities;
import org.gbif.registry.metasync.protocols.tapir.model.capabilities.Schema;
import org.gbif.registry.metasync.protocols.tapir.model.metadata.TapirContact;
import org.gbif.registry.metasync.protocols.tapir.model.metadata.TapirMetadata;
import org.gbif.registry.metasync.protocols.tapir.model.metadata.TapirRelatedEntity;
import org.gbif.registry.metasync.util.Constants;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;

import static org.gbif.registry.metasync.util.Constants.METADATA_NAMESPACE;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Synchronises metadata from a TAPIR Installation.
 * <p/>
 * Every TAPIR Installation can have multiple Endpoints. Each of these Endpoints represents a single Dataset and each
 * of the Datasets can have a single Endpoint. Note: TAPIR supports Archives but no one seems to be using them so they
 * are not supported here.
 * <p/>
 * The process is as follows:
 * <p/>
 * <ol>
 * <li>For every Endpoint do a {@code capabilities} as well as a {@code metadata} request</li>
 * <li>Convert the data into a {@link Dataset} object</li>
 * <li>When we have all new {@code Dataset} objects try to map them to existing ones using the
 * {@code local id} (which is the last part of the URL)</li>
 * </ol>
 * Note: If there is an exception during processing one of the Endpoints the whole synchronisation process will be
 * aborted. I'm doing this to prevent inconsistencies.
 */
public class TapirMetadataSynchroniser extends BaseProtocolHandler {

  public TapirMetadataSynchroniser(HttpClient httpClient) {
    super(httpClient);
  }

  @Override
  public boolean canHandle(Installation installation) {
    return installation.getType() == InstallationType.TAPIR_INSTALLATION;
  }

  @Override
  public SyncResult syncInstallation(Installation installation, List<Dataset> datasets) throws MetadataException {
    checkArgument(installation.getType() == InstallationType.TAPIR_INSTALLATION, "Only supports TAPIR Installations");

    List<Dataset> added = Lists.newArrayList();
    List<Dataset> deleted = Lists.newArrayList();
    Map<Dataset, Dataset> updated = Maps.newHashMap();

    // This metadata will be used to update the Installation itself
    TapirMetadata updaterMetadata = null;

    for (Endpoint endpoint : installation.getEndpoints()) {
      String localId = getLocalId(endpoint);

      Capabilities capabilities = getCapabilities(endpoint);
      if (capabilities == null) {
        throw new MetadataException("Did not receive a valid Capabilities response for [" + endpoint.getKey() + "]",
                                    ErrorCode.PROTOCOL_ERROR);
      }

      TapirMetadata metadata = getTapirMetadata(endpoint);

      updateInstallationEndpoint(metadata, endpoint);

      Dataset newDataset = convertToDataset(capabilities, metadata);
      Dataset existingDataset = findDataset(localId, datasets);
      if (existingDataset == null) {
        added.add(newDataset);
      } else {
        updated.put(existingDataset, newDataset);
      }

      updaterMetadata = metadata;
    }

    // All Datasets that weren't updated must have been deleted
    for (Dataset dataset : datasets) {
      if (!updated.containsKey(dataset)) {
        deleted.add(dataset);
      }
    }

    updateInstallation(installation, updaterMetadata);

    return new SyncResult(updated, added, deleted, installation);
  }

  /**
   * Gets the <em>local id</em> from the Endpoint. This is the last part of the URL and the only uniquely identifying
   * piece for a TAPIR Dataset.
   */
  private String getLocalId(Endpoint endpoint) throws MetadataException {
    String[] split = endpoint.getUrl().split("/");

    if (split.length < 2) {
      throw new MetadataException("Could not find local Id for [" + endpoint.getUrl() + "]", ErrorCode.OTHER_ERROR);
    }

    return split[split.length - 1];
  }

  /**
   * Does a Capabilities request against the TAPIR Endpoint.
   */
  private Capabilities getCapabilities(Endpoint endpoint) throws MetadataException {
    URI uri;
    try {
      uri = new URIBuilder(endpoint.getUrl()).addParameter("op", "capabilities").build();
    } catch (URISyntaxException e) {
      throw new MetadataException(e, ErrorCode.OTHER_ERROR);
    }

    return doHttpRequest(uri, newDigester(Capabilities.class));
  }

  /**
   * Does a Metadata request against the TAPIR Endpoint.
   */
  private TapirMetadata getTapirMetadata(Endpoint endpoint) throws MetadataException {
    return doHttpRequest(URI.create(endpoint.getUrl()), newDigester(TapirMetadata.class));
  }

  /**
   * Updates the Endpoint of the Installation that we're currently working on.
   */
  private void updateInstallationEndpoint(TapirMetadata metadata, Endpoint endpoint) {
    endpoint.setDescription(metadata.getDescriptions().toString());
  }

  /**
   * Converts the Capabilities and Metadata response from TAPIR into a GBIF Dataset.
   */
  private Dataset convertToDataset(Capabilities capabilities, TapirMetadata metadata) {
    Dataset dataset = new Dataset();
    dataset.setTitle(metadata.getTitles().toString());
    dataset.setDescription(metadata.getDescriptions().toString());
    dataset.setHomepage(URI.create(metadata.getAccessPoint()));
    dataset.setLanguage(metadata.getDefaultLanguage());

    List<Contact> contacts = Lists.newArrayList();
    for (TapirRelatedEntity tapirRelatedEntity : metadata.getRelatedEntities()) {
      for (TapirContact tapirContact : tapirRelatedEntity.getContacts()) {
        Contact contact = new Contact();
        contact.setPosition(tapirContact.getRoles().toString());
        contact.setFirstName(tapirContact.getFullName());
        contact.setPhone(tapirContact.getTelephone());
        contact.setEmail(tapirContact.getEmail());
        contact.setDescription(tapirContact.getTitle());
        contacts.add(contact);
      }
    }
    dataset.setContacts(contacts);

    Endpoint endpoint = new Endpoint();
    endpoint.setType(EndpointType.TAPIR);
    endpoint.setDescription(metadata.getTitles().toString());
    endpoint.setUrl(metadata.getAccessPoint());
    dataset.addEndpoint(endpoint);

    for (Schema schema : capabilities.getSchemas()) {
      dataset.addMachineTag(MachineTag.newInstance(METADATA_NAMESPACE,
                                                   Constants.CONCEPTUAL_SCHEMA,
                                                   schema.getNamespace().toASCIIString()));
    }

    return dataset;
  }

  /**
   * Tries to find a matching Dataset in the list of provided Datasets.
   *
   * @return the matching Dataset or {@code null} if it could not be found
   */
  @Nullable
  private Dataset findDataset(String localId, Iterable<Dataset> datasets) throws MetadataException {
    for (Dataset dataset : datasets) {
      for (Endpoint endpoint : dataset.getEndpoints()) {
        if (localId.equals(getLocalId(endpoint))) {
          return dataset;
        }
      }
    }
    return null;
  }

  /**
   * Updates the Installation with the data that is universal across all Datasets. As the Installation itself doesn't
   * have any Metadata Endpoint there is very little information we can extract.
   */
  private void updateInstallation(Installation installation, TapirMetadata updaterMetadata) {
    installation.addMachineTag(MachineTag.newInstance(METADATA_NAMESPACE,
                                                      "version",
                                                      updaterMetadata.getSoftwareVersion()));
    installation.addMachineTag(MachineTag.newInstance(METADATA_NAMESPACE,
                                                      "software_name",
                                                      updaterMetadata.getSoftwareName()));
  }

}
