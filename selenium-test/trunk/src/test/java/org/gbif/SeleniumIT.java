package org.gbif;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SeleniumIT {

  private WebDriver driver;
  private final String baseUrl = "http://staging.gbif.org:8080/portal-web-dynamic";

  @Before
  public void setUp() throws Exception {
    System.out.println("Setting up driver");
    driver = new LoggingFirefoxDriver();
    System.out.println("Driver set up");
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testFoo() throws Exception {
    System.out.println("Getting URL");
    driver.get(baseUrl);
    driver.findElement(By.linkText("Datasets")).click();
    driver.findElement(By.cssSelector("button.search_button")).click();
    driver.findElement(By.xpath("//*[contains(.,'CATE Araceae')]"));
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
  }

}
