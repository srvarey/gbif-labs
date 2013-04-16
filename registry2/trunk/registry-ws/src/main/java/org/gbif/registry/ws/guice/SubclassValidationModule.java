package org.gbif.registry.ws.guice;

import javax.validation.MessageInterpolator;
import javax.validation.TraversableResolver;
import javax.validation.spi.ConfigurationState;
import javax.validation.spi.ValidationProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import org.aopalliance.intercept.MethodInterceptor;
import org.apache.bval.guice.ConfigurationStateProvider;
import org.apache.bval.guice.Validate;
import org.apache.bval.guice.ValidateMethodInterceptor;
import org.apache.bval.jsr303.ApacheValidationProvider;
import org.apache.bval.jsr303.DefaultMessageInterpolator;
import org.apache.bval.jsr303.resolver.DefaultTraversableResolver;


public class SubclassValidationModule extends AbstractModule {

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configure() {
    // apache bval bootstrap
    this.bind(MessageInterpolator.class).to(DefaultMessageInterpolator.class).in(Scopes.SINGLETON);
    this.bind(TraversableResolver.class).to(DefaultTraversableResolver.class).in(Scopes.SINGLETON);
    // this.bind(ConstraintValidatorFactory.class).to(GuiceAwareConstraintValidatorFactory.class);
    this.bind(new TypeLiteral<ValidationProvider<?>>() {
    }).to(ApacheValidationProvider.class).in(Scopes.SINGLETON);
    this.bind(ConfigurationState.class).toProvider(ConfigurationStateProvider.class).in(Scopes.SINGLETON);
    // this.bind(ValidatorFactory.class).toProvider(ValidatorFactoryProvider.class).in(Scopes.SINGLETON);
    // this.bind(Validator.class).toProvider(ValidatorProvider.class);

    // AOP stuff
    MethodInterceptor validateMethodInterceptor = new ValidateMethodInterceptor();
    this.binder().requestInjection(validateMethodInterceptor);
    this.bindInterceptor(Matchers.any(), Matchers.annotatedWith(Validate.class), validateMethodInterceptor);
  }

}