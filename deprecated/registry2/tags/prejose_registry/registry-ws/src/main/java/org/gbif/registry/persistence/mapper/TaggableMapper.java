package org.gbif.registry.persistence.mapper;

import org.gbif.api.registry.model.Tag;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Param;


public interface TaggableMapper {

  int addTag(@Param("targetEntityKey") UUID entityKey, @Param("tagKey") int tagKey);

  int deleteTag(@Param("targetEntityKey") UUID entityKey, @Param("tagKey") int tagKey);

  List<Tag> listTags(@Param("targetEntityKey") UUID targetEntityKey);
}
