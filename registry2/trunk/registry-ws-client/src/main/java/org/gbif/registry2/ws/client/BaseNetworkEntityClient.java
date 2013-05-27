package org.gbif.registry2.ws.client;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;
import org.gbif.api.model.registry2.Comment;
import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.Endpoint;
import org.gbif.api.model.registry2.Identifier;
import org.gbif.api.model.registry2.MachineTag;
import org.gbif.api.model.registry2.NetworkEntity;
import org.gbif.api.model.registry2.Tag;
import org.gbif.api.service.registry2.CommentService;
import org.gbif.api.service.registry2.ContactService;
import org.gbif.api.service.registry2.EndpointService;
import org.gbif.api.service.registry2.IdentifierService;
import org.gbif.api.service.registry2.MachineTagService;
import org.gbif.api.service.registry2.NetworkEntityService;
import org.gbif.api.service.registry2.TagService;
import org.gbif.ws.client.BaseWsGetClient;
import org.gbif.ws.client.QueryParamBuilder;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;

public class BaseNetworkEntityClient<T extends NetworkEntity> extends BaseWsGetClient<T, UUID>
  implements NetworkEntityService<T>,
  CommentService, MachineTagService, TagService, ContactService, EndpointService, IdentifierService {

  private final GenericType<PagingResponse<T>> PAGING_TYPE;

  public BaseNetworkEntityClient(Class<T> resourceClass, WebResource resource, @Nullable ClientFilter authFilter,
    GenericType<PagingResponse<T>> PAGING_TYPE) {
    super(resourceClass, resource, authFilter);
    this.PAGING_TYPE = PAGING_TYPE;
  }

  @Override
  public UUID create(T entity) {
    return post(UUID.class, entity, "/");
  }

  @Override
  public void delete(UUID key) {
    delete(key.toString());
  }

  @Override
  public PagingResponse<T> list(Pageable page) {
    return get(PAGING_TYPE, null, null, page);
  }

  @Override
  public void update(T entity) {
    put(entity, entity.getKey().toString());
  }

  @Override
  public T get(UUID key) {
    return get(key.toString());
  }

  @Override
  public PagingResponse<T> search(String query, Pageable page) {
    return get(PAGING_TYPE, (Locale) null, QueryParamBuilder.create("q", query).build(), page);
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
}
