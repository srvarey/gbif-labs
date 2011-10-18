package org.gbif.testws;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * Overarching configuration of our WebService. Tying together Jersey, MyBatis and our own classes using three
 * different modules.
 */
public class GuiceConfig extends GuiceServletContextListener {

  @Override
  protected Injector getInjector() {
    return Guice.createInjector(Stage.PRODUCTION, new JerseyModule());
  }

  /**
   * A Guice module for all Jersey relevant details
   */
  private static class JerseyModule extends JerseyServletModule {

    @Override
    protected void configureServlets() {
      Map<String, String> params = new HashMap<String, String>(2);

      // Let Jersey look for root resources and Providers automatically
      params.put(PackagesResourceConfig.PROPERTY_PACKAGES, "org.gbif.testws");

      serve("/*").with(GuiceContainer.class, params);
    }

  }

}
