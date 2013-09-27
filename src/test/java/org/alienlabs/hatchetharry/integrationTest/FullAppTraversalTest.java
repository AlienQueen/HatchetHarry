package org.alienlabs.hatchetharry.integrationTest;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FullAppTraversalTest
{
	private static WebDriver chromeDriver;
	private static WebDriver firefoxDriver;

	private static final String PORT = "8088";
	private static final String HOST = "localhost";

	private static final String SHOW_AND_OPEN_MOBILE_MENUBAR = "jQuery('#jMenu').hide(); jQuery('.dropdownmenu').show(); jQuery('.dropdownmenu:first').click();";

	@BeforeClass
	public static void setUpClass()
	{
		System.setProperty("webdriver.chrome.driver", "/home/nostromo/chromedriver");
		FullAppTraversalTest.chromeDriver = new ChromeDriver();
		FullAppTraversalTest.chromeDriver.get(FullAppTraversalTest.HOST + ":"
				+ FullAppTraversalTest.PORT + "/");

		FullAppTraversalTest.firefoxDriver = new FirefoxDriver();
		FullAppTraversalTest.firefoxDriver.get(FullAppTraversalTest.HOST + ":"
				+ FullAppTraversalTest.PORT + "/");
	}

	@AfterClass
	public static void tearDownClass()
	{
		FullAppTraversalTest.chromeDriver.quit();
		FullAppTraversalTest.firefoxDriver.quit();
	}

	@Test
	public void testFullAppTraversal() throws InterruptedException
	{
		// Create a game in Chrome
		FullAppTraversalTest.waitForJQueryProcessing(FullAppTraversalTest.chromeDriver, 60);

		((JavascriptExecutor)FullAppTraversalTest.chromeDriver)
		.executeScript(FullAppTraversalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);

		FullAppTraversalTest.chromeDriver.findElement(By.id("createGameLinkResponsive")).click();
		Thread.sleep(5000);
		FullAppTraversalTest.chromeDriver.findElement(By.id("name")).clear();
		FullAppTraversalTest.chromeDriver.findElement(By.id("name")).sendKeys("Zala");
		new Select(FullAppTraversalTest.chromeDriver.findElement(By.id("sideInput")))
		.selectByVisibleText("infrared");
		new Select(FullAppTraversalTest.chromeDriver.findElement(By.id("decks")))
		.selectByVisibleText("Aura Bant");

		final Long gameId = Long.parseLong(FullAppTraversalTest.chromeDriver.findElement(
				By.id("gameId")).getText());

		FullAppTraversalTest.chromeDriver.findElement(By.id("createSubmit")).click();

		// Join a game in Opera
		FullAppTraversalTest.waitForJQueryProcessing(FullAppTraversalTest.firefoxDriver, 60);

		((JavascriptExecutor)FullAppTraversalTest.firefoxDriver)
		.executeScript(FullAppTraversalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);

		FullAppTraversalTest.firefoxDriver.findElement(By.id("joinGameLinkResponsive")).click();
		Thread.sleep(5000);
		FullAppTraversalTest.firefoxDriver.findElement(By.id("name")).clear();
		FullAppTraversalTest.firefoxDriver.findElement(By.id("name")).sendKeys("Zala");
		new Select(FullAppTraversalTest.firefoxDriver.findElement(By.id("sideInput")))
		.selectByVisibleText("ultraviolet");
		new Select(FullAppTraversalTest.firefoxDriver.findElement(By.id("decks")))
		.selectByVisibleText("aggro-combo Red / Black");
		FullAppTraversalTest.firefoxDriver.findElement(By.id("gameIdInput")).clear();
		FullAppTraversalTest.firefoxDriver.findElement(By.id("gameIdInput")).sendKeys(
				gameId.toString());

		FullAppTraversalTest.firefoxDriver.findElement(By.id("joinSubmit")).click();
	}

	public static boolean waitForJQueryProcessing(final WebDriver driver, final int timeOutInSeconds)
	{
		boolean jQcondition = false;
		try
		{
			new WebDriverWait(driver, timeOutInSeconds)
			{
			}.until(new ExpectedCondition<Boolean>()
					{

				@Override
				public Boolean apply(final WebDriver driverObject)
				{
					return (Boolean)((JavascriptExecutor)driverObject)
							.executeScript("return jQuery.active == 0");
				}
					});
			jQcondition = (Boolean)((JavascriptExecutor)driver)
					.executeScript("return window.jQuery != undefined && jQuery.active === 0");
			return jQcondition;
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		return jQcondition;
	}

}
