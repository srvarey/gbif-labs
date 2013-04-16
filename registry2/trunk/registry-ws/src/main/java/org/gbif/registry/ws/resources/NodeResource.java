package org.gbif.registry.ws.resources;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.registry.model.Comment;
import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.model.MachineTag;
import org.gbif.api.registry.model.Node;
import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.model.Tag;
import org.gbif.api.registry.service.NodeService;
import org.gbif.registry.persistence.WithMyBatis;
import org.gbif.registry.persistence.mapper.CommentMapper;
import org.gbif.registry.persistence.mapper.ContactMapper;
import org.gbif.registry.persistence.mapper.MachineTagMapper;
import org.gbif.registry.persistence.mapper.NodeMapper;
import org.gbif.registry.persistence.mapper.OrganizationMapper;
import org.gbif.registry.persistence.mapper.TagMapper;
import org.gbif.registry.ws.guice.Trim;
import org.gbif.registry.ws.resources.rest.AbstractNetworkEntityResource;
import org.gbif.registry.ws.resources.rest.CommentRest;
import org.gbif.registry.ws.resources.rest.ContactRest;
import org.gbif.registry.ws.resources.rest.MachineTagRest;
import org.gbif.registry.ws.resources.rest.TagRest;

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
public class NodeResource extends AbstractNetworkEntityResource<Node> implements NodeService, ContactRest,
  MachineTagRest, CommentRest, TagRest {

  private final NodeMapper nodeMapper;
  private final OrganizationMapper organizationMapper;
  private final ContactMapper contactMapper;
  private final MachineTagMapper machineTagMapper;
  private final TagMapper tagMapper;
  private final CommentMapper commentMapper;

  @Inject
  public NodeResource(NodeMapper nodeMapper, OrganizationMapper organizationMapper, ContactMapper contactMapper,
    MachineTagMapper machineTagMapper, TagMapper tagMapper, CommentMapper commentMapper) {
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
