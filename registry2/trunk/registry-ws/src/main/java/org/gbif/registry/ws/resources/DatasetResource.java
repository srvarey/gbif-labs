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
package org.gbif.registry.ws.resources;

import org.gbif.api.registry.model.Comment;
import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.model.Dataset;
import org.gbif.api.registry.model.Endpoint;
import org.gbif.api.registry.model.Identifier;
import org.gbif.api.registry.model.MachineTag;
import org.gbif.api.registry.model.Tag;
import org.gbif.api.registry.service.DatasetService;
import org.gbif.registry.persistence.WithMyBatis;
import org.gbif.registry.persistence.mapper.CommentMapper;
import org.gbif.registry.persistence.mapper.ContactMapper;
import org.gbif.registry.persistence.mapper.DatasetMapper;
import org.gbif.registry.persistence.mapper.EndpointMapper;
import org.gbif.registry.persistence.mapper.IdentifierMapper;
import org.gbif.registry.persistence.mapper.MachineTagMapper;
import org.gbif.registry.persistence.mapper.TagMapper;
import org.gbif.registry.ws.guice.Trim;
import org.gbif.registry.ws.resources.rest.AbstractNetworkEntityResource;
import org.gbif.registry.ws.resources.rest.CommentRest;
import org.gbif.registry.ws.resources.rest.ContactRest;
import org.gbif.registry.ws.resources.rest.EndpointRest;
import org.gbif.registry.ws.resources.rest.IdentifierRest;
import org.gbif.registry.ws.resources.rest.MachineTagRest;
import org.gbif.registry.ws.resources.rest.TagRest;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.bval.guice.Validate;
import org.mybatis.guice.transactional.Transactional;

/**
 * A MyBATIS implementation of the service.
 */
@Path("dataset")
@Singleton
public class DatasetResource extends AbstractNetworkEntityResource<Dataset>
  implements DatasetService, ContactRest, EndpointRest, MachineTagRest, TagRest, IdentifierRest, CommentRest {

  private final DatasetMapper datasetMapper;
  private final ContactMapper contactMapper;
  private final EndpointMapper endpointMapper;
  private final MachineTagMapper machineTagMapper;
  private final IdentifierMapper identifierMapper;
  private final CommentMapper commentMapper;
  private final TagMapper tagMapper;

  @Inject
  public DatasetResource(
    DatasetMapper datasetMapper,
    ContactMapper contactMapper,
    EndpointMapper endpointMapper,
    MachineTagMapper machineTagMapper,
    TagMapper tagMapper,
    IdentifierMapper identifierMapper,
    CommentMapper commentMapper
  ) {
    super(datasetMapper);
    this.datasetMapper = datasetMapper;
    this.contactMapper = contactMapper;
    this.endpointMapper = endpointMapper;
    this.machineTagMapper = machineTagMapper;
    this.tagMapper = tagMapper;
    this.identifierMapper = identifierMapper;
    this.commentMapper = commentMapper;
  }

  @Validate
  @Transactional
  @Override
  public int addContact(UUID targetEntityKey, @NotNull @Valid @Trim Contact contact) {
    return WithMyBatis.addContact(contactMapper, datasetMapper, targetEntityKey, contact);
  }

  @Override
  public void deleteContact(UUID targetEntityKey, int contactKey) {
    WithMyBatis.deleteContact(datasetMapper, targetEntityKey, contactKey);
  }

  @Override
  public List<Contact> listContacts(UUID targetEntityKey) {
    return WithMyBatis.listContacts(datasetMapper, targetEntityKey);
  }

  @Validate
  @Transactional
  @Override
  public int addTag(UUID targetEntityKey, @NotNull @Size(min = 1) String value) {
    return WithMyBatis.addTag(tagMapper, datasetMapper, targetEntityKey, value);
  }

  @Override
  public void deleteTag(UUID taggedEntityKey, int tagKey) {
    WithMyBatis.deleteTag(datasetMapper, taggedEntityKey, tagKey);
  }

  @Override
  public List<Tag> listTags(UUID taggedEntityKey, String owner) {
    return WithMyBatis.listTags(datasetMapper, taggedEntityKey, owner);
  }

  @Validate
  @Transactional
  @Override
  public int addEndpoint(UUID targetEntityKey, @NotNull @Valid @Trim Endpoint endpoint) {
    return WithMyBatis.addEndpoint(endpointMapper, datasetMapper, targetEntityKey, endpoint);
  }

  @Override
  public void deleteEndpoint(UUID targetEntityKey, int endpointKey) {
    WithMyBatis.deleteEndpoint(datasetMapper, targetEntityKey, endpointKey);
  }

  @Override
  public List<Endpoint> listEndpoints(UUID targetEntityKey) {
    return WithMyBatis.listEndpoints(datasetMapper, targetEntityKey);
  }

  @Validate
  @Transactional
  @Override
  public int addMachineTag(UUID targetEntityKey, @NotNull @Valid @Trim MachineTag machineTag) {
    return WithMyBatis.addMachineTag(machineTagMapper, datasetMapper, targetEntityKey, machineTag);
  }

  @Override
  public void deleteMachineTag(UUID targetEntityKey, int machineTagKey) {
    WithMyBatis.deleteMachineTag(datasetMapper, targetEntityKey, machineTagKey);
  }

  @Override
  public List<MachineTag> listMachineTags(UUID targetEntityKey) {
    return WithMyBatis.listMachineTags(datasetMapper, targetEntityKey);
  }

  @Validate
  @Transactional
  @Override
  public int addIdentifier(UUID targetEntityKey, @NotNull @Valid @Trim Identifier identifier) {
    return WithMyBatis.addIdentifier(identifierMapper, datasetMapper, targetEntityKey, identifier);
  }

  @Override
  public void deleteIdentifier(UUID targetEntityKey, int identifierKey) {
    WithMyBatis.deleteIdentifier(datasetMapper, targetEntityKey, identifierKey);
  }

  @Override
  public List<Identifier> listIdentifiers(UUID targetEntityKey) {
    return WithMyBatis.listIdentifiers(datasetMapper, targetEntityKey);
  }

  @Validate
  @Transactional
  @Override
  public int addComment(UUID targetEntityKey, @NotNull @Valid @Trim Comment comment) {
    return WithMyBatis.addComment(commentMapper, datasetMapper, targetEntityKey, comment);
  }

  @Override
  public void deleteComment(UUID targetEntityKey, int commentKey) {
    WithMyBatis.deleteComment(datasetMapper, targetEntityKey, commentKey);
  }

  @Override
  public List<Comment> listComments(UUID targetEntityKey) {
    return WithMyBatis.listComments(datasetMapper, targetEntityKey);
  }

}
