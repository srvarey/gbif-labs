package org.gbif;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class SeleniumIT {

  private WebDriver driver;
  private final String baseUrl = "http://staging.gbif.org:8080/portal-web-dynamic";

  @Before
  public void setUp() throws Exception {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setBrowserName("firefox");
    driver = new RemoteWebDriver(new URL("http://127.0.0.1:4444/wd/hub/"), capabilities);
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testFoo() throws Exception {
    driver.get(baseUrl);
    driver.findElement(By.linkText("Datasets")).click();
    driver.findElement(By.cssSelector("button.search_button")).click();
    driver.findElement(By.xpath("//*[contains(.,'CATE Araceae')]"));
  }

  @After
  public void tearDown() throws Exception {
    if (driver != null) {
      driver.quit();
    }
  }

}
