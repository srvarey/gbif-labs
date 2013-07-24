package org.gbif.registry.metasync.api;

import org.gbif.registry.metasync.SyncResult;

import java.util.List;
import java.util.UUID;

public interface MetadataSynchroniser {

  /**
   * Runs a synchronisation against the installation provided.
   * <p/>
   * All datasets from this installation will be updated, old ones deleted and new ones registered if needed.
   *
   * @param key of the installation to synchronise
   *
   * @return the result of the synchronisation
   *
   * @throws IllegalArgumentException if no technical installation with the given key exists, the installation doesn't
   *                                  have any endpoints or none that we can use to gather metadata (might also be
   *                                  missing the proper protocol handler)
   */
  SyncResult synchroniseInstallation(UUID key);

  /**
   * Synchronises all registered Installations ignoring any failures (because there will definitely be failures due to
   * unsupported Installation types and missing Endpoints). This will run single threaded.
   */
  List<SyncResult> synchroniseAllInstallations();

  /**
   * Synchronises all registered Installations ignoring any failures (because there will definitely be failures due to
   * unsupported Installation types and missing Endpoints).
   *
   * @param parallel how many threads to run in parallel
   */
  List<SyncResult> synchroniseAllInstallations(int parallel);

  /**
   * The logic to synchronise the protocols is separated out in their own classes per protocol. Each Protocol has a
   * handler that needs to be registered with this method.
   */
  void registerProtocolHandler(MetadataProtocolHandler handler);

}
