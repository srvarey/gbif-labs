package org.gbif.api.registry.model;


import org.gbif.api.vocabulary.Language;

import java.util.UUID;

/**
 * 
 */
public interface WritableNetworkEntity {

  public UUID getKey();

  public void setKey(UUID key);

  String getTitle();

  void setTitle(String title);

  String getAlias();

  void setAlias(String alias);

  String getDescription();

  void setDescription(String String);

  Language getLanguage();

  void setLanguage(Language language);
}
