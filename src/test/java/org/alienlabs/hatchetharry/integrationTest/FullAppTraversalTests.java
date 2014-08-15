package org.alienlabs.hatchetharry.integrationTest;

import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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

public class FullAppTraversalTests {
	private static final Logger LOGGER = LoggerFactory.getLogger(FullAppTraversalTests.class);

	private static final String PORT = "8088";
	private static final String HOST = "localhost";
	private static final Server server = new Server();

	private static WebDriver chromeDriver1;
	private static WebDriver chromeDriver2;


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

	@Before
	public void setUp() throws Exception {
		FullAppTraversalTests.LOGGER
				.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> STARTING EMBEDDED JETTY SERVER");

		final ServerConnector http = new ServerConnector(FullAppTraversalTests.server);
		http.setHost(FullAppTraversalTests.HOST);
		http.setPort(Integer.parseInt(FullAppTraversalTests.PORT));
		http.setIdleTimeout(30000);
		FullAppTraversalTests.server.addConnector(http);
		final WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setWar("src/main/webapp");
		FullAppTraversalTests.server.setHandler(webapp);
		FullAppTraversalTests.server.start();

		FullAppTraversalTests.LOGGER
				.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SUCCESSFULLY STARTED EMBEDDED JETTY SERVER");

		System.setProperty("webdriver.chrome.driver", "/home/nostromo/chromedriver");
		FullAppTraversalTests.chromeDriver1 = new ChromeDriver();
		FullAppTraversalTests.chromeDriver1.manage().timeouts()
				.implicitlyWait(30, TimeUnit.SECONDS);

		FullAppTraversalTests.chromeDriver2 = new ChromeDriver();
		FullAppTraversalTests.chromeDriver2.manage().timeouts()
				.implicitlyWait(30, TimeUnit.SECONDS);

		Thread.sleep(5000);

		FullAppTraversalTests.chromeDriver1.get("http://" + FullAppTraversalTests.HOST + ":"
														+ FullAppTraversalTests.PORT + "/");
		FullAppTraversalTests.chromeDriver2.get("http://" + FullAppTraversalTests.HOST + ":"
														+ FullAppTraversalTests.PORT + "/");

		Thread.sleep(5000);
	}

	@After
	public void tearDown() throws Exception {
		if (null != FullAppTraversalTests.chromeDriver1) {
			FullAppTraversalTests.chromeDriver1.quit();
		}
		if (null != FullAppTraversalTests.chromeDriver2) {
			FullAppTraversalTests.chromeDriver2.quit();
		}

		FullAppTraversalTests.LOGGER
				.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> STOPPING EMBEDDED JETTY SERVER");
		FullAppTraversalTests.server.stop();
		FullAppTraversalTests.server.join();
		Thread.sleep(30000);
	}

