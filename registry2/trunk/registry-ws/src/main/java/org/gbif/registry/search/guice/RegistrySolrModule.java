package org.gbif.registry.search.guice;

import com.google.common.base.Throwables;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;


public class RegistrySolrModule extends AbstractModule {

  @Override
  protected void configure() {
  }

  @Provides
  @Singleton
  SolrServer todoRewriteMe() {
    System.setProperty("solr.solr.home", "/tmp/registry-solr");
    CoreContainer.Initializer initializer = new CoreContainer.Initializer();
    try {
      CoreContainer coreContainer = initializer.initialize();
      return new EmbeddedSolrServer(coreContainer, "");
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

}
