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
package org.gbif.registry2.ws.resources;

import org.gbif.api.model.registry2.Contact;
import org.gbif.api.model.registry2.Endpoint;
import org.gbif.api.model.registry2.NetworkEntity;
import org.gbif.api.service.registry2.ContactService;
import org.gbif.api.service.registry2.EndpointService;
import org.gbif.registry2.persistence.WithMyBatis;
import org.gbif.registry2.persistence.mapper.BaseNetworkEntityMapper2;
import org.gbif.registry2.persistence.mapper.CommentMapper;
import org.gbif.registry2.persistence.mapper.ContactMapper;
import org.gbif.registry2.persistence.mapper.EndpointMapper;
import org.gbif.registry2.persistence.mapper.MachineTagMapper;
import org.gbif.registry2.persistence.mapper.TagMapper;
import org.gbif.registry2.ws.guice.Trim;
import org.gbif.ws.util.ExtraMediaTypes;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.common.eventbus.EventBus;
import org.apache.bval.guice.Validate;
import org.mybatis.guice.transactional.Transactional;

/**
 * Provides a skeleton implementation of the following:
 * <ul>
 * <li>Base CRUD operations for a network entity</li>
 * <li>Comment operations</li>
 * <li>Contact operations (in addition to BaseNetworkEntityResource)</li>
 * <li>Endpoint operations (in addition to BaseNetworkEntityResource)</li>
 * <li>MachineTag operations</li>
 * <li>Tag operations</li>
 * </ul>
 * 
 * @param <T> The type of resource that is under CRUD
 */
@Produces({MediaType.APPLICATION_JSON, ExtraMediaTypes.APPLICATION_JAVASCRIPT})
@Consumes(MediaType.APPLICATION_JSON)
public class BaseNetworkEntityResource2<T extends NetworkEntity> extends BaseNetworkEntityResource<T>
  implements ContactService, EndpointService {

  private final BaseNetworkEntityMapper2<T> mapper;
  private final ContactMapper contactMapper;
  private final EndpointMapper endpointMapper;

  protected BaseNetworkEntityResource2(
    BaseNetworkEntityMapper2<T> mapper,
    CommentMapper commentMapper,
    ContactMapper contactMapper,
    EndpointMapper endpointMapper,
    MachineTagMapper machineTagMapper,
    TagMapper tagMapper,
    Class<T> objectClass,
    EventBus eventBus) {
    super(mapper, commentMapper, machineTagMapper, tagMapper, objectClass, eventBus);
    this.mapper = mapper;
    this.contactMapper = contactMapper;
    this.endpointMapper = endpointMapper;
  }

  @POST
  @Path("{key}/contact")
  @Validate
  @Transactional
  @Override
  public int addContact(@PathParam("key") UUID targetEntityKey, @NotNull @Valid @Trim Contact contact) {
    return WithMyBatis.addContact(contactMapper, mapper, targetEntityKey, contact);
  }

  // relax content-type to wildcard to allow angularjs
  @DELETE
  @Path("{key}/contact/{contactKey}")
  @Override
  @Consumes({MediaType.WILDCARD})
  public void deleteContact(@PathParam("key") UUID targetEntityKey, @PathParam("contactKey") int contactKey) {
    WithMyBatis.deleteContact(mapper, targetEntityKey, contactKey);
  }

  @GET
  @Path("{key}/contact")
  @Override
  public List<Contact> listContacts(@PathParam("key") UUID targetEntityKey) {
    return WithMyBatis.listContacts(mapper, targetEntityKey);
  }

  @POST
  @Path("{key}/endpoint")
  @Validate
  @Transactional
  @Override
  public int addEndpoint(@PathParam("key") UUID targetEntityKey, @NotNull @Valid @Trim Endpoint endpoint) {
    return WithMyBatis.addEndpoint(endpointMapper, mapper, targetEntityKey, endpoint);
  }

  // relax content-type to wildcard to allow angularjs
  @DELETE
  @Path("{key}/endpoint/{endpointKey}")
  @Override
  @Consumes({MediaType.WILDCARD})
  public void deleteEndpoint(@PathParam("key") UUID targetEntityKey, @PathParam("endpointKey") int endpointKey) {
    WithMyBatis.deleteEndpoint(mapper, targetEntityKey, endpointKey);
  }

  @GET
  @Path("{key}/endpoint")
  @Override
  public List<Endpoint> listEndpoints(@PathParam("key") UUID targetEntityKey) {
    return WithMyBatis.listEndpoints(mapper, targetEntityKey);
  }

}
