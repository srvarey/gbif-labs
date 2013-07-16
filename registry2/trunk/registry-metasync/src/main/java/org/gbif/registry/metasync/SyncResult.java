package org.gbif.registry.metasync;

import org.gbif.api.model.registry2.Dataset;

import java.util.List;
import java.util.Map;

/**
 * A simple holder object used to pass around the result of metadata synchronisation.
 */
@SuppressWarnings({"PublicField", "AssignmentToCollectionOrArrayFieldFromParameter"})
public class SyncResult {

  /**
   * Maps from the existing Dataset in the Registry to a new object that we just parsed from the Endpoint.
   */
  public Map<Dataset, Dataset> existingDatasets;
  public List<Dataset> addedDatasets;
  public List<Dataset> deletedDatasets;

  public SyncResult(
    Map<Dataset, Dataset> existingDatasets, List<Dataset> addedDatasets, List<Dataset> deletedDatasets
  ) {
    this.existingDatasets = existingDatasets;
    this.addedDatasets = addedDatasets;
    this.deletedDatasets = deletedDatasets;
  }

}
