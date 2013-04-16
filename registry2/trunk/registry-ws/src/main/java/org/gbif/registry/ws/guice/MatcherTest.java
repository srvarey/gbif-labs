package org.gbif.registry.ws.guice;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;


public class MatcherTest {

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.PARAMETER, ElementType.METHOD})
  @interface DoIt {
  }


  interface I {

    @DoIt
    public void get();
  }

  static class C implements I {

    @Override
    public void get() {
    }
  }

  public static void main(String[] args) {
    Matcher<AnnotatedElement> m = Matchers.annotatedWith(DoIt.class);
    C instance = new C();

    for (Method method : (((I) instance).getClass()).getMethods()) {
      // System.out.println(method.getName() + ":");

      System.out.println(method.getName() + ": " + (AnnotationUtils.findAnnotation(method, DoIt.class) != null));

      for (Annotation a : method.getAnnotations()) {
        System.out.println(method.getName() + ": " + a.annotationType());
      }

      if (m.matches(method)) {
        System.out.println(method.getName() + ": " + m.matches(method));
      }

    }


  }
}
