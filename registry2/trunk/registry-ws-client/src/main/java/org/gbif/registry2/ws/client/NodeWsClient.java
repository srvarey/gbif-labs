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
import org.gbif.api.model.registry2.Identifier;
import org.gbif.api.model.registry2.MachineTag;
import org.gbif.api.model.registry2.Node;
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.model.registry2.Tag;
import org.gbif.api.service.registry2.NodeService;
import org.gbif.api.vocabulary.Country;
import org.gbif.registry2.ws.client.guice.RegistryWs;
import org.gbif.ws.client.BaseWsGetClient;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.sun.jersey.api.client.WebResource;

/**
 * Client-side implementation to the NodeService.
 */
public class NodeWsClient extends BaseWsGetClient<Node, UUID> implements NodeService {

  @Inject
  public NodeWsClient(@RegistryWs WebResource resource) {
    super(Node.class, resource.path("node"), null);
  }

  @Override
  public UUID create(Node entity) {
    return post(UUID.class, entity, "/");
  }

  @Override
  public void delete(UUID key) {
    delete(key.toString());
  }

  @Override
  public PagingResponse<Node> list(Pageable page) {
    return get(GenericTypes.PAGING_NODE, null, null, page);
  }

  @Override
  public void update(Node entity) {
    Preconditions.checkArgument(entity.getKey() != null, "An entity must have a key to be updated");
    put(entity, entity.getKey().toString());
  }

  @Override
  public Node get(UUID key) {
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
      // TODO: identifier type?
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

  @Override
  public PagingResponse<Organization> organizationsEndorsedBy(UUID nodeKey, Pageable page) {
    return get(GenericTypes.PAGING_ORGANIZATION, null, null, page, nodeKey.toString(), "organization");
  }

  @Override
  public PagingResponse<Organization> pendingEndorsements(Pageable page) {
    return get(GenericTypes.PAGING_ORGANIZATION, null, null, page, "pendingEndorsement");
  }

  @Override
  public Node getByCountry(Country country) {
    return get("country", country.getIso2LetterCode());
  }

  @Override
  public List<Country> listNodeCountries() {
    return get(GenericTypes.LIST_COUNTRY, "country");
  }

  @Override
  public PagingResponse<Node> search(String query, Pageable page) {
    return get(GenericTypes.PAGING_NODE, (Locale) null, Params.of("q", query), page);
  }
}
