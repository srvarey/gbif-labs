package org.gbif.registry.metasync.resulthandler;

import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Endpoint;
import org.gbif.api.model.registry2.Identifier;
import org.gbif.api.model.registry2.MachineTag;
import org.gbif.api.service.registry2.DatasetService;
import org.gbif.api.vocabulary.registry2.DatasetType;
import org.gbif.registry.metasync.api.SyncResult;
import org.gbif.registry2.ws.client.guice.RegistryWsClientModule;
import org.gbif.ws.client.guice.GbifApplicationAuthModule;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes synchronisation results and saves those back to the registry.
 */
public class RegistryUpdater {

  private static final Logger LOG = LoggerFactory.getLogger(RegistryUpdater.class);
  private final DatasetService datasetService;

  public RegistryUpdater() {
    Properties props = new Properties();
    props.setProperty("registry.ws.url", "http://localhost:8080");
    props.setProperty("application.key", "gbif.registry-ws-client-it");
    props.setProperty("application.secret", "foobar");

    // Create authentication module, and set principal name, equal to a GBIF User unique account name
    GbifApplicationAuthModule auth = new GbifApplicationAuthModule(props);
    auth.setPrincipal("admin");

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

  private void saveUpdatedDatasets(SyncResult result) {
    for (Map.Entry<Dataset, Dataset> entry : result.existingDatasets.entrySet()) {
      Dataset existingDataset = entry.getKey();
      if (existingDataset.isLockedForAutoUpdate()) {
        LOG.info("Dataset [{}] updated at source untouched in Registry because it's locked", existingDataset.getKey());
      } else {
        LOG.info("Updating dataset [{}]", existingDataset.getKey());
        datasetService.update(existingDataset);
      }

      // TODO: Update the rest
      // TODO: Map existing endpoints to new ones or rely on the synchroniser that it gave us proper stuff?
    }
  }

  private void saveDeletedDatasets(SyncResult result) {
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
    for (Dataset dataset : result.addedDatasets) {
      dataset.setOwningOrganizationKey(result.installation.getOrganizationKey());
      dataset.setInstallationKey(result.installation.getKey());
      dataset.setType(DatasetType.OCCURRENCE);

      UUID uuid = datasetService.create(dataset);
      LOG.info("Created new Dataset with id [{}]", uuid);
      for (Contact contact : dataset.getContacts()) {
        datasetService.addContact(uuid, contact);
      }
      for (MachineTag machineTag : dataset.getMachineTags()) {
        datasetService.addMachineTag(uuid, machineTag);
      }
      for (Endpoint endpoint : dataset.getEndpoints()) {
        datasetService.addEndpoint(uuid, endpoint);
      }
      for (Identifier identifier : dataset.getIdentifiers()) {
        datasetService.addIdentifier(uuid, identifier);
      }
    }
  }

}
