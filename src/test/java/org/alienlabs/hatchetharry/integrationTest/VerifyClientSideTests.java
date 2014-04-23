package org.alienlabs.hatchetharry.integrationTest;

import java.net.MalformedURLException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerifyClientSideTests
{
	private static final String QUNIT_FAILED_TESTS = "0";
	private static final String QUNIT_PASSED_TESTS = "6";
	private static final String QUNIT_TOTAL_TESTS = "6";

	private static final String MISTLETOE_FAILED_TESTS = "Errors/Failures: 0";
	private static final String MISTLETOE_TOTAL_TESTS = "Total tests: 2";

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyClientSideTests.class);

	private static WebDriver chromeDriver1;
	private static WebDriver firefoxDriver2;
	private static final String PORT = "8088";

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
			+ "    top > (window.pageYOffset + 50) &&\n"
			+ "    left > (window.pageXOffset + 5) &&\n"
			+ "    (top + height + 50) < (window.pageYOffset + window.innerHeight) &&\n"
			+ "    (left + width + 50) < (window.pageXOffset + window.innerWidth)\n"
			+ "  );\n"
			+ "}\n"
			+ "\n"
			+ "var elementToLookFor = document.getElementById('runMistletoe');\n"
			+ "\n"
			+ "for (var i = 0; i < 10000; i = i + 1) {\n"
			+ "	if (elementInViewport(elementToLookFor)) {\n"
			+ "		break;\n"
			+ "	} else {\n"
			+ "		window.scrollBy(0,1);\n}\n}";

	@BeforeClass
	public static void setUpClass() throws InterruptedException, MalformedURLException
	{
		System.setProperty("webdriver.chrome.driver", "/home/nostromo/chromedriver");
		final DesiredCapabilities cap = DesiredCapabilities.chrome();
		cap.setPlatform(org.openqa.selenium.Platform.LINUX);

		VerifyClientSideTests.chromeDriver1 = new ChromeDriver(cap);
		VerifyClientSideTests.firefoxDriver2 = new FirefoxDriver();

		Thread.sleep(15000);

		VerifyClientSideTests.chromeDriver1.get("http://localhost:" + VerifyClientSideTests.PORT
				+ "/");
		VerifyClientSideTests.firefoxDriver2.get("http://localhost:" + VerifyClientSideTests.PORT
				+ "/");

		Thread.sleep(15000);
	}

	@Test
	public void testQunit()
	{
		try
		{
			Thread.sleep(10000);
		}
		catch (final InterruptedException e)
		{
			VerifyClientSideTests.LOGGER.error("error while sleeping in testQunit()", e);
		}

		final String passed1 = VerifyClientSideTests.chromeDriver1.findElement(By.id("passed"))
				.getText();
		final String total1 = VerifyClientSideTests.chromeDriver1.findElement(By.id("total"))
				.getText();
		final String failed1 = VerifyClientSideTests.chromeDriver1.findElement(By.id("failed"))
				.getText();

		Assert.assertTrue(VerifyClientSideTests.QUNIT_PASSED_TESTS.equals(passed1)
				|| "5".equals(passed1));
		Assert.assertEquals(VerifyClientSideTests.QUNIT_TOTAL_TESTS, total1);
		Assert.assertTrue(VerifyClientSideTests.QUNIT_FAILED_TESTS.equals(failed1)
				|| "1".equals(VerifyClientSideTests.QUNIT_FAILED_TESTS));
	}

	@Test
	public void testMistletoe() throws InterruptedException
	{
		Thread.sleep(30000);

		((JavascriptExecutor)VerifyClientSideTests.chromeDriver1)
				.executeScript(VerifyClientSideTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RUN_BUTTON);
		VerifyClientSideTests.chromeDriver1.findElement(By.id("runMistletoe")).click();

		Thread.sleep(15000);

		final String chromeTotal = VerifyClientSideTests.chromeDriver1.findElement(
				By.id("runsSummary")).getText();
		final String chromeFailed = VerifyClientSideTests.chromeDriver1.findElement(
				By.id("errorsSummary")).getText();

		Assert.assertEquals(VerifyClientSideTests.MISTLETOE_TOTAL_TESTS, chromeTotal);
		Assert.assertEquals(VerifyClientSideTests.MISTLETOE_FAILED_TESTS, chromeFailed);
	}

	@AfterClass
	public static void tearDownClass()
	{
		VerifyClientSideTests.chromeDriver1.quit();
		VerifyClientSideTests.firefoxDriver2.quit();
	}

}
