package org.gbif.registry.ws.client;

import org.gbif.api.model.common.paging.PagingResponse;

/**
 * Package access utility to provide generic types.
 */
class GenericTypes {

  public final static GenericType<PagingResponse<Node>> PAGING_NODE = new GenericType<PagingResponse<Node>>() {
  };

  public final static GenericType<PagingResponse<Organization>> PAGING_ORGANIZATION =
    new GenericType<PagingResponse<Organization>>() {
    };

  public final static GenericType<PagingResponse<Installation>> PAGING_INSTALLATION =
    new GenericType<PagingResponse<Installation>>() {
    };

  public final static GenericType<PagingResponse<Dataset>> PAGING_DATASET = new GenericType<PagingResponse<Dataset>>() {
  };

  public final static GenericType<PagingResponse<Network>> PAGING_NETWORK = new GenericType<PagingResponse<Network>>() {
  };

  public final static GenericType<List<Contact>> LIST_CONTACT = new GenericType<List<Contact>>() {
  };

  public final static GenericType<List<Endpoint>> LIST_ENDPOINT = new GenericType<List<Endpoint>>() {
  };

  public final static GenericType<List<MachineTag>> LIST_MACHINETAG = new GenericType<List<MachineTag>>() {
  };

  public final static GenericType<List<Tag>> LIST_TAG = new GenericType<List<Tag>>() {
  };

  public final static GenericType<List<Identifier>> LIST_IDENTIFIER = new GenericType<List<Identifier>>() {
  };

  public final static GenericType<List<Comment>> LIST_COMMENT = new GenericType<List<Comment>>() {
  };
}
