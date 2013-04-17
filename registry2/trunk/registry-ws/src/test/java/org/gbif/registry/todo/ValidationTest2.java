/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
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
package org.gbif.registry.todo;

import org.gbif.api.registry.model.Node;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.bval.jsr303.ApacheValidationProvider;


public class ValidationTest2 {

  public static void main(String[] args) {

    ValidatorFactory validatorFactory =
      Validation.byProvider(ApacheValidationProvider.class).configure().buildValidatorFactory();

    Validator validator = validatorFactory.getValidator();

    Node n = new Node();
    n.setTitle("1");
    Set<ConstraintViolation<Node>> violations = validator.validate(n);
    for (ConstraintViolation<?> cv : violations) {
      System.out.println(cv.getPropertyPath());
      System.out.println(cv.getMessage());
      System.out.println(cv.getMessageTemplate());
    }
  }
}
