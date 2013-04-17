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
package org.gbif.registry2;

import org.gbif.api.model.registry2.Network;
import org.gbif.api.service.registry2.NetworkService;
import org.gbif.registry2.guice.RegistryTestModules;
import org.gbif.registry2.utils.Networks;
import org.gbif.registry2.ws.resources.NetworkResource;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * This is parameterized to run the same test routines for the following:
 * <ol>
 * <li>The persistence layer</li>
 * <li>The WS service layer</li>
 * <li>The WS service client layer</li>
 * </ol>
 */
@RunWith(Parameterized.class)
public class NetworkIT extends NetworkEntityTest<Network> {

  private final NetworkService service;

  @Parameters
  public static Iterable<Object[]> data() {
    return ImmutableList.<Object[]>of(new Object[] {
      RegistryTestModules.webservice().getInstance(NetworkResource.class)},
                                      new Object[] {
                                        RegistryTestModules.webserviceClient().getInstance(NetworkService.class)});
  }

  public NetworkIT(NetworkService service) {
    super(service);
    this.service = service;
  }

  @Test
  public void testContacts() {
    Network network = create(newEntity(), 1);
    ContactTests.testAddDelete(service, network);
  }

  @Test
  public void testEndpoints() {
    Network network = create(newEntity(), 1);
    EndpointTests.testAddDelete(service, network);
  }

  @Test
  public void testMachineTags() {
    Network network = create(newEntity(), 1);
    MachineTagTests.testAddDelete(service, network);
  }

  @Test
  public void testTags() {
    Network network = create(newEntity(), 1);
    TagTests.testAddDelete(service, network);
    network = create(newEntity(), 2);
    TagTests.testTagErroneousDelete(service, network);
  }

  @Test
  public void testComments() {
    Network network = create(newEntity(), 1);
    CommentTests.testAddDelete(service, network);
  }

  @Override
  protected Network newEntity() {
    return Networks.newInstance();
  }

}
