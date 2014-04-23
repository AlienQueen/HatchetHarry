package org.alienlabs.hatchetharry.integrationTest;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;

public class FullAppTraversalTests
{
	private static WebDriver firefoxDriver;
	private static WebDriver chromeDriver;

	private static final String PORT = "8088";
	private static final String HOST = "http://localhost";

	private static final String SHOW_AND_OPEN_MOBILE_MENUBAR = "jQuery('#cssmenu').hide(); jQuery('.categories').hide(); jQuery('.dropdownmenu').show(); jQuery('.dropdownmenu:first').click();";

	private static final String JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS = "window.scrollBy(0,100); jQuery('.w_content_container').scrollTop(200);";

	private static final String JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RESPONSIVE_MENU = "function elementInViewport(el) {\n"
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
			+ "    left > (window.pageXOffset + 50) &&\n"
			+ "    (top + height + 50) < (window.pageYOffset + window.innerHeight) &&\n"
			+ "    (left + width + 50) < (window.pageXOffset + window.innerWidth)\n"
			+ "  );\n"
			+ "}\n"
			+ "\n"
			+ "var elementToLookFor = document.getElementById('revealTopLibraryCardLinkResponsive');\n"
			+ "\n"
			+ "for (var i = 0; i < 10000; i = i + 1) {\n"
			+ "	if (elementInViewport(elementToLookFor)) {\n"
			+ "		break;\n"
			+ "	} else {\n"
			+ "		window.scrollBy(0,5);\n}\n}";

	private static final String JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_CARD = "function elementInViewport(el) {\n"
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
			+ "    left > (window.pageXOffset + 50) &&\n"
			+ "    (top + height + 50) < (window.pageYOffset + window.innerHeight) &&\n"
			+ "    (left + width + 50) < (window.pageXOffset + window.innerWidth)\n"
			+ "  );\n"
			+ "}\n"
			+ "\n"
			+ "var elementToLookFor = jQuery(\"img[id^='tapHandleImage']\");\n"
			+ "\n"
			+ "for (var i = 0; i < 10000; i = i + 1) {\n"
			+ "	if (elementInViewport(elementToLookFor)) {\n"
			+ "		break;\n"
			+ "	} else {\n"
			+ "		window.scrollBy(0,5);\n}\n}";

	private static final String JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_PUT_TO_ZONE_SUMBIT_BUTTON_FOR_HAND = "function elementInViewport(el) {\n"
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
			+ "    left > (window.pageXOffset + 50) &&\n"
			+ "    (top + height + 50) < (window.pageYOffset + window.innerHeight) &&\n"
			+ "    (left + width + 50) < (window.pageXOffset + window.innerWidth)\n"
			+ "  );\n"
			+ "}\n"
			+ "\n"
			+ "var elementToLookFor = jQuery(\"#moveToZoneSubmitHand\");\n"
			+ "\n"
			+ "for (var i = 0; i < 10000; i = i + 1) {\n"
			+ "	if (elementInViewport(elementToLookFor)) {\n"
			+ "		break;\n"
			+ "	} else {\n"
			+ "		window.scrollBy(0,5);\n}\n}";

	@BeforeClass
	public static void setUpClass() throws InterruptedException, MalformedURLException
	{
		System.setProperty("webdriver.chrome.driver", "/home/nostromo/chromedriver");
		final DesiredCapabilities cap = DesiredCapabilities.chrome();
		cap.setPlatform(org.openqa.selenium.Platform.LINUX);

		FullAppTraversalTests.chromeDriver = new ChromeDriver(cap);
		FullAppTraversalTests.chromeDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		FullAppTraversalTests.firefoxDriver = new FirefoxDriver();
		FullAppTraversalTests.firefoxDriver.manage().timeouts()
		.implicitlyWait(30, TimeUnit.SECONDS);

		Thread.sleep(2000);

		FullAppTraversalTests.chromeDriver.get(FullAppTraversalTests.HOST + ":"
				+ FullAppTraversalTests.PORT + "/");
		FullAppTraversalTests.firefoxDriver.get(FullAppTraversalTests.HOST + ":"
				+ FullAppTraversalTests.PORT + "/");

		Thread.sleep(2000);
	}

	@AfterClass
	public static void tearDownClass()
	{
		FullAppTraversalTests.firefoxDriver.quit();
		FullAppTraversalTests.chromeDriver.quit();
	}

