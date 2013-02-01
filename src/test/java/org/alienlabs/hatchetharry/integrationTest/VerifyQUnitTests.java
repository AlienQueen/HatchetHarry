package org.alienlabs.hatchetharry.integrationTest;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opera.core.systems.OperaDriver;

public class VerifyQUnitTests
{
	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyQUnitTests.class);

	private WebDriver operaDriver;
	private WebDriver firefoxDriver;
	private WebDriver chromeDriver1, chromeDriver2;
	private final String port = "9999";

	@Before
	public void setUp()
	{

		this.operaDriver = new OperaDriver();
		this.operaDriver.get("http://localhost:" + this.port + "/");

		this.firefoxDriver = new FirefoxDriver();
		this.firefoxDriver.get("http://localhost:" + this.port + "/");

		System.setProperty("webdriver.chrome.driver", "/home/nostromo/chromedriver");
		this.chromeDriver1 = new ChromeDriver();
		this.chromeDriver1.get("http://localhost:" + this.port + "/");

		this.chromeDriver2 = new ChromeDriver();
		this.chromeDriver2.get("http://localhost:" + this.port + "/");


	}

	@Test
	public void testQunit()
	{
		final WebElement parent1 = this.chromeDriver1.findElement(By.id("qunit-testresult"));
		final String chromePassed1 = parent1.findElement(By.className("passed")).getText();
		final String chromeTotal1 = parent1.findElement(By.className("total")).getText();
		final String chromeFailed1 = parent1.findElement(By.className("failed")).getText();

		Assert.assertEquals("0", chromePassed1);
		Assert.assertEquals("0", chromeTotal1);
		Assert.assertEquals("0", chromeFailed1);

		final WebElement parent2 = this.chromeDriver2.findElement(By.id("qunit-testresult"));
		final String chromePassed2 = parent2.findElement(By.className("passed")).getText();
		final String chromeTotal2 = parent2.findElement(By.className("total")).getText();
		final String chromeFailed2 = parent2.findElement(By.className("failed")).getText();

		Assert.assertEquals("0", chromePassed2);
		Assert.assertEquals("0", chromeTotal2);
		Assert.assertEquals("0", chromeFailed2);

		final WebElement parent3 = this.firefoxDriver.findElement(By.id("qunit-testresult"));
		final String firefoxPassed3 = parent3.findElement(By.className("passed")).getText();
		final String firefoxTotal3 = parent3.findElement(By.className("total")).getText();
		final String firefoxFailed3 = parent3.findElement(By.className("failed")).getText();

		Assert.assertEquals("0", firefoxPassed3);
		Assert.assertEquals("0", firefoxTotal3);
		Assert.assertEquals("0", firefoxFailed3);

		final WebElement parent4 = this.operaDriver.findElement(By.id("qunit-testresult"));
		final String operaPassed4 = parent4.findElement(By.className("passed")).getText();
		final String operaTotal4 = parent4.findElement(By.className("total")).getText();
		final String operaFailed4 = parent4.findElement(By.className("failed")).getText();

		Assert.assertEquals("0", operaPassed4);
		Assert.assertEquals("0", operaTotal4);
		Assert.assertEquals("0", operaFailed4);
	}

	@After
	public void tearDown()
	{
		this.operaDriver.quit();
		this.firefoxDriver.quit();
		this.chromeDriver1.quit();
		this.chromeDriver2.quit();
	}

}
