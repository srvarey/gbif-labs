package org.gbif.registry2.ims;

import org.gbif.api.model.registry2.Node;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AugmenterImpl implements Augmenter {

  private NodeMapper mapper;

  @Inject
  public AugmenterImpl(NodeMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public Node augment(Node node) {
    if (node != null && node.getCountry() != null) {
      Node imsNode = mapper.get(node.getCountry());
      if (imsNode != null) {
        // update node with IMS info if it exists
        node.setTitle(imsNode.getTitle());
        node.setContacts(imsNode.getContacts());
        node.setDescription(imsNode.getDescription());
        node.setParticipantSince(imsNode.getParticipantSince());
        node.setParticipationStatus(imsNode.getParticipationStatus());
        node.setAddress(imsNode.getAddress());
        node.setPostalCode(imsNode.getPostalCode());
        node.setCity(imsNode.getCity());
        node.setProvince(imsNode.getProvince());
        node.setEmail(imsNode.getEmail());
        node.setPhone(imsNode.getPhone());
        node.setGbifRegion(imsNode.getGbifRegion());
        node.setContinent(imsNode.getContinent());
        node.setHomepage(imsNode.getHomepage());
      }
    }
    return node;
  }
}
