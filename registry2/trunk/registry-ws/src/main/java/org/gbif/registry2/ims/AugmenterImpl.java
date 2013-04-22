package org.gbif.registry2.ims;

import org.gbif.api.model.registry2.Node;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AugmenterImpl implements Augmenter {
  private static Logger LOG = LoggerFactory.getLogger(AugmenterImpl.class);

  private ImsNodeMapper mapper;

  @Inject
  public AugmenterImpl(ImsNodeMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public Node augment(Node node) {
    if (node != null && node.getCountry() != null) {
      try {
        Node imsNode = mapper.get(node.getCountry());
        if (imsNode != null) {
          // update node with IMS info if it exists
          node.setContacts(imsNode.getContacts());
          node.setDescription(imsNode.getDescription());
          node.setParticipantSince(imsNode.getParticipantSince());
          node.setAddress(imsNode.getAddress());
          node.setPostalCode(imsNode.getPostalCode());
          node.setCity(imsNode.getCity());
          node.setProvince(imsNode.getProvince());
          node.setEmail(imsNode.getEmail());
          node.setPhone(imsNode.getPhone());
          node.setHomepage(imsNode.getHomepage());
          // registry info takes precedence, don't update
          node.setTitle(imsNode.getTitle());
          node.setGbifRegion(imsNode.getGbifRegion());
          node.setContinent(imsNode.getContinent());
          node.setParticipationStatus(imsNode.getParticipationStatus());
        }
      } catch (Exception e) {
        LOG.error("Failed to augment node %s with IMS information", node.getKey(), e);
      }
    }

    return node;
  }
}
