package org.gbif.registry;

import org.gbif.registry.guice.TestRegistryWsServletListener;

import java.io.IOException;

import com.google.common.base.Throwables;
import com.google.inject.servlet.GuiceFilter;
import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.http.servlet.ServletAdapter;

/**
 * An embedded web server using Grizzly that run the registry ws application.
 */
public class RegistryServer {

  /**
   * The system property one can set to override the port.
   */
  public static final String PORT_PROPERTY = "grizzly.port"; // to override as a system property

  /**
   * The default port to use, should no system property be supplied.
   */
  public static final int DEFAULT_PORT = 7001;

  private final GrizzlyWebServer webServer;

  public RegistryServer() {
    webServer = new GrizzlyWebServer(getPort());
    ServletAdapter sa = new ServletAdapter();
    sa.addServletListener(TestRegistryWsServletListener.class.getName());
    sa.addFilter(new GuiceFilter(), "Guice", null);
    sa.setContextPath("/");
    sa.setServletPath("/");
    webServer.addGrizzlyAdapter(sa, null);
  }

  public void start() {
    try {
      webServer.start();
    } catch (IOException e) {
      Throwables.propagate(e);
    }
  }

  public void stop() {
    webServer.stop();
  }

  /**
   * Gets the port that grizzly will run on. This will either be {@link #DEFAULT_PORT} or the value supplied as a system
   * property named {@link #PORT_PROPERTY}.
   */
  public static int getPort() {
    String port = System.getProperty(PORT_PROPERTY);
    try {
      return (port == null) ? DEFAULT_PORT : Integer.parseInt(port);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(PORT_PROPERTY + " does not hold a valid port: " + port);
    }
  }
}
