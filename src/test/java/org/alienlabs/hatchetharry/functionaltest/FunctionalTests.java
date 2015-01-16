package org.alienlabs.hatchetharry.functionaltest;

import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class FunctionalTests
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FunctionalTests.class);

	private static final String PORT = "8088";
	private static final String HOST = "localhost";
	private static final Server SERVER = new Server();

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
			+ "var elementToLookFor = jQuery(\".magicCard .tap-handle-image\");\n"
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

	private static final String QUNIT_FAILED_TESTS = "0";
	private static final String QUNIT_PASSED_TESTS = "6";
	private static final String QUNIT_TOTAL_TESTS = "6";

	private static final String MISTLETOE_FAILED_TESTS = "Errors/Failures: 0";
	private static final String MISTLETOE_TOTAL_TESTS = "Total tests: 2";

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
			+ "    (left + width + 10) < (window.pageXOffset + window.innerWidth)\n"
			+ "  );\n"
			+ "}\n"
			+ "\n"
			+ "var elementToLookFor = document.getElementById('runMistletoe');\n"
			+ "\n"
			+ "for (var i = 0; i < 10000; i = i + 1) {\n"
			+ "	if (elementInViewport(elementToLookFor)) {\n"
			+ "		break;\n"
			+ "	} else {\n"
			+ "		window.scrollBy(1,10);\n}\n}";
	private static final String SCROLL_DOWN = "window.scrollBy(0,50);";
	private static final String CLICK_PLAY_CARD_LINK = "$('#playCardLink0').click();";

	private static WebDriver chromeDriver1;
	private static WebDriver chromeDriver2;

	@BeforeClass
	@Ignore("Just to be able to push to Github")
	public static void setUp() throws Exception
	{
		FunctionalTests.LOGGER
				.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> STARTING EMBEDDED JETTY SERVER");

		final ServerConnector http = new ServerConnector(FunctionalTests.SERVER);
		http.setHost(FunctionalTests.HOST);
		http.setPort(Integer.parseInt(FunctionalTests.PORT));
		http.setIdleTimeout(30000);
		FunctionalTests.SERVER.addConnector(http);
		final WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setWar("src/main/webapp");
		FunctionalTests.SERVER.setHandler(webapp);
		FunctionalTests.SERVER.start();

		FunctionalTests.LOGGER
				.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SUCCESSFULLY STARTED EMBEDDED JETTY SERVER");

		System.setProperty("webdriver.chrome.driver", "/home/nostromo/chromedriver");
		FunctionalTests.chromeDriver1 = new ChromeDriver();
		FunctionalTests.chromeDriver1.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		FunctionalTests.chromeDriver2 = new ChromeDriver();
		FunctionalTests.chromeDriver2.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		FunctionalTests.chromeDriver1.get("http://" + FunctionalTests.HOST + ":"
				+ FunctionalTests.PORT + "/");
		FunctionalTests.chromeDriver2.get("http://" + FunctionalTests.HOST + ":"
				+ FunctionalTests.PORT + "/");
	}

	@AfterClass
	public static void tearDown()
	{
		if (null != FunctionalTests.chromeDriver1)
		{
			FunctionalTests.chromeDriver1.quit();
		}
		if (null != FunctionalTests.chromeDriver2)
		{
			FunctionalTests.chromeDriver2.quit();
		}

		FunctionalTests.LOGGER
				.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> STOPPING EMBEDDED JETTY SERVER");
		try
		{
			FunctionalTests.SERVER.stop();
			FunctionalTests.SERVER.join();
		}
		catch (final Exception e)
		{
			FunctionalTests.LOGGER.error("Error in stopping EMBEDDED JETTY SERVER", e);
		}
	}

	@Test
	@Ignore("Just to be able to push to Github")
	public void testFullAppTraversal() throws InterruptedException
	{
		// Create a game in Chrome 1
		((JavascriptExecutor)FunctionalTests.chromeDriver1)
				.executeScript(FunctionalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);

		FunctionalTests.chromeDriver1.findElement(By.id("createGameLinkResponsive")).click();
		new WebDriverWait(FunctionalTests.chromeDriver1, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("name")));

		FunctionalTests.chromeDriver1.findElement(By.id("name")).clear();
		FunctionalTests.chromeDriver1.findElement(By.id("name")).sendKeys("Zala");
		new Select(FunctionalTests.chromeDriver1.findElement(By.id("sideInput"))).getOptions()
				.get(1).click();
		new Select(FunctionalTests.chromeDriver1.findElement(By.id("decks"))).getOptions().get(1)
				.click();

		final String gameId = FunctionalTests.chromeDriver1.findElement(By.id("gameId")).getText();

		FunctionalTests.chromeDriver1.findElement(By.id("createSubmit")).click();
		Thread.sleep(5000);

		// Join a game in chrome
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
				.executeScript(FunctionalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));
		Thread.sleep(10000);

		((JavascriptExecutor)FunctionalTests.chromeDriver2)
				.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RESPONSIVE_MENU);
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("revealTopLibraryCardLinkResponsive")));
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("joinGameLinkResponsive")));
		FunctionalTests.chromeDriver2.findElement(By.id("joinGameLinkResponsive")).click();

		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("name")));
		FunctionalTests.chromeDriver2.findElement(By.id("name")).clear();
		FunctionalTests.chromeDriver2.findElement(By.id("name")).sendKeys("Marie");
		new Select(FunctionalTests.chromeDriver2.findElement(By.id("sideInput"))).getOptions()
				.get(2).click();
		new Select(FunctionalTests.chromeDriver2.findElement(By.id("decks"))).getOptions().get(3)
				.click();
		FunctionalTests.chromeDriver2.findElement(By.id("gameIdInput")).clear();
		FunctionalTests.chromeDriver2.findElement(By.id("gameIdInput")).sendKeys(gameId);

		FunctionalTests.chromeDriver2.findElement(By.id("joinSubmit")).click();

		// Assert that no card is present on battlefield
		Thread.sleep(10000);
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".magicCard")));
		Assert.assertEquals(1,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".magicCard")).size());
		Assert.assertEquals(1,
				FunctionalTests.chromeDriver1.findElements(By.cssSelector(".magicCard")).size());

		// Verify that the hands contains 7 cards
		Assert.assertEquals(7,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".movers-row")).size());
		Assert.assertEquals(7,
				FunctionalTests.chromeDriver1.findElements(By.cssSelector(".movers-row")).size());

		// Find first hand card name of Chrome2
		final String battlefieldCardName = FunctionalTests.chromeDriver2
				.findElements(By.cssSelector(".active-thumb-Hand img")).get(0).getAttribute("name");

		// Play a card in chrome2
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("playCardLink0")));
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
				.executeScript(FunctionalTests.CLICK_PLAY_CARD_LINK);
		Thread.sleep(15000);

		// Verify that the hand contains only 6 cards, now
		Assert.assertEquals(6,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".movers-row")).size());

		// Verify that card is present on the battlefield
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".clickableCard")));
		Assert.assertEquals(1,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".clickableCard")).size());

		Assert.assertEquals(1,
				FunctionalTests.chromeDriver1.findElements(By.cssSelector(".clickableCard")).size());

		// Verify the name of the card on the battlefield
		Assert.assertEquals(
				battlefieldCardName,
				FunctionalTests.chromeDriver1.findElements(By.cssSelector(".clickableCard")).get(0)
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.getAttribute("name"));
		Assert.assertEquals(
				battlefieldCardName,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".clickableCard")).get(0)
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.getAttribute("name"));

		// Verify that the card is untapped
		assertFalse(FunctionalTests.chromeDriver2.findElements(By.cssSelector(".magicCard")).get(2)
				.getAttribute("style").contains("transform"));
		assertFalse(FunctionalTests.chromeDriver1.findElements(By.cssSelector(".magicCard")).get(2)
				.getAttribute("style").contains("transform"));

		// Tap card
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
				.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_CARD);
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.cssSelector(".clickableCard + .tap-handle-image")));
		FunctionalTests.chromeDriver2.findElement(
				By.cssSelector(".clickableCard + .tap-handle-image")).click();
		Thread.sleep(10000);

		// Verify card is tapped
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".clickableCard")));
		assertTrue(FunctionalTests.chromeDriver2.findElements(By.cssSelector(".magicCard")).get(1)
				.findElement(By.cssSelector(".clickableCard")).getAttribute("style")
				.contains("rotate(90deg)"));
		assertTrue(FunctionalTests.chromeDriver1.findElements(By.cssSelector(".magicCard")).get(1)
				.findElement(By.cssSelector(".clickableCard")).getAttribute("style")
				.contains("rotate(90deg)"));

		// Assert that graveyard is not visible
		assertTrue(FunctionalTests.chromeDriver2.findElements(By.id("graveyard-page-wrap"))
				.isEmpty());

		// Grow up zone images
		((JavascriptExecutor)FunctionalTests.chromeDriver1)
				.executeScript("$('#putToGraveyard').attr('src', 'image/graveyard.jpg');");
		((JavascriptExecutor)FunctionalTests.chromeDriver1)
				.executeScript("$('#putToHand').attr('src', 'image/hand.jpg');");
		((JavascriptExecutor)FunctionalTests.chromeDriver1)
				.executeScript("$('#putToExile').attr('src', 'image/exile.jpg');");

		((JavascriptExecutor)FunctionalTests.chromeDriver2)
				.executeScript("$('#putToGraveyard').attr('src', 'image/graveyard.jpg');");
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
				.executeScript("$('#putToHand').attr('src', 'image/hand.jpg');");
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
				.executeScript("$('#putToExile').attr('src', 'image/exile.jpg');");

		// Put card to graveyard
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector("img[id^='handleImage")));
		WebElement draggable = FunctionalTests.chromeDriver1.findElements(
				By.cssSelector("img[id^='handleImage']")).get(0);
		WebElement to = FunctionalTests.chromeDriver1.findElement(By.id("putToGraveyard"));
		new Actions(FunctionalTests.chromeDriver1).dragAndDrop(draggable, to).build().perform();
		Thread.sleep(15000);

		// Play card from graveyard
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
				.executeScript(FunctionalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		Thread.sleep(10000);
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));

		((JavascriptExecutor)FunctionalTests.chromeDriver2)
				.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RESPONSIVE_MENU);

		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("playCardFromGraveyardLinkResponsive")));
		Thread.sleep(10000);
		FunctionalTests.chromeDriver2.findElement(By.id("playCardFromGraveyardLinkResponsive"))
				.click();
		Thread.sleep(10000);

		// Verify the name of the card on the battlefield
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".clickableCard")));
		Assert.assertEquals(
				battlefieldCardName,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".clickableCard")).get(0)
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.getAttribute("name"));
		Assert.assertEquals(
				battlefieldCardName,
				FunctionalTests.chromeDriver1.findElements(By.cssSelector(".clickableCard")).get(0)
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.getAttribute("name"));

		// Assert that the graveyard is visible and empty
		assertFalse(FunctionalTests.chromeDriver2.findElements(By.id("graveyard-page-wrap"))
				.isEmpty());
		assertTrue(FunctionalTests.chromeDriver2.findElements(
				By.cssSelector(".graveyard-cross-link")).isEmpty());

		// Put card to hand
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector("img.drag-handle-image")));
		draggable = FunctionalTests.chromeDriver2.findElements(
				By.cssSelector("img.drag-handle-image")).get(0);
		to = FunctionalTests.chromeDriver2.findElement(By.id("putToHand"));
		new Actions(FunctionalTests.chromeDriver2).dragAndDrop(draggable, to).build().perform();

		Thread.sleep(10000);

		// Assert that the hand contains 7 cards again
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".movers-row")));
		Assert.assertEquals(7,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".movers-row")).size());

		// Reveal top card of library
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
				.executeScript(FunctionalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));
		Thread.sleep(10000);
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
				.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RESPONSIVE_MENU);
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("revealTopLibraryCardLinkResponsive")));
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("revealTopLibraryCardLinkResponsive")));
		FunctionalTests.chromeDriver2.findElement(By.id("revealTopLibraryCardLinkResponsive"))
				.click();
		Thread.sleep(10000);

		// Get top card name
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("topLibraryCard")));
		final String topCardName = FunctionalTests.chromeDriver2.findElement(
				By.id("topLibraryCard")).getAttribute("name");

		// Verify that the card name is the same in the second browser
		new WebDriverWait(FunctionalTests.chromeDriver1, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("topLibraryCard")));
		Assert.assertEquals(
				topCardName,
				FunctionalTests.chromeDriver1.findElement(By.id("topLibraryCard")).getAttribute(
						"name"));

		// Click on the button "Do nothing"
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("doNothing")));
		FunctionalTests.chromeDriver2.findElement(By.id("doNothing")).click();
		FunctionalTests.chromeDriver1.findElement(By.id("doNothing")).click();
		Thread.sleep(10000);

		// Reveal again
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
				.executeScript(FunctionalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("revealTopLibraryCardLinkResponsive")));
		FunctionalTests.chromeDriver2.findElement(By.id("revealTopLibraryCardLinkResponsive"))
				.click();
		Thread.sleep(10000);

		// Assert that the card is the same
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("topLibraryCard")));
		Assert.assertEquals(
				topCardName,
				FunctionalTests.chromeDriver2.findElement(By.id("topLibraryCard")).getAttribute(
						"name"));
		Assert.assertEquals(
				topCardName,
				FunctionalTests.chromeDriver1.findElement(By.id("topLibraryCard")).getAttribute(
						"name"));

		// Put to battlefield
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
				.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));
		((JavascriptExecutor)FunctionalTests.chromeDriver1)
				.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("putToBattlefieldFromModalWindow")));

		FunctionalTests.chromeDriver2.findElement(By.id("putToBattlefieldFromModalWindow")).click();
		FunctionalTests.chromeDriver1.findElement(By.id("doNothing")).click();

		// Verify that the card is present on the battlefield
		Thread.sleep(10000);
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".clickableCard")));
		Assert.assertEquals(1,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".clickableCard")).size());
		Assert.assertEquals(1,
				FunctionalTests.chromeDriver1.findElements(By.cssSelector(".clickableCard")).size());

		// Assert that the card on the battlefield is the same
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".clickableCard")));
		Assert.assertEquals(
				topCardName,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".clickableCard")).get(0)
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.getAttribute("name"));
		Assert.assertEquals(
				topCardName,
				FunctionalTests.chromeDriver1.findElements(By.cssSelector(".clickableCard")).get(0)
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.getAttribute("name"));

		// Reveal top card of library
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
				.executeScript(FunctionalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));
		FunctionalTests.chromeDriver2.findElement(By.id("revealTopLibraryCardLinkResponsive"))
				.click();
		Thread.sleep(10000);

		// Put to hand
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
				.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));
		((JavascriptExecutor)FunctionalTests.chromeDriver1)
				.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		new WebDriverWait(FunctionalTests.chromeDriver1, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));


		Thread.sleep(5000);
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("putToHandFromModalWindow")));
		FunctionalTests.chromeDriver2.findElement(By.id("putToHandFromModalWindow")).click();
		FunctionalTests.chromeDriver1.findElement(By.id("doNothing")).click();

		Thread.sleep(10000);

		// Assert that the hand contains 8 cards
		Assert.assertEquals(8,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".movers-row")).size());

		// Verify that there is still two cards on the battlefield
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".tap-handle-image")));
		Assert.assertEquals(1,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".tap-handle-image"))
						.size());
		Assert.assertEquals(1,
				FunctionalTests.chromeDriver1.findElements(By.cssSelector(".tap-handle-image"))
						.size());

		// Reveal again
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
				.executeScript(FunctionalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("revealTopLibraryCardLinkResponsive")));
		FunctionalTests.chromeDriver2.findElement(By.id("revealTopLibraryCardLinkResponsive"))
				.click();
		Thread.sleep(10000);

		// Get top card name
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("topLibraryCard")));
		final String graveyardCardName = FunctionalTests.chromeDriver2.findElement(
				By.id("topLibraryCard")).getAttribute("name");

		// Put to graveyard
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
				.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));
		((JavascriptExecutor)FunctionalTests.chromeDriver1)
				.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		new WebDriverWait(FunctionalTests.chromeDriver1, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));

		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("putToGraveyardFromModalWindow")));
		FunctionalTests.chromeDriver2.findElement(By.id("putToGraveyardFromModalWindow")).click();
		FunctionalTests.chromeDriver1.findElement(By.id("doNothing")).click();
		Thread.sleep(10000);

		// Assert graveyard is visible and contains one card
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("graveyard-page-wrap")));
		assertFalse(FunctionalTests.chromeDriver2.findElements(By.id("graveyard-page-wrap"))
				.isEmpty());
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".graveyard-cross-link")));
		Assert.assertEquals(1,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".graveyard-cross-link"))
						.size());

		// Verify name of the card in the graveyard
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".graveyard-cross-link")));
		Assert.assertEquals(
				graveyardCardName,
				FunctionalTests.chromeDriver2
						.findElements(By.cssSelector(".graveyard-cross-link:nth-child(1) img"))
						.get(0).getAttribute("name"));

		// Verify that there is still one card on the battlefield
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".tap-handle-image")));
		Assert.assertEquals(1,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".tap-handle-image"))
						.size());
		Assert.assertEquals(1,
				FunctionalTests.chromeDriver1.findElements(By.cssSelector(".tap-handle-image"))
						.size());

		// Verify the name of the card on the battlefield
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".clickableCard")));
		Assert.assertEquals(
				topCardName,
				FunctionalTests.chromeDriver1.findElements(By.cssSelector(".clickableCard")).get(0)
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.getAttribute("name"));
		Assert.assertEquals(
				topCardName,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".clickableCard")).get(0)
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.getAttribute("name"));

		// Verify that the hands contains 8 cards
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".movers-row")));
		Assert.assertEquals(8,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".movers-row")).size());
		Assert.assertEquals(7,
				FunctionalTests.chromeDriver1.findElements(By.cssSelector(".movers-row")).size());

		// Put one card from hand to graveyard
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
				.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_PUT_TO_ZONE_SUMBIT_BUTTON_FOR_HAND);
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("moveToZoneSubmitHand")));
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("putToZoneSelectForHand")));
		new Select(FunctionalTests.chromeDriver2.findElement(By.id("putToZoneSelectForHand")))
				.getOptions().get(1).click();
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("moveToZoneSubmitHand")));
		FunctionalTests.chromeDriver2.findElement(By.id("moveToZoneSubmitHand")).click();
		Thread.sleep(10000);

		// Verify that there is one more card in the graveyard
		assertFalse(FunctionalTests.chromeDriver2.findElements(By.id("graveyard-page-wrap"))
				.isEmpty());
		Assert.assertEquals(2,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".graveyard-cross-link"))
						.size());

		// Put current card from hand to exile
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("putToZoneSelectForHand")));
		new Select(FunctionalTests.chromeDriver2.findElement(By.id("putToZoneSelectForHand")))
				.getOptions().get(2).click();
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("moveToZoneSubmitHand")));
		FunctionalTests.chromeDriver2.findElement(By.id("moveToZoneSubmitHand")).click();
		Thread.sleep(10000);

		// Verify that there is one more card in the exile and that it is
		// visible
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("exile-page-wrap")));
		assertFalse(FunctionalTests.chromeDriver2.findElements(By.id("exile-page-wrap")).isEmpty());
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".exile-cross-link")));
		Assert.assertEquals(1,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".exile-cross-link"))
						.size());

		// Put current card in exile to graveyard
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("putToZoneSelectForExile")));
		new Select(FunctionalTests.chromeDriver2.findElement(By.id("putToZoneSelectForExile")))
				.getOptions().get(1).click();
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("moveToZoneSubmitExile")));
		FunctionalTests.chromeDriver2.findElement(By.id("moveToZoneSubmitExile")).click();
		Thread.sleep(10000);

		// Verify that there is one more card in the graveyard
		Assert.assertEquals(3,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".graveyard-nav-thumb"))
						.size());

		// Get name of the current card in the hand
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".cross-link")));
		final String handCardName = FunctionalTests.chromeDriver2
				.findElements(By.cssSelector(".cross-link:nth-child(1) img")).get(0)
				.getAttribute("name");

		// Put current card from hand to exile
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("putToZoneSelectForHand")));
		new Select(FunctionalTests.chromeDriver2.findElement(By.id("putToZoneSelectForHand")))
				.getOptions().get(2).click();
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("moveToZoneSubmitHand")));
		FunctionalTests.chromeDriver2.findElement(By.id("moveToZoneSubmitHand")).click();
		Thread.sleep(10000);

		// Verify that there is one more card in the exile
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("exile-page-wrap")));
		assertFalse(FunctionalTests.chromeDriver2.findElements(By.id("exile-page-wrap")).isEmpty());
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".exile-cross-link")));
		Assert.assertEquals(1,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".exile-cross-link"))
						.size());

		// Get name of the current card in the exile
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".exile-cross-link")));
		final String exileCardName = FunctionalTests.chromeDriver2
				.findElements(By.cssSelector(".exile-cross-link:nth-child(1) img")).get(0)
				.getAttribute("name");

		// Verify that active card in exile is same than card from hand
		Assert.assertEquals(handCardName, exileCardName);

		// Put card from exile to battlefield
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("putToZoneSelectForExile")));
		new Select(FunctionalTests.chromeDriver2.findElement(By.id("putToZoneSelectForExile")))
				.getOptions().get(0).click();
		new WebDriverWait(FunctionalTests.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("moveToZoneSubmitExile")));
		FunctionalTests.chromeDriver2.findElement(By.id("moveToZoneSubmitExile")).click();
		Thread.sleep(10000);

		// Verify that there are 2 cards on the battlefield
		Assert.assertEquals(2,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".tap-handle-image"))
						.size());
		Assert.assertEquals(2,
				FunctionalTests.chromeDriver1.findElements(By.cssSelector(".tap-handle-image"))
						.size());

		// Verify the name of the card on the battlefield
		Assert.assertEquals(
				exileCardName,
				FunctionalTests.chromeDriver2.findElements(By.cssSelector(".clickableCard")).get(1)
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.getAttribute("name"));
		Assert.assertEquals(
				exileCardName,
				FunctionalTests.chromeDriver1.findElements(By.cssSelector(".clickableCard")).get(1)
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.findElement(By.xpath("..")).findElement(By.xpath(".."))
						.getAttribute("name"));
	}

	@Test
	@Ignore("Just to be able to push to Github")
	public void testQunit()
	{
		final String passed1 = FunctionalTests.chromeDriver1.findElement(By.id("passed")).getText();
		final String total1 = FunctionalTests.chromeDriver1.findElement(By.id("total")).getText();
		final String failed1 = FunctionalTests.chromeDriver1.findElement(By.id("failed")).getText();

		Assert.assertEquals(FunctionalTests.QUNIT_PASSED_TESTS, passed1);
		Assert.assertEquals(FunctionalTests.QUNIT_TOTAL_TESTS, total1);
		Assert.assertEquals(FunctionalTests.QUNIT_FAILED_TESTS, failed1);
	}

	@Test
	public void testMistletoe() throws InterruptedException
	{
		// Sleep in order for the page scrolling up (because of the qunit tests) not to disturb us
		Thread.sleep(12000);
		// TODO vérifier qu'il y a bien 3 decks de disponibles
		((JavascriptExecutor)FunctionalTests.chromeDriver1)
				.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RUN_BUTTON);
		FunctionalTests.chromeDriver1.findElement(By.id("runMistletoe")).click();

		((JavascriptExecutor)FunctionalTests.chromeDriver1)
				.executeScript(FunctionalTests.SCROLL_DOWN);

		// Sleep in order to wait for the results to appear
		Thread.sleep(12000);

		final String chromeTotal = FunctionalTests.chromeDriver1.findElement(By.id("runsSummary"))
				.getText();
		final String chromeFailed = FunctionalTests.chromeDriver1.findElement(
				By.id("errorsSummary")).getText();

		Assert.assertEquals(FunctionalTests.MISTLETOE_TOTAL_TESTS, chromeTotal);
		Assert.assertEquals(FunctionalTests.MISTLETOE_FAILED_TESTS, chromeFailed);
	}

}
