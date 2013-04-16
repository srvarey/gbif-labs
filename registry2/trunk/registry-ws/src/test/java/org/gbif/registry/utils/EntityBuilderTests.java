package org.gbif.registry.utils;

import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.bval.jsr303.ApacheValidationProvider;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests that the builders in this package provide valid objects.
 */
public class EntityBuilderTests {

  private final static Logger LOG = LoggerFactory.getLogger(EntityBuilderTests.class);

  @Test
  public void testBuilders() {
    test(Comments.newInstance());
    test(Contacts.newInstance());
    test(Datasets.newInstance(UUID.randomUUID()));
    test(Endpoints.newInstance());
    test(Identifiers.newInstance());
    test(Installations.newInstance(UUID.randomUUID()));
    test(MachineTags.newInstance());
    test(Networks.newInstance());
    test(Nodes.newInstance());
    test(Organizations.newInstance(UUID.randomUUID()));
  }

  private <T> void test(T entity) {
    ValidatorFactory validatorFactory =
      Validation.byProvider(ApacheValidationProvider.class).configure().buildValidatorFactory();
    Validator validator = validatorFactory.getValidator();

    Set<ConstraintViolation<T>> violations = validator.validate(entity);
    for (ConstraintViolation<T> cv : violations) {
      LOG.info("Class[{}] property[{}] failed validation with[{}]", new String[] {entity.getClass().getSimpleName(),
        String.valueOf(cv.getPropertyPath()), cv.getMessage()});
    }
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}
