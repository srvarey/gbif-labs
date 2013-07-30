package org.gbif.rabbit;


public interface Callback {

  void handleMessage(String message);
}
