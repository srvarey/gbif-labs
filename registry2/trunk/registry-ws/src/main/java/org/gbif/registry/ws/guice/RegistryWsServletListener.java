package org.gbif.registry.ws.guice;

import org.gbif.registry.persistence.guice.RegistryMyBatisModule;
import org.gbif.ws.server.guice.GbifServletListener;

import java.util.List;
import java.util.Properties;

import com.google.common.collect.Lists;
import com.google.inject.Module;
import org.apache.bval.guice.ValidationModule;


/**
 * The Registry WS module.
 */
public class RegistryWsServletListener extends GbifServletListener {

  public static final String APPLICATION_PROPERTIES = "registry.properties";

  public RegistryWsServletListener() {
    super(APPLICATION_PROPERTIES, "org.gbif.registry.ws.resources", false);
  }

  @Override
  protected List<Module> getModules(Properties props) {
    return Lists.<Module>newArrayList(
      new RegistryMyBatisModule(props),
      StringTrimInterceptor.newMethodInterceptingModule(),
      new ValidationModule());
  }
}
