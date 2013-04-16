package org.gbif.api.registry.model;

import java.util.Date;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * This interface provides a minimal contract that all network entities (The readable version) will adhere to. It is
 * used <em>only</em> to simplify consistent testing of operations on network entities, hence the restriction to package
 * visibility only.
 */
public interface NetworkEntity {

  @NotNull
  UUID getKey();

  void setKey(UUID key);

  @NotNull
  @Size(min = 2, max = 255)
  String getTitle();

  void setTitle(String title);

  @Nullable
  @Size(min = 10)
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
