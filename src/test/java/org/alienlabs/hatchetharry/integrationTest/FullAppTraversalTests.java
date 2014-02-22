package org.alienlabs.hatchetharry.integrationTest;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.BrowserVersion;

public class FullAppTraversalTests
{
	private static WebDriver firefoxDriver;
	private static WebDriver htmlUnitDriver;

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
	public static void setUpClass() throws InterruptedException
	{
		FullAppTraversalTests.htmlUnitDriver = new HtmlUnitDriver(BrowserVersion.FIREFOX_17);
		FullAppTraversalTests.htmlUnitDriver.manage().timeouts()
				.implicitlyWait(15, TimeUnit.SECONDS);

		final ProfilesIni allProfiles = new ProfilesIni();
		final FirefoxProfile profile = allProfiles.getProfile("WebDriver");
		profile.setPreference("foo.bar", 23);
		FullAppTraversalTests.firefoxDriver = new FirefoxDriver(profile);
		FullAppTraversalTests.firefoxDriver.manage().timeouts()
				.implicitlyWait(15, TimeUnit.SECONDS);

		Thread.sleep(15000);

		FullAppTraversalTests.firefoxDriver.get(FullAppTraversalTests.HOST + ":"
				+ FullAppTraversalTests.PORT + "/");
		FullAppTraversalTests.firefoxDriver.getPageSource();

		FullAppTraversalTests.htmlUnitDriver.get(FullAppTraversalTests.HOST + ":"
				+ FullAppTraversalTests.PORT + "/");
		FullAppTraversalTests.htmlUnitDriver.getPageSource();

		Thread.sleep(15000);
	}

	@AfterClass
	public static void tearDownClass()
	{
		FullAppTraversalTests.firefoxDriver.quit();
		FullAppTraversalTests.htmlUnitDriver.quit();
	}

	@Test
	public void testFullAppTraversal() throws InterruptedException
	{
		// Create a game in Chrome 1
		FullAppTraversalTests.waitForJQueryProcessing(FullAppTraversalTests.firefoxDriver, 30);
		FullAppTraversalTests.waitForJQueryProcessing(FullAppTraversalTests.htmlUnitDriver, 30);

		((JavascriptExecutor)FullAppTraversalTests.htmlUnitDriver)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);

		Thread.sleep(8000);

		FullAppTraversalTests.htmlUnitDriver.findElement(By.id("createGameLinkResponsive")).click();
		FullAppTraversalTests.htmlUnitDriver.findElement(By.id("createGameLinkResponsive")).click();
		Thread.sleep(8000);
		FullAppTraversalTests.htmlUnitDriver.findElement(By.id("name")).clear();
		FullAppTraversalTests.htmlUnitDriver.findElement(By.id("name")).sendKeys("Zala");
		new Select(FullAppTraversalTests.htmlUnitDriver.findElement(By.id("sideInput")))
				.selectByVisibleText("infrared");
		new Select(FullAppTraversalTests.htmlUnitDriver.findElement(By.id("decks")))
				.selectByVisibleText("Aura Bant");

		final String gameId = FullAppTraversalTests.htmlUnitDriver.findElement(By.id("gameId"))
				.getText();

		FullAppTraversalTests.htmlUnitDriver.findElement(By.id("createSubmit")).click();
		Thread.sleep(8000);

		// Join a game in Chrome 2
		FullAppTraversalTests.firefoxDriver.findElement(By.id("joinGameLinkResponsive")).click();
		Thread.sleep(8000);
		FullAppTraversalTests.firefoxDriver.findElement(By.id("name")).clear();
		FullAppTraversalTests.firefoxDriver.findElement(By.id("name")).sendKeys("Marie");
		new Select(FullAppTraversalTests.firefoxDriver.findElement(By.id("sideInput")))
				.selectByVisibleText("ultraviolet");
		new Select(FullAppTraversalTests.firefoxDriver.findElement(By.id("decks")))
				.selectByVisibleText("Aura Bant");
		FullAppTraversalTests.firefoxDriver.findElement(By.id("gameIdInput")).clear();
		FullAppTraversalTests.firefoxDriver.findElement(By.id("gameIdInput")).sendKeys(gameId);

