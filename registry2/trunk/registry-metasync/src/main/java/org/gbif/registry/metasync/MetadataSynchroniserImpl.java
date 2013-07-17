package org.gbif.registry.metasync;

import org.gbif.api.model.common.paging.PagingRequest;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Endpoint;
import org.gbif.api.model.registry2.Identifier;
import org.gbif.api.model.registry2.Installation;
import org.gbif.api.model.registry2.MachineTag;
import org.gbif.api.service.registry2.DatasetService;
import org.gbif.api.service.registry2.InstallationService;
import org.gbif.api.vocabulary.registry2.DatasetType;
import org.gbif.registry.metasync.api.MetadataException;
import org.gbif.registry.metasync.api.MetadataProtocolHandler;
import org.gbif.registry.metasync.api.MetadataSynchroniser;
import org.gbif.registry2.ws.client.guice.RegistryWsClientModule;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class MetadataSynchroniserImpl implements MetadataSynchroniser {

  private static final Logger LOG = LoggerFactory.getLogger(MetadataSynchroniserImpl.class);
  private static final int PAGING_LIMIT = 10000;
  private final InstallationService installationService;
  private final DatasetService datasetService;
  private final List<MetadataProtocolHandler> protocolHandlers = Lists.newArrayList();

  protected MetadataSynchroniserImpl() {
    Properties props = new Properties();
    props.setProperty("registry.ws.url", "http://localhost:8080");
    Injector injector = Guice.createInjector(new RegistryWsClientModule(props));
    installationService = injector.getInstance(InstallationService.class);
    datasetService = injector.getInstance(DatasetService.class);
  }

  @Override
  public void synchroniseInstallation(UUID key, Context context) {
    checkNotNull(key, "key can't be null");

    Installation installation = validateInstallation(key, context);
    List<Dataset> hostedDatasets = getHostedDatasets(key);

    for (MetadataProtocolHandler protocolHandler : protocolHandlers) {
      if (protocolHandler.canHandle(installation)) {
        doSynchroniseInstallation(installation, hostedDatasets, protocolHandler, context);
      }
    }
  }

  @Override
  public void synchroniseAllInstallations(Context context) {
    synchroniseAllInstallations(1, context);
  }

  @Override
  public void synchroniseAllInstallations(int parallel, final Context context) {
    checkArgument(parallel > 0, "parallel has to be greater than 0");
    ExecutorService executor = Executors.newFixedThreadPool(parallel);

    PagingResponse<Installation> results;
    PagingRequest page = new PagingRequest();
    do {
      results = installationService.list(page);
      for (final Installation installation : results.getResults()) {
        executor.submit(new Runnable() {

          @Override
          public void run() {
            try {
              synchroniseInstallation(installation.getKey(), context);
            } catch (Exception e) {
              LOG.debug("Failed sync [{}]", installation.getKey());
              context.incrementCounter(installation.getType() + ".exception." + e.getClass().getSimpleName());
              // LOG.debug("Caught exception synchronising Installation [{}], ignoring", installation.getKey(), e);
            }
          }
        });
      }
      page.nextPage();
    } while (!results.isEndOfRecords());

    executor.shutdown();
    while (!executor.isTerminated()) {
      LOG.info("Waiting for synchronisations to finish");
      try {
        executor.awaitTermination(1, TimeUnit.MINUTES);
      } catch (InterruptedException ignored) {
      }
    }

  }

  @Override
  public void registerProtocolHandler(MetadataProtocolHandler handler) {
    protocolHandlers.add(handler);
  }

  /**
   * This method actually runs the metadata synchronisation by calling out to the protocol handler and then validating
   * and processing its result.
   */
  private void doSynchroniseInstallation(
    Installation installation, List<Dataset> hostedDatasets, MetadataProtocolHandler protocolHandler, Context context
    ) {
    LOG.info("Syncing Installation [{}] of type [{}]", installation.getKey(), installation.getType());
    try {
      SyncResult result = protocolHandler.syncInstallation(installation, hostedDatasets);
      if (result == null) {
        return;
      }
      LOG.debug("Added: [{}], Deleted: [{}], Updated [{}]",
        result.addedDatasets.size(),
        result.deletedDatasets.size(),
        result.existingDatasets.size());
      saveSyncResults(result, installation, context);
    } catch (MetadataException e) {
      failedSynchronisation(installation, e, context);
    }
  }

  /**
   * Does some checks whether we can synchronise this Installation or not. They are not exhaustive as some things can
   * only be determined by the protocol handlers.
   */
  private Installation validateInstallation(UUID key, Context context) {
    Installation installation = installationService.get(key);
    if (installation == null) {
      context.incrementCounter("installation.notFoundInRegistry");
      throw new IllegalArgumentException("Installation with key [" + key + "] does not exist");
    }

    if (installation.getEndpoints() == null || installation.getEndpoints().isEmpty()) {
      context.incrementCounter("installation.noEndpointsInRegistry");
      throw new IllegalArgumentException("Installation with key [" + key + "]" + " has no endpoints");
    }
    return installation;
  }

  /**
   * Processes the result of a synchronisation by:
   * <ul>
   * <li>Creating new Datasets and Endpoints</li>
   * <li>Deleting existing Datasets</li>
   * <li>Updating existing Installations, Datasets and Endpoints</li>
   * </ul>
   */
  private void saveSyncResults(SyncResult result, Installation installation, Context context) {
    saveAddedDatasets(result, installation, context);
    saveDeletedDatasets(result, installation, context);
    saveUpdatedDatasets(result, installation, context);
  }

  private void saveUpdatedDatasets(SyncResult result, Installation installation, Context context) {// Update existing
// datasets and their endpoints
    for (Map.Entry<Dataset, Dataset> entry : result.existingDatasets.entrySet()) {
      Dataset existingDataset = entry.getKey();
      if (existingDataset.isLockedForAutoUpdate()) {
        LOG.info("Dataset [{}] updated at source untouched in Registry because it's locked", existingDataset.getKey());
        context.incrementCounter(installation.getType() + ".dataset.updateIgnoredDueToLock");
      } else {
        LOG.info("Updating dataset [{}]", existingDataset.getKey());
        datasetService.update(existingDataset);
        context.incrementCounter(installation.getType() + ".dataset.updated");
      }

      // TODO: Update the rest
      // TODO: Map existing endpoints to new ones or rely on the synchroniser that it gave us proper stuff?
    }
  }

  private void saveDeletedDatasets(SyncResult result, Installation installation, Context context) {// Delete datasets
// that don't exist anymore
    for (Dataset dataset : result.deletedDatasets) {
      if (dataset.isLockedForAutoUpdate()) {
        LOG.info("Dataset [{}] deleted at source but left in Registry because it's locked", dataset.getKey());
        context.incrementCounter(installation.getType() + ".dataset.deletedIgnoredDueToLock");
      } else {
        LOG.info("Deleting dataset [{}]", dataset.getKey());
        datasetService.delete(dataset.getKey());
        context.incrementCounter(installation.getType() + ".dataset.deleted");
      }
    }
  }

  private void saveAddedDatasets(SyncResult result, Installation installation, Context context) {
    // Process all added datasets, currently there's a bug in Registry WS where the full object is validated
    // (including nested objects) even though only a subset is set. That's why we have to manually back up machine tags
    // and endpoints and set them to null.
    for (Dataset dataset : result.addedDatasets) {
      dataset.setOwningOrganizationKey(installation.getOrganizationKey());
      dataset.setInstallationKey(installation.getKey());
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
      context.incrementCounter(installation.getType() + ".dataset.newDatasetCreated");
    }
  }

  private void failedSynchronisation(Installation installation, MetadataException e, Context context) {
    LOG.info("Failed synchronisation because of [{}]", e.getError());
    context.incrementCounter(installation.getType() + ".exception." + e.getClass().getSimpleName());
  }

  /**
   * Gets all hosted datasets for an Installation.
   * 
   * @param key of the Installation
   * @return list of Datasets for this Installation, might be empty but never null
   */
  private List<Dataset> getHostedDatasets(UUID key) {
    PagingRequest page = new PagingRequest(0, PAGING_LIMIT);
    PagingResponse<Dataset> results;
    List<Dataset> hostedDatasets = Lists.newArrayList();
    do {
      results = installationService.getHostedDatasets(key, page);
      hostedDatasets.addAll(results.getResults());
      page.nextPage();
    } while (!results.isEndOfRecords());
    return hostedDatasets;
  }

}
