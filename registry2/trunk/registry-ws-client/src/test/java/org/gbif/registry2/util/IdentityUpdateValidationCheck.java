package org.gbif.registry2.util;

import org.gbif.api.model.common.paging.PagingRequest;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.Contactable;
import org.gbif.api.model.registry2.NetworkEntity;
import org.gbif.api.service.registry2.DatasetService;
import org.gbif.api.service.registry2.InstallationService;
import org.gbif.api.service.registry2.NetworkEntityService;
import org.gbif.api.service.registry2.NetworkService;
import org.gbif.api.service.registry2.NodeService;
import org.gbif.api.service.registry2.OrganizationService;
import org.gbif.registry2.ws.client.guice.RegistryWsClientModule;

import java.util.Properties;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class does the identity function update for entities in the registry. That is to say that it reads and updates
 * all entities.
 * Because the database was migrated, and we have Java validation rules, this will ensure that all migrated data can be
 * used in updates.
 */
public class IdentityUpdateValidationCheck {

  private static final Logger LOG = LoggerFactory.getLogger(IdentityUpdateValidationCheck.class);

  /**
   * @param args Base url
   */
  public static void main(String[] args) {
    Properties p = new Properties();
    p.put("registry.ws.url", args[0]);
    Injector injector = Guice.createInjector(new RegistryWsClientModule(p));

    LOG.info("Starting Node tests");
    int nodeErrorCount = verifyEntity(injector.getInstance(NodeService.class));
    LOG.info("Node tests produced {} errors", nodeErrorCount);

    LOG.info("Starting Organization tests");
    int organisationErrorCount = verifyEntity(injector.getInstance(OrganizationService.class));
    LOG.info("Organization tests produced {} errors", organisationErrorCount);

    LOG.info("Starting Installation tests");
    int installationErrorCount = verifyEntity(injector.getInstance(InstallationService.class));
    LOG.info("Installation tests produced {} errors", installationErrorCount);

    LOG.info("Starting Dataset tests");
    int datasetErrorCount = verifyEntity(injector.getInstance(DatasetService.class));
    LOG.info("Dataset tests produced {} errors", datasetErrorCount);

    LOG.info("Starting Network tests");
    int networkErrorCount = verifyEntity(injector.getInstance(NetworkService.class));
    LOG.info("Network tests produced {} errors", networkErrorCount);

    // summarise again jsut for ease of reading
    LOG.info("Test results:");
    LOG.info("Node tests produced {} errors", nodeErrorCount);
    LOG.info("Organization tests produced {} errors", organisationErrorCount);
    LOG.info("Installation tests produced {} errors", installationErrorCount);
    LOG.info("Dataset tests produced {} errors", datasetErrorCount);
    LOG.info("Network tests produced {} errors", networkErrorCount);

  }

  private static <T extends NetworkEntity> int verifyEntity(NetworkEntityService<T> service) {
    int errorCount = 0;
    PagingRequest page = new PagingRequest(0, 100);
    PagingResponse<T> response = null;
    do {
      response = service.list(page);
      LOG.debug("Page with offset[{}] return[{}] records", page.getOffset(), response.getCount());
      for (T e : response.getResults()) {
        LOG.debug("Updating entity[{}]", e.getKey());
        try {
          service.update(e);

          if (e instanceof Contactable) {
            Contactable contactable = (Contactable) e;
            for (Contact c : contactable.getContacts()) {
              service.updateContact(e.getKey(), c);
            }
          }

        } catch (Exception ex) {
          LOG.error("Unable to update entity[{}]", e.getKey(), ex);
          errorCount++;
        }
      }
      page.nextPage();
    } while (!response.isEndOfRecords());
    return errorCount;
  }
}
