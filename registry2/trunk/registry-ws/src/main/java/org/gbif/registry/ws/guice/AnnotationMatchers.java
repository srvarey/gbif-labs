package org.gbif.registry.ws.guice;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;

/**
 * Matchers suitable for use in Guice that inspect the class hierarchies to determine if an annotation is present.
 * Unlike Spring, Guice will only observe annotations that are declared on the implementation, when using
 * Matchers.annotatedWith(...). This class borrows methods from Spring to make it possible to intercept methods when the
 * annotations are declared on the interface.
 */
public class AnnotationMatchers {

  private static final Map<Class<?>, Boolean> annotatedInterfaceCache = new WeakHashMap<Class<?>, Boolean>();

  public static Matcher<Method> annotationInHierarchy(final Class<? extends Annotation> annotationType) {
    return new AnnotationMatcher(annotationType);
  }

  static class AnnotationMatcher extends AbstractMatcher<Method> {

    private final Class<? extends Annotation> annotationType;

    public AnnotationMatcher(Class<? extends Annotation> annotationType) {
      this.annotationType = annotationType;
    }

    @Override
    public boolean matches(Method method) {
      return findAnnotation(method, annotationType) != null;
    }
  }

  /**
   * Get a single {@link Annotation} of <code>annotationType</code> from the supplied {@link Method},
   * traversing its super methods if no annotation can be found on the given method itself.
   * <p>
   * Annotations on methods are not inherited by default, so we need to handle this explicitly.
   * 
   * @param method the method to look for annotations on
   * @param annotationType the annotation class to look for
   * @return the annotation found, or <code>null</code> if none found
   */
  private static <A extends Annotation> A findAnnotation(Method method, Class<A> annotationType) {
    A annotation = getAnnotation(method, annotationType);
    Class<?> cl = method.getDeclaringClass();
    if (annotation == null) {
      annotation = searchOnInterfaces(method, annotationType, cl.getInterfaces());
    }
    while (annotation == null) {
      cl = cl.getSuperclass();
      if (cl == null || cl == Object.class) {
        break;
      }
      try {
        Method equivalentMethod = cl.getDeclaredMethod(method.getName(), method.getParameterTypes());
        annotation = getAnnotation(equivalentMethod, annotationType);
        if (annotation == null) {
          annotation = searchOnInterfaces(method, annotationType, cl.getInterfaces());
        }
      } catch (NoSuchMethodException ex) {
        // We're done...
      }
    }
    return annotation;
  }

  /**
   * Get a single {@link Annotation} of {@code annotationType} from the supplied
   * Method, Constructor or Field. Meta-annotations will be searched if the annotation
   * is not declared locally on the supplied element.
   * 
   * @param ae the Method, Constructor or Field from which to get the annotation
   * @param annotationType the annotation class to look for, both locally and as a meta-annotation
   * @return the matching annotation or {@code null} if not found
   * @since 3.1
   */
  private static <T extends Annotation> T getAnnotation(AnnotatedElement ae, Class<T> annotationType) {
    T ann = ae.getAnnotation(annotationType);
    if (ann == null) {
      for (Annotation metaAnn : ae.getAnnotations()) {
        ann = metaAnn.annotationType().getAnnotation(annotationType);
        if (ann != null) {
          break;
        }
      }
    }
    return ann;
  }

  private static <A extends Annotation> A searchOnInterfaces(Method method, Class<A> annotationType, Class<?>[] ifcs) {
    A annotation = null;
    for (Class<?> iface : ifcs) {
      if (isInterfaceWithAnnotatedMethods(iface)) {
        try {
          Method equivalentMethod = iface.getMethod(method.getName(), method.getParameterTypes());
          annotation = getAnnotation(equivalentMethod, annotationType);
        } catch (NoSuchMethodException ex) {
          // Skip this interface - it doesn't have the method...
        }
        if (annotation != null) {
          break;
        }
      }
    }
    return annotation;
  }

  private static boolean isInterfaceWithAnnotatedMethods(Class<?> iface) {
    synchronized (annotatedInterfaceCache) {
      Boolean flag = annotatedInterfaceCache.get(iface);
      if (flag != null) {
        return flag;
      }
      boolean found = false;
      for (Method ifcMethod : iface.getMethods()) {
        if (ifcMethod.getAnnotations().length > 0) {
          found = true;
          break;
        }
      }
      annotatedInterfaceCache.put(iface, found);
      return found;
    }
  }
}