		FullAppTraversalTests.firefoxDriver.findElement(By.id("joinSubmit")).click();

		// Assert that no card is present on battlefield
		// The Balduvian Horde is hidden but still there
		// And it contains TWO elements of class magicCard
		Thread.sleep(45000);
		Assert.assertEquals(2,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
						.size());
		Assert.assertEquals(2,
				FullAppTraversalTests.htmlUnitDriver.findElements(By.cssSelector(".magicCard"))
						.size());

		// Verify that the hands contains 7 cards
		Assert.assertEquals(7,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".movers-row"))
						.size());
		Assert.assertEquals(7,
				FullAppTraversalTests.htmlUnitDriver.findElements(By.cssSelector(".movers-row"))
						.size());

		// Find first hand card name of Chrome1
		final String battlefieldCardName = FullAppTraversalTests.firefoxDriver
				.findElements(By.cssSelector(".cross-link:nth-child(1) img")).get(0)
				.getAttribute("name");

		// Play a card in Chrome1
		FullAppTraversalTests.firefoxDriver.findElement(By.id("playCardLink0")).click();

		// Verify that the hand contains only 6 cards, now
		Thread.sleep(45000);
		Assert.assertEquals(6,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".movers-row"))
						.size());
		Thread.sleep(15000);
		// Verify that card is present on the battlefield
		// Two HTML elements with class "magicCard" are created for each card
		Assert.assertEquals(4,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
						.size());

		Assert.assertEquals(4,
				FullAppTraversalTests.htmlUnitDriver.findElements(By.cssSelector(".magicCard"))
						.size());

		// Verify the name of the card on the battlefield
		Assert.assertEquals(
				battlefieldCardName,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
						.get(2).getAttribute("name"));
		Assert.assertEquals(battlefieldCardName,
				FullAppTraversalTests.htmlUnitDriver.findElements(By.cssSelector(".magicCard"))
						.get(2).getAttribute("name"));

		// Verify that the card is untapped
		Assert.assertFalse(FullAppTraversalTests.firefoxDriver
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("transform"));
		Assert.assertFalse(FullAppTraversalTests.htmlUnitDriver
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("transform"));

		// Tap card
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_CARD);
		FullAppTraversalTests.firefoxDriver
				.findElement(By.cssSelector("img[id^='tapHandleImage']")).click();
		Thread.sleep(15000);

		// Verify card is tapped
		Assert.assertTrue(FullAppTraversalTests.firefoxDriver
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("rotate(90deg)"));
		Assert.assertTrue(FullAppTraversalTests.htmlUnitDriver
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("rotate(90deg)"));

		// Assert that graveyard is not visible
		Assert.assertTrue(FullAppTraversalTests.firefoxDriver.findElements(
				By.id("graveyard-page-wrap")).isEmpty());

		// Drag card to graveyard
		WebElement draggable = FullAppTraversalTests.firefoxDriver.findElement(By
				.cssSelector("img[id^='handleImage']"));
		WebElement to = FullAppTraversalTests.firefoxDriver.findElement(By.id("putToGraveyard"));
		new Actions(FullAppTraversalTests.firefoxDriver).dragAndDrop(draggable, to).build()
				.perform();

		Thread.sleep(25000);

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
		Thread.sleep(10000);
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RESPONSIVE_MENU);

		FullAppTraversalTests.firefoxDriver.findElement(
				By.id("playCardFromGraveyardLinkResponsive")).click();
		Thread.sleep(25000);

		// Verify the name of the card on the battlefield
		Assert.assertEquals(
				battlefieldCardName,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
						.get(2).getAttribute("name"));
		Assert.assertEquals(battlefieldCardName,
				FullAppTraversalTests.htmlUnitDriver.findElements(By.cssSelector(".magicCard"))
						.get(2).getAttribute("name"));

		// Assert that the graveyard is visible and empty
		Assert.assertFalse(FullAppTraversalTests.firefoxDriver.findElements(
				By.id("graveyard-page-wrap")).isEmpty());
		Assert.assertTrue(FullAppTraversalTests.firefoxDriver.findElements(
				By.cssSelector(".graveyard-cross-link")).isEmpty());

		// Drag card to hand
		draggable = FullAppTraversalTests.firefoxDriver.findElement(By
				.cssSelector("img[id^='handleImage']"));
		to = FullAppTraversalTests.firefoxDriver.findElement(By.id("putToHand"));
		new Actions(FullAppTraversalTests.firefoxDriver).dragAndDrop(draggable, to).build()
				.perform();

		Thread.sleep(15000);

		// Assert that the hand contains 7 cards again
		Assert.assertEquals(7,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".movers-row"))
						.size());

		// Reveal top card of library
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		Thread.sleep(8000);
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RESPONSIVE_MENU);

		FullAppTraversalTests.firefoxDriver
				.findElement(By.id("revealTopLibraryCardLinkResponsive")).click();
		Thread.sleep(8000);

		// Get top card name
		final String topCardName = FullAppTraversalTests.firefoxDriver.findElement(
				By.id("topLibraryCard")).getAttribute("name");

		// Verify that the card name is the same in the second browser
		Assert.assertTrue(topCardName.equals(FullAppTraversalTests.htmlUnitDriver.findElement(
				By.id("topLibraryCard")).getAttribute("name")));

		// Click on the button "Do nothing"
		FullAppTraversalTests.firefoxDriver.findElement(By.id("doNothing")).click();
		FullAppTraversalTests.htmlUnitDriver.findElement(By.id("doNothing")).click();
		Thread.sleep(8000);

		// Reveal again
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FullAppTraversalTests.firefoxDriver
				.findElement(By.id("revealTopLibraryCardLinkResponsive")).click();
		Thread.sleep(15000);

		// Assert that the card is the same
		Assert.assertTrue(topCardName.equals(FullAppTraversalTests.firefoxDriver.findElement(
				By.id("topLibraryCard")).getAttribute("name")));
		Assert.assertTrue(topCardName.equals(FullAppTraversalTests.htmlUnitDriver.findElement(
				By.id("topLibraryCard")).getAttribute("name")));

		// Put to battlefield
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		((JavascriptExecutor)FullAppTraversalTests.htmlUnitDriver)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);

		FullAppTraversalTests.firefoxDriver.findElement(By.id("putToBattlefieldFromModalWindow"))
				.click();
		FullAppTraversalTests.htmlUnitDriver.findElement(By.id("doNothing")).click();

		// Verify that the card is present on the battlefield
		Thread.sleep(25000);
		Assert.assertEquals(4,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
						.size());
		Assert.assertEquals(4,
				FullAppTraversalTests.htmlUnitDriver.findElements(By.cssSelector(".magicCard"))
						.size());

		// Assert that the card on the battlefield is the same
		Assert.assertEquals(
				topCardName,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
						.get(2).getAttribute("name"));
		Assert.assertEquals(topCardName,
				FullAppTraversalTests.htmlUnitDriver.findElements(By.cssSelector(".magicCard"))
						.get(2).getAttribute("name"));

		// Reveal top card of library
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FullAppTraversalTests.firefoxDriver
				.findElement(By.id("revealTopLibraryCardLinkResponsive")).click();
		Thread.sleep(8000);

		// Put to hand
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		((JavascriptExecutor)FullAppTraversalTests.htmlUnitDriver)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);

		FullAppTraversalTests.firefoxDriver.findElement(By.id("putToHandFromModalWindow")).click();
		FullAppTraversalTests.htmlUnitDriver.findElement(By.id("doNothing")).click();

		Thread.sleep(15000);

		// Assert that the hand contains 8 cards
		Assert.assertEquals(8,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".movers-row"))
						.size());

		// Verify that there is still two cards on the battlefield
		Assert.assertEquals(4,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
						.size());
		Assert.assertEquals(4,
				FullAppTraversalTests.htmlUnitDriver.findElements(By.cssSelector(".magicCard"))
						.size());

		// Reveal again
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FullAppTraversalTests.firefoxDriver
				.findElement(By.id("revealTopLibraryCardLinkResponsive")).click();
		Thread.sleep(12000);

		// Get top card name
		final String graveyardCardName = FullAppTraversalTests.firefoxDriver.findElement(
				By.id("topLibraryCard")).getAttribute("name");

		// Put to graveyard
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		((JavascriptExecutor)FullAppTraversalTests.htmlUnitDriver)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);

		FullAppTraversalTests.firefoxDriver.findElement(By.id("putToGraveyardFromModalWindow"))
				.click();
		FullAppTraversalTests.htmlUnitDriver.findElement(By.id("doNothing")).click();
		Thread.sleep(15000);

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
				FullAppTraversalTests.htmlUnitDriver.findElements(By.cssSelector(".magicCard"))
						.size());

		// Verify the name of the card on the battlefield
		Assert.assertEquals(
				topCardName,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
						.get(2).getAttribute("name"));
		Assert.assertEquals(topCardName,
				FullAppTraversalTests.htmlUnitDriver.findElements(By.cssSelector(".magicCard"))
						.get(2).getAttribute("name"));

		// Verify that the hands contains 8 cards
		Assert.assertEquals(8,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".movers-row"))
						.size());
		Assert.assertEquals(7,
				FullAppTraversalTests.htmlUnitDriver.findElements(By.cssSelector(".movers-row"))
						.size());

		// Put one card from hand to graveyard
		((JavascriptExecutor)FullAppTraversalTests.firefoxDriver)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_PUT_TO_ZONE_SUMBIT_BUTTON_FOR_HAND);
		new Select(FullAppTraversalTests.firefoxDriver.findElement(By.id("putToZoneSelectForHand")))
				.selectByVisibleText("Graveyard");
		FullAppTraversalTests.firefoxDriver.findElement(By.id("moveToZoneSubmitHand")).click();
		Thread.sleep(15000);

		// Verify that there is one more card in the graveyard
		Assert.assertFalse(FullAppTraversalTests.firefoxDriver.findElements(
				By.id("graveyard-page-wrap")).isEmpty());
		Assert.assertEquals(
				2,
				FullAppTraversalTests.firefoxDriver.findElements(
						By.cssSelector(".graveyard-cross-link")).size());

		// Put current card from hand to exile
		new Select(FullAppTraversalTests.firefoxDriver.findElement(By.id("putToZoneSelectForHand")))
				.selectByVisibleText("Exile");
		FullAppTraversalTests.firefoxDriver.findElement(By.id("moveToZoneSubmitHand")).click();
		Thread.sleep(15000);

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
				.selectByVisibleText("Graveyard");
		FullAppTraversalTests.firefoxDriver.findElement(By.id("moveToZoneSubmitExile")).click();
		Thread.sleep(15000);

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
				.selectByVisibleText("Exile");
		FullAppTraversalTests.firefoxDriver.findElement(By.id("moveToZoneSubmitHand")).click();
		Thread.sleep(15000);

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
				.selectByVisibleText("Battlefield");
		FullAppTraversalTests.firefoxDriver.findElement(By.id("moveToZoneSubmitExile")).click();
		Thread.sleep(30000);

		// Verify that there are three cards on the battlefield
		Assert.assertEquals(6,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
						.size());
		Assert.assertEquals(6,
				FullAppTraversalTests.htmlUnitDriver.findElements(By.cssSelector(".magicCard"))
						.size());

		// Verify the name of the card on the battlefield
		Assert.assertEquals(
				exileCardName,
				FullAppTraversalTests.firefoxDriver.findElements(By.cssSelector(".magicCard"))
						.get(4).getAttribute("name"));
		Assert.assertEquals(exileCardName,
				FullAppTraversalTests.htmlUnitDriver.findElements(By.cssSelector(".magicCard"))
						.get(4).getAttribute("name"));
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
