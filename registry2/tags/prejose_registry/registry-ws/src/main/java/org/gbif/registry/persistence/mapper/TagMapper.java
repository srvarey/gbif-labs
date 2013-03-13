package org.gbif.registry.persistence.mapper;

import org.gbif.api.registry.model.Tag;


public interface TagMapper {

  int createTag(Tag tag);
}
