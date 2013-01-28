/**
 * 
 */
package org.gbif.registry;

import org.gbif.api.model.common.paging.PagingRequest;
import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.model.NetworkEntity;
import org.gbif.api.registry.model.Tag;
import org.gbif.api.registry.model.WritableNetworkEntity;
import org.gbif.api.registry.service.NetworkEntityService;
import org.gbif.registry.data.Contacts;

import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * A generic test that runs against the registry interfaces.
 */
public abstract class NetworkEntityTest<WRITABLE extends WritableNetworkEntity, READABLE extends NetworkEntity, SERVICE extends NetworkEntityService<READABLE, WRITABLE>> {

  // Runs liquibase, and puts DB in a correct initial state for each test
  @Rule
  public final DatabaseInitializer<SERVICE> initializer = new DatabaseInitializer<SERVICE>();
  private final SERVICE service; // under test

  /**
   * @return a new example instance
   */
  protected abstract WRITABLE newWritable();

  /**
   * @param service Under test
   */
  public NetworkEntityTest(SERVICE service) {
    this.service = service;
  }

  @Test(expected = IllegalArgumentException.class)
  public void createWithKey() {
    WRITABLE e = newWritable();
    e.setKey(UUID.randomUUID()); // illegal to provide a key
    service.create(e);
  }

  @Test
  public void testCreate() {
    create(newWritable(), 1);
    create(newWritable(), 2);
  }

  @Test
  public void testUpdate() {
    READABLE n1 = create(newWritable(), 1);
    n1.setTitle("New title");
    service.update(asWritable(n1));
    NetworkEntity n2 = service.get(n1.getKey());
    assertEquals("Persisted does not reflect update", "New title", n2.getTitle());
    assertTrue("Modification date not changing on update",
      n2.getModified().after(n1.getModified()));
    assertTrue("Modification date is not after the creation date",
      n2.getModified().after(n1.getCreated()));
    assertEquals("List service does not reflect the number of created entities", 1,
      service.list(new PagingRequest()).getResults().size());
  }

  @Test
  public void testDelete() {
    NetworkEntity n1 = create(newWritable(), 1);
    NetworkEntity n2 = create(newWritable(), 2);
    service.delete(n1.getKey());
    READABLE n4 = service.get(n1.getKey()); // one can get a deleted entity
    n1.setDeleted(n4.getDeleted());
    assertEquals("Persisted does not reflect original after a deletion", n1, n4);
    // check that one cannot see the deleted entity in a list
    assertEquals("List service does not reflect the number of created entities", 1,
      service.list(new PagingRequest()).getResults().size());
    assertEquals("Following a delete, the wrong entity is returned in list results", n2,
      service.list(new PagingRequest()).getResults().get(0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDoubleDelete() {
    NetworkEntity n1 = create(newWritable(), 1);
    service.delete(n1.getKey());
    service.delete(n1.getKey()); // should fail
  }

  /**
   * Creates 5 entities, and then pages over them using differing paging strategies, confirming the correct number of
   * records are returned for each strategy.
   */
  @Test
  public void testPaging() {
    for (int i = 1; i <= 5; i++) {
      create(newWritable(), i);
    }

    // the expected number of records returned when paging at different page sizes
    int[][] expectedPages = new int[][] {
      {1, 1, 1, 1, 1, 0}, // page size of 1
      {2, 2, 0}, // page size of 2
      {3, 2, 0}, // page size of 3
      {4, 1, 0}, // page size of 4
      {5, 0}, // page size of 5
      {5, 0}, // page size of 6
    };

    // test the various paging strategies (e.g. page size of 1,2,3 etc to verify they behave as outlined above)
    for (int pageSize = 1; pageSize <= expectedPages.length - 1; pageSize++) {
      int offset = 0;
      for (int page = 0; page < expectedPages[pageSize].length - 1; page++, offset += pageSize) {
        // request the page using the page size and offset
        List<READABLE> results = service.list(new PagingRequest(offset, expectedPages[pageSize][page])).getResults();
        // confirm it is the correct number of results as outlined above
        assertEquals("Paging is not operating as expected when requesting pages of size " + pageSize,
          expectedPages[pageSize][page], results.size());
      }
    }
  }

  @Test
  public void testTags() {
    NetworkEntity n1 = create(newWritable(), 1);
    List<Tag> tags = service.listTags(n1.getKey(), null);
    assertNotNull("Tag list should be empty, not null when no tags exist", tags);
    assertTrue("Tags should be empty when none added", tags.isEmpty());
    service.addTag(n1.getKey(), "tag1");
    service.addTag(n1.getKey(), "tag2");
    tags = service.listTags(n1.getKey(), null);
    assertNotNull(tags);
    assertEquals("2 tags have been added", 2, tags.size());
    service.deleteTag(n1.getKey(), tags.get(0).getKey());
    tags = service.listTags(n1.getKey(), null);
    assertNotNull(tags);
    assertEquals("1 tag should remain after the deletion", 1, tags.size());
  }

  @Test
  public void testTagErroneousDelete() {
    NetworkEntity n1 = create(newWritable(), 1);
    int tagKey = service.addTag(n1.getKey(), "tag1");
    service.deleteTag(UUID.randomUUID(), tagKey); // wrong parent UUID
    // nothing happens - expected?
  }

  @Test
  public void testContacts() {
    NetworkEntity n1 = create(newWritable(), 1);
    List<Contact> contacts = service.listContacts(n1.getKey());
    assertNotNull("Contact list should be empty, not null when no contacts exist", contacts);
    assertTrue("Contact should be empty when none added", contacts.isEmpty());
    service.addContact(n1.getKey(), Contacts.newInstance());
    service.addContact(n1.getKey(), Contacts.newInstance());
    contacts = service.listContacts(n1.getKey());
    assertNotNull(contacts);
    assertEquals("2 contacts have been added", 2, contacts.size());
    service.deleteContact(n1.getKey(), contacts.get(0).getKey());
    contacts = service.listContacts(n1.getKey());
    assertNotNull(contacts);
    assertEquals("1 contact should remain after the deletion", 1, contacts.size());
    Contact expected = Contacts.newInstance();
    Contact created = contacts.get(0);
    expected.setKey(created.getKey());
    expected.setCreated(created.getCreated());
    expected.setModified(created.getModified());
    assertEquals("Created contact does not read as expected", expected, created);
  }

  // Repeatable entity creation with verification tests
  protected READABLE create(WRITABLE entity, int expectedCount) {
    Preconditions.checkNotNull(entity, "Cannot create a non existing entity");
    UUID key = service.create(entity);
    entity.setKey(key);
    READABLE written = service.get(key);
    assertNotNull(written.getCreated());
    assertNotNull(written.getModified());
    assertNull(written.getDeleted());
    assertEquals("Persisted does not reflect original", entity, asWritable(written));
    assertEquals("List service does not reflect the number of created entities", expectedCount,
      service.list(new PagingRequest()).getResults().size());
    return written;
  }

  /**
   * Creates a new instance of the supplied entity in it's writable form, to allow type safe comparison.
   * Note, that this will be a lossy transformation.
   */
  protected WRITABLE asWritable(READABLE source) {
    try {
      // use reflection to create a new instance of the generic specified class
      ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
      @SuppressWarnings("unchecked")
      Class<WRITABLE> t1 = (Class<WRITABLE>) type.getActualTypeArguments()[0]; // WRITABLE is the first generic type
      WRITABLE r = t1.newInstance();

      ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
      ConvertUtils.register(new DateConverter(null), Date.class);
      BeanUtils.copyProperties(r, source);
      return r;
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  protected SERVICE getService() {
    return service;
  }
}
