/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry2.ws.guice;

import org.gbif.registry2.events.EventModule;
import org.gbif.registry2.ims.ImsModule;
import org.gbif.registry2.persistence.guice.RegistryMyBatisModule;
import org.gbif.registry2.search.guice.RegistrySearchModule;
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
    super(APPLICATION_PROPERTIES, "org.gbif.registry2.ws.resources, org.gbif.registry2.ws.provider", false);
  }

  @Override
  protected List<Module> getModules(Properties props) {
    return Lists.newArrayList(new RegistryMyBatisModule(props),
      new ImsModule(props),
      StringTrimInterceptor.newMethodInterceptingModule(),
      new ValidationModule(),
      new EventModule(),
      new RegistrySearchModule(props)
      );
  }
}
