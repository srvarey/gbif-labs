package org.gbif.util.zookeeper;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperCleaner implements Watcher {

  private static final String ZK_HOST = "c1n8.gbif.org:2181,c1n9.gbif.org:2181,c1n10.gbif.org:2181";
  //  private static final String ZK_HOST = "localhost:2181";
  private static final Logger LOG = LoggerFactory.getLogger(ZookeeperCleanup.class);

  private ZooKeeper zk;

  public ZookeeperCleaner() throws IOException {
    init();
  }

  private void init() throws IOException {
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
  public void clean(String path) throws InterruptedException {
    try {
      recursiveDelete(path, zk.getChildren(path, false));
    } catch (KeeperException e) {
      LOG.warn("Could not delete node at [{}]", path, e);
    }
  }

  public void recursiveDelete(String parentPath, List<String> paths) throws InterruptedException {
    if (paths.isEmpty()) return;

    for (String path : paths) {
      try {
        String fullPath = parentPath + '/' + path;
        recursiveDelete(fullPath, zk.getChildren(fullPath, false));
        LOG.debug("Deleting leaf [{}]", fullPath);
        zk.delete(fullPath, -1);
      } catch (KeeperException e) {
        LOG.warn("Could not delete node at [{}]", parentPath + '/' + path, e);
      }
    }
  }

  @Override
  public void process(WatchedEvent watchedEvent) {
  }

}
