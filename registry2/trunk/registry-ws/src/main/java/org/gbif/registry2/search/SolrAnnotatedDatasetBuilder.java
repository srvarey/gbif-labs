package org.gbif.registry2.search;

import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Installation;
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.model.registry2.Tag;
import org.gbif.api.service.registry2.InstallationService;
import org.gbif.api.service.registry2.OrganizationService;
import org.gbif.registry2.search.util.DecadeExtractor;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * A utility builder to prepare objects suitable for SOLR.
 */
public class SolrAnnotatedDatasetBuilder {

  private final OrganizationService organizationService;
  private final InstallationService installationService;

  @Inject
  public SolrAnnotatedDatasetBuilder(OrganizationService organizationService,
    InstallationService installationService) {
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
    // Cannot be done until the Dataset object is fleshed out
    sad.setCountryCoverage(d.getCountryCoverage());
    sad.setNetworkOfOriginKey(d.getNetworkOfOriginKey());
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
    }
    if (host != null) {
      sad.setHostingOrganizationKey(String.valueOf(host.getKey()));
      sad.setHostingOrganizationTitle(host.getTitle());
    }
    return sad;
  }
}
