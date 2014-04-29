package org.alienlabs.hatchetharry.integrationTest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerifyClientSideTests
{
	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyClientSideTests.class);

	private static final String PORT = "8088";
	private static final String HOST = "localhost";
	private static final Server server = new Server();

	private static final String QUNIT_FAILED_TESTS = "0";
	private static final String QUNIT_PASSED_TESTS = "6";
	private static final String QUNIT_TOTAL_TESTS = "6";

	private static final String MISTLETOE_FAILED_TESTS = "Errors/Failures: 0";
	private static final String MISTLETOE_TOTAL_TESTS = "Total tests: 2";

	private static WebDriver firefoxDriver1;
	private static WebDriver firefoxDriver2;

	private static final String JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_ELEMENT = "function elementInViewport(el) {\n"
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
			+ "var elementToLookFor = document.getElementById('what');\n"
			+ "\n"
			+ "for (var i = 0; i < 10000; i = i + 1) {\n"
			+ "	if (elementInViewport(elementToLookFor)) {\n"
			+ "		break;\n"
			+ "	} else {\n"
			+ "		window.scrollBy(0,1);\n}\n}";

	@BeforeClass
	public static void setUpClass() throws Exception
	{
		VerifyClientSideTests.LOGGER
		.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> STARTING EMBEDDED JETTY SERVER");

		final ServerConnector http = new ServerConnector(VerifyClientSideTests.server);
		http.setHost(VerifyClientSideTests.HOST);
		http.setPort(Integer.parseInt(VerifyClientSideTests.PORT));
		http.setIdleTimeout(30000);
		VerifyClientSideTests.server.addConnector(http);
		final WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setWar("src/main/webapp");
		VerifyClientSideTests.server.setHandler(webapp);
		VerifyClientSideTests.server.start();

		VerifyClientSideTests.LOGGER
		.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SUCCESSFULLY STARTED EMBEDDED JETTY SERVER");

		VerifyClientSideTests.firefoxDriver1 = new FirefoxDriver();
		VerifyClientSideTests.firefoxDriver2 = new FirefoxDriver();

		Thread.sleep(15000);

		VerifyClientSideTests.firefoxDriver1.get("http://" + VerifyClientSideTests.HOST + ":"
				+ VerifyClientSideTests.PORT + "/");
		VerifyClientSideTests.firefoxDriver2.get("http://" + VerifyClientSideTests.HOST + ":"
				+ VerifyClientSideTests.PORT + "/");

		Thread.sleep(30000);
	}

	@Test
	public void testQunit()
	{
		final String passed1 = VerifyClientSideTests.firefoxDriver1.findElement(By.id("passed"))
				.getText();
		final String total1 = VerifyClientSideTests.firefoxDriver1.findElement(By.id("total"))
				.getText();
		final String failed1 = VerifyClientSideTests.firefoxDriver1.findElement(By.id("failed"))
				.getText();

		Assert.assertEquals(VerifyClientSideTests.QUNIT_PASSED_TESTS, passed1);
		Assert.assertEquals(VerifyClientSideTests.QUNIT_TOTAL_TESTS, total1);
		Assert.assertEquals(VerifyClientSideTests.QUNIT_FAILED_TESTS, failed1);
	}

	@Test
	public void testMistletoe() throws InterruptedException
	{
		((JavascriptExecutor)VerifyClientSideTests.firefoxDriver1)
		.executeScript(VerifyClientSideTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_ELEMENT
				.replaceAll("what", "runMistletoe"));
		VerifyClientSideTests.firefoxDriver1.findElement(By.id("runMistletoe")).click();

		Thread.sleep(45000);

		((JavascriptExecutor)VerifyClientSideTests.firefoxDriver1)
		.executeScript(VerifyClientSideTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_ELEMENT
				.replaceAll("what", "runsSummary"));

		final String chromeTotal = VerifyClientSideTests.firefoxDriver1.findElement(
				By.id("runsSummary")).getText();
		final String chromeFailed = VerifyClientSideTests.firefoxDriver1.findElement(
				By.id("errorsSummary")).getText();

		Assert.assertEquals(VerifyClientSideTests.MISTLETOE_TOTAL_TESTS, chromeTotal);
		Assert.assertEquals(VerifyClientSideTests.MISTLETOE_FAILED_TESTS, chromeFailed);
	}

	@AfterClass
	public static void tearDownClass() throws Exception
	{
		if (null != VerifyClientSideTests.firefoxDriver1)
		{
			VerifyClientSideTests.firefoxDriver1.quit();
		}
		if (null != VerifyClientSideTests.firefoxDriver2)
		{
			VerifyClientSideTests.firefoxDriver2.quit();
		}

		VerifyClientSideTests.LOGGER
		.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> STOPPING EMBEDDED JETTY SERVER");
		VerifyClientSideTests.server.stop();
		VerifyClientSideTests.server.join();
		Thread.sleep(30000);
	}

}
