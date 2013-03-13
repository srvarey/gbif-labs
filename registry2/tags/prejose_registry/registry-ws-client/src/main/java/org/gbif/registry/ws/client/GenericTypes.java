package org.gbif.registry.ws.client;

import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.model.Node;
import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.model.Tag;

import java.util.List;

import com.sun.jersey.api.client.GenericType;

/**
 * Package access utility to provide generic types.
 */
class GenericTypes {

  public final static GenericType<PagingResponse<Node>> PAGING_NODE = new GenericType<PagingResponse<Node>>() {
  };

  public final static GenericType<PagingResponse<Organization>> PAGING_ORGANIZATION =
    new GenericType<PagingResponse<Organization>>() {
    };

  public final static GenericType<List<Tag>> LIST_TAG = new GenericType<List<Tag>>() {
  };

  public final static GenericType<List<Contact>> LIST_CONTACT = new GenericType<List<Contact>>() {
  };
}
