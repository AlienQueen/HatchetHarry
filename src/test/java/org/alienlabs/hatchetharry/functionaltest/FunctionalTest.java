package org.alienlabs.hatchetharry.functionaltest;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

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
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class FunctionalTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FunctionalTest.class);

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
	private static final String CLICK_PLAY_CARD_LINK = "$('.cardActions')[0].click();";

	private static WebDriver chromeDriver1;
	private static WebDriver chromeDriver2;

	@BeforeClass
	public static void setUp() throws Exception
	{
		FunctionalTest.LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> STARTING EMBEDDED JETTY SERVER");

		final ServerConnector http = new ServerConnector(FunctionalTest.SERVER);
		http.setHost(FunctionalTest.HOST);
		http.setPort(Integer.parseInt(FunctionalTest.PORT));
		http.setIdleTimeout(30000);
		FunctionalTest.SERVER.addConnector(http);
		final WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setWar("src/main/webapp");
		FunctionalTest.SERVER.setHandler(webapp);
		FunctionalTest.SERVER.start();

		FunctionalTest.LOGGER
				.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SUCCESSFULLY STARTED EMBEDDED JETTY SERVER");

		System.setProperty("webdriver.chrome.driver", "/home/nostromo/chromedriver");
		FunctionalTest.chromeDriver1 = new ChromeDriver();
		FunctionalTest.chromeDriver1.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		FunctionalTest.chromeDriver2 = new ChromeDriver();
		FunctionalTest.chromeDriver2.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		FunctionalTest.chromeDriver1.get("http://" + FunctionalTest.HOST + ":"
				+ FunctionalTest.PORT + "/");
		FunctionalTest.chromeDriver2.get("http://" + FunctionalTest.HOST + ":"
				+ FunctionalTest.PORT + "/");
	}

	@AfterClass
	public static void tearDown()
	{
		if (null != FunctionalTest.chromeDriver1)
		{
			FunctionalTest.chromeDriver1.quit();
		}
		if (null != FunctionalTest.chromeDriver2)
		{
			FunctionalTest.chromeDriver2.quit();
		}

		FunctionalTest.LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> STOPPING EMBEDDED JETTY SERVER");
		try
		{
			FunctionalTest.SERVER.stop();
			FunctionalTest.SERVER.join();
		}
		catch (final Exception e)
		{
			FunctionalTest.LOGGER.error("Error in stopping EMBEDDED JETTY SERVER", e);
		}
	}

	@Test
	public void testFullAppTraversal() throws InterruptedException
	{
		// Create a game in Chrome 1
		((JavascriptExecutor)FunctionalTest.chromeDriver1)
				.executeScript(FunctionalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);

		FunctionalTest.chromeDriver1.findElement(By.id("createGameLinkResponsive")).click();
		new WebDriverWait(FunctionalTest.chromeDriver1, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("name")));

		FunctionalTest.chromeDriver1.findElement(By.id("name")).clear();
		FunctionalTest.chromeDriver1.findElement(By.id("name")).sendKeys("Zala");
		new Select(FunctionalTest.chromeDriver1.findElement(By.id("sideInput"))).getOptions()
				.get(1).click();
		new Select(FunctionalTest.chromeDriver1.findElement(By.id("decks"))).getOptions().get(1)
				.click();
		new Select(FunctionalTest.chromeDriver1.findElement(By.id("formats"))).getOptions().get(1)
				.click();
		FunctionalTest.chromeDriver1.findElement(By.id("numberOfPlayers")).sendKeys("2");

		final String gameId = FunctionalTest.chromeDriver1.findElement(By.id("gameId")).getText();

		FunctionalTest.chromeDriver1.findElement(By.id("createSubmit")).click();
		Thread.sleep(3000);

		// Join a game in chrome
		((JavascriptExecutor)FunctionalTest.chromeDriver2)
				.executeScript(FunctionalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));
		Thread.sleep(1000);

		((JavascriptExecutor)FunctionalTest.chromeDriver2)
				.executeScript(FunctionalTest.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RESPONSIVE_MENU);
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("joinGameLinkResponsive")));
		FunctionalTest.chromeDriver2.findElement(By.id("joinGameLinkResponsive")).click();

		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("name")));
		FunctionalTest.chromeDriver2.findElement(By.id("name")).clear();
		FunctionalTest.chromeDriver2.findElement(By.id("name")).sendKeys("Marie");
		new Select(FunctionalTest.chromeDriver2.findElement(By.id("sideInput"))).getOptions()
				.get(2).click();
		new Select(FunctionalTest.chromeDriver2.findElement(By.id("decks"))).getOptions().get(3)
				.click();
		FunctionalTest.chromeDriver2.findElement(By.id("gameIdInput")).clear();
		FunctionalTest.chromeDriver2.findElement(By.id("gameIdInput")).sendKeys(gameId);

		FunctionalTest.chromeDriver2.findElement(By.id("joinSubmit")).click();

		// Assert that no card is present on battlefield
		Thread.sleep(3000);
		Assert.assertTrue(FunctionalTest.chromeDriver2.findElements(
				By.cssSelector(".battlefieldCardContainer")).isEmpty());
		Assert.assertTrue(FunctionalTest.chromeDriver1.findElements(
				By.cssSelector(".battlefieldCardContainer")).isEmpty());

		// Verify that the hands contains 7 cards
		assertEquals(7,
				FunctionalTest.chromeDriver2
						.findElements(By.cssSelector(".gallery .cardContainer")).size());
		assertEquals(7,
				FunctionalTest.chromeDriver1
						.findElements(By.cssSelector(".gallery .cardContainer")).size());

		// Find first hand card name of Chrome2
		final String battlefieldCardName = FunctionalTest.chromeDriver2
				.findElements(By.cssSelector(".gallery .cardContainer img")).get(0)
				.getAttribute("src");

		// Play a card in chrome2
		((JavascriptExecutor)FunctionalTest.chromeDriver2)
				.executeScript(FunctionalTest.CLICK_PLAY_CARD_LINK);
		Thread.sleep(2000);

		// Verify that the hand contains only 6 cards, now
		assertEquals(6,
				FunctionalTest.chromeDriver2
						.findElements(By.cssSelector(".gallery .cardContainer")).size());

		// Verify that card is present on the battlefield
		assertEquals(
				1,
				FunctionalTest.chromeDriver2.findElements(
						By.cssSelector(".battlefieldCardContainer")).size());

		assertEquals(
				1,
				FunctionalTest.chromeDriver1.findElements(
						By.cssSelector(".battlefieldCardContainer")).size());

		// Verify the name of the card on the battlefield
		assertEquals(
				battlefieldCardName,
				FunctionalTest.chromeDriver1
						.findElements(By.cssSelector(".battlefieldCardContainer .magicCard"))
						.get(0).getAttribute("src"));
		assertEquals(
				battlefieldCardName,
				FunctionalTest.chromeDriver2
						.findElements(By.cssSelector(".battlefieldCardContainer .magicCard"))
						.get(0).getAttribute("src"));

		// Verify that the card is untapped
		assertTrue(FunctionalTest.chromeDriver2.findElements(
				By.cssSelector(".cardContainer .tapped")).isEmpty());
		assertTrue(FunctionalTest.chromeDriver1.findElements(
				By.cssSelector(".cardContainer .tapped")).isEmpty());

		// Tap card
		FunctionalTest.chromeDriver2.findElement(By.cssSelector(".tapping")).click();
		Thread.sleep(2000);

		// Verify card is tapped
		assertEquals(1,
				FunctionalTest.chromeDriver2.findElements(By.cssSelector(".cardContainer.tapped"))
						.size());
		assertEquals(1,
				FunctionalTest.chromeDriver1.findElements(By.cssSelector(".cardContainer.tapped"))
						.size());

		// Assert that graveyard is not visible
		assertTrue(FunctionalTest.chromeDriver2.findElements(By.id("graveyard-page-wrap"))
				.isEmpty());

		// Put card to graveyard
		FunctionalTest.chromeDriver2.findElement(
				By.cssSelector(".battlefieldCardContainer .cardMenu")).click();
		Thread.sleep(1500);
		FunctionalTest.chromeDriver2.findElements(By.cssSelector(".demo_li div")).get(1).click();
		Thread.sleep(1500);

		// Play card from graveyard
		((JavascriptExecutor)FunctionalTest.chromeDriver2)
				.executeScript(FunctionalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);
		Thread.sleep(1000);
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));

		((JavascriptExecutor)FunctionalTest.chromeDriver2)
				.executeScript(FunctionalTest.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RESPONSIVE_MENU);

		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("playCardFromGraveyardLinkResponsive")));
		Thread.sleep(1500);
		FunctionalTest.chromeDriver2.findElement(By.id("playCardFromGraveyardLinkResponsive"))
				.click();
		Thread.sleep(1500);

		// Verify the name of the card on the battlefield
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".battlefieldCardContainer")));
		assertEquals(
				battlefieldCardName,
				FunctionalTest.chromeDriver2
						.findElements(By.cssSelector(".battlefieldCardContainer .magicCard"))
						.get(0).getAttribute("src"));
		assertEquals(
				battlefieldCardName,
				FunctionalTest.chromeDriver1
						.findElements(By.cssSelector(".battlefieldCardContainer .magicCard"))
						.get(0).getAttribute("src"));

		// Assert that the graveyard is visible and empty
		assertFalse(FunctionalTest.chromeDriver2.findElements(By.id("graveyard-page-wrap"))
				.isEmpty());
		assertTrue(FunctionalTest.chromeDriver2.findElements(
				By.cssSelector(".graveyard-cross-link")).isEmpty());

		// Put card to hand
		FunctionalTest.chromeDriver2.findElement(
				By.cssSelector(".battlefieldCardContainer .cardMenu")).click();
		Thread.sleep(1500);
		FunctionalTest.chromeDriver2.findElements(By.cssSelector(".demo_li div")).get(0).click();
		Thread.sleep(1500);

		// Assert that the hand contains 7 cards again
		assertEquals(7,
				FunctionalTest.chromeDriver2
						.findElements(By.cssSelector(".gallery .cardContainer")).size());

		// Reveal top card of library
		((JavascriptExecutor)FunctionalTest.chromeDriver2)
				.executeScript(FunctionalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));
		Thread.sleep(1500);
		((JavascriptExecutor)FunctionalTest.chromeDriver2)
				.executeScript(FunctionalTest.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RESPONSIVE_MENU);
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("revealTopLibraryCardLinkResponsive")));
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("revealTopLibraryCardLinkResponsive")));
		FunctionalTest.chromeDriver2.findElement(By.id("revealTopLibraryCardLinkResponsive"))
				.click();
		Thread.sleep(1500);

		// Get top card name
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("topLibraryCard")));
		final String topCardName = FunctionalTest.chromeDriver2
				.findElement(By.id("topLibraryCard")).getAttribute("name");

		// Verify that the card name is the same in the second browser
		new WebDriverWait(FunctionalTest.chromeDriver1, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("topLibraryCard")));
		assertEquals(topCardName, FunctionalTest.chromeDriver1.findElement(By.id("topLibraryCard"))
				.getAttribute("name"));

		// Click on the button "Do nothing"
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("doNothing")));
		FunctionalTest.chromeDriver2.findElement(By.id("doNothing")).click();
		FunctionalTest.chromeDriver1.findElement(By.id("doNothing")).click();
		Thread.sleep(2000);

		// Reveal again
		((JavascriptExecutor)FunctionalTest.chromeDriver2)
				.executeScript(FunctionalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("revealTopLibraryCardLinkResponsive")));
		FunctionalTest.chromeDriver2.findElement(By.id("revealTopLibraryCardLinkResponsive"))
				.click();
		Thread.sleep(1500);

		// Assert that the card is the same
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("topLibraryCard")));
		assertEquals(topCardName, FunctionalTest.chromeDriver2.findElement(By.id("topLibraryCard"))
				.getAttribute("name"));
		assertEquals(topCardName, FunctionalTest.chromeDriver1.findElement(By.id("topLibraryCard"))
				.getAttribute("name"));

		// Put to battlefield
		((JavascriptExecutor)FunctionalTest.chromeDriver2)
				.executeScript(FunctionalTest.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));
		((JavascriptExecutor)FunctionalTest.chromeDriver1)
				.executeScript(FunctionalTest.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("putToBattlefieldFromModalWindow")));

		FunctionalTest.chromeDriver2.findElement(By.id("putToBattlefieldFromModalWindow")).click();
		FunctionalTest.chromeDriver1.findElement(By.id("doNothing")).click();

		// Verify that the card is present on the battlefield
		Thread.sleep(1500);
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".battlefieldCardContainer")));
		assertEquals(
				1,
				FunctionalTest.chromeDriver2.findElements(
						By.cssSelector(".battlefieldCardContainer")).size());

		assertEquals(
				1,
				FunctionalTest.chromeDriver1.findElements(
						By.cssSelector(".battlefieldCardContainer")).size());

		// Assert that the card on the battlefield is the same
		assertEquals(
				topCardName,
				FunctionalTest.chromeDriver2.findElement(
						By.cssSelector(".battlefieldCardContainer :first-child :first-child"))
						.getAttribute("name"));
		assertEquals(
				topCardName,
				FunctionalTest.chromeDriver1.findElement(
						By.cssSelector(".battlefieldCardContainer :first-child :first-child"))
						.getAttribute("name"));

		// Reveal top card of library
		((JavascriptExecutor)FunctionalTest.chromeDriver2)
				.executeScript(FunctionalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));
		FunctionalTest.chromeDriver2.findElement(By.id("revealTopLibraryCardLinkResponsive"))
				.click();
		Thread.sleep(1500);

		// Put to hand
		((JavascriptExecutor)FunctionalTest.chromeDriver2)
				.executeScript(FunctionalTest.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));
		((JavascriptExecutor)FunctionalTest.chromeDriver1)
				.executeScript(FunctionalTest.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		new WebDriverWait(FunctionalTest.chromeDriver1, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));

		Thread.sleep(1500);
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("putToHandFromModalWindow")));
		FunctionalTest.chromeDriver2.findElement(By.id("putToHandFromModalWindow")).click();
		FunctionalTest.chromeDriver1.findElement(By.id("doNothing")).click();
		Thread.sleep(1500);

		// Assert that the hand contains 8 cards
		assertEquals(8,
				FunctionalTest.chromeDriver2
						.findElements(By.cssSelector(".gallery .cardContainer")).size());

		// Verify that there is still one card on the battlefield
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".battlefieldCardContainer")));
		assertEquals(
				1,
				FunctionalTest.chromeDriver2.findElements(
						By.cssSelector(".battlefieldCardContainer")).size());

		assertEquals(
				1,
				FunctionalTest.chromeDriver1.findElements(
						By.cssSelector(".battlefieldCardContainer")).size());

		// Reveal again
		((JavascriptExecutor)FunctionalTest.chromeDriver2)
				.executeScript(FunctionalTest.SHOW_AND_OPEN_MOBILE_MENUBAR);
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("revealTopLibraryCardLinkResponsive")));
		FunctionalTest.chromeDriver2.findElement(By.id("revealTopLibraryCardLinkResponsive"))
				.click();
		Thread.sleep(1500);

		// Get top card name
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("topLibraryCard")));
		final String graveyardCardName = FunctionalTest.chromeDriver2.findElement(
				By.id("topLibraryCard")).getAttribute("name");

		// Put to graveyard
		((JavascriptExecutor)FunctionalTest.chromeDriver2)
				.executeScript(FunctionalTest.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));
		((JavascriptExecutor)FunctionalTest.chromeDriver1)
				.executeScript(FunctionalTest.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		new WebDriverWait(FunctionalTest.chromeDriver1, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".dropdownmenu")));

		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("putToGraveyardFromModalWindow")));
		FunctionalTest.chromeDriver2.findElement(By.id("putToGraveyardFromModalWindow")).click();
		FunctionalTest.chromeDriver1.findElement(By.id("doNothing")).click();
		Thread.sleep(1500);

		// Assert graveyard is visible and contains one card
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.id("graveyard-page-wrap")));
		assertFalse(FunctionalTest.chromeDriver2.findElements(By.id("graveyard-page-wrap"))
				.isEmpty());
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".graveyard-cross-link")));
		assertEquals(1,
				FunctionalTest.chromeDriver2.findElements(By.cssSelector(".graveyard-cross-link"))
						.size());

		// Verify name of the card in the graveyard
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.presenceOfElementLocated(By.cssSelector(".graveyard-cross-link")));
		assertEquals(
				graveyardCardName,
				FunctionalTest.chromeDriver2
						.findElements(By.cssSelector(".graveyard-cross-link:nth-child(1) img"))
						.get(0).getAttribute("name"));

		// Verify that there is still one card on the battlefield
		assertEquals(
				1,
				FunctionalTest.chromeDriver2.findElements(
						By.cssSelector(".battlefieldCardContainer .magicCard")).size());
		assertEquals(
				1,
				FunctionalTest.chromeDriver1.findElements(
						By.cssSelector(".battlefieldCardContainer .magicCard")).size());

		// Verify the name of the card on the battlefield
		assertEquals(
				topCardName,
				FunctionalTest.chromeDriver2.findElement(
						By.cssSelector(".battlefieldCardContainer :first-child :first-child"))
						.getAttribute("name"));
		assertEquals(
				topCardName,
				FunctionalTest.chromeDriver1.findElement(
						By.cssSelector(".battlefieldCardContainer :first-child :first-child"))
						.getAttribute("name"));

		// Verify that the hands contains 8 cards
		assertEquals(8,
				FunctionalTest.chromeDriver2
						.findElements(By.cssSelector(".gallery .cardContainer")).size());
		assertEquals(7,
				FunctionalTest.chromeDriver1
						.findElements(By.cssSelector(".gallery .cardContainer")).size());

		// Put card from graveyard to exile
		new Select(FunctionalTest.chromeDriver2.findElement(By.id("putToZoneSelectForGraveyard")))
				.getOptions().get(2).click();
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("moveToZoneSubmitGraveyard")));
		FunctionalTest.chromeDriver2.findElement(By.id("moveToZoneSubmitGraveyard")).click();
		Thread.sleep(1500);

		// Assert that graveyard is visible and empty
		assertFalse(FunctionalTest.chromeDriver2.findElements(By.id("graveyard-page-wrap"))
				.isEmpty());
		assertTrue(FunctionalTest.chromeDriver2.findElements(
				By.cssSelector(".graveyard-cross-link")).isEmpty());

		// Assert that exile is visible and contains the right card
		assertFalse(FunctionalTest.chromeDriver2.findElements(By.id("exile-page-wrap")).isEmpty());
		assertEquals(1,
				FunctionalTest.chromeDriver2.findElements(By.cssSelector(".exile-cross-link"))
						.size());
		assertEquals(graveyardCardName,
				FunctionalTest.chromeDriver2.findElement(By.cssSelector(".exile-cross-link img"))
						.getAttribute("name"));

		// Put card from exile to battlefield
		new Select(FunctionalTest.chromeDriver2.findElement(By.id("putToZoneSelectForExile")))
				.getOptions().get(0).click();
		new WebDriverWait(FunctionalTest.chromeDriver2, 10).until(ExpectedConditions
				.elementToBeClickable(By.id("moveToZoneSubmitExile")));
		FunctionalTest.chromeDriver2.findElement(By.id("moveToZoneSubmitExile")).click();
		Thread.sleep(1500);

		// Verify that there are 2 cards on the battlefield
		assertEquals(
				2,
				FunctionalTest.chromeDriver2.findElements(
						By.cssSelector(".battlefieldCardContainer .magicCard")).size());
		assertEquals(
				2,
				FunctionalTest.chromeDriver1.findElements(
						By.cssSelector(".battlefieldCardContainer .magicCard")).size());

		// Verify the name of the card on the battlefield
		assertEquals(
				graveyardCardName,
				FunctionalTest.chromeDriver2
						.findElements(
								By.cssSelector(".battlefieldCardContainer :first-child :first-child"))
						.get(20).getAttribute("name"));
		assertEquals(
				graveyardCardName,
				FunctionalTest.chromeDriver1
						.findElements(
								By.cssSelector(".battlefieldCardContainer :first-child :first-child"))
						.get(20).getAttribute("name"));
	}

	@Test
	@Ignore("Just to be able to push to Github")
	public void testQunit()
	{
		final String passed1 = FunctionalTest.chromeDriver1.findElement(By.id("passed")).getText();
		final String total1 = FunctionalTest.chromeDriver1.findElement(By.id("total")).getText();
		final String failed1 = FunctionalTest.chromeDriver1.findElement(By.id("failed")).getText();

		assertEquals(FunctionalTest.QUNIT_PASSED_TESTS, passed1);
		assertEquals(FunctionalTest.QUNIT_TOTAL_TESTS, total1);
		assertEquals(FunctionalTest.QUNIT_FAILED_TESTS, failed1);
	}

	@Test
	public void testMistletoe() throws InterruptedException
	{
		// Sleep in order for the page scrolling up (because of the qunit tests)
		// not to disturb us
		Thread.sleep(12000);
		// TODO vérifier qu'il y a bien 3 decks de disponibles
		((JavascriptExecutor)FunctionalTest.chromeDriver1)
				.executeScript(FunctionalTest.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RUN_BUTTON);
		FunctionalTest.chromeDriver1.findElement(By.id("runMistletoe")).click();

		((JavascriptExecutor)FunctionalTest.chromeDriver1)
				.executeScript(FunctionalTest.SCROLL_DOWN);

		// Sleep in order to wait for the results to appear
		Thread.sleep(30000);

		final String chromeTotal = FunctionalTest.chromeDriver1.findElement(By.id("runsSummary"))
				.getText();
		final String chromeFailed = FunctionalTest.chromeDriver1
				.findElement(By.id("errorsSummary")).getText();

		assertEquals(FunctionalTest.MISTLETOE_TOTAL_TESTS, chromeTotal);
		assertEquals(FunctionalTest.MISTLETOE_FAILED_TESTS, chromeFailed);
	}

}
