package org.gbif.registry2.search;

import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Installation;
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.model.registry2.Tag;
import org.gbif.api.service.registry2.NetworkEntityService;
import org.gbif.api.vocabulary.Country;
import org.gbif.registry2.search.util.DecadeExtractor;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * A utility builder to prepare objects suitable for SOLR.
 */
class SolrAnnotatedDatasetBuilder {

  private final NetworkEntityService<Organization> organizationService;
  private final NetworkEntityService<Installation> installationService;

  public SolrAnnotatedDatasetBuilder(NetworkEntityService<Organization> organizationService,
    NetworkEntityService<Installation> installationService) {
    this.organizationService = organizationService;
    this.installationService = installationService;
  }

  /**
   * Creates a SolrAnnotatedDataset from the given dataset, copying only the relevant fields for Solr from
   * the given dataset.
   * 
   * @param d The Dataset which will be copied into this object
   */
  public SolrAnnotatedDataset build(Dataset d) {
    SolrAnnotatedDataset sad = new SolrAnnotatedDataset();

    sad.setDescription(d.getDescription());
    sad.setKey(d.getKey());
    sad.setTitle(d.getTitle());
    sad.setType(d.getType());
    sad.setSubtype(d.getSubtype());
    //TODO: http://dev.gbif.org/issues/browse/REG-393
    sad.setCountryCoverage(d.getCountryCoverage());
    List<String> kw = Lists.newArrayList();
    for (Tag t : d.getTags()) {
      kw.add(t.getValue());
    }
    sad.setKeywords(kw);
    sad.setDecades(DecadeExtractor.extractDecades(d.getTemporalCoverages()));
    sad.setOwningOrganizationKey(d.getOwningOrganizationKey());

    Organization owner =
      d.getOwningOrganizationKey() != null ? organizationService.get(d.getOwningOrganizationKey()) : null;

    Installation installation =
      d.getInstallationKey() != null ? installationService.get(d.getInstallationKey()) : null;

    Organization host =
      installation != null && installation.getOrganizationKey() != null ? organizationService.get(installation
        .getOrganizationKey()) : null;

    if (owner != null) {
      sad.setOwningOrganizationTitle(owner.getTitle());
      sad.setPublishingCountry(owner.getCountry());
    } else {
      sad.setPublishingCountry(Country.UNKNOWN);
    }
    if (host != null) {
      sad.setHostingOrganizationKey(String.valueOf(host.getKey()));
      sad.setHostingOrganizationTitle(host.getTitle());
    }

    return sad;
  }
}
