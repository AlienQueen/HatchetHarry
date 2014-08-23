package org.alienlabs.hatchetharry.integrationTest;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Pebble
{
	private static final String LOGIN = "http://droledeprincesse.com/blog/";
	private static final String COMMENTS = "http://droledeprincesse.com/blog/viewResponses.secureaction?type=pending"; // &page=2";
	private static final String ACCOUNT = "admin";
	private static final String PASSWORD = "Lordofchqos#33";
	private static WebDriver firefoxDriver;

	@Test
	public void testFullAppTraversal() throws InterruptedException
	{

		Pebble.firefoxDriver = new FirefoxDriver();
		Pebble.firefoxDriver.manage().timeouts().implicitlyWait(15, TimeUnit.MINUTES);

		Pebble.firefoxDriver.get(Pebble.LOGIN);

		Pebble.firefoxDriver.findElement(By.id("username")).clear();
		Pebble.firefoxDriver.findElement(By.id("username")).sendKeys("admin");
		Pebble.firefoxDriver.findElement(By.id("password")).clear();
		Pebble.firefoxDriver.findElement(By.id("password")).sendKeys("Lordofchqos#33");
		Pebble.firefoxDriver.findElement(
			By.cssSelector("div.loginButtons > input[type=\"submit\"]")).click();


		// Pebble.firefoxDriver.findElement(By.linkText("viewResponses.secureaction?type=pending"))
		// .click();
		Pebble.firefoxDriver.get(Pebble.COMMENTS);

		while (true)
		{
			Pebble.firefoxDriver.findElement(By.name("allResponses")).click();
			Pebble.firefoxDriver.findElement(By.id("remove")).click();
		}
	}

}
