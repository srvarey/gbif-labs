package org.gbif.registry.ws.resources;

import org.gbif.api.model.common.search.SearchResponse;
import org.gbif.api.registry.model.search.NodeSearchParameter;
import org.gbif.api.registry.model.search.NodeSearchRequest;
import org.gbif.common.search.service.SolrSearchService;
import org.gbif.registry.search.SearchableNode;
import org.gbif.ws.util.ExtraMediaTypes;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.common.collect.ImmutableSortedMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.solr.client.solrj.SolrQuery;

/**
 * A MyBATIS implementation of the service.
 */
@Produces({MediaType.APPLICATION_JSON, ExtraMediaTypes.APPLICATION_JAVASCRIPT})
@Consumes({MediaType.APPLICATION_JSON})
@Path("node/search")
@Singleton
public class NodeSearchResource extends
  SolrSearchService<SearchableNode, NodeSearchParameter, SearchableNode, NodeSearchRequest> {

  // sort by rank and then alphabetically on title
  private static final Map<String, SolrQuery.ORDER> SORT_ORDER =
    ImmutableSortedMap.of("score", SolrQuery.ORDER.desc, "title", SolrQuery.ORDER.asc);

// @Inject
// public NodeSearchResource(SolrServer solr) {
// super(solr, "node", SearchableNode.class, SearchableNode.class, NodeSearchParameter.class, SORT_ORDER);
// }
  @Inject
  public NodeSearchResource() {

    super(null, "node", SearchableNode.class, SearchableNode.class, NodeSearchParameter.class, SORT_ORDER);
  }

  @GET
  public SearchResponse<SearchableNode, NodeSearchParameter> search() {
    // @Context NodeSearchRequest request
    return super.search(new NodeSearchRequest());
  }
}
