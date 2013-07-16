package org.gbif.registry.metasync.api;

import org.gbif.registry.metasync.Context;

import java.util.UUID;

public interface MetadataSynchroniser {

  /**
   * Runs a synchronisation against the installation provided.
   * <p/>
   * All datasets from this installation will be updated, old ones deleted and new ones registered if needed.
   * 
   * @param key of the installation to synchronise
   * @param context of the running job
   * @throws IllegalArgumentException if no technical installation with the given key exists, the installation doesn't
   *         have any endpoints or none that we can use to gather metadata (might also be
   *         missing the proper protocol handler)
   */
  void synchroniseInstallation(UUID key, Context context);

  /**
   * Synchronises all registered Installations ignoring any failures (because there will definitely be failures due to
   * unsupported Installation types and missing Endpoints). This will run single threaded.
   * 
   * @param context of the running job
   */
  void synchroniseAllInstallations(Context context);

  /**
   * Synchronises all registered Installations ignoring any failures (because there will definitely be failures due to
   * unsupported Installation types and missing Endpoints).
   * 
   * @param parallel how many threads to run in parallel
   * @param context of the running job
   */
  void synchroniseAllInstallations(int parallel, Context context);

  /**
   * The logic to synchronise the protocols is separated out in their own classes per protocol. Each Protocol has a
   * handler that needs to be registered with this method.
   */
  void registerProtocolHandler(MetadataProtocolHandler handler);

}
