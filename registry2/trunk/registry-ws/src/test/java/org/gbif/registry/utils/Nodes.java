package org.gbif.registry.utils;

import org.gbif.api.registry.model.Node;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.bval.jsr303.ApacheValidationProvider;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;


public class Nodes extends JsonBackedData<Node> {

  private static final Nodes INSTANCE = new Nodes();

  public Nodes() {
    super("data/node.json", new TypeReference<Node>() {
    });
  }

  public static Node newInstance() {
    Node n = INSTANCE.newTypedInstance();
    return n;
  }

  @Test
  public void testConstraints() {
    ValidatorFactory validatorFactory =
      Validation.byProvider(ApacheValidationProvider.class).configure().buildValidatorFactory();
    Validator validator = validatorFactory.getValidator();

    Set<ConstraintViolation<Node>> violations = validator.validate(Nodes.newInstance());
    for (ConstraintViolation<?> cv : violations) {
      System.out.println(cv.getPropertyPath());
      System.out.println(cv.getMessage());
      System.out.println(cv.getMessageTemplate());
    }

  }
}
