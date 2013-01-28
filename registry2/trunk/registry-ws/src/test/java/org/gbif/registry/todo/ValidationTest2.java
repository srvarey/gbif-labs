package org.gbif.registry.todo;

import org.gbif.api.registry.model.WritableNode;

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

    WritableNode n = new WritableNode();
    n.setTitle("1");
    Set<ConstraintViolation<WritableNode>> violations = validator.validate(n);
    for (ConstraintViolation<?> cv : violations) {
      System.out.println(cv.getPropertyPath());
      System.out.println(cv.getMessage());
      System.out.println(cv.getMessageTemplate());
    }
  }
}
