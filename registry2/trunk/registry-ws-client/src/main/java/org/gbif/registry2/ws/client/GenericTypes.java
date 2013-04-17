/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry2.ws.client;

import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry2.Comment;
import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Endpoint;
import org.gbif.api.model.registry2.Identifier;
import org.gbif.api.model.registry2.Installation;
import org.gbif.api.model.registry2.MachineTag;
import org.gbif.api.model.registry2.Network;
import org.gbif.api.model.registry2.Node;
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.model.registry2.Tag;

import java.util.List;

import com.sun.jersey.api.client.GenericType;

/**
 * Package access utility to provide generic types.
 */
class GenericTypes {

  public static final GenericType<PagingResponse<Node>> PAGING_NODE = new GenericType<PagingResponse<Node>>() {
  };
  public static final GenericType<PagingResponse<Organization>> PAGING_ORGANIZATION =
    new GenericType<PagingResponse<Organization>>() {
    };
  public static final GenericType<PagingResponse<Installation>> PAGING_INSTALLATION =
    new GenericType<PagingResponse<Installation>>() {
    };
  public static final GenericType<PagingResponse<Dataset>> PAGING_DATASET = new GenericType<PagingResponse<Dataset>>() {
  };
  public static final GenericType<PagingResponse<Network>> PAGING_NETWORK = new GenericType<PagingResponse<Network>>() {
  };
  public static final GenericType<List<Contact>> LIST_CONTACT = new GenericType<List<Contact>>() {
  };
  public static final GenericType<List<Endpoint>> LIST_ENDPOINT = new GenericType<List<Endpoint>>() {
  };
  public static final GenericType<List<MachineTag>> LIST_MACHINETAG = new GenericType<List<MachineTag>>() {
  };
  public static final GenericType<List<Tag>> LIST_TAG = new GenericType<List<Tag>>() {
  };
  public static final GenericType<List<Identifier>> LIST_IDENTIFIER = new GenericType<List<Identifier>>() {
  };
  public static final GenericType<List<Comment>> LIST_COMMENT = new GenericType<List<Comment>>() {
  };

  private GenericTypes() {
    throw new UnsupportedOperationException("Can't initialize class");
  }

}
