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
package org.gbif.registry2.ws.guice;

import java.lang.annotation.Annotation;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.matcher.Matchers;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.bval.guice.Validate;
import org.apache.bval.guice.ValidationModule;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.WrapDynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An interceptor that will trim all possible strings of a bean.
 * Nested properties are not handled, only top level string properties.
 */
public class StringTrimInterceptor implements MethodInterceptor {

  private static final Logger LOG = LoggerFactory.getLogger(StringTrimInterceptor.class);

  /**
   * Sets up method level interception for those methods annotated with {@link Trim}.
   * Normally this would go before any {@link ValidationModule}, so Strings are trimmed before validated.
   */
  public static Module newMethodInterceptingModule() {
    return new AbstractModule() {

      @Override
      protected void configure() {
        MethodInterceptor trimMethodInterceptor = new StringTrimInterceptor();
        this.binder().requestInjection(trimMethodInterceptor);
        this.bindInterceptor(Matchers.any(), Matchers.annotatedWith(Trim.class), trimMethodInterceptor);
      }
    };
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Validate validate = invocation.getMethod().getAnnotation(Validate.class); // ensure it is annotated
    if (validate != null) {
      Annotation[][] paramAnnotations = invocation.getMethod().getParameterAnnotations();
      for (int i = 0; i < paramAnnotations.length; i++) {
        for (Annotation a : paramAnnotations[i]) {
          if (Trim.class.isAssignableFrom(a.annotationType())) {
            trimStringsOf(invocation.getArguments()[i]);
          }
        }
      }
    }
    return invocation.proceed();
  }

  private void trimStringsOf(Object target) {
    WrapDynaBean wrapped = new WrapDynaBean(target);
    DynaClass dynaClass = wrapped.getDynaClass();
    for (DynaProperty dynaProp : dynaClass.getDynaProperties()) {
      // Only operate on strings
      if (String.class.isAssignableFrom(dynaProp.getType())) {
        String prop = dynaProp.getName();
        String orig = (String) wrapped.get(prop);
        if (orig != null) {
          String trimmed = Strings.emptyToNull(orig.trim());
          if (!Objects.equal(orig, trimmed)) {
            LOG.debug("Overriding value of [{}] from [{}] with [{}]", prop, orig, trimmed);
            wrapped.set(prop, trimmed);
          }
        }
      }
    }
  }

}
