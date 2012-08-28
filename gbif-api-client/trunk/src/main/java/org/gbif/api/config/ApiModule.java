package org.gbif.api.config;

import org.gbif.checklistbank.ws.client.guice.ChecklistBankWsClientModule;
import org.gbif.occurrencestore.ws.client.guice.OccurrenceWsClientModule;
import org.gbif.registry.ws.client.guice.RegistryWsClientModule;
import org.gbif.utils.HttpUtil;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.google.inject.spi.Message;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.apache.http.client.HttpClient;

public class ApiModule extends AbstractModule {

  /**
   * The portal module combines all service modules needed for the portal.
   * You can switch between the mybatis and the ws-client api implementation for checklistbank
   * by swapping the used module below.
   * When mybatis is used make sure your maven settings include the checklistbank jdbc properties.
   *
   * @throws com.google.inject.ConfigurationException If the application properties cannot be read
   */
  private Properties bindApplicationProperties() throws ConfigurationException {
    try {
      // load and bind single properties to pass on to other modules.
      Properties properties = new Properties();
      properties.load(this.getClass().getResourceAsStream("/api.properties"));
      Names.bindProperties(binder(), properties);
      return properties;
    } catch (IOException e) {
      Set<Message> messages = Sets.newHashSet();
      messages.add(new Message(e, "Unable to read the api.properties"));
      throw new ConfigurationException(messages);
    }
  }

  @Override
  protected void configure() {
    Properties properties = bindApplicationProperties();
    //TODO: move credentials into app properties
    HTTPBasicAuthFilter authFilter = new HTTPBasicAuthFilter("username", "password");
    bind(ClientFilter.class).toInstance(authFilter);

    // bind checklist bank API
    install(new ChecklistBankWsClientModule(properties));

    // bind registry API
    install(new RegistryWsClientModule(properties, true, true));

    // bind occurrence API
    install(new OccurrenceWsClientModule(properties));

  }

  @Provides
  @Singleton
  public HttpClient provideHttpClient() {
    return HttpUtil.newMultithreadedClient();
  }

}
