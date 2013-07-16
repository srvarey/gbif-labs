/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry.metasync;

import org.gbif.registry.metasync.api.MetadataSynchroniser;
import org.gbif.registry.metasync.protocols.digir.DigirMetadataSynchroniser;
import org.gbif.registry.metasync.protocols.tapir.TapirMetadataSynchroniser;
import org.gbif.registry.metasync.util.HttpClientFactory;

import java.util.UUID;

import org.slf4j.bridge.SLF4JBridgeHandler;

public class Runner {

  public static void main(String[] args) {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();

    HttpClientFactory clientFactory = new HttpClientFactory(10000);

    MetadataSynchroniser synchroniser = new MetadataSynchroniserImpl();

    synchroniser.registerProtocolHandler(new DigirMetadataSynchroniser(clientFactory.provideHttpClient()));
    synchroniser.registerProtocolHandler(new TapirMetadataSynchroniser(clientFactory.provideHttpClient()));

    // Mock TAPIR 7ee758ae-2548-4d22-8bc8-b10950a4bce9
    UUID uuid = UUID.fromString("7ee758ae-2548-4d22-8bc8-b10950a4bce9");

    synchroniser.synchroniseInstallation(uuid);
    //synchroniser.synchroniseAllInstallations();
  }

}
