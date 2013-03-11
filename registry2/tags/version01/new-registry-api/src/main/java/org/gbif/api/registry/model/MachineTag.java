package org.gbif.api.registry.model;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.common.base.Objects;


public class MachineTag {

  private Integer key;
  private String namespace;
  private String name;
  private String value;
  private String createdBy;
  private Date created;

  @NotNull
  public Integer getKey() {
    return key;
  }

  public void setKey(Integer key) {
    this.key = key;
  }

  @NotNull
  @Size(min = 1, max = 255)
  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  @NotNull
  @Size(min = 1, max = 255)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @NotNull
  @Size(min = 1, max = 255)
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @NotNull
  @Size(min = 3)
  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  @NotNull
  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("key", key).add("namespace", namespace).add("name", name)
      .add("value", value).add("createdBy", createdBy).add("created", created).toString();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key, namespace, name, value, createdBy, created);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof MachineTag) {
      MachineTag that = (MachineTag) object;
      return Objects.equal(this.key, that.key) && Objects.equal(this.namespace, that.namespace)
        && Objects.equal(this.name, that.name) && Objects.equal(this.value, that.value)
        && Objects.equal(this.createdBy, that.createdBy) && Objects.equal(this.created, that.created);
    }
    return false;
  }

}
