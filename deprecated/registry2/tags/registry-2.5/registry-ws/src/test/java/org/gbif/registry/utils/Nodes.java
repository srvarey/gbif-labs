/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry.utils;

import org.gbif.api.model.registry.Node;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.bval.jsr303.ApacheValidationProvider;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class Nodes extends JsonBackedData<Node> {

  private static final Nodes INSTANCE = new Nodes();

  public static Node newInstance() {
    return INSTANCE.newTypedInstance();
  }

  public Nodes() {
    super("data/node.json", new TypeReference<Node>() {
    });
  }

  @Test
  public void testConstraints() {
    ValidatorFactory validatorFactory =
      Validation.byProvider(ApacheValidationProvider.class).configure().buildValidatorFactory();
    Validator validator = validatorFactory.getValidator();

    Set<ConstraintViolation<Node>> violations = validator.validate(Nodes.newInstance());
    assertTrue(violations.isEmpty());
  }

}