	@Test
	public void testFullAppTraversal() throws InterruptedException
	{
		// Create a game in Chrome 1
		((JavascriptExecutor)FullAppTraversalTests.chromeDriver)
		.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
		.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);

		Thread.sleep(2000);
		FullAppTraversalTests.chromeDriver.findElement(By.id("createGameLinkResponsive")).click();
		Thread.sleep(2000);

		FullAppTraversalTests.chromeDriver.findElement(By.id("name")).clear();
		FullAppTraversalTests.chromeDriver.findElement(By.id("name")).sendKeys("Zala");
		new Select(FullAppTraversalTests.chromeDriver.findElement(By.id("sideInput"))).getOptions()
		.get(1).click();
		new Select(FullAppTraversalTests.chromeDriver.findElement(By.id("decks"))).getOptions()
		.get(1).click();

		final String gameId = FullAppTraversalTests.chromeDriver.findElement(By.id("gameId"))
				.getText();

		FullAppTraversalTests.chromeDriver.findElement(By.id("createSubmit")).click();
		Thread.sleep(2000);

		// Join a game in Chrome 2
		FullAppTraversalTests.firefoxDriver.findElement(By.id("joinGameLinkResponsive")).click();
		Thread.sleep(2000);
		FullAppTraversalTests.firefoxDriver.findElement(By.id("name")).clear();
		FullAppTraversalTests.firefoxDriver.findElement(By.id("name")).sendKeys("Marie");
		new Select(FullAppTraversalTests.firefoxDriver.findElement(By.id("sideInput")))
		.getOptions().get(2).click();
		new Select(FullAppTraversalTests.firefoxDriver.findElement(By.id("decks"))).getOptions()
		.get(2).click();
		FullAppTraversalTests.firefoxDriver.findElement(By.id("gameIdInput")).clear();
		FullAppTraversalTests.firefoxDriver.findElement(By.id("gameIdInput")).sendKeys(gameId);

		FullAppTraversalTests.firefoxDriver.findElement(By.id("joinSubmit")).click();

		// Assert that no card is present on battlefield
		// The Balduvian Horde is hidden but still there
		// And it contains TWO elements of class magicCard
		Thread.sleep(4000);
		Assert.assertEquals(2,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
				.size());
		Assert.assertEquals(2,
				FullAppTraversalTests.chromeDriver.findElements(By.cssSelector(".magicCard"))
				.size());

