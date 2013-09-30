package org.gbif.api.registry.service;

import org.gbif.api.registry.model.Tag;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;


public interface TagService {

  int addTag(@NotNull UUID targetEntityKey, String value);

  void deleteTag(@NotNull UUID taggedEntityKey, @NotNull int tagKey);

  List<Tag> listTags(@NotNull UUID taggedEntityKey, @Nullable String owner);
}
