package org.gbif.registry.guice;

import org.gbif.registry.persistence.guice.RegistryMyBatisModule;
import org.gbif.registry.ws.guice.StringTrimInterceptor;
import org.gbif.ws.server.guice.GbifServletListener;

import java.util.List;
import java.util.Properties;

import com.google.common.collect.Lists;
import com.google.inject.Module;
import org.apache.bval.guice.ValidationModule;


/**
 * The Registry WS module for testing in Grizzly.
 * TODO: This exists only to provide the different properties name - can this be unified?
 */
public class TestRegistryWsServletListener extends GbifServletListener {

  public static final String APPLICATION_PROPERTIES = "registry-test.properties";

  public TestRegistryWsServletListener() {
    super(APPLICATION_PROPERTIES, "org.gbif.registry.ws", false);
  }

  @Override
  protected List<Module> getModules(Properties props) {
    return Lists.<Module>newArrayList(new RegistryMyBatisModule(props),
      StringTrimInterceptor.newMethodInterceptingModule(), new ValidationModule());
  }
}
