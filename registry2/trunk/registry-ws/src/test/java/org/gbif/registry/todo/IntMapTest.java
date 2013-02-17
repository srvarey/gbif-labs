package org.gbif.registry.todo;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.mrbean.MrBeanModule;


public class IntMapTest {

  public interface IFace {

    public int getTotal();
  }

  static class Imp implements IFace {

    public int total;

    @Override
    public int getTotal() {
      return total;
    }

    public void setTotal(int total) {
      this.total = total;
    }
  }

  public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
    Imp i = new Imp();
    i.setTotal(10);
    ObjectMapper MAPPER = new ObjectMapper();
    MAPPER.registerModule(new MrBeanModule());
    String json = MAPPER.writeValueAsString(i);
    IFace client = MAPPER.readValue(json, IFace.class);
    System.out.println(client.getTotal());
  }
}
