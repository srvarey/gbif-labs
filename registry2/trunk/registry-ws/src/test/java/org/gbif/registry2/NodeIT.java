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

import org.gbif.api.model.registry2.Node;
import org.gbif.api.service.registry2.NodeService;
import org.gbif.api.vocabulary.Country;
import org.gbif.registry2.guice.RegistryTestModules;
import org.gbif.registry2.utils.Nodes;
import org.gbif.registry2.ws.resources.NodeResource;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * This is parameterized to run the same test routines for the following:
 * <ol>
 * <li>The persistence layer</li>
 * <li>The WS service layer</li>
 * <li>The WS service client layer</li>
 * </ol>
 */
@RunWith(Parameterized.class)
public class NodeIT extends NetworkEntityTest<Node> {

  private final NodeService service;
  private static Set<Country> TEST_COUNTRIES = Sets.newHashSet(
    Country.AFGHANISTAN, Country.ARGENTINA, Country.DENMARK, Country.GHANA
  );

  @Parameters
  public static Iterable<Object[]> data() {
    return ImmutableList.<Object[]>of(new Object[] {RegistryTestModules.webservice().getInstance(NodeResource.class)},
                                      new Object[] {
                                        RegistryTestModules.webserviceClient().getInstance(NodeService.class)});
  }

  public NodeIT(NodeService service) {
    super(service);
    this.service = service;
  }

  @Test
  public void testContacts() {
    Node node = create(newEntity(), 1);
    ContactTests.testAddDelete(service, node);
  }

  @Test
  public void testMachineTags() {
    Node node = create(newEntity(), 1);
    MachineTagTests.testAddDelete(service, node);
  }

  @Test
  public void testTags() {
    Node node = create(newEntity(), 1);
    TagTests.testAddDelete(service, node);
    node = create(newEntity(), 2);
    TagTests.testTagErroneousDelete(service, node);
  }

  @Test
  public void testComments() {
    Node node = create(newEntity(), 1);
    CommentTests.testAddDelete(service, node);
  }

  @Test
  @Ignore("Waiting for a proper country provider")
  public void testGetByCountry() {
    initCountryNodes();
    Node n = service.getByCountry(Country.ANGOLA);
    assertNull(n);

    for (Country c : TEST_COUNTRIES) {
      n = service.getByCountry(c);
      assertEquals(c, n.getCountry());
    }
  }

  private void initCountryNodes() {
    int count = 0;
    for (Country c : TEST_COUNTRIES) {
      Node n = newEntity();
      n.setCountry(c);
      n.setTitle("GBIF Node " + c.getTitle());
      create(n, count + 1);
      count++;
    }
  }

  @Test
  public void testCountries() {
    initCountryNodes();
    List<Country> countries = service.listNodeCountries();
    assertEquals(TEST_COUNTRIES.size(), countries.size());
    for (Country c : countries) {
      assertTrue("Unexpected node country" + c, TEST_COUNTRIES.contains(c));
    }
  }

  @Override
  protected Node newEntity() {
    return Nodes.newInstance();
  }

}
