package org.gbif.api.model.registry2.search;

import org.gbif.api.vocabulary.Country;
import org.gbif.api.vocabulary.registry2.DatasetSubtype;
import org.gbif.api.vocabulary.registry2.DatasetType;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.base.Objects;

/**
 * The dataset search model object for faceted search in SOLR.
 */
public class DatasetSearchResult {

  private UUID key;
  private String title;
  private String publisherTitle;

  public UUID getKey() {
    return key;
  }

  public void setKey(UUID key) {
    this.key = key;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getPublisherTitle() {
    return publisherTitle;
  }

  public void setPublisherTitle(String publisherTitle) {
    this.publisherTitle = publisherTitle;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key, title, publisherTitle);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof DatasetSearchResult) {
      DatasetSearchResult that = (DatasetSearchResult) object;
      return Objects.equal(this.key, that.key)
        && Objects.equal(this.title, that.title)
        && Objects.equal(this.publisherTitle, that.publisherTitle);
    }
    return false;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("key", key)
      .add("title", title)
      .add("publisherTitle", publisherTitle)
      .toString();
  }

  // TODO All of these are needed
  public void setCountryCoverage(Set<Country> countries) {
  }

  public void setDecades(List<Integer> decades) {
  }

  public void setDescription(String description) {
  }

  public void setEastBoundingCoordinates(List<Double> eastBoundingCoordinates) {
  }

  public void setFullText(String fullText) {
  }

  public void setHostingOrganizationKey(UUID hostingOrganizationKey) {
  }

  public void setHostingOrganizationTitle(String hostingOrganizationTitle) {
  }

  public void setKeywords(List<String> keywords) {
  }

  public void setNetworkOfOriginKey(UUID networkOfOriginKey) {
  }

  public void setNorthBoundingCoordinates(List<Double> northBoundingCoordinates) {
  }

  public void setOwningOrganizationKey(UUID owningOrganizationKey) {
  }

  public void setOwningOrganizationTitle(String owningOrganizationTitle) {
  }

  public void setSouthBoundingCoordinates(List<Double> southBoundingCoordinates) {
  }

  public void setSubtype(DatasetSubtype datasetSubtype) {
  }

  public void setType(DatasetType datasetType) {
  }

  public void setWestBoundingCoordinates(List<Double> westBoundingCoordinates) {
  }

}
