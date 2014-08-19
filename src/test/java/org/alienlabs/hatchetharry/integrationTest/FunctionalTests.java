package org.alienlabs.hatchetharry.integrationTest;

import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class FunctionalTests
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FunctionalTests.class);

	private static final String PORT = "8088";
	private static final String HOST = "localhost";
	private static final Server server = new Server();

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
		+ "		window.scrollBy(0,1);\n}\n}";
	private static final String SCROLL_DOWN = "window.scrollBy(0,50);";

	private static WebDriver chromeDriver1;
	private static WebDriver chromeDriver2;

	@BeforeClass
	public static void setUp() throws Exception
	{
		FunctionalTests.LOGGER
			.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> STARTING EMBEDDED JETTY SERVER");

		final ServerConnector http = new ServerConnector(FunctionalTests.server);
		http.setHost(FunctionalTests.HOST);
		http.setPort(Integer.parseInt(FunctionalTests.PORT));
		http.setIdleTimeout(30000);
		FunctionalTests.server.addConnector(http);
		final WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setWar("src/main/webapp");
		FunctionalTests.server.setHandler(webapp);
		FunctionalTests.server.start();

		FunctionalTests.LOGGER
			.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SUCCESSFULLY STARTED EMBEDDED JETTY SERVER");

		System.setProperty("webdriver.chrome.driver", "/home/nostromo/chromedriver");
		FunctionalTests.chromeDriver1 = new ChromeDriver();
		FunctionalTests.chromeDriver1.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		FunctionalTests.chromeDriver2 = new ChromeDriver();
		FunctionalTests.chromeDriver2.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		Thread.sleep(5000);

		FunctionalTests.chromeDriver1.get("http://" + FunctionalTests.HOST + ":"
			+ FunctionalTests.PORT + "/");
		FunctionalTests.chromeDriver2.get("http://" + FunctionalTests.HOST + ":"
			+ FunctionalTests.PORT + "/");

		Thread.sleep(15000);
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
			FunctionalTests.server.stop();
			FunctionalTests.server.join();
		}
		catch (Exception e)
		{
            FunctionalTests.LOGGER.error("Error in stopping EMBEDDED JETTY SERVER, e");
		}
	}

	@Test
	public void testFullAppTraversal() throws InterruptedException
	{
		// Create a game in Chrome 1
		((JavascriptExecutor)FunctionalTests.chromeDriver1)
			.executeScript(FunctionalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);

		FunctionalTests.chromeDriver1.findElement(By.id("createGameLinkResponsive")).click();
		Thread.sleep(1000);

		FunctionalTests.chromeDriver1.findElement(By.id("name")).clear();
		FunctionalTests.chromeDriver1.findElement(By.id("name")).sendKeys("Zala");
		new Select(FunctionalTests.chromeDriver1.findElement(By.id("sideInput"))).getOptions()
			.get(1).click();
		new Select(FunctionalTests.chromeDriver1.findElement(By.id("decks"))).getOptions().get(1)
			.click();

		final String gameId = FunctionalTests.chromeDriver1.findElement(By.id("gameId")).getText();

		FunctionalTests.chromeDriver1.findElement(By.id("createSubmit")).click();
		Thread.sleep(2000);

		// Join a game in chrome
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
			.executeScript(FunctionalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		Thread.sleep(1000);
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
			.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RESPONSIVE_MENU);
		Thread.sleep(1000);
		FunctionalTests.chromeDriver2.findElement(By.id("joinGameLinkResponsive")).click();
		Thread.sleep(1000);

		FunctionalTests.chromeDriver2.findElement(By.id("name")).clear();
		FunctionalTests.chromeDriver2.findElement(By.id("name")).sendKeys("Marie");
		Thread.sleep(1000);
		new Select(FunctionalTests.chromeDriver2.findElement(By.id("sideInput"))).getOptions()
			.get(2).click();
		new Select(FunctionalTests.chromeDriver2.findElement(By.id("decks"))).getOptions().get(3)
			.click();
		FunctionalTests.chromeDriver2.findElement(By.id("gameIdInput")).clear();
		FunctionalTests.chromeDriver2.findElement(By.id("gameIdInput")).sendKeys(gameId);

		FunctionalTests.chromeDriver2.findElement(By.id("joinSubmit")).click();

		// Assert that no card is present on battlefield
		Thread.sleep(2000);
		assertEquals(1, FunctionalTests.chromeDriver2.findElements(By.cssSelector(".magicCard"))
			.size());
		assertEquals(1, FunctionalTests.chromeDriver1.findElements(By.cssSelector(".magicCard"))
			.size());

		// Verify that the hands contains 7 cards
		assertEquals(7, FunctionalTests.chromeDriver2.findElements(By.cssSelector(".movers-row"))
			.size());
		assertEquals(7, FunctionalTests.chromeDriver1.findElements(By.cssSelector(".movers-row"))
			.size());

		// Find first hand card name of Chrome1
		final String battlefieldCardName = FunctionalTests.chromeDriver2
			.findElements(By.cssSelector(".cross-link:nth-child(1) img")).get(0)
			.getAttribute("name");

		// Play a card in chrome
		Thread.sleep(2500);
		FunctionalTests.chromeDriver2.findElement(By.id("playCardLink0")).click();

		// Verify that the hand contains only 6 cards, now
		Thread.sleep(3000);
		assertEquals(6, FunctionalTests.chromeDriver2.findElements(By.cssSelector(".movers-row"))
			.size());

		// Verify that card is present on the battlefield
		assertEquals(1, FunctionalTests.chromeDriver2
			.findElements(By.cssSelector(".clickableCard")).size());

		assertEquals(1, FunctionalTests.chromeDriver1
			.findElements(By.cssSelector(".clickableCard")).size());

		// Verify the name of the card on the battlefield
		assertEquals(battlefieldCardName,
			FunctionalTests.chromeDriver1.findElements(By.cssSelector(".clickableCard")).get(0)
				.findElement(By.xpath("..")).findElement(By.xpath(".."))
				.findElement(By.xpath("..")).findElement(By.xpath("..")).getAttribute("name"));
		assertEquals(battlefieldCardName,
			FunctionalTests.chromeDriver2.findElements(By.cssSelector(".clickableCard")).get(0)
				.findElement(By.xpath("..")).findElement(By.xpath(".."))
				.findElement(By.xpath("..")).findElement(By.xpath("..")).getAttribute("name"));

		// Verify that the card is untapped
		assertFalse(FunctionalTests.chromeDriver2.findElements(By.cssSelector(".magicCard")).get(2)
			.getAttribute("style").contains("transform"));
		assertFalse(FunctionalTests.chromeDriver1.findElements(By.cssSelector(".magicCard")).get(2)
			.getAttribute("style").contains("transform"));

		// Tap card
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
			.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_CARD);
		FunctionalTests.chromeDriver2.findElements(By.cssSelector("img[id^='tapHandleImage']"))
			.get(0).click();
		Thread.sleep(2000);

		// Verify card is tapped
		assertTrue(FunctionalTests.chromeDriver2.findElements(By.cssSelector(".clickableCard"))
			.get(0).getAttribute("style").contains("rotate(90deg)"));
		assertTrue(FunctionalTests.chromeDriver1.findElements(By.cssSelector(".clickableCard"))
			.get(0).getAttribute("style").contains("rotate(90deg)"));

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
		WebElement draggable = FunctionalTests.chromeDriver1.findElements(
			By.cssSelector("img[id^='handleImage']")).get(0);
		WebElement to = FunctionalTests.chromeDriver1.findElement(By.id("putToGraveyard"));
		new Actions(FunctionalTests.chromeDriver1).dragAndDrop(draggable, to).build().perform();

		Thread.sleep(2000);

		// Assert graveyard is visible and contains one card
		assertFalse(FunctionalTests.chromeDriver2.findElements(By.id("graveyard-page-wrap"))
			.isEmpty());
		assertTrue(FunctionalTests.chromeDriver2.findElements(
			By.cssSelector(".graveyard-cross-link")).size() == 1);

		// Verify name of the card in the graveyard
		assertTrue(battlefieldCardName.equals(FunctionalTests.chromeDriver2
			.findElements(By.cssSelector(".graveyard-cross-link:nth-child(1) img")).get(0)
			.getAttribute("name")));

		// Play card from graveyard
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
			.executeScript(FunctionalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		Thread.sleep(2000);
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
			.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RESPONSIVE_MENU);

		FunctionalTests.chromeDriver2.findElement(By.id("playCardFromGraveyardLinkResponsive"))
			.click();
		Thread.sleep(2000);

		// Verify the name of the card on the battlefield
		assertEquals(battlefieldCardName,
			FunctionalTests.chromeDriver2.findElements(By.cssSelector(".magicCard")).get(1)
				.getAttribute("name"));
		assertEquals(battlefieldCardName,
			FunctionalTests.chromeDriver1.findElements(By.cssSelector(".magicCard")).get(1)
				.getAttribute("name"));

		// Assert that the graveyard is visible and empty
		assertFalse(FunctionalTests.chromeDriver2.findElements(By.id("graveyard-page-wrap"))
			.isEmpty());
		assertTrue(FunctionalTests.chromeDriver2.findElements(
			By.cssSelector(".graveyard-cross-link")).isEmpty());

		// Put card to hand
		draggable = FunctionalTests.chromeDriver2.findElements(
			By.cssSelector("img.drag-handle-image")).get(0);
		to = FunctionalTests.chromeDriver2.findElement(By.id("putToHand"));
		new Actions(FunctionalTests.chromeDriver2).dragAndDrop(draggable, to).build().perform();

		Thread.sleep(2000);

		// Assert that the hand contains 7 cards again
		assertEquals(7, FunctionalTests.chromeDriver2.findElements(By.cssSelector(".movers-row"))
			.size());

		// Reveal top card of library
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
			.executeScript(FunctionalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		Thread.sleep(2000);
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
			.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RESPONSIVE_MENU);

		FunctionalTests.chromeDriver2.findElement(By.id("revealTopLibraryCardLinkResponsive"))
			.click();
		Thread.sleep(2000);

		// Get top card name
		final String topCardName = FunctionalTests.chromeDriver2.findElement(
			By.id("topLibraryCard")).getAttribute("name");

		// Verify that the card name is the same in the second browser
		assertTrue(topCardName.equals(FunctionalTests.chromeDriver1.findElement(
			By.id("topLibraryCard")).getAttribute("name")));

		// Click on the button "Do nothing"
		FunctionalTests.chromeDriver2.findElement(By.id("doNothing")).click();
		FunctionalTests.chromeDriver1.findElement(By.id("doNothing")).click();
		Thread.sleep(2000);

		// Reveal again
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
			.executeScript(FunctionalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FunctionalTests.chromeDriver2.findElement(By.id("revealTopLibraryCardLinkResponsive"))
			.click();
		Thread.sleep(2000);

		// Assert that the card is the same
		assertTrue(topCardName.equals(FunctionalTests.chromeDriver2.findElement(
			By.id("topLibraryCard")).getAttribute("name")));
		assertTrue(topCardName.equals(FunctionalTests.chromeDriver1.findElement(
			By.id("topLibraryCard")).getAttribute("name")));

		// Put to battlefield
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
			.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		((JavascriptExecutor)FunctionalTests.chromeDriver1)
			.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);

		FunctionalTests.chromeDriver2.findElement(By.id("putToBattlefieldFromModalWindow")).click();
		FunctionalTests.chromeDriver1.findElement(By.id("doNothing")).click();

		// Verify that the card is present on the battlefield
		Thread.sleep(2000);
		assertEquals(1, FunctionalTests.chromeDriver2
			.findElements(By.cssSelector(".clickableCard")).size());
		assertEquals(1, FunctionalTests.chromeDriver1
			.findElements(By.cssSelector(".clickableCard")).size());

		// Assert that the card on the battlefield is the same
		assertEquals(topCardName,
			FunctionalTests.chromeDriver2.findElements(By.cssSelector(".clickableCard")).get(0)
				.findElement(By.xpath("..")).findElement(By.xpath(".."))
				.findElement(By.xpath("..")).findElement(By.xpath("..")).getAttribute("name"));
		assertEquals(topCardName,
			FunctionalTests.chromeDriver1.findElements(By.cssSelector(".clickableCard")).get(0)
				.findElement(By.xpath("..")).findElement(By.xpath(".."))
				.findElement(By.xpath("..")).findElement(By.xpath("..")).getAttribute("name"));

		// Reveal top card of library
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
			.executeScript(FunctionalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FunctionalTests.chromeDriver2.findElement(By.id("revealTopLibraryCardLinkResponsive"))
			.click();
		Thread.sleep(2000);

		// Put to hand
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
			.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		((JavascriptExecutor)FunctionalTests.chromeDriver1)
			.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);

		FunctionalTests.chromeDriver2.findElement(By.id("putToHandFromModalWindow")).click();
		FunctionalTests.chromeDriver1.findElement(By.id("doNothing")).click();

		Thread.sleep(2000);

		// Assert that the hand contains 8 cards
		assertEquals(8, FunctionalTests.chromeDriver2.findElements(By.cssSelector(".movers-row"))
			.size());

		// Verify that there is still two cards on the battlefield
		assertEquals(1,
			FunctionalTests.chromeDriver2.findElements(By.cssSelector(".tap-handle-image")).size());
		assertEquals(1,
			FunctionalTests.chromeDriver1.findElements(By.cssSelector(".tap-handle-image")).size());

		// Reveal again
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
			.executeScript(FunctionalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FunctionalTests.chromeDriver2.findElement(By.id("revealTopLibraryCardLinkResponsive"))
			.click();
		Thread.sleep(2000);

		// Get top card name
		final String graveyardCardName = FunctionalTests.chromeDriver2.findElement(
			By.id("topLibraryCard")).getAttribute("name");

		// Put to graveyard
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
			.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		((JavascriptExecutor)FunctionalTests.chromeDriver1)
			.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);

		FunctionalTests.chromeDriver2.findElement(By.id("putToGraveyardFromModalWindow")).click();
		FunctionalTests.chromeDriver1.findElement(By.id("doNothing")).click();
		Thread.sleep(2000);

		// Assert graveyard is visible and contains one card
		assertFalse(FunctionalTests.chromeDriver2.findElements(By.id("graveyard-page-wrap"))
			.isEmpty());
		assertTrue(FunctionalTests.chromeDriver2.findElements(
			By.cssSelector(".graveyard-cross-link")).size() == 1);

		// Verify name of the card in the graveyard
		assertTrue(graveyardCardName.equals(FunctionalTests.chromeDriver2
			.findElements(By.cssSelector(".graveyard-cross-link:nth-child(1) img")).get(0)
			.getAttribute("name")));

		// Verify that there is still one card on the battlefield
		assertEquals(1,
			FunctionalTests.chromeDriver2.findElements(By.cssSelector(".tap-handle-image")).size());
		assertEquals(1,
			FunctionalTests.chromeDriver1.findElements(By.cssSelector(".tap-handle-image")).size());

		// Verify the name of the card on the battlefield
		assertEquals(topCardName,
			FunctionalTests.chromeDriver1.findElements(By.cssSelector(".clickableCard")).get(0)
				.findElement(By.xpath("..")).findElement(By.xpath(".."))
				.findElement(By.xpath("..")).findElement(By.xpath("..")).getAttribute("name"));
		assertEquals(topCardName,
			FunctionalTests.chromeDriver2.findElements(By.cssSelector(".clickableCard")).get(0)
				.findElement(By.xpath("..")).findElement(By.xpath(".."))
				.findElement(By.xpath("..")).findElement(By.xpath("..")).getAttribute("name"));

		// Verify that the hands contains 8 cards
		assertEquals(8, FunctionalTests.chromeDriver2.findElements(By.cssSelector(".movers-row"))
			.size());
		assertEquals(7, FunctionalTests.chromeDriver1.findElements(By.cssSelector(".movers-row"))
			.size());

		// Put one card from hand to graveyard
		((JavascriptExecutor)FunctionalTests.chromeDriver2)
			.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_PUT_TO_ZONE_SUMBIT_BUTTON_FOR_HAND);
		new Select(FunctionalTests.chromeDriver2.findElement(By.id("putToZoneSelectForHand")))
			.getOptions().get(1).click();
		FunctionalTests.chromeDriver2.findElement(By.id("moveToZoneSubmitHand")).click();
		Thread.sleep(2000);

		// Verify that there is one more card in the graveyard
		assertFalse(FunctionalTests.chromeDriver2.findElements(By.id("graveyard-page-wrap"))
			.isEmpty());
		assertEquals(2,
			FunctionalTests.chromeDriver2.findElements(By.cssSelector(".graveyard-cross-link"))
				.size());

		// Put current card from hand to exile
		new Select(FunctionalTests.chromeDriver2.findElement(By.id("putToZoneSelectForHand")))
			.getOptions().get(2).click();
		FunctionalTests.chromeDriver2.findElement(By.id("moveToZoneSubmitHand")).click();
		Thread.sleep(2000);

		// Verify that there is one more card in the exile and that it is
		// visible
		assertFalse(FunctionalTests.chromeDriver2.findElements(By.id("exile-page-wrap")).isEmpty());
		assertEquals(1,
			FunctionalTests.chromeDriver2.findElements(By.cssSelector(".exile-cross-link")).size());

		// Put current card in exile to graveyard
		new Select(FunctionalTests.chromeDriver2.findElement(By.id("putToZoneSelectForExile")))
			.getOptions().get(1).click();
		FunctionalTests.chromeDriver2.findElement(By.id("moveToZoneSubmitExile")).click();
		Thread.sleep(2000);

		// Verify that there is one more card in the graveyard
		assertFalse(FunctionalTests.chromeDriver2.findElements(By.id("graveyard-page-wrap"))
			.isEmpty());
		assertEquals(3,
			FunctionalTests.chromeDriver2.findElements(By.cssSelector(".graveyard-cross-link"))
				.size());

		// Get name of the current card in the hand
		final String handCardName = FunctionalTests.chromeDriver2
			.findElements(By.cssSelector(".cross-link:nth-child(1) img")).get(0)
			.getAttribute("name");

		// Put current card from hand to exile
		new Select(FunctionalTests.chromeDriver2.findElement(By.id("putToZoneSelectForHand")))
			.getOptions().get(2).click();
		FunctionalTests.chromeDriver2.findElement(By.id("moveToZoneSubmitHand")).click();
		Thread.sleep(2000);

		// Verify that there is one more card in the exile
		assertFalse(FunctionalTests.chromeDriver2.findElements(By.id("exile-page-wrap")).isEmpty());
		assertEquals(1,
			FunctionalTests.chromeDriver2.findElements(By.cssSelector(".exile-cross-link")).size());

		// Get name of the current card in the exile
		final String exileCardName = FunctionalTests.chromeDriver2
			.findElements(By.cssSelector(".exile-cross-link:nth-child(1) img")).get(0)
			.getAttribute("name");

		// Verify that active card in exile is same than card from hand
		assertEquals(handCardName, exileCardName);

		// Put card from exile to battlefield
		new Select(FunctionalTests.chromeDriver2.findElement(By.id("putToZoneSelectForExile")))
			.getOptions().get(0).click();
		FunctionalTests.chromeDriver2.findElement(By.id("moveToZoneSubmitExile")).click();
		Thread.sleep(2000);

		// Verify that there are 2 cards on the battlefield
		assertEquals(2,
			FunctionalTests.chromeDriver2.findElements(By.cssSelector(".tap-handle-image")).size());
		assertEquals(2,
			FunctionalTests.chromeDriver1.findElements(By.cssSelector(".tap-handle-image")).size());

		// Verify the name of the card on the battlefield
		assertEquals(exileCardName,
			FunctionalTests.chromeDriver2.findElements(By.cssSelector(".clickableCard")).get(1)
				.findElement(By.xpath("..")).findElement(By.xpath(".."))
				.findElement(By.xpath("..")).findElement(By.xpath("..")).getAttribute("name"));
		assertEquals(exileCardName,
			FunctionalTests.chromeDriver1.findElements(By.cssSelector(".clickableCard")).get(1)
				.findElement(By.xpath("..")).findElement(By.xpath(".."))
				.findElement(By.xpath("..")).findElement(By.xpath("..")).getAttribute("name"));
	}

	@Test
	public void testQunit()
	{
		final String passed1 = FunctionalTests.chromeDriver1.findElement(By.id("passed")).getText();
		final String total1 = FunctionalTests.chromeDriver1.findElement(By.id("total")).getText();
		final String failed1 = FunctionalTests.chromeDriver1.findElement(By.id("failed")).getText();

		assertEquals(FunctionalTests.QUNIT_PASSED_TESTS, passed1);
		assertEquals(FunctionalTests.QUNIT_TOTAL_TESTS, total1);
		assertEquals(FunctionalTests.QUNIT_FAILED_TESTS, failed1);
	}

	@Test
	public void testMistletoe() throws InterruptedException
	{
		// TODO vérifier qu'il y a bien 3 decks de disponibles
		((JavascriptExecutor)FunctionalTests.chromeDriver1)
			.executeScript(FunctionalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RUN_BUTTON);
		FunctionalTests.chromeDriver1.findElement(By.id("runMistletoe")).click();

		Thread.sleep(60000);

		((JavascriptExecutor)FunctionalTests.chromeDriver1)
			.executeScript(FunctionalTests.SCROLL_DOWN);

		final String chromeTotal = FunctionalTests.chromeDriver1.findElement(By.id("runsSummary"))
			.getText();
		final String chromeFailed = FunctionalTests.chromeDriver1.findElement(
			By.id("errorsSummary")).getText();

		assertEquals(FunctionalTests.MISTLETOE_TOTAL_TESTS, chromeTotal);
		assertEquals(FunctionalTests.MISTLETOE_FAILED_TESTS, chromeFailed);
	}

}
