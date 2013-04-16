package org.gbif.registry.ws.guice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import com.google.inject.matcher.Matcher;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class AnnotationUtilsTest {

  // Sample annotation
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.PARAMETER, ElementType.METHOD})
  private @interface DoIt {
  }

  // Interface with annotation
  private interface Iface {

    @DoIt
    void go();
  }

  private static class Impl implements Iface {

    @Override
    public void go() {
      // Note that I have no annotation
    }

    @SuppressWarnings("unused")
    @DoIt
    public void annotatedGo() {
      // Note that I am annotated
    }
  }


  @Test
  public void testAnnotations() {
    Matcher<Method> matcher = AnnotationMatchers.annotationInHierarchy(DoIt.class);
    try {
      assertTrue("Matcher not detecting that go() is annotated",
        matcher.matches(Impl.class.getMethod("go")));
      assertTrue("Matcher not detecting that annotatedGo() is annotated",
        matcher.matches(Impl.class.getMethod("annotatedGo")));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

}
