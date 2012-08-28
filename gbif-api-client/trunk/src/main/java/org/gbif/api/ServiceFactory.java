package org.gbif.api;

import org.gbif.api.config.ApiModule;
import org.gbif.checklistbank.api.service.DescriptionService;
import org.gbif.checklistbank.api.service.DistributionService;
import org.gbif.checklistbank.api.service.ImageService;
import org.gbif.checklistbank.api.service.NameListService;
import org.gbif.checklistbank.api.service.NameUsageService;
import org.gbif.checklistbank.api.service.ReferenceService;
import org.gbif.checklistbank.api.service.SpeciesProfileService;
import org.gbif.checklistbank.api.service.TypeSpecimenService;
import org.gbif.checklistbank.api.service.VernacularNameService;
import org.gbif.registry.api.service.DatasetService;
import org.gbif.registry.api.service.GraphService;
import org.gbif.registry.api.service.NetworkService;
import org.gbif.registry.api.service.NodeService;
import org.gbif.registry.api.service.OrganizationService;
import org.gbif.registry.api.service.TechnicalInstallationService;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * Factory returning all GBIF API Services as singletons to be used with an optional credential.
 */
public class ServiceFactory {

  private static ServiceFactory factory;
  private Injector injector;

  private ServiceFactory() {
    injector = Guice.createInjector(new ApiModule());
    ClientFilter authFilter = injector.getInstance(ClientFilter.class);
  }

  public static ServiceFactory build() {
    if (factory == null) {
      factory = new ServiceFactory();
    }
    return factory;
  }

  /*  public NameUsageSearchService<NameUsageSearchResult> nameUsageSearchService(){
    TypeLiteral<NameUsageSearchService<NameUsageSearchResult>> tl = new TypeLiteral<NameUsageSearchService<NameUsageSearchResult>>(){};
    return injector.getInstance(tl);
  }
  public DatasetSearchService<DatasetSearchResult> nameUsageSearchService(){
    TypeLiteral<DatasetSearchService<DatasetSearchResult>> tl = new TypeLiteral<DatasetSearchService<DatasetSearchResult>>() {};
    return injector.getInstance(tl);
  }*/

  public NameUsageService nameUsageService() {
    return injector.getInstance(NameUsageService.class);
  }

  public DescriptionService descriptionService() {
    return injector.getInstance(DescriptionService.class);
  }

  public DistributionService distributionService() {
    return injector.getInstance(DistributionService.class);
  }

  public ImageService imageService() {
    return injector.getInstance(ImageService.class);
  }

  public ReferenceService referenceService() {
    return injector.getInstance(ReferenceService.class);
  }

  public SpeciesProfileService speciesProfileService() {
    return injector.getInstance(SpeciesProfileService.class);
  }

  public TypeSpecimenService typeSpecimenService() {
    return injector.getInstance(TypeSpecimenService.class);
  }

  public VernacularNameService vernacularNameService() {
    return injector.getInstance(VernacularNameService.class);
  }

  public NameListService nameListService() {
    return injector.getInstance(NameListService.class);
  }

  public DatasetService datasetService() {
    return injector.getInstance(DatasetService.class);
  }

  public OrganizationService organizationService() {
    return injector.getInstance(OrganizationService.class);
  }

  public NodeService nodeService() {
    return injector.getInstance(NodeService.class);
  }

  public NetworkService networkService() {
    return injector.getInstance(NetworkService.class);
  }

  public TechnicalInstallationService technicalInstallationService() {
    return injector.getInstance(TechnicalInstallationService.class);
  }

  public GraphService graphService() {
    return injector.getInstance(GraphService.class);
  }

}
