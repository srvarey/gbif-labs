/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry2.ws.client;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry2.Comment;
import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Endpoint;
import org.gbif.api.model.registry2.Identifier;
import org.gbif.api.model.registry2.MachineTag;
import org.gbif.api.model.registry2.Tag;
import org.gbif.api.service.registry2.DatasetService;
import org.gbif.registry2.ws.client.guice.RegistryWs;
import org.gbif.ws.client.BaseWsGetClient;

import java.util.List;
import java.util.UUID;

import com.google.inject.Inject;
import com.sun.jersey.api.client.WebResource;

/**
 * Client-side implementation to the DatasetService.
 */
public class DatasetWsClient extends BaseWsGetClient<Dataset, UUID> implements DatasetService {

  @Inject
  public DatasetWsClient(@RegistryWs WebResource resource) {
    super(Dataset.class, resource.path("dataset"), null);
  }

  @Override
  public UUID create(Dataset entity) {
    return post(UUID.class, entity, "/");
  }

  @Override
  public void delete(UUID key) {
    delete(key.toString());
  }

  @Override
  public PagingResponse<Dataset> list(Pageable page) {
    return get(GenericTypes.PAGING_DATASET, null, null, page);
  }

  @Override
  public void update(Dataset entity) {
    put(entity, entity.getKey().toString());
  }

  @Override
  public Dataset get(UUID key) {
    return get(key.toString());
  }

  @Override
  public int addTag(UUID targetEntityKey, String value) {
    // post the value to .../uuid/tag and expect an int back
    return post(Integer.class, (Object) value, targetEntityKey.toString(), "tag");
  }

  @Override
  public void deleteTag(UUID taggedEntityKey, int tagKey) {
    delete(taggedEntityKey.toString(), "tag", String.valueOf(tagKey));
  }

  @Override
  public List<Tag> listTags(UUID taggedEntityKey, String owner) {
    return get(GenericTypes.LIST_TAG, null, null, // TODO add owner here
      (Pageable) null, taggedEntityKey.toString(), "tag");
  }

  @Override
  public int addContact(UUID targetEntityKey, Contact contact) {
    // post the contact to .../uuid/contact and expect an int back
    return post(Integer.class, contact, targetEntityKey.toString(), "contact");
  }

  @Override
  public void deleteContact(UUID targetEntityKey, int contactKey) {
    delete(targetEntityKey.toString(), "contact", String.valueOf(contactKey));
  }

  @Override
  public List<Contact> listContacts(UUID targetEntityKey) {
    return get(GenericTypes.LIST_CONTACT, null, null,
      // TODO: type on contact?
      (Pageable) null, targetEntityKey.toString(), "contact");
  }

  @Override
  public int addEndpoint(UUID targetEntityKey, Endpoint endpoint) {
    return post(Integer.class, endpoint, targetEntityKey.toString(), "endpoint");
  }

  @Override
  public void deleteEndpoint(UUID targetEntityKey, int endpointKey) {
    delete(targetEntityKey.toString(), "endpoint", String.valueOf(endpointKey));
  }

  @Override
  public List<Endpoint> listEndpoints(UUID targetEntityKey) {
    return get(GenericTypes.LIST_ENDPOINT, null, null,
      // TODO: endpoint type
      (Pageable) null, targetEntityKey.toString(), "endpoint");
  }

  @Override
  public int addMachineTag(UUID targetEntityKey, MachineTag machineTag) {
    return post(Integer.class, machineTag, targetEntityKey.toString(), "machinetag");
  }

  @Override
  public void deleteMachineTag(UUID targetEntityKey, int machineTagKey) {
    delete(targetEntityKey.toString(), "machinetag", String.valueOf(machineTagKey));
  }

  @Override
  public List<MachineTag> listMachineTags(UUID targetEntityKey) {
    return get(GenericTypes.LIST_MACHINETAG, null, null, (Pageable) null, targetEntityKey.toString(), "machinetag");
  }

  @Override
  public int addIdentifier(UUID targetEntityKey, Identifier identifier) {
    return post(Integer.class, identifier, targetEntityKey.toString(), "identifier");
  }

  @Override
  public void deleteIdentifier(UUID targetEntityKey, int identifierKey) {
    delete(targetEntityKey.toString(), "identifier", String.valueOf(identifierKey));
  }

  @Override
  public List<Identifier> listIdentifiers(UUID targetEntityKey) {
    return get(GenericTypes.LIST_IDENTIFIER, null, null,
      // TODO: identifier type
      (Pageable) null, targetEntityKey.toString(), "identifier");
  }

  @Override
  public int addComment(UUID targetEntityKey, Comment comment) {
    return post(Integer.class, comment, targetEntityKey.toString(), "comment");
  }

  @Override
  public void deleteComment(UUID targetEntityKey, int commentKey) {
    delete(targetEntityKey.toString(), "comment", String.valueOf(commentKey));
  }

  @Override
  public List<Comment> listComments(UUID targetEntityKey) {
    return get(GenericTypes.LIST_COMMENT, null, null, (Pageable) null, targetEntityKey.toString(), "comment");
  }
}
