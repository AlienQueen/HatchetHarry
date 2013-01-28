package org.alienlabs.hatchetharry.integrationTest;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerifyQUnitTests
{
	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyQUnitTests.class);

	private WebDriver operaDriver;
	private WebDriver firefoxDriver;
	private WebDriver chromeDriver1, chromeDriver2;

	@Before
	public void setUp() throws IOException
	{
		// TODO/ Opera
		System.setProperty("webdriver.chrome.driver", "/home/nostromo/chromedriver");
		this.chromeDriver1 = new ChromeDriver();
		this.chromeDriver1.get("http://localhost:8080/");

		System.setProperty("webdriver.chrome.driver", "/home/nostromo/chromedriver");
		this.chromeDriver2 = new ChromeDriver();
		this.chromeDriver2.get("http://localhost:8080/");

		// System.setProperty("webdriver.firefox.driver",
		// "/home/nostromo/firefoxdriver");
		// this.firefoxDriver = new FirefoxDriver();
		// this.firefoxDriver.get("http://localhost:8080/");


	}

	@Test
	public void testQunit()
	{
		final String chromePassed1 = this.chromeDriver1.findElement(By.className("passed"))
				.getText();
		final String chromeTotal1 = this.chromeDriver1.findElement(By.className("total")).getText();
		final String chromeFailed1 = this.chromeDriver1.findElement(By.className("failed"))
				.getText();

		Assert.assertEquals("0", chromePassed1);
		Assert.assertEquals("0", chromeTotal1);
		Assert.assertEquals("0", chromeFailed1);

		try
		{
			this.chromeDriver2.wait(6000l);
		}
		catch (final Exception e)
		{
			VerifyQUnitTests.LOGGER.error("error!", e);
		}
		final WebElement parent = this.chromeDriver2.findElement(By.id("qunit-testresult"));
		final String chromePassed2 = parent.findElement(By.className("passed")).getText();
		final String chromeTotal2 = parent.findElement(By.className("total")).getText();
		final String chromeFailed2 = parent.findElement(By.className("failed")).getText();

		Assert.assertEquals("0", chromePassed2);
		Assert.assertEquals("0", chromeTotal2);
		Assert.assertEquals("0", chromeFailed2);

		this.chromeDriver1.quit();
		this.chromeDriver2.quit();
	}

}