	@Test
	public void testFullAppTraversal() throws InterruptedException {
		// Create a game in Chrome 1
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver1)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);

		Thread.sleep(8000);
		FullAppTraversalTests.chromeDriver1.findElement(By.id("createGameLinkResponsive")).click();
		Thread.sleep(8000);

		FullAppTraversalTests.chromeDriver1.findElement(By.id("name")).clear();
		FullAppTraversalTests.chromeDriver1.findElement(By.id("name")).sendKeys("Zala");
		new Select(FullAppTraversalTests.chromeDriver1.findElement(By.id("sideInput")))
				.getOptions().get(1).click();
		new Select(FullAppTraversalTests.chromeDriver1.findElement(By.id("decks"))).getOptions()
				.get(1).click();

		final String gameId = FullAppTraversalTests.chromeDriver1.findElement(By.id("gameId"))
									  .getText();

		FullAppTraversalTests.chromeDriver1.findElement(By.id("createSubmit")).click();
		Thread.sleep(8000);

		// Join a game in Firefox
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver2)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);

		FullAppTraversalTests.chromeDriver2.findElement(By.id("joinGameLinkResponsive")).click();
		Thread.sleep(8000);

		FullAppTraversalTests.chromeDriver2.findElement(By.id("name")).clear();
		FullAppTraversalTests.chromeDriver2.findElement(By.id("name")).sendKeys("Marie");
		new Select(FullAppTraversalTests.chromeDriver2.findElement(By.id("sideInput")))
				.getOptions().get(2).click();
		new Select(FullAppTraversalTests.chromeDriver2.findElement(By.id("decks"))).getOptions()
				.get(2).click();
		FullAppTraversalTests.chromeDriver2.findElement(By.id("gameIdInput")).clear();
		FullAppTraversalTests.chromeDriver2.findElement(By.id("gameIdInput")).sendKeys(gameId);

		FullAppTraversalTests.chromeDriver2.findElement(By.id("joinSubmit")).click();

		// Assert that no card is present on battlefield
		// The Balduvian Horde is hidden but still there
		// And it contains TWO elements of class magicCard
		Thread.sleep(8000);
		Assert.assertEquals(2,
								   FullAppTraversalTests.chromeDriver2.findElements(By.cssSelector(".magicCard"))
										   .size());
		Assert.assertEquals(2,
								   FullAppTraversalTests.chromeDriver1.findElements(By.cssSelector(".magicCard"))
										   .size());

		// Verify that the hands contains 7 cards
		Assert.assertEquals(7,
								   FullAppTraversalTests.chromeDriver2.findElements(By.cssSelector(".movers-row"))
										   .size());
		Assert.assertEquals(7,
								   FullAppTraversalTests.chromeDriver1.findElements(By.cssSelector(".movers-row"))
										   .size());

		// Find first hand card name of Chrome1
		final String battlefieldCardName = FullAppTraversalTests.chromeDriver2
												   .findElements(By.cssSelector(".cross-link:nth-child(1) img")).get(0)
												   .getAttribute("name");

		// Play a card in firefox
		FullAppTraversalTests.chromeDriver2.findElement(By.id("playCardLink0")).click();

		// Verify that the hand contains only 6 cards, now
		Thread.sleep(12000);
		Assert.assertEquals(6,
								   FullAppTraversalTests.chromeDriver2.findElements(By.cssSelector(".movers-row"))
										   .size());

		// Verify that card is present on the battlefield
		// Two HTML elements with class "magicCard" are created for each card
		Assert.assertEquals(4,
								   FullAppTraversalTests.chromeDriver2.findElements(By.cssSelector(".magicCard"))
										   .size());

		Assert.assertEquals(4,
								   FullAppTraversalTests.chromeDriver1.findElements(By.cssSelector(".magicCard"))
										   .size());

		// Verify the name of the card on the battlefield
		Assert.assertEquals(
								   battlefieldCardName,
								   FullAppTraversalTests.chromeDriver2.findElements(By.cssSelector(".magicCard"))
										   .get(2).getAttribute("name"));
		Assert.assertEquals(
								   battlefieldCardName,
								   FullAppTraversalTests.chromeDriver1.findElements(By.cssSelector(".magicCard"))
										   .get(2).getAttribute("name"));

		// Verify that the card is untapped
		Assert.assertFalse(FullAppTraversalTests.chromeDriver2
								   .findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
								   .contains("transform"));
		Assert.assertFalse(FullAppTraversalTests.chromeDriver1
								   .findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
								   .contains("transform"));

		// Tap card
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver2)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_CARD);
		FullAppTraversalTests.chromeDriver2
				.findElement(By.cssSelector("img[id^='tapHandleImage']")).click();
		Thread.sleep(8000);

		// Verify card is tapped
		Assert.assertTrue(FullAppTraversalTests.chromeDriver2
								  .findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
								  .contains("rotate(90deg)"));
		Assert.assertTrue(FullAppTraversalTests.chromeDriver1
								  .findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
								  .contains("rotate(90deg)"));

		// Assert that graveyard is not visible
		Assert.assertTrue(FullAppTraversalTests.chromeDriver2.findElements(
																				  By.id("graveyard-page-wrap")).isEmpty());

		// Grow up zone images
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver1)
				.executeScript("$('#putToGraveyard').attr('src', 'image/graveyard.jpg');");
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver1)
				.executeScript("$('#putToHand').attr('src', 'image/hand.jpg');");
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver1)
				.executeScript("$('#putToExile').attr('src', 'image/exile.jpg');");

		((JavascriptExecutor) FullAppTraversalTests.chromeDriver2)
				.executeScript("$('#putToGraveyard').attr('src', 'image/graveyard.jpg');");
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver2)
				.executeScript("$('#putToHand').attr('src', 'image/hand.jpg');");
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver2)
				.executeScript("$('#putToExile').attr('src', 'image/exile.jpg');");

		// Put card to graveyard
		WebElement draggable = FullAppTraversalTests.chromeDriver1.findElement(By
																					   .cssSelector("img[id^='handleImage']"));
		WebElement to = FullAppTraversalTests.chromeDriver1.findElement(By.id("putToGraveyard"));
		new Actions(FullAppTraversalTests.chromeDriver1).dragAndDrop(draggable, to).build()
				.perform();

		Thread.sleep(30000);

		// Assert graveyard is visible and contains one card
		// Assert.assertFalse(FullAppTraversalTests.chromeDriver2.findElements(
		// By.id("graveyard-page-wrap")).isEmpty());
		// Assert.assertTrue(FullAppTraversalTests.chromeDriver2.findElements(
		// By.cssSelector(".graveyard-cross-link")).size() == 1);

		// Verify name of the card in the graveyard
		// Assert.assertTrue(battlefieldCardName.equals(FullAppTraversalTests.chromeDriver2
		// .findElements(By.cssSelector(".graveyard-cross-link:nth-child(1) img")).get(0)
		// .getAttribute("name")));

		// Play card from graveyard
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver2)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		Thread.sleep(8000);
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver2)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RESPONSIVE_MENU);

		FullAppTraversalTests.chromeDriver2.findElement(
															   By.id("playCardFromGraveyardLinkResponsive")).click();
		Thread.sleep(8000);

		// Verify the name of the card on the battlefield
		Assert.assertEquals(
								   battlefieldCardName,
								   FullAppTraversalTests.chromeDriver2.findElements(By.cssSelector(".magicCard"))
										   .get(2).getAttribute("name"));
		Assert.assertEquals(
								   battlefieldCardName,
								   FullAppTraversalTests.chromeDriver1.findElements(By.cssSelector(".magicCard"))
										   .get(2).getAttribute("name"));

		// Assert that the graveyard is visible and empty
		Assert.assertFalse(FullAppTraversalTests.chromeDriver2.findElements(
																				   By.id("graveyard-page-wrap")).isEmpty());
		Assert.assertTrue(FullAppTraversalTests.chromeDriver2.findElements(
																				  By.cssSelector(".graveyard-cross-link")).isEmpty());

		// Put card to hand
		draggable = FullAppTraversalTests.chromeDriver2.findElement(By
																			.cssSelector("img[id^='handleImage']"));
		to = FullAppTraversalTests.chromeDriver2.findElement(By.id("putToHand"));
		new Actions(FullAppTraversalTests.chromeDriver2).dragAndDrop(draggable, to).build()
				.perform();

		Thread.sleep(8000);

		// Assert that the hand contains 7 cards again
		Assert.assertEquals(7,
								   FullAppTraversalTests.chromeDriver2.findElements(By.cssSelector(".movers-row"))
										   .size());

		// Reveal top card of library
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver2)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		Thread.sleep(2000);
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver2)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RESPONSIVE_MENU);

		FullAppTraversalTests.chromeDriver2
				.findElement(By.id("revealTopLibraryCardLinkResponsive")).click();
		Thread.sleep(8000);

		// Get top card name
		final String topCardName = FullAppTraversalTests.chromeDriver2.findElement(
																						  By.id("topLibraryCard")).getAttribute("name");

		// Verify that the card name is the same in the second browser
		Assert.assertTrue(topCardName.equals(FullAppTraversalTests.chromeDriver1.findElement(
																									By.id("topLibraryCard")).getAttribute("name")));

		// Click on the button "Do nothing"
		FullAppTraversalTests.chromeDriver2.findElement(By.id("doNothing")).click();
		FullAppTraversalTests.chromeDriver1.findElement(By.id("doNothing")).click();
		Thread.sleep(8000);

		// Reveal again
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver2)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FullAppTraversalTests.chromeDriver2
				.findElement(By.id("revealTopLibraryCardLinkResponsive")).click();
		Thread.sleep(8000);

		// Assert that the card is the same
		Assert.assertTrue(topCardName.equals(FullAppTraversalTests.chromeDriver2.findElement(
																									By.id("topLibraryCard")).getAttribute("name")));
		Assert.assertTrue(topCardName.equals(FullAppTraversalTests.chromeDriver1.findElement(
																									By.id("topLibraryCard")).getAttribute("name")));

		// Put to battlefield
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver2)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver1)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);

		FullAppTraversalTests.chromeDriver2.findElement(By.id("putToBattlefieldFromModalWindow"))
				.click();
		FullAppTraversalTests.chromeDriver1.findElement(By.id("doNothing")).click();

		// Verify that the card is present on the battlefield
		Thread.sleep(8000);
		Assert.assertEquals(4,
								   FullAppTraversalTests.chromeDriver2.findElements(By.cssSelector(".magicCard"))
										   .size());
		Assert.assertEquals(4,
								   FullAppTraversalTests.chromeDriver1.findElements(By.cssSelector(".magicCard"))
										   .size());

		// Assert that the card on the battlefield is the same
		Assert.assertEquals(
								   topCardName,
								   FullAppTraversalTests.chromeDriver2.findElements(By.cssSelector(".magicCard"))
										   .get(2).getAttribute("name"));
		Assert.assertEquals(
								   topCardName,
								   FullAppTraversalTests.chromeDriver1.findElements(By.cssSelector(".magicCard"))
										   .get(2).getAttribute("name"));

		// Reveal top card of library
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver2)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FullAppTraversalTests.chromeDriver2
				.findElement(By.id("revealTopLibraryCardLinkResponsive")).click();
		Thread.sleep(8000);

		// Put to hand
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver2)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver1)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);

		FullAppTraversalTests.chromeDriver2.findElement(By.id("putToHandFromModalWindow")).click();
		FullAppTraversalTests.chromeDriver1.findElement(By.id("doNothing")).click();

		Thread.sleep(8000);

		// Assert that the hand contains 8 cards
		Assert.assertEquals(8,
								   FullAppTraversalTests.chromeDriver2.findElements(By.cssSelector(".movers-row"))
										   .size());

		// Verify that there is still two cards on the battlefield
		Assert.assertEquals(4,
								   FullAppTraversalTests.chromeDriver2.findElements(By.cssSelector(".magicCard"))
										   .size());
		Assert.assertEquals(4,
								   FullAppTraversalTests.chromeDriver1.findElements(By.cssSelector(".magicCard"))
										   .size());

		// Reveal again
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver2)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FullAppTraversalTests.chromeDriver2
				.findElement(By.id("revealTopLibraryCardLinkResponsive")).click();
		Thread.sleep(8000);

		// Get top card name
		final String graveyardCardName = FullAppTraversalTests.chromeDriver2.findElement(
																								By.id("topLibraryCard")).getAttribute("name");

		// Put to graveyard
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver2)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver1)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);

		FullAppTraversalTests.chromeDriver2.findElement(By.id("putToGraveyardFromModalWindow"))
				.click();
		FullAppTraversalTests.chromeDriver1.findElement(By.id("doNothing")).click();
		Thread.sleep(8000);

		// Assert graveyard is visible and contains one card
		Assert.assertFalse(FullAppTraversalTests.chromeDriver2.findElements(
																				   By.id("graveyard-page-wrap")).isEmpty());
		Assert.assertTrue(FullAppTraversalTests.chromeDriver2.findElements(
																				  By.cssSelector(".graveyard-cross-link")).size() == 1);

		// Verify name of the card in the graveyard
		Assert.assertTrue(graveyardCardName.equals(FullAppTraversalTests.chromeDriver2
														   .findElements(By.cssSelector(".graveyard-cross-link:nth-child(1) img")).get(0)
														   .getAttribute("name")));

		// Verify that there is still two cards on the battlefield
		Assert.assertEquals(4,
								   FullAppTraversalTests.chromeDriver2.findElements(By.cssSelector(".magicCard"))
										   .size());
		Assert.assertEquals(4,
								   FullAppTraversalTests.chromeDriver1.findElements(By.cssSelector(".magicCard"))
										   .size());

		// Verify the name of the card on the battlefield
		Assert.assertEquals(
								   topCardName,
								   FullAppTraversalTests.chromeDriver2.findElements(By.cssSelector(".magicCard"))
										   .get(2).getAttribute("name"));
		Assert.assertEquals(
								   topCardName,
								   FullAppTraversalTests.chromeDriver1.findElements(By.cssSelector(".magicCard"))
										   .get(2).getAttribute("name"));

		// Verify that the hands contains 8 cards
		Assert.assertEquals(8,
								   FullAppTraversalTests.chromeDriver2.findElements(By.cssSelector(".movers-row"))
										   .size());
		Assert.assertEquals(7,
								   FullAppTraversalTests.chromeDriver1.findElements(By.cssSelector(".movers-row"))
										   .size());

		// Put one card from hand to graveyard
		((JavascriptExecutor) FullAppTraversalTests.chromeDriver2)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_PUT_TO_ZONE_SUMBIT_BUTTON_FOR_HAND);
		new Select(FullAppTraversalTests.chromeDriver2.findElement(By.id("putToZoneSelectForHand")))
				.getOptions().get(1).click();
		FullAppTraversalTests.chromeDriver2.findElement(By.id("moveToZoneSubmitHand")).click();
		Thread.sleep(8000);

		// Verify that there is one more card in the graveyard
		Assert.assertFalse(FullAppTraversalTests.chromeDriver2.findElements(
																				   By.id("graveyard-page-wrap")).isEmpty());
		Assert.assertEquals(
								   2,
								   FullAppTraversalTests.chromeDriver2.findElements(
																						   By.cssSelector(".graveyard-cross-link")).size());

		// Put current card from hand to exile
		new Select(FullAppTraversalTests.chromeDriver2.findElement(By.id("putToZoneSelectForHand")))
				.getOptions().get(2).click();
		FullAppTraversalTests.chromeDriver2.findElement(By.id("moveToZoneSubmitHand")).click();
		Thread.sleep(8000);

		// Verify that there is one more card in the exile and that it is
		// visible
		Assert.assertFalse(FullAppTraversalTests.chromeDriver2.findElements(
																				   By.id("exile-page-wrap")).isEmpty());
		Assert.assertEquals(
								   1,
								   FullAppTraversalTests.chromeDriver2.findElements(
																						   By.cssSelector(".exile-cross-link")).size());

		// Put current card in exile to graveyard
		new Select(
						  FullAppTraversalTests.chromeDriver2.findElement(By.id("putToZoneSelectForExile")))
				.getOptions().get(1).click();
		FullAppTraversalTests.chromeDriver2.findElement(By.id("moveToZoneSubmitExile")).click();
		Thread.sleep(8000);

		// Verify that there is one more card in the graveyard
		Assert.assertFalse(FullAppTraversalTests.chromeDriver2.findElements(
																				   By.id("graveyard-page-wrap")).isEmpty());
		Assert.assertEquals(
								   3,
								   FullAppTraversalTests.chromeDriver2.findElements(
																						   By.cssSelector(".graveyard-cross-link")).size());

		// Get name of the current card in the hand
		final String handCardName = FullAppTraversalTests.chromeDriver2
											.findElements(By.cssSelector(".cross-link:nth-child(1) img")).get(0)
											.getAttribute("name");

		// Put current card from hand to exile
		new Select(FullAppTraversalTests.chromeDriver2.findElement(By.id("putToZoneSelectForHand")))
				.getOptions().get(2).click();
		FullAppTraversalTests.chromeDriver2.findElement(By.id("moveToZoneSubmitHand")).click();
		Thread.sleep(8000);

		// Verify that there is one more card in the exile
		Assert.assertFalse(FullAppTraversalTests.chromeDriver2.findElements(
																				   By.id("exile-page-wrap")).isEmpty());
		Assert.assertEquals(
								   1,
								   FullAppTraversalTests.chromeDriver2.findElements(
																						   By.cssSelector(".exile-cross-link")).size());

		// Get name of the current card in the exile
		final String exileCardName = FullAppTraversalTests.chromeDriver2
											 .findElements(By.cssSelector(".exile-cross-link:nth-child(1) img")).get(0)
											 .getAttribute("name");

		// Verify that active card in exile is same than card from hand
		Assert.assertEquals(handCardName, exileCardName);

		// Put card from exile to battlefield
		new Select(
						  FullAppTraversalTests.chromeDriver2.findElement(By.id("putToZoneSelectForExile")))
				.getOptions().get(0).click();
		FullAppTraversalTests.chromeDriver2.findElement(By.id("moveToZoneSubmitExile")).click();
		Thread.sleep(8000);

		// Verify that there are three cards on the battlefield
		Assert.assertEquals(6,
								   FullAppTraversalTests.chromeDriver2.findElements(By.cssSelector(".magicCard"))
										   .size());
		Assert.assertEquals(6,
								   FullAppTraversalTests.chromeDriver1.findElements(By.cssSelector(".magicCard"))
										   .size());

		// Verify the name of the card on the battlefield
		Assert.assertEquals(
								   exileCardName,
								   FullAppTraversalTests.chromeDriver2.findElements(By.cssSelector(".magicCard"))
										   .get(4).getAttribute("name"));
		Assert.assertEquals(
								   exileCardName,
								   FullAppTraversalTests.chromeDriver1.findElements(By.cssSelector(".magicCard"))
										   .get(4).getAttribute("name"));
	}

}
