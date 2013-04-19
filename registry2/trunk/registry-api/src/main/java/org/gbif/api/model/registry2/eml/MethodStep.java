/*
 * Copyright 2012 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.api.model.registry2.eml;

import java.io.Serializable;

import com.google.common.base.Objects;


public class MethodStep implements Serializable {

  private static final long serialVersionUID = -8664488895612629327L;

  private String title;

  private String description;

  private String instrumentation;

  public MethodStep() {
  }

  public MethodStep(String title, String description, String instrumentation) {
    this.title = title;
    this.description = description;
    this.instrumentation = instrumentation;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getInstrumentation() {
    return instrumentation;
  }

  public void setInstrumentation(String instrumentation) {
    this.instrumentation = instrumentation;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof MethodStep)) {
      return false;
    }

    MethodStep that = (MethodStep) obj;
    return Objects.equal(this.title, that.title)
           && Objects.equal(this.description, that.description)
           && Objects.equal(this.instrumentation, that.instrumentation);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(title, description, instrumentation);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("title", title)
      .add("description", description)
      .add("instrumentation", instrumentation)
      .toString();
  }

}
