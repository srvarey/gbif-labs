package org.gbif;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.SessionId;

public class LoggingFirefoxDriver extends FirefoxDriver {

  @Override
  protected void log(SessionId sessionId, String commandName, Object toLog) {
    System.out.println(sessionId + " - " + commandName + " - " + toLog.toString());
  }
}
