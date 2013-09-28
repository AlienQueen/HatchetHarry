package org.alienlabs.hatchetharry.integrationTest;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FullAppTraversalTest
{
	private static WebDriver chromeDriver1;
	private static WebDriver chromeDriver2;

	private static final String PORT = "8088";
	private static final String HOST = "localhost";

	private static final String SHOW_AND_OPEN_MOBILE_MENUBAR = "jQuery('#jMenu').hide(); jQuery('.dropdownmenu').show(); jQuery('.dropdownmenu:first').click();";

	@BeforeClass
	public static void setUpClass()
	{
		System.setProperty("webdriver.chrome.driver", "/home/nostromo/chromedriver");
		FullAppTraversalTest.chromeDriver1 = new ChromeDriver();
		FullAppTraversalTest.chromeDriver1.get(FullAppTraversalTest.HOST + ":"
				+ FullAppTraversalTest.PORT + "/");

		FullAppTraversalTest.chromeDriver2 = new ChromeDriver();
		FullAppTraversalTest.chromeDriver2.get(FullAppTraversalTest.HOST + ":"
				+ FullAppTraversalTest.PORT + "/");
	}

	@AfterClass
	public static void tearDownClass()
	{
		FullAppTraversalTest.chromeDriver1.quit();
		FullAppTraversalTest.chromeDriver2.quit();
	}

	@Test
	public void testFullAppTraversal() throws InterruptedException
	{
		// Create a game in Chrome 1
		FullAppTraversalTest.waitForJQueryProcessing(FullAppTraversalTest.chromeDriver1, 60);

		((JavascriptExecutor)FullAppTraversalTest.chromeDriver1)
				.executeScript(FullAppTraversalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);

		FullAppTraversalTest.chromeDriver1.findElement(By.id("createGameLinkResponsive")).click();
		Thread.sleep(5000);
		FullAppTraversalTest.chromeDriver1.findElement(By.id("name")).clear();
		FullAppTraversalTest.chromeDriver1.findElement(By.id("name")).sendKeys("Zala");
		new Select(FullAppTraversalTest.chromeDriver1.findElement(By.id("sideInput")))
				.selectByVisibleText("infrared");
		new Select(FullAppTraversalTest.chromeDriver1.findElement(By.id("decks")))
				.selectByVisibleText("Aura Bant");

		final Long gameId = Long.parseLong(FullAppTraversalTest.chromeDriver1.findElement(
				By.id("gameId")).getText());

		FullAppTraversalTest.chromeDriver1.findElement(By.id("createSubmit")).click();

		// Join a game in Chrome 2
		FullAppTraversalTest.waitForJQueryProcessing(FullAppTraversalTest.chromeDriver2, 60);

		((JavascriptExecutor)FullAppTraversalTest.chromeDriver2)
				.executeScript(FullAppTraversalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);

		FullAppTraversalTest.chromeDriver2.findElement(By.id("joinGameLinkResponsive")).click();
		Thread.sleep(5000);
		FullAppTraversalTest.chromeDriver2.findElement(By.id("name")).clear();
		FullAppTraversalTest.chromeDriver2.findElement(By.id("name")).sendKeys("Zala");
		new Select(FullAppTraversalTest.chromeDriver2.findElement(By.id("sideInput")))
				.selectByVisibleText("ultraviolet");
		new Select(FullAppTraversalTest.chromeDriver2.findElement(By.id("decks")))
				.selectByVisibleText("Aura Bant");
		FullAppTraversalTest.chromeDriver2.findElement(By.id("gameIdInput")).clear();
		FullAppTraversalTest.chromeDriver2.findElement(By.id("gameIdInput")).sendKeys(
				gameId.toString());

		FullAppTraversalTest.chromeDriver2.findElement(By.id("joinSubmit")).click();

		// Assert no card present
		Thread.sleep(5000);
		assertTrue(FullAppTraversalTest.chromeDriver1.findElements(
				By.cssSelector("span[id^='cardHandle']")).isEmpty());
		assertTrue(FullAppTraversalTest.chromeDriver2.findElements(
				By.cssSelector("span[id^='cardHandle']")).isEmpty());

		// Play a card in Chrome1
		FullAppTraversalTest.chromeDriver1.findElement(By.id("playCardLink0")).click();

		// Verify card
		Thread.sleep(5000);
		assertTrue(FullAppTraversalTest.chromeDriver1.findElements(
				By.cssSelector("span[id^='cardHandle']")).size() == 1);
		assertTrue(FullAppTraversalTest.chromeDriver2.findElements(
				By.cssSelector("span[id^='cardHandle']")).size() == 1);

		// Verify card is untapped
		assertFalse(FullAppTraversalTest.chromeDriver1
				.findElements(By.cssSelector("img[id^='card']")).get(0).getCssValue("transform")
				.contains("rotate"));
		assertFalse(FullAppTraversalTest.chromeDriver2
				.findElements(By.cssSelector("img[id^='card']")).get(0).getCssValue("transform")
				.contains("rotate"));

		// Tap card
		FullAppTraversalTest.chromeDriver1.findElement(By.cssSelector("img[id^='tapHandleImage']"))
				.click();
		Thread.sleep(5000);

		// Verify card is tapped
		FullAppTraversalTest.chromeDriver1.findElements(By.cssSelector("img[id^='card']")).get(0)
				.getCssValue("transform").contains("rotate(90deg)");
		FullAppTraversalTest.chromeDriver2.findElements(By.cssSelector("img[id^='card']")).get(0)
				.getCssValue("transform").contains("rotate(90deg)");
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
