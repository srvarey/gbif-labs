package org.gbif.registry.ws.guice;

import org.gbif.registry.persistence.guice.RegistryServiceMyBatisModule;
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
    super(APPLICATION_PROPERTIES, "org.gbif.registry.ws", false);
  }

  @Override
  protected List<Module> getModules(Properties props) {
    // https://issues.apache.org/jira/browse/BVAL-107
//
// Jsr303MetaBeanFactory f = null;
// Module m = new AbstractModule() {
//
// @Override
// protected void configure() {
// bindConstant().annotatedWith(Names.named("apache.bval.metabean-factory-classnames")).to(
// "org.apache.bval.jsr303.extensions.MethodValidatorMetaBeanFactory");
//
// }
// };


    return Lists.<Module>newArrayList(new RegistryServiceMyBatisModule(props),
      StringTrimInterceptor.newMethodInterceptingModule(), new ValidationModule());
  }
}
