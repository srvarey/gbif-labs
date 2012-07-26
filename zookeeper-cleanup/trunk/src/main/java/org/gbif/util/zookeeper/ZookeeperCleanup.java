package org.gbif.util.zookeeper;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility to recursively delete the contents of a node in ZooKeeper.  The passed in node is not deleted - only the
 * nodes below it.  Note the hardcoded zookeeper url.
 */
public class ZookeeperCleanup implements Watcher {

  private static final String ZK_HOST = "c1n1.gbif.org:2181";
  private static final Logger LOG = LoggerFactory.getLogger(ZookeeperCleanup.class);

  private ZooKeeper zk;

  private void init() throws IOException, KeeperException {
    LOG.debug("Initiating ZooKeeper connection");
    zk = new ZooKeeper(ZK_HOST, 3000, this);
    // wait for zk to fully connect
    while (!zk.getState().isAlive()) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        // do nothing
      }
    }
    LOG.debug("ZooKeeper connection created");
  }

  /**
   * Recursively delete the node at the given path from ZooKeeper.
   */
  private void clean(String path) throws InterruptedException, KeeperException {
    recursiveDelete(path, zk.getChildren(path, false));
  }

  private void recursiveDelete(String parentPath, List<String> paths) throws InterruptedException, KeeperException {
    if (paths.isEmpty()) return;

    for (String path : paths) {
      String fullPath = parentPath + "/" + path;
      recursiveDelete(fullPath, zk.getChildren(fullPath, false));
      LOG.debug("Deleting leaf [{}]", fullPath);
      zk.delete(fullPath, -1);
    }
  }

  public void process(WatchedEvent watchedEvent) {
  }

  /**
   * First and only arg needs to be the node whose content should be deleted, e.g. "/hbase".
   */
  public static void main(String[] args) throws Exception {
    LOG.debug("ZookeeperCleanup starting");
    ZookeeperCleanup instance = new ZookeeperCleanup();
    instance.init();
    instance.clean(args[0]);
    LOG.debug("ZookeeperCleanup finished");
  }
}
