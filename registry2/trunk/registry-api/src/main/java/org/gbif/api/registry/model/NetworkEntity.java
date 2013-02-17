package org.gbif.api.registry.model;

import java.util.Date;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * This interface provides a minimal contract that all network entities (The readable version) will adhere to. It is
 * used <em>only</em> to simplify consistent testing of operations on network entities, hence the restriction to package
 * visibility only.
 */
public interface NetworkEntity {

  @Nullable
  public UUID getKey();

  public void setKey(UUID key);

  @Nullable
  String getTitle();

  void setTitle(String title);

  @Nullable
  String getDescription();

  void setDescription(String description);

  @NotNull
  Date getCreated();

  void setCreated(Date created);

  @NotNull
  Date getModified();

  void setModified(Date modified);

  @Nullable
  Date getDeleted();

  void setDeleted(Date deleted);
}
