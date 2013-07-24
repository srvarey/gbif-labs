package org.gbif.registry.metasync.resulthandler;

import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Endpoint;
import org.gbif.api.model.registry2.Identifier;
import org.gbif.api.model.registry2.MachineTag;
import org.gbif.api.service.registry2.DatasetService;
import org.gbif.api.vocabulary.Language;
import org.gbif.api.vocabulary.registry2.DatasetType;
import org.gbif.registry.metasync.SyncResult;
import org.gbif.registry2.ws.client.guice.RegistryWsClientModule;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.google.common.base.Strings;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryUpdater {

  private static final Logger LOG = LoggerFactory.getLogger(RegistryUpdater.class);
  private final DatasetService datasetService;

  public RegistryUpdater() {
    Properties props = new Properties();
    props.setProperty("registry.ws.url", "http://localhost:8080");
    Injector injector = Guice.createInjector(new RegistryWsClientModule(props));
    datasetService = injector.getInstance(DatasetService.class);
  }

  public void saveSyncResultsToRegistry(Iterable<SyncResult> syncResults) {
    for (SyncResult syncResult : syncResults) {
      saveSyncResults(syncResult);
    }
  }

  /**
   * Processes the result of a synchronisation by:
   * <ul>
   * <li>Creating new Datasets and Endpoints</li>
   * <li>Deleting existing Datasets</li>
   * <li>Updating existing Installations, Datasets and Endpoints</li>
   * </ul>
   */
  private void saveSyncResults(SyncResult result) {
    if (result.exception == null) {
      saveAddedDatasets(result);
      saveDeletedDatasets(result);
      saveUpdatedDatasets(result);
    } else {
      LOG.warn("Installation [{}] failed sync because of [{}]",
               result.installation.getKey(),
               result.exception.getMessage());
    }
  }

  private void saveUpdatedDatasets(SyncResult result) {// Update existing datasets and their endpoints
    for (Map.Entry<Dataset, Dataset> entry : result.existingDatasets.entrySet()) {
      Dataset existingDataset = entry.getKey();
      if (existingDataset.isLockedForAutoUpdate()) {
        LOG.info("Dataset [{}] updated at source untouched in Registry because it's locked", existingDataset.getKey());
      } else {
        LOG.info("Updating dataset [{}]", existingDataset.getKey());
        existingDataset.setModifiedBy("Metadata synchroniser");
        if (existingDataset.getDescription() == null) {
          existingDataset.setDescription("DUMMY DESCRIPTION");
        }
        datasetService.update(existingDataset);
      }

      // TODO: Update the rest
      // TODO: Map existing endpoints to new ones or rely on the synchroniser that it gave us proper stuff?
    }
  }

  private void saveDeletedDatasets(SyncResult result) {// Delete datasets that don't exist anymore
    for (Dataset dataset : result.deletedDatasets) {
      if (dataset.isLockedForAutoUpdate()) {
        LOG.info("Dataset [{}] deleted at source but left in Registry because it's locked", dataset.getKey());
      } else {
        LOG.info("Deleting dataset [{}]", dataset.getKey());
        datasetService.delete(dataset.getKey());
      }
    }
  }

  private void saveAddedDatasets(SyncResult result) {
    // Process all added datasets, currently there's a bug in Registry WS where the full object is validated
    // (including nested objects) even though only a subset is set. That's why we have to manually back up machine tags
    // and endpoints and set them to null.
    for (Dataset dataset : result.addedDatasets) {
      dataset.setOwningOrganizationKey(result.installation.getOrganizationKey());
      dataset.setInstallationKey(result.installation.getKey());
      dataset.setType(DatasetType.OCCURRENCE);

      // BEGIN: Workaround for required language
      if (dataset.getLanguage() == null) {
        dataset.setLanguage(Language.ENGLISH);
      }
      // END: Workaround for required language

      // BEGIN: Workaround for validation of nested objects
      List<Contact> contacts = dataset.getContacts();
      dataset.setContacts(null);
      List<MachineTag> machineTags = dataset.getMachineTags();
      dataset.setMachineTags(null);
      List<Endpoint> endpoints = dataset.getEndpoints();
      dataset.setEndpoints(null);
      List<Identifier> identifiers = dataset.getIdentifiers();
      dataset.setIdentifiers(null);
      // END: Workaround for validation of nested objects

      // BEGIN: Workaround for minimum length of 10 for Dataset descriptions
      String tmpString = dataset.getDescription() == null ? "" : dataset.getDescription();
      dataset.setDescription(Strings.padEnd(tmpString, 10, 'X'));
      // END: Workaround for minimum length of 10 for Dataset descriptions

      // BEGIN: Workaround for minimum length of 10 for Dataset citation text
      if (dataset.getCitation() != null) {
        tmpString = dataset.getCitation().getText() == null ? "" : dataset.getCitation().getText();
        dataset.getCitation().setText(Strings.padEnd(tmpString, 10, 'X'));
      }
      // END: Workaround for minimum length of 10 for Dataset citation text

      UUID uuid = datasetService.create(dataset);
      LOG.info("Created new Dataset with id [{}]", uuid);
      for (Contact contact : contacts) {
        datasetService.addContact(uuid, contact);
      }
      for (MachineTag machineTag : machineTags) {
        datasetService.addMachineTag(uuid, machineTag);
      }
      for (Endpoint endpoint : endpoints) {
        // BEGIN: Workaround for minimum length of 10 for Endpoint descriptions
        tmpString = endpoint.getDescription() == null ? "" : endpoint.getDescription();
        endpoint.setDescription(Strings.padEnd(tmpString, 10, 'X'));
        // END: Workaround for minimum length of 10 for Endpoint descriptions

        datasetService.addEndpoint(uuid, endpoint);
      }
      for (Identifier identifier : identifiers) {
        datasetService.addIdentifier(uuid, identifier);
      }
    }
  }

}
