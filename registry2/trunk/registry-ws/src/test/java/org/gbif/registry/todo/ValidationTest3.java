package org.gbif.registry.todo;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.apache.bval.guice.Validate;
import org.apache.bval.guice.ValidationModule;


public class ValidationTest3 {

  static class O {

    @Min(value = 0)
    private int i;

    public int getI() {
      return i;
    }

    public void setI(int i) {
      this.i = i;
    }
  }

  public static void main(String[] args) {
    Module m = new AbstractModule() {

      @Override
      protected void configure() {
        bind(Test.class);
      }
    };
    Injector i = Guice.createInjector(m, new ValidationModule());
    O o = new O();
    o.i = -10;
    O o1 = new O();
    o1.i = 10;
    try {
      i.getInstance(Test.class).print(o, o1);
    } catch (ConstraintViolationException e) {
      e.printStackTrace();
      for (ConstraintViolation<?> cv : e.getConstraintViolations()) {
        System.out.println(cv.getPropertyPath());
        System.out.println(cv.getMessage());
        System.out.println(cv.getMessageTemplate());
      }
    }
  }


  static class Test {

    @Validate
    public void print(@Valid O o, O o1) {
      System.out.println(o.i);
      System.out.println(o1.i);
    }

  }
}
