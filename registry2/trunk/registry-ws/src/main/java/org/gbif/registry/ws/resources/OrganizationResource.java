package org.gbif.registry.ws.resources;

import org.gbif.api.registry.model.Comment;
import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.model.Endpoint;
import org.gbif.api.registry.model.Identifier;
import org.gbif.api.registry.model.MachineTag;
import org.gbif.api.registry.model.Organization;
import org.gbif.api.registry.model.Tag;
import org.gbif.api.registry.service.OrganizationService;
import org.gbif.registry.persistence.WithMyBatis;
import org.gbif.registry.persistence.mapper.CommentMapper;
import org.gbif.registry.persistence.mapper.ContactMapper;
import org.gbif.registry.persistence.mapper.EndpointMapper;
import org.gbif.registry.persistence.mapper.IdentifierMapper;
import org.gbif.registry.persistence.mapper.MachineTagMapper;
import org.gbif.registry.persistence.mapper.OrganizationMapper;
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
@Path("organization")
@Singleton
public class OrganizationResource extends AbstractNetworkEntityResource<Organization> implements OrganizationService,
  ContactRest, EndpointRest, MachineTagRest, TagRest, IdentifierRest, CommentRest {

  private final OrganizationMapper organizationMapper;
  private final ContactMapper contactMapper;
  private final EndpointMapper endpointMapper;
  private final MachineTagMapper machineTagMapper;
  private final TagMapper tagMapper;
  private final IdentifierMapper identifierMapper;
  private final CommentMapper commentMapper;

  @Inject
  public OrganizationResource(OrganizationMapper organizationMapper, ContactMapper contactMapper,
    EndpointMapper endpointMapper, MachineTagMapper machineTagMapper, TagMapper tagMapper,
    IdentifierMapper identifierMapper, CommentMapper commentMapper) {
    super(organizationMapper);
    this.organizationMapper = organizationMapper;
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
    return WithMyBatis.addContact(contactMapper, organizationMapper, targetEntityKey, contact);
  }

  @Override
  public void deleteContact(UUID targetEntityKey, int contactKey) {
    WithMyBatis.deleteContact(organizationMapper, targetEntityKey, contactKey);
  }

  @Override
  public List<Contact> listContacts(UUID targetEntityKey) {
    return WithMyBatis.listContacts(organizationMapper, targetEntityKey);
  }

  @Validate
  @Transactional
  @Override
  public int addEndpoint(UUID targetEntityKey, @NotNull @Valid @Trim Endpoint endpoint) {
    return WithMyBatis.addEndpoint(endpointMapper, organizationMapper, targetEntityKey, endpoint);
  }

  @Override
  public void deleteEndpoint(UUID targetEntityKey, int endpointKey) {
    WithMyBatis.deleteEndpoint(organizationMapper, targetEntityKey, endpointKey);
  }

  @Override
  public List<Endpoint> listEndpoints(UUID targetEntityKey) {
    return WithMyBatis.listEndpoints(organizationMapper, targetEntityKey);
  }

  @Validate
  @Transactional
  @Override
  public int addMachineTag(UUID targetEntityKey, @NotNull @Valid @Trim MachineTag machineTag) {
    return WithMyBatis.addMachineTag(machineTagMapper, organizationMapper, targetEntityKey, machineTag);
  }

  @Override
  public void deleteMachineTag(UUID targetEntityKey, int machineTagKey) {
    WithMyBatis.deleteMachineTag(organizationMapper, targetEntityKey, machineTagKey);
  }

  @Override
  public List<MachineTag> listMachineTags(UUID targetEntityKey) {
    return WithMyBatis.listMachineTags(organizationMapper, targetEntityKey);
  }

  @Validate
  @Transactional
  @Override
  public int addTag(UUID targetEntityKey, @NotNull @Size(min = 1) String value) {
    return WithMyBatis.addTag(tagMapper, organizationMapper, targetEntityKey, value);
  }

  @Override
  public void deleteTag(UUID targetEntityKey, int tagKey) {
    WithMyBatis.deleteTag(organizationMapper, targetEntityKey, tagKey);
  }

  @Override
  public List<Tag> listTags(UUID targetEntityKey, String owner) {
    return WithMyBatis.listTags(organizationMapper, targetEntityKey, owner);
  }

  @Validate
  @Transactional
  @Override
  public int addIdentifier(UUID targetEntityKey, @NotNull @Valid @Trim Identifier identifier) {
    return WithMyBatis.addIdentifier(identifierMapper, organizationMapper, targetEntityKey, identifier);
  }

  @Override
  public void deleteIdentifier(UUID targetEntityKey, int identifierKey) {
    WithMyBatis.deleteIdentifier(organizationMapper, targetEntityKey, identifierKey);
  }

  @Override
  public List<Identifier> listIdentifiers(UUID targetEntityKey) {
    return WithMyBatis.listIdentifiers(organizationMapper, targetEntityKey);
  }

  @Validate
  @Transactional
  @Override
  public int addComment(UUID targetEntityKey, @NotNull @Valid @Trim Comment comment) {
    return WithMyBatis.addComment(commentMapper, organizationMapper, targetEntityKey, comment);
  }

  @Override
  public void deleteComment(UUID targetEntityKey, int commentKey) {
    WithMyBatis.deleteComment(organizationMapper, targetEntityKey, commentKey);
  }

  @Override
  public List<Comment> listComments(UUID targetEntityKey) {
    return WithMyBatis.listComments(organizationMapper, targetEntityKey);
  }
}
