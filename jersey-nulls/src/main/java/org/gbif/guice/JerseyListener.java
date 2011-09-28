package org.gbif.guice;

import org.gbif.resource.AlwaysNullResource;
import org.gbif.resource.interceptors.NullResponseInterceptor;
import org.gbif.resource.interceptors.NullableResponse;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class JerseyListener extends GuiceServletContextListener {

  @Override
  protected Injector getInjector() {
    return Guice.createInjector(new JerseyTestModule());
  }

  private static class JerseyTestModule extends JerseyServletModule {

    @Override
    protected void configureServlets() {
      Map<String, String> params = new HashMap<String, String>(2);
      //The Jersey ws-Resource must be registered in the Guice context,
      // in this way the class can be intercepted by the NullResponseInterceptor
      bind(AlwaysNullResource.class).in(Scopes.SINGLETON);
      // Configure automatic JSON output for Jersey
      params.put(JSONConfiguration.FEATURE_POJO_MAPPING, "true");
      // Let Jersey look for root resources and Providers automatically
      params.put(PackagesResourceConfig.PROPERTY_PACKAGES, "org.gbif.resource");
      //Register the response filter as init-param for the JerseyServletModule/Listener/Filter
      //This line should be removed to avoid the obstructive execution of the filter for every method
      params.put("com.sun.jersey.spi.container.ContainerResponseFilters","org.gbif.resource.interceptors.NullResponseFilter");
      //Binds the NullResponseInterceptor
      bindInterceptor(Matchers.any(), Matchers.annotatedWith(NullableResponse.class), new NullResponseInterceptor());

      serve("/*").with(GuiceContainer.class, params);
    }
  }
}
