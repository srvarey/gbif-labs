package org.gbif.registry2.ws.guice;

import org.gbif.api.model.common.paging.PagingRequest;
import org.gbif.api.service.common.UserService;
import org.gbif.api.service.registry2.DatasetService;
import org.gbif.api.service.registry2.OrganizationService;
import org.gbif.utils.file.properties.PropertiesUtil;
import org.gbif.ws.security.GbifAppAuthService;

import java.io.IOException;
import java.util.Properties;

import com.google.inject.Injector;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

public class RegistryWsServletListenerTest {

  private static Properties properties;

  static {
    try {
      properties = PropertiesUtil.loadProperties("registry-test.properties");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Makes sure that two mybatis projects each with its own Datasource work fine together.
   * Tests the complete listener module, calling real methods to force guice to finalize the bindings.
   */
  @Test
  public void testListenerModule() {
    RegistryWsServletListener mod = new RegistryWsServletListener(properties);
    Injector injector = mod.getInjector();

    GbifAppAuthService auth = injector.getInstance(GbifAppAuthService.class);
    assertNotNull(auth);

    DatasetService datasetService = injector.getInstance(DatasetService.class);
    datasetService.list(new PagingRequest());

    UserService userService = injector.getInstance(UserService.class);
    userService.get("admin");

    OrganizationService orgService = injector.getInstance(OrganizationService.class);
    orgService.list(new PagingRequest());
  }

}
