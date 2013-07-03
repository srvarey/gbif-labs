package org.gbif.registry2.ims;

import org.gbif.api.model.registry2.Node;

public interface Augmenter {

  /**
   * Adds all IMS infos found to an existing node instance which is required to have a country field filled.
   * @param node with country field filled
   * @return same node with IMS data added
   */
  Node augment(Node node);
}