		// Verify that the hands contains 7 cards
		Assert.assertEquals(7,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".movers-row"))
				.size());
		Assert.assertEquals(7,
				FullAppTraversalTests.chromeDriver.findElements(By.cssSelector(".movers-row"))
				.size());

		// Find first hand card name of Chrome1
		final String battlefieldCardName = FullAppTraversalTests.firefoxDriver
				.findElements(By.cssSelector(".cross-link:nth-child(1) img")).get(0)
				.getAttribute("name");

		// Play a card in firefox
		FullAppTraversalTests.firefoxDriver.findElement(By.id("playCardLink0")).click();

		// Verify that the hand contains only 6 cards, now
		Thread.sleep(4000);
		Assert.assertEquals(6,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".movers-row"))
				.size());
		Thread.sleep(2000);
		// Verify that card is present on the battlefield
		// Two HTML elements with class "magicCard" are created for each card
		Assert.assertEquals(4,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
				.size());

		Assert.assertEquals(4,
				FullAppTraversalTests.chromeDriver.findElements(By.cssSelector(".magicCard"))
				.size());

		// Verify the name of the card on the battlefield
		Assert.assertEquals(
				battlefieldCardName,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
				.get(2).getAttribute("name"));
		Assert.assertEquals(battlefieldCardName,
				FullAppTraversalTests.chromeDriver.findElements(By.cssSelector(".magicCard"))
				.get(2).getAttribute("name"));

		// Verify that the card is untapped
		Assert.assertFalse(FullAppTraversalTests.firefoxDriver
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("transform"));
		Assert.assertFalse(FullAppTraversalTests.chromeDriver
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("transform"));

		// Tap card
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
		.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_CARD);
		FullAppTraversalTests.firefoxDriver
		.findElement(By.cssSelector("img[id^='tapHandleImage']")).click();
		Thread.sleep(2000);

		// Verify card is tapped
		Assert.assertTrue(FullAppTraversalTests.firefoxDriver
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("rotate(90deg)"));
		Assert.assertTrue(FullAppTraversalTests.chromeDriver
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("rotate(90deg)"));

		// Assert that graveyard is not visible
		Assert.assertTrue(FullAppTraversalTests.firefoxDriver.findElements(
				By.id("graveyard-page-wrap")).isEmpty());

		// Grow up zone images
		((JavascriptExecutor)FullAppTraversalTests.chromeDriver)
		.executeScript("$('#putToGraveyard').attr('src', 'image/graveyard.jpg');");
		((JavascriptExecutor)FullAppTraversalTests.chromeDriver)
		.executeScript("$('#putToHand').attr('src', 'image/hand.jpg');");
		((JavascriptExecutor)FullAppTraversalTests.chromeDriver)
		.executeScript("$('#putToExile').attr('src', 'image/exile.jpg');");

		// Put card to graveyard
		WebElement draggable = FullAppTraversalTests.chromeDriver.findElement(By
				.cssSelector("img[id^='handleImage']"));
		WebElement to = FullAppTraversalTests.chromeDriver.findElement(By.id("putToGraveyard"));
		new Actions(FullAppTraversalTests.chromeDriver).dragAndDrop(draggable, to).build()
				.perform();

		Thread.sleep(2000);

		// Assert graveyard is visible and contains one card
		Assert.assertFalse(FullAppTraversalTests.firefoxDriver.findElements(
				By.id("graveyard-page-wrap")).isEmpty());
		Assert.assertTrue(FullAppTraversalTests.firefoxDriver.findElements(
				By.cssSelector(".graveyard-cross-link")).size() == 1);

		// Verify name of the card in the graveyard
		Assert.assertTrue(battlefieldCardName.equals(FullAppTraversalTests.firefoxDriver
				.findElements(By.cssSelector(".graveyard-cross-link:nth-child(1) img")).get(0)
				.getAttribute("name")));

		// Play card from graveyard
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
		.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		Thread.sleep(2000);
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
		.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RESPONSIVE_MENU);

		FullAppTraversalTests.firefoxDriver.findElement(
				By.id("playCardFromGraveyardLinkResponsive")).click();
		Thread.sleep(2000);

		// Verify the name of the card on the battlefield
		Assert.assertEquals(
				battlefieldCardName,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
				.get(2).getAttribute("name"));
		Assert.assertEquals(battlefieldCardName,
				FullAppTraversalTests.chromeDriver.findElements(By.cssSelector(".magicCard"))
				.get(2).getAttribute("name"));

		// Assert that the graveyard is visible and empty
		Assert.assertFalse(FullAppTraversalTests.firefoxDriver.findElements(
				By.id("graveyard-page-wrap")).isEmpty());
		Assert.assertTrue(FullAppTraversalTests.firefoxDriver.findElements(
				By.cssSelector(".graveyard-cross-link")).isEmpty());

		// Put card to hand
		draggable = FullAppTraversalTests.chromeDriver.findElement(By
				.cssSelector("img[id^='handleImage']"));
		to = FullAppTraversalTests.chromeDriver.findElement(By.id("putToHand"));
		new Actions(FullAppTraversalTests.chromeDriver).dragAndDrop(draggable, to).build()
		.perform();

		Thread.sleep(2000);

		// Assert that the hand contains 7 cards again
		Assert.assertEquals(7,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".movers-row"))
				.size());

		// Reveal top card of library
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
		.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		Thread.sleep(2000);
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
		.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RESPONSIVE_MENU);

		FullAppTraversalTests.firefoxDriver
		.findElement(By.id("revealTopLibraryCardLinkResponsive")).click();
		Thread.sleep(2000);

		// Get top card name
		final String topCardName = FullAppTraversalTests.firefoxDriver.findElement(
				By.id("topLibraryCard")).getAttribute("name");

		// Verify that the card name is the same in the second browser
		Assert.assertTrue(topCardName.equals(FullAppTraversalTests.chromeDriver.findElement(
				By.id("topLibraryCard")).getAttribute("name")));

		// Click on the button "Do nothing"
		FullAppTraversalTests.firefoxDriver.findElement(By.id("doNothing")).click();
		FullAppTraversalTests.chromeDriver.findElement(By.id("doNothing")).click();
		Thread.sleep(2000);

		// Reveal again
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
		.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FullAppTraversalTests.firefoxDriver
		.findElement(By.id("revealTopLibraryCardLinkResponsive")).click();
		Thread.sleep(2000);

		// Assert that the card is the same
		Assert.assertTrue(topCardName.equals(FullAppTraversalTests.firefoxDriver.findElement(
				By.id("topLibraryCard")).getAttribute("name")));
		Assert.assertTrue(topCardName.equals(FullAppTraversalTests.chromeDriver.findElement(
				By.id("topLibraryCard")).getAttribute("name")));

		// Put to battlefield
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
		.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		((JavascriptExecutor)FullAppTraversalTests.chromeDriver)
		.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);

		FullAppTraversalTests.firefoxDriver.findElement(By.id("putToBattlefieldFromModalWindow"))
		.click();
		FullAppTraversalTests.chromeDriver.findElement(By.id("doNothing")).click();

		// Verify that the card is present on the battlefield
		Thread.sleep(2000);
		Assert.assertEquals(4,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
				.size());
		Assert.assertEquals(4,
				FullAppTraversalTests.chromeDriver.findElements(By.cssSelector(".magicCard"))
				.size());

		// Assert that the card on the battlefield is the same
		Assert.assertEquals(
				topCardName,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
				.get(2).getAttribute("name"));
		Assert.assertEquals(topCardName,
				FullAppTraversalTests.chromeDriver.findElements(By.cssSelector(".magicCard"))
				.get(2).getAttribute("name"));

		// Reveal top card of library
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
		.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FullAppTraversalTests.firefoxDriver
		.findElement(By.id("revealTopLibraryCardLinkResponsive")).click();
		Thread.sleep(2000);

		// Put to hand
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
		.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		((JavascriptExecutor)FullAppTraversalTests.chromeDriver)
		.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);

		FullAppTraversalTests.firefoxDriver.findElement(By.id("putToHandFromModalWindow")).click();
		FullAppTraversalTests.chromeDriver.findElement(By.id("doNothing")).click();

		Thread.sleep(2000);

		// Assert that the hand contains 8 cards
		Assert.assertEquals(8,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".movers-row"))
				.size());

		// Verify that there is still two cards on the battlefield
		Assert.assertEquals(4,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
				.size());
		Assert.assertEquals(4,
				FullAppTraversalTests.chromeDriver.findElements(By.cssSelector(".magicCard"))
				.size());

		// Reveal again
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
		.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FullAppTraversalTests.firefoxDriver
		.findElement(By.id("revealTopLibraryCardLinkResponsive")).click();
		Thread.sleep(2000);

		// Get top card name
		final String graveyardCardName = FullAppTraversalTests.firefoxDriver.findElement(
				By.id("topLibraryCard")).getAttribute("name");

		// Put to graveyard
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
		.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		((JavascriptExecutor)FullAppTraversalTests.chromeDriver)
		.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);

		FullAppTraversalTests.firefoxDriver.findElement(By.id("putToGraveyardFromModalWindow"))
		.click();
		FullAppTraversalTests.chromeDriver.findElement(By.id("doNothing")).click();
		Thread.sleep(2000);

		// Assert graveyard is visible and contains one card
		Assert.assertFalse(FullAppTraversalTests.firefoxDriver.findElements(
				By.id("graveyard-page-wrap")).isEmpty());
		Assert.assertTrue(FullAppTraversalTests.firefoxDriver.findElements(
				By.cssSelector(".graveyard-cross-link")).size() == 1);

		// Verify name of the card in the graveyard
		Assert.assertTrue(graveyardCardName.equals(FullAppTraversalTests.firefoxDriver
				.findElements(By.cssSelector(".graveyard-cross-link:nth-child(1) img")).get(0)
				.getAttribute("name")));

		// Verify that there is still two cards on the battlefield
		Assert.assertEquals(4,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
				.size());
		Assert.assertEquals(4,
				FullAppTraversalTests.chromeDriver.findElements(By.cssSelector(".magicCard"))
				.size());

		// Verify the name of the card on the battlefield
		Assert.assertEquals(
				topCardName,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
				.get(2).getAttribute("name"));
		Assert.assertEquals(topCardName,
				FullAppTraversalTests.chromeDriver.findElements(By.cssSelector(".magicCard"))
				.get(2).getAttribute("name"));

		// Verify that the hands contains 8 cards
		Assert.assertEquals(8,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".movers-row"))
				.size());
		Assert.assertEquals(7,
				FullAppTraversalTests.chromeDriver.findElements(By.cssSelector(".movers-row"))
				.size());

		// Put one card from hand to graveyard
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
		.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_PUT_TO_ZONE_SUMBIT_BUTTON_FOR_HAND);
		new Select(FullAppTraversalTests.firefoxDriver.findElement(By.id("putToZoneSelectForHand")))
		.getOptions().get(1).click();
		FullAppTraversalTests.firefoxDriver.findElement(By.id("moveToZoneSubmitHand")).click();
		Thread.sleep(2000);

		// Verify that there is one more card in the graveyard
		Assert.assertFalse(FullAppTraversalTests.firefoxDriver.findElements(
				By.id("graveyard-page-wrap")).isEmpty());
		Assert.assertEquals(
				2,
				FullAppTraversalTests.firefoxDriver.findElements(
						By.cssSelector(".graveyard-cross-link")).size());

		// Put current card from hand to exile
		new Select(FullAppTraversalTests.firefoxDriver.findElement(By.id("putToZoneSelectForHand")))
		.getOptions().get(2).click();
		FullAppTraversalTests.firefoxDriver.findElement(By.id("moveToZoneSubmitHand")).click();
		Thread.sleep(2000);

		// Verify that there is one more card in the exile and that it is
		// visible
		Assert.assertFalse(FullAppTraversalTests.firefoxDriver.findElements(
				By.id("exile-page-wrap")).isEmpty());
		Assert.assertEquals(
				1,
				FullAppTraversalTests.firefoxDriver.findElements(
						By.cssSelector(".exile-cross-link")).size());

		// Put current card in exile to graveyard
		new Select(
				FullAppTraversalTests.firefoxDriver.findElement(By.id("putToZoneSelectForExile")))
		.getOptions().get(1).click();
		FullAppTraversalTests.firefoxDriver.findElement(By.id("moveToZoneSubmitExile")).click();
		Thread.sleep(2000);

		// Verify that there is one more card in the graveyard
		Assert.assertFalse(FullAppTraversalTests.firefoxDriver.findElements(
				By.id("graveyard-page-wrap")).isEmpty());
		Assert.assertEquals(
				3,
				FullAppTraversalTests.firefoxDriver.findElements(
						By.cssSelector(".graveyard-cross-link")).size());

		// Get name of the current card in the hand
		final String handCardName = FullAppTraversalTests.firefoxDriver
				.findElements(By.cssSelector(".cross-link:nth-child(1) img")).get(0)
				.getAttribute("name");

		// Put current card from hand to exile
		new Select(FullAppTraversalTests.firefoxDriver.findElement(By.id("putToZoneSelectForHand")))
		.getOptions().get(2).click();
		FullAppTraversalTests.firefoxDriver.findElement(By.id("moveToZoneSubmitHand")).click();
		Thread.sleep(2000);

		// Verify that there is one more card in the exile
		Assert.assertFalse(FullAppTraversalTests.firefoxDriver.findElements(
				By.id("exile-page-wrap")).isEmpty());
		Assert.assertEquals(
				1,
				FullAppTraversalTests.firefoxDriver.findElements(
						By.cssSelector(".exile-cross-link")).size());

		// Get name of the current card in the exile
		final String exileCardName = FullAppTraversalTests.firefoxDriver
				.findElements(By.cssSelector(".exile-cross-link:nth-child(1) img")).get(0)
				.getAttribute("name");

		// Verify that active card in exile is same than card from hand
		Assert.assertEquals(handCardName, exileCardName);

		// Put card from exile to battlefield
		new Select(
				FullAppTraversalTests.firefoxDriver.findElement(By.id("putToZoneSelectForExile")))
		.getOptions().get(0).click();
		FullAppTraversalTests.firefoxDriver.findElement(By.id("moveToZoneSubmitExile")).click();
		Thread.sleep(2000);

		// Verify that there are three cards on the battlefield
		Assert.assertEquals(6,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
				.size());
		Assert.assertEquals(6,
				FullAppTraversalTests.chromeDriver.findElements(By.cssSelector(".magicCard"))
				.size());

		// Verify the name of the card on the battlefield
		Assert.assertEquals(
				exileCardName,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
				.get(4).getAttribute("name"));
		Assert.assertEquals(exileCardName,
				FullAppTraversalTests.chromeDriver.findElements(By.cssSelector(".magicCard"))
				.get(4).getAttribute("name"));
	}

}
