package org.gbif.api.registry.model.search;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.search.FacetedSearchRequest;

/**
 * 
 */
public class NodeSearchRequest extends FacetedSearchRequest<NodeSearchParameter> {

  public NodeSearchRequest() {
  }

  public NodeSearchRequest(Pageable page) {
    super(page);
  }

  public NodeSearchRequest(long offset, int limit) {
    super(offset, limit);
  }

  public NodeSearchRequest(long offset, int limit, boolean facetsOnly) {
    super(offset, limit, facetsOnly);
  }


  public void addTitleFilter(String title) {
    addParameter(NodeSearchParameter.TITLE, title);
  }

}
