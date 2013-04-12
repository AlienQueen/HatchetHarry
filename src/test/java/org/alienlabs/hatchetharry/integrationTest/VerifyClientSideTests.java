package org.alienlabs.hatchetharry.integrationTest;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opera.core.systems.OperaDriver;

public class VerifyClientSideTests
{
	private static final String QUNIT_FAILED_TESTS = "0";
	private static final String QUNIT_PASSED_TESTS = "9";
	private static final String QUNIT_TOTAL_TESTS = "9";

	private static final String MISTLETOE_FAILED_TESTS = "Errors/Failures: 0";
	private static final String MISTLETOE_TOTAL_TESTS = "Total tests: 2";

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyClientSideTests.class);

	private static WebDriver chromeDriver;
	private static WebDriver operaDriver;
	private static final String port = "8088";

	private static final String JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RUN_BUTTON = "function elementInViewport(el) {\n"
			+ "  var top = el.offsetTop;\n"
			+ "  var left = el.offsetLeft;\n"
			+ "  var width = el.offsetWidth;\n"
			+ "  var height = el.offsetHeight;\n"
			+ "\n"
			+ "  while(el.offsetParent) {\n"
			+ "    el = el.offsetParent;\n"
			+ "    top += el.offsetTop;\n"
			+ "    left += el.offsetLeft;\n"
			+ "  }\n"
			+ "\n"
			+ "  return (\n"
			+ "    top >= window.pageYOffset &&\n"
			+ "    left >= window.pageXOffset &&\n"
			+ "    (top + height) <= (window.pageYOffset + window.innerHeight) &&\n"
			+ "    (left + width) <= (window.pageXOffset + window.innerWidth)\n"
			+ "  );\n"
			+ "}\n"
			+ "\n"
			+ "var elementToLookFor = document.getElementById('runMistletoe');\n"
			+ "\n"
			+ "for (var i = 0; i < 5000; i = i + 100) {\n"
			+ "	if (elementInViewport(elementToLookFor)) {\n"
			+ "		break;\n"
			+ "	} else {\n"
			+ "		window.scrollBy(0,100);\n}\n}";

	@BeforeClass
	public static void setUpClass()
	{
		System.setProperty("webdriver.chrome.driver", "/home/nostromo/chromedriver");
		VerifyClientSideTests.chromeDriver = new ChromeDriver();
		VerifyClientSideTests.chromeDriver.get("http://localhost:" + VerifyClientSideTests.port
				+ "/");

		VerifyClientSideTests.operaDriver = new OperaDriver();
		VerifyClientSideTests.operaDriver.get("http://localhost:" + VerifyClientSideTests.port
				+ "/");
	}

	@Test
	public void testQunit()
	{
		try
		{
			Thread.sleep(4000);
		}
		catch (final InterruptedException e)
		{
			VerifyClientSideTests.LOGGER.error("error while sleeping in testQunit()", e);
		}

		final String passed1 = VerifyClientSideTests.chromeDriver.findElement(By.id("passed"))
				.getText();
		final String total1 = VerifyClientSideTests.chromeDriver.findElement(By.id("total"))
				.getText();
		final String failed1 = VerifyClientSideTests.chromeDriver.findElement(By.id("failed"))
				.getText();

		Assert.assertEquals(VerifyClientSideTests.QUNIT_PASSED_TESTS, passed1);
		Assert.assertEquals(VerifyClientSideTests.QUNIT_TOTAL_TESTS, total1);
		Assert.assertEquals(VerifyClientSideTests.QUNIT_FAILED_TESTS, failed1);

		final String passed2 = VerifyClientSideTests.operaDriver.findElement(By.id("passed"))
				.getText();
		final String total2 = VerifyClientSideTests.operaDriver.findElement(By.id("total"))
				.getText();
		final String failed2 = VerifyClientSideTests.operaDriver.findElement(By.id("failed"))
				.getText();

		Assert.assertEquals(VerifyClientSideTests.QUNIT_PASSED_TESTS, passed2);
		Assert.assertEquals(VerifyClientSideTests.QUNIT_TOTAL_TESTS, total2);
		Assert.assertEquals(VerifyClientSideTests.QUNIT_FAILED_TESTS, failed2);
	}

	@Test
	public void testMistletoe()
	{
		try
		{
			Thread.sleep(4000);
		}
		catch (final InterruptedException e)
		{
			VerifyClientSideTests.LOGGER.error("error while sleeping in testMistletoe()", e);
		}

		((JavascriptExecutor)VerifyClientSideTests.chromeDriver)
				.executeScript(VerifyClientSideTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RUN_BUTTON);
		VerifyClientSideTests.chromeDriver.findElement(By.id("runMistletoe")).click();

		try
		{
			Thread.sleep(6000);
		}
		catch (final InterruptedException e)
		{
			VerifyClientSideTests.LOGGER.error("error while sleeping in testMistletoe()", e);
		}

		final String chromeTotal = VerifyClientSideTests.chromeDriver.findElement(
				By.id("runsSummary")).getText();
		final String chromeFailed = VerifyClientSideTests.chromeDriver.findElement(
				By.id("errorsSummary")).getText();

		Assert.assertEquals(VerifyClientSideTests.MISTLETOE_TOTAL_TESTS, chromeTotal);
		Assert.assertEquals(VerifyClientSideTests.MISTLETOE_FAILED_TESTS, chromeFailed);
	}

	@AfterClass
	public static void tearDownClass()
	{
		VerifyClientSideTests.chromeDriver.quit();
		VerifyClientSideTests.operaDriver.quit();
	}

}
