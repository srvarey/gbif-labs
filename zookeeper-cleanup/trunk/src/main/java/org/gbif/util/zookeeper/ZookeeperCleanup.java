package org.gbif.util.zookeeper;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility to recursively delete the contents of a node in ZooKeeper.  The passed in node is not deleted - only the
 * nodes below it.  Note the hardcoded zookeeper url.
 */
public class ZookeeperCleanup {

  private static final Logger LOG = LoggerFactory.getLogger(ZookeeperCleanup.class);

  private ZookeeperCleanup() {
  }

  /**
   * First and only arg needs to be the node whose content should be deleted, e.g. "/hbase".
   */
  public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
    LOG.debug("ZookeeperCleanup starting");
    ZookeeperCleaner zkCleaner = new ZookeeperCleaner();
    zkCleaner.clean(args[0]);
    LOG.debug("ZookeeperCleanup finished");
  }
}
