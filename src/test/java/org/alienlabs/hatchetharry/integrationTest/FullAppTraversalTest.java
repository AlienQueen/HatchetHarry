package org.alienlabs.hatchetharry.integrationTest;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import com.opera.core.systems.OperaDriver;

public class FullAppTraversalTest
{
	private static WebDriver chromeDriver;
	private static WebDriver operaDriver;

	// private static final String HOST = "http://localhost"; // For development

	// For production
	private static final String PORT = "8088";
	private static final String HOST = "http://hatchetharry.net";

	private static final String SHOW_AND_OPEN_MOBILE_MENUBAR = "jQuery('#jMenu').hide(); jQuery('.dropdownmenu').show(); jQuery('.dropdownmenu:first').click();";

	@BeforeClass
	public static void setUpClass()
	{
		System.setProperty("webdriver.chrome.driver", "/home/nostromo/chromedriver");
		FullAppTraversalTest.chromeDriver = new ChromeDriver();
		FullAppTraversalTest.chromeDriver.get(FullAppTraversalTest.HOST + ":"
				+ FullAppTraversalTest.PORT + "/");

		FullAppTraversalTest.operaDriver = new OperaDriver();
		FullAppTraversalTest.operaDriver.get(FullAppTraversalTest.HOST + ":"
				+ FullAppTraversalTest.PORT + "/");
	}

	@Test
	public void testFullAppTraversal() throws InterruptedException
	{
		Thread.sleep(20000);

		// Create a game in Chrome
		((JavascriptExecutor)FullAppTraversalTest.chromeDriver)
				.executeScript(FullAppTraversalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);

		FullAppTraversalTest.chromeDriver.findElement(By.id("createGameLinkResponsive")).click();
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
		((JavascriptExecutor)FullAppTraversalTest.operaDriver)
				.executeScript(FullAppTraversalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);

		FullAppTraversalTest.operaDriver.findElement(By.id("joinGameLinkResponsive")).click();
		FullAppTraversalTest.operaDriver.findElement(By.id("name")).clear();
		FullAppTraversalTest.operaDriver.findElement(By.id("name")).sendKeys("Zala");
		new Select(FullAppTraversalTest.operaDriver.findElement(By.id("sideInput")))
				.selectByVisibleText("ultraviolet");
		new Select(FullAppTraversalTest.operaDriver.findElement(By.id("decks")))
				.selectByVisibleText("aggro-combo Red / Black");
		FullAppTraversalTest.operaDriver.findElement(By.id("gameIdInput")).clear();
		FullAppTraversalTest.operaDriver.findElement(By.id("gameIdInput")).sendKeys(
				gameId.toString());

		FullAppTraversalTest.operaDriver.findElement(By.id("joinSubmit")).click();
	}

}
