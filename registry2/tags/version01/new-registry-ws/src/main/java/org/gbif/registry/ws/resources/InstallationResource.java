package org.gbif.registry.ws.resources;

import org.gbif.api.registry.model.Comment;
import org.gbif.api.registry.model.Contact;
import org.gbif.api.registry.model.Endpoint;
import org.gbif.api.registry.model.Installation;
import org.gbif.api.registry.model.MachineTag;
import org.gbif.api.registry.model.Tag;
import org.gbif.api.registry.service.InstallationService;
import org.gbif.registry.persistence.WithMyBatis;
import org.gbif.registry.persistence.mapper.CommentMapper;
import org.gbif.registry.persistence.mapper.ContactMapper;
import org.gbif.registry.persistence.mapper.EndpointMapper;
import org.gbif.registry.persistence.mapper.InstallationMapper;
import org.gbif.registry.persistence.mapper.MachineTagMapper;
import org.gbif.registry.persistence.mapper.TagMapper;
import org.gbif.registry.ws.resources.rest.AbstractNetworkEntityResource;
import org.gbif.registry.ws.resources.rest.CommentRest;
import org.gbif.registry.ws.resources.rest.ContactRest;
import org.gbif.registry.ws.resources.rest.EndpointRest;
import org.gbif.registry.ws.resources.rest.MachineTagRest;
import org.gbif.registry.ws.resources.rest.TagRest;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * A MyBATIS implementation of the service.
 */
@Path("installation")
@Singleton
public class InstallationResource extends AbstractNetworkEntityResource<Installation> implements InstallationService,
  ContactRest, EndpointRest, MachineTagRest, CommentRest, TagRest {

  private final InstallationMapper installationMapper;
  private final ContactMapper contactMapper;
  private final EndpointMapper endpointMapper;
  private final MachineTagMapper machineTagMapper;
  private final TagMapper tagMapper;
  private final CommentMapper commentMapper;

  @Inject
  public InstallationResource(InstallationMapper installationMapper, ContactMapper contactMapper,
    EndpointMapper endpointMapper, MachineTagMapper machineTagMapper, TagMapper tagMapper, CommentMapper commentMapper) {
    super(installationMapper);
    this.installationMapper = installationMapper;
    this.contactMapper = contactMapper;
    this.endpointMapper = endpointMapper;
    this.machineTagMapper = machineTagMapper;
    this.tagMapper = tagMapper;
    this.commentMapper = commentMapper;
  }

  // CONTACTS
  @Override
  public int addContact(UUID targetEntityKey, Contact contact) {
    return WithMyBatis.addContact(contactMapper, installationMapper, targetEntityKey, contact);
  }

  @Override
  public void deleteContact(UUID targetEntityKey, int contactKey) {
    WithMyBatis.deleteContact(installationMapper, targetEntityKey, contactKey);
  }

  @Override
  public List<Contact> listContacts(UUID targetEntityKey) {
    return WithMyBatis.listContacts(installationMapper, targetEntityKey);
  }

  // ENDPOINTS
  @Override
  public int addEndpoint(UUID targetEntityKey, Endpoint endpoint) {
    return WithMyBatis.addEndpoint(endpointMapper, installationMapper, targetEntityKey, endpoint);
  }

  @Override
  public void deleteEndpoint(UUID targetEntityKey, int endpointKey) {
    WithMyBatis.deleteEndpoint(installationMapper, targetEntityKey, endpointKey);
  }

  @Override
  public List<Endpoint> listEndpoints(UUID targetEntityKey) {
    return WithMyBatis.listEndpoints(installationMapper, targetEntityKey);
  }

  // MACHINE TAGS
  @Override
  public int addMachineTag(UUID targetEntityKey, MachineTag machineTag) {
    return WithMyBatis.addMachineTag(machineTagMapper, installationMapper, targetEntityKey, machineTag);
  }

  @Override
  public void deleteMachineTag(UUID targetEntityKey, int machineTagKey) {
    WithMyBatis.deleteMachineTag(installationMapper, targetEntityKey, machineTagKey);
  }

  @Override
  public List<MachineTag> listMachineTags(UUID targetEntityKey) {
    return WithMyBatis.listMachineTags(installationMapper, targetEntityKey);
  }

  // TAGS
  @Override
  public int addTag(UUID targetEntityKey, String value) {
    return WithMyBatis.addTag(tagMapper, installationMapper, targetEntityKey, value);
  }

  @Override
  public void deleteTag(UUID targetEntityKey, int tagKey) {
    WithMyBatis.deleteTag(installationMapper, targetEntityKey, tagKey);
  }

  @Override
  public List<Tag> listTags(UUID targetEntityKey, String owner) {
    return WithMyBatis.listTags(installationMapper, targetEntityKey, owner);
  }

  // COMMENTS
  @Override
  public int addComment(UUID targetEntityKey, Comment comment) {
    return WithMyBatis.addComment(commentMapper, installationMapper, targetEntityKey, comment);
  }

  @Override
  public void deleteComment(UUID targetEntityKey, int commentKey) {
    WithMyBatis.deleteComment(installationMapper, targetEntityKey, commentKey);
  }

  @Override
  public List<Comment> listComments(UUID targetEntityKey) {
    return WithMyBatis.listComments(installationMapper, targetEntityKey);
  }
}
