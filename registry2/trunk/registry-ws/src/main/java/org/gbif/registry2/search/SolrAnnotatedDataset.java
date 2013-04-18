package org.gbif.registry2.search;

import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.model.registry2.search.DatasetSearchResult;
import org.gbif.api.vocabulary.Country;
import org.gbif.api.vocabulary.registry2.DatasetSubtype;
import org.gbif.api.vocabulary.registry2.DatasetType;
import org.gbif.common.search.model.FacetField;
import org.gbif.common.search.model.FullTextSearchField;
import org.gbif.common.search.model.Key;
import org.gbif.common.search.model.SearchMapping;
import org.gbif.common.search.model.SuggestMapping;
import org.gbif.common.search.model.WildcardPadding;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;
import org.apache.solr.client.solrj.beans.Field;

/**
 * Annotated version of the dataset search result providing the required mapping to the SOLR schema and translation
 * between String to UUID as SOLR does not support UUID well.
 */
@SearchMapping(
  facets = {
    @FacetField(name = "NETWORK_ORIGIN", field = "network_of_origin_key", sort = FacetField.SortOrder.INDEX),
    @FacetField(name = "TYPE", field = "dataset_type", sort = FacetField.SortOrder.INDEX),
    @FacetField(name = "SUBTYPE", field = "dataset_subtype", sort = FacetField.SortOrder.INDEX),
    @FacetField(name = "KEYWORD", field = "keyword"),
    @FacetField(name = "OWNING_ORG", field = "owning_organization_key"),
    @FacetField(name = "HOSTING_ORG", field = "hosting_organization_key"),
    @FacetField(name = "DECADE", field = "decade", sort = FacetField.SortOrder.INDEX),
    @FacetField(name = "COUNTRY", field = "iso_country_code", sort = FacetField.SortOrder.INDEX)
  },
  fulltextFields = {
    @FullTextSearchField(field = "dataset_title", highlightField = "dataset_title", exactMatchScore = 10.0d,
      partialMatchScore = 1.0d),
    @FullTextSearchField(field = "keyword", partialMatching = WildcardPadding.NONE, exactMatchScore = 4.0d),
    @FullTextSearchField(field = "iso_country_code", partialMatching = WildcardPadding.NONE, exactMatchScore = 3.0d),
    @FullTextSearchField(field = "owning_organization_title", highlightField = "owning_organization_title",
      partialMatching = WildcardPadding.NONE, exactMatchScore = 2.0d),
    @FullTextSearchField(field = "hosting_organization_title", partialMatching = WildcardPadding.NONE,
      exactMatchScore = 2.0d),
    @FullTextSearchField(field = "description", partialMatching = WildcardPadding.NONE),
    @FullTextSearchField(field = "full_text", partialMatching = WildcardPadding.NONE, exactMatchScore = 0.5d)
  })
@SuggestMapping(field = "dataset_title_ngram", phraseQueryField = "dataset_title_nedge")
public class SolrAnnotatedDataset extends DatasetSearchResult {

  /**
   * Empty constructor required by SOLR.
   */
  public SolrAnnotatedDataset() {
  }

  /**
   * Creates a SolrAnnotatedDataset from the given dataset, copying only the relevant fields for Solr from
   * the given dataset.
   * 
   * @param d The Dataset which will be copied into this object
   * @param owner The owning organization
   * @param host The hosting organization
   */
  public SolrAnnotatedDataset(Dataset d, Organization owner, Organization host) {
    this.setDescription(d.getDescription());
    this.setKey(d.getKey());
    this.setTitle(d.getTitle());
    // this.setCountryCoverage(d.getCountryCoverage());
    // this.setNetworkOfOriginKey(d.getNetworkOfOriginKey());
    // this.setType(d.getType());
    // this.setSubtype(d.getSubtype());
    // this.setKeywords(d.getKeywords());
    // this.setDecades(DecadeExtractor.extractDecades(d.getTemporalCoverages()));
    // CoordinateExtractor.populateCoordinates(this, d.getGeographicCoverages());
    // this.setOwningOrganizationKey(d.getOwningOrganizationKey());
    if (owner != null) {
      this.setOwningOrganizationTitle(owner.getTitle());
    }
    if (host != null) {
      this.setHostingOrganizationKey(host.getKey().toString());
      this.setHostingOrganizationTitle(host.getTitle());
    }
  }

  @Field("iso_country_code")
  public void setCountryCoverage(List<String> isoCountryCodes) {
    Set<Country> countries = Sets.newHashSet();
    for (String iso : isoCountryCodes) {
      Country c = Country.fromIsoCode(iso);
      if (c != null) {
        countries.add(c);
      }
    }
    super.setCountryCoverage(countries);
  }

  @Field("decade")
  @Override
  public void setDecades(List<Integer> decades) {
    super.setDecades(decades);
  }

  @Field("description")
  @Override
  public void setDescription(String description) {
    super.setDescription(description);
  }

  @Field("east_bounding_coordinate")
  @Override
  public void setEastBoundingCoordinates(List<Double> eastBoundingCoordinates) {
    super.setEastBoundingCoordinates(eastBoundingCoordinates);
  }

  @Field("full_text")
  @Override
  public void setFullText(String fullText) {
    super.setFullText(fullText);
  }

  @Field("hosting_organization_key")
  public void setHostingOrganizationKey(String hostingOrganizationKey) {
    super.setHostingOrganizationKey(UUID.fromString(hostingOrganizationKey));
  }

  @Override
  @Field("hosting_organization_title")
  public void setHostingOrganizationTitle(String hostingOrganizationTitle) {
    super.setHostingOrganizationTitle(hostingOrganizationTitle);
  }

  @Field("key")
  @Key
  public void setKey(String key) {
    super.setKey(UUID.fromString(key));
  }

  @Override
  @Field("keyword")
  public void setKeywords(List<String> keywords) {
    super.setKeywords(keywords);
  }

  @Field("network_of_origin_key")
  public void setNetworkOfOriginKey(String networkOfOriginKey) {
    super.setNetworkOfOriginKey(UUID.fromString(networkOfOriginKey));
  }

  @Field("north_bounding_coordinate")
  @Override
  public void setNorthBoundingCoordinates(List<Double> northBoundingCoordinates) {
    super.setNorthBoundingCoordinates(northBoundingCoordinates);
  }

  @Field("owning_organization_key")
  public void setOwningOrganizationKey(String owningOrganizationKey) {
    super.setOwningOrganizationKey(UUID.fromString(owningOrganizationKey));
  }

  @Override
  @Field("owning_organization_title")
  public void setOwningOrganizationTitle(String owningOrganizationTitle) {
    super.setOwningOrganizationTitle(owningOrganizationTitle);
  }

  @Field("south_bounding_coordinate")
  @Override
  public void setSouthBoundingCoordinates(List<Double> southBoundingCoordinates) {
    super.setSouthBoundingCoordinates(southBoundingCoordinates);
  }

  @Field("dataset_subtype")
  public void setSubtype(String datasetSubtype) {
    super.setSubtype(DatasetSubtype.valueOf(datasetSubtype));
  }

  @Field("dataset_title")
  @Override
  public void setTitle(String title) {
    super.setTitle(title);
  }

  @Field("dataset_type")
  public void setType(String datasetType) {
    super.setType(DatasetType.valueOf(datasetType));
  }

  @Field("west_bounding_coordinate")
  @Override
  public void setWestBoundingCoordinates(List<Double> westBoundingCoordinates) {
    super.setWestBoundingCoordinates(westBoundingCoordinates);
  }
}
