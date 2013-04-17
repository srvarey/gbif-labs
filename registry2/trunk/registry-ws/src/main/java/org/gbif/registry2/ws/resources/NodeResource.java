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
package org.gbif.registry2.ws.resources;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry2.Comment;
import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.MachineTag;
import org.gbif.api.model.registry2.Node;
import org.gbif.api.model.registry2.Organization;
import org.gbif.api.model.registry2.Tag;
import org.gbif.api.service.registry2.NodeService;
import org.gbif.registry2.persistence.WithMyBatis;
import org.gbif.registry2.persistence.mapper.CommentMapper;
import org.gbif.registry2.persistence.mapper.ContactMapper;
import org.gbif.registry2.persistence.mapper.MachineTagMapper;
import org.gbif.registry2.persistence.mapper.NodeMapper;
import org.gbif.registry2.persistence.mapper.OrganizationMapper;
import org.gbif.registry2.persistence.mapper.TagMapper;
import org.gbif.registry2.ws.guice.Trim;
import org.gbif.registry2.ws.resources.rest.AbstractNetworkEntityResource;
import org.gbif.registry2.ws.resources.rest.CommentRest;
import org.gbif.registry2.ws.resources.rest.ContactRest;
import org.gbif.registry2.ws.resources.rest.MachineTagRest;
import org.gbif.registry2.ws.resources.rest.TagRest;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.bval.guice.Validate;
import org.mybatis.guice.transactional.Transactional;

@Singleton
@Path("node")
public class NodeResource extends AbstractNetworkEntityResource<Node>
  implements NodeService, ContactRest, MachineTagRest, CommentRest, TagRest {

  private final NodeMapper nodeMapper;
  private final OrganizationMapper organizationMapper;
  private final ContactMapper contactMapper;
  private final MachineTagMapper machineTagMapper;
  private final TagMapper tagMapper;
  private final CommentMapper commentMapper;

  @Inject
  public NodeResource(
    NodeMapper nodeMapper,
    OrganizationMapper organizationMapper,
    ContactMapper contactMapper,
    MachineTagMapper machineTagMapper,
    TagMapper tagMapper,
    CommentMapper commentMapper
  ) {
    super(nodeMapper);
    this.nodeMapper = nodeMapper;
    this.organizationMapper = organizationMapper;
    this.contactMapper = contactMapper;
    this.machineTagMapper = machineTagMapper;
    this.tagMapper = tagMapper;
    this.commentMapper = commentMapper;
  }

  @GET
  @Path("{key}/organization")
  @Override
  public PagingResponse<Organization> organizationsEndorsedBy(@PathParam("key") UUID nodeKey, @Context Pageable page) {
    return new PagingResponse<Organization>(page, null, organizationMapper.organizationsEndorsedBy(nodeKey, page));
  }

  @GET
  @Path("pendingEndorsement")
  @Override
  public PagingResponse<Organization> pendingEndorsements(@Context Pageable page) {
    return new PagingResponse<Organization>(page, null, organizationMapper.pendingEndorsements(page));
  }

  @Validate
  @Transactional
  @Override
  public int addContact(UUID targetEntityKey, Contact contact) {
    return WithMyBatis.addContact(contactMapper, nodeMapper, targetEntityKey, contact);
  }

  @Override
  public void deleteContact(UUID targetEntityKey, int contactKey) {
    WithMyBatis.deleteContact(nodeMapper, targetEntityKey, contactKey);
  }

  @Override
  public List<Contact> listContacts(UUID targetEntityKey) {
    return WithMyBatis.listContacts(nodeMapper, targetEntityKey);
  }

  @Validate
  @Transactional
  @Override
  public int addMachineTag(UUID targetEntityKey, @NotNull @Valid @Trim MachineTag machineTag) {
    return WithMyBatis.addMachineTag(machineTagMapper, nodeMapper, targetEntityKey, machineTag);
  }

  @Override
  public void deleteMachineTag(UUID targetEntityKey, int machineTagKey) {
    WithMyBatis.deleteMachineTag(nodeMapper, targetEntityKey, machineTagKey);
  }

  @Override
  public List<MachineTag> listMachineTags(UUID targetEntityKey) {
    return WithMyBatis.listMachineTags(nodeMapper, targetEntityKey);
  }

  @Validate
  @Transactional
  @Override
  public int addTag(UUID targetEntityKey, @NotNull @Size(min = 1) String value) {
    return WithMyBatis.addTag(tagMapper, nodeMapper, targetEntityKey, value);
  }

  @Override
  public void deleteTag(UUID targetEntityKey, int tagKey) {
    WithMyBatis.deleteTag(nodeMapper, targetEntityKey, tagKey);
  }

  @Override
  public List<Tag> listTags(UUID targetEntityKey, String owner) {
    return WithMyBatis.listTags(nodeMapper, targetEntityKey, owner);
  }

  @Validate
  @Transactional
  @Override
  public int addComment(UUID targetEntityKey, @NotNull @Valid @Trim Comment comment) {
    return WithMyBatis.addComment(commentMapper, nodeMapper, targetEntityKey, comment);
  }

  @Override
  public void deleteComment(UUID targetEntityKey, int commentKey) {
    WithMyBatis.deleteComment(nodeMapper, targetEntityKey, commentKey);
  }

  @Override
  public List<Comment> listComments(UUID targetEntityKey) {
    return WithMyBatis.listComments(nodeMapper, targetEntityKey);
  }

}
