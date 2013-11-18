package org.gbif.biovel.locality.guice;

import org.gbif.ws.server.guice.GbifServletListener;
import org.gbif.ws.util.PropertiesUtil;

import java.util.List;
import java.util.Properties;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * The Registry WS module.
 */
public class LocalityWsServletListener extends GbifServletListener {

  public static final String APPLICATION_PROPERTIES = "locality.properties";

  private static final String PACKAGES = "org.gbif.biovel.locality.ws";

  public LocalityWsServletListener() {
    // TODO: sort out the deprecation
    super(PropertiesUtil.readFromClasspath(APPLICATION_PROPERTIES), PACKAGES, false, null, null);
  }

  @VisibleForTesting
  public LocalityWsServletListener(Properties properties) {
    super(properties, PACKAGES, true, null, null);
  }

  @VisibleForTesting
  @Override
  protected Injector getInjector() {
    return super.getInjector();
  }

  @Override
  protected List<Module> getModules(Properties properties) {
    return Lists.<Module>newArrayList(new LocalityMyBatisModule(properties));
  }

}
