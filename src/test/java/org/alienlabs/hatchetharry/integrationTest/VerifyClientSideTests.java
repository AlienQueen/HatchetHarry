package org.alienlabs.hatchetharry.integrationTest;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.opera.core.systems.OperaDriver;

public class VerifyClientSideTests
{
	private static final String QUNIT_FAILED_TESTS = "0";
	private static final String QUNIT_PASSED_TESTS = "9";
	private static final String QUNIT_TOTAL_TESTS = "9";

	private static final String MISTLETOE_FAILED_TESTS = "Errors/Failures: 0";
	private static final String MISTLETOE_TOTAL_TESTS = "Total tests: 2";

	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(VerifyClientSideTests.class);

	private static WebDriver operaDriver;
	private static WebDriver firefoxDriver;
	private static WebDriver chromeDriver1, chromeDriver2;
	private static final String port = "9999";

	@BeforeClass
	public static void setUpClass()
	{
		System.setProperty("webdriver.chrome.driver", "/home/nostromo/chromedriver");
		VerifyClientSideTests.chromeDriver1 = new ChromeDriver();
		VerifyClientSideTests.chromeDriver1.get("http://localhost:" + VerifyClientSideTests.port
				+ "/");

		VerifyClientSideTests.chromeDriver2 = new ChromeDriver();
		VerifyClientSideTests.chromeDriver2.get("http://localhost:" + VerifyClientSideTests.port
				+ "/");

		VerifyClientSideTests.firefoxDriver = new FirefoxDriver();
		VerifyClientSideTests.firefoxDriver.get("http://localhost:" + VerifyClientSideTests.port
				+ "/");

		VerifyClientSideTests.operaDriver = new OperaDriver();
		VerifyClientSideTests.operaDriver.get("http://localhost:" + VerifyClientSideTests.port
				+ "/");
	}

	@Test
	public void testQunit()
	{
		final WebElement parent1 = VerifyClientSideTests.chromeDriver1.findElement(By
				.id("qunit-testresult"));
		final String chromePassed1 = parent1.findElement(By.className("passed")).getText();
		final String chromeTotal1 = parent1.findElement(By.className("total")).getText();
		final String chromeFailed1 = parent1.findElement(By.className("failed")).getText();

		Assert.assertEquals(VerifyClientSideTests.QUNIT_PASSED_TESTS, chromePassed1);
		Assert.assertEquals(VerifyClientSideTests.QUNIT_TOTAL_TESTS, chromeTotal1);
		Assert.assertEquals(VerifyClientSideTests.QUNIT_FAILED_TESTS, chromeFailed1);

		final WebElement parent2 = VerifyClientSideTests.chromeDriver2.findElement(By
				.id("qunit-testresult"));
		final String chromePassed2 = parent2.findElement(By.className("passed")).getText();
		final String chromeTotal2 = parent2.findElement(By.className("total")).getText();
		final String chromeFailed2 = parent2.findElement(By.className("failed")).getText();

		Assert.assertEquals(VerifyClientSideTests.QUNIT_PASSED_TESTS, chromePassed2);
		Assert.assertEquals(VerifyClientSideTests.QUNIT_TOTAL_TESTS, chromeTotal2);
		Assert.assertEquals(VerifyClientSideTests.QUNIT_FAILED_TESTS, chromeFailed2);

		final WebElement parent3 = VerifyClientSideTests.firefoxDriver.findElement(By
				.id("qunit-testresult"));
		final String firefoxPassed3 = parent3.findElement(By.className("passed")).getText();
		final String firefoxTotal3 = parent3.findElement(By.className("total")).getText();
		final String firefoxFailed3 = parent3.findElement(By.className("failed")).getText();

		Assert.assertEquals(VerifyClientSideTests.QUNIT_PASSED_TESTS, firefoxPassed3);
		Assert.assertEquals(VerifyClientSideTests.QUNIT_TOTAL_TESTS, firefoxTotal3);
		Assert.assertEquals(VerifyClientSideTests.QUNIT_FAILED_TESTS, firefoxFailed3);

		final WebElement parent4 = VerifyClientSideTests.operaDriver.findElement(By
				.id("qunit-testresult"));
		final String operaPassed4 = parent4.findElement(By.className("passed")).getText();
		final String operaTotal4 = parent4.findElement(By.className("total")).getText();
		final String operaFailed4 = parent4.findElement(By.className("failed")).getText();

		Assert.assertEquals(VerifyClientSideTests.QUNIT_PASSED_TESTS, operaPassed4);
		Assert.assertEquals(VerifyClientSideTests.QUNIT_TOTAL_TESTS, operaTotal4);
		Assert.assertEquals(VerifyClientSideTests.QUNIT_FAILED_TESTS, operaFailed4);
	}

	@Test
	public void testMistletoe()
	{
		VerifyClientSideTests.chromeDriver1.findElement(By.id("runMistletoe")).click();
		new WebDriverWait(VerifyClientSideTests.chromeDriver1, 3).until(ExpectedConditions
				.presenceOfElementLocated(By.id("runsSummary")));

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
		VerifyClientSideTests.chromeDriver2.quit();
		VerifyClientSideTests.firefoxDriver.quit();
		VerifyClientSideTests.operaDriver.quit();
	}

}
