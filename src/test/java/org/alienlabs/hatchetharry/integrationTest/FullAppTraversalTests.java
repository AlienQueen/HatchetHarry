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
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.opera.core.systems.OperaDriver;

public class FullAppTraversalTests
{
	private static WebDriver operaDriver1;
	private static WebDriver operaDriver2;

	private static final String PORT = "8088";
	private static final String HOST = "localhost";

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
		FullAppTraversalTests.operaDriver1 = new OperaDriver();
		FullAppTraversalTests.operaDriver2 = new OperaDriver();

		FullAppTraversalTests.operaDriver1.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		FullAppTraversalTests.operaDriver2.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		Thread.sleep(15000);

		FullAppTraversalTests.operaDriver1.navigate().to(
				FullAppTraversalTests.HOST + ":" + FullAppTraversalTests.PORT + "/");
		FullAppTraversalTests.operaDriver2.navigate().to(
				FullAppTraversalTests.HOST + ":" + FullAppTraversalTests.PORT + "/");

		Thread.sleep(15000);
	}

	@AfterClass
	public static void tearDownClass()
	{
		FullAppTraversalTests.operaDriver1.quit();
		FullAppTraversalTests.operaDriver2.quit();
	}

	@Test
	public void testFullAppTraversal() throws InterruptedException
	{

		// Create a game in Chrome 1
		FullAppTraversalTests.waitForJQueryProcessing(FullAppTraversalTests.operaDriver1, 30);

		((JavascriptExecutor)FullAppTraversalTests.operaDriver1)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);

		FullAppTraversalTests.operaDriver1.findElement(By.id("createGameLinkResponsive")).click();
		Thread.sleep(8000);
		FullAppTraversalTests.operaDriver1.findElement(By.id("name")).clear();
		FullAppTraversalTests.operaDriver1.findElement(By.id("name")).sendKeys("Zala");
		new Select(FullAppTraversalTests.operaDriver1.findElement(By.id("sideInput")))
				.selectByVisibleText("infrared");
		new Select(FullAppTraversalTests.operaDriver1.findElement(By.id("decks")))
				.selectByVisibleText("Aura Bant");

		final String gameId = FullAppTraversalTests.operaDriver1.findElement(By.id("gameId"))
				.getText();

		FullAppTraversalTests.operaDriver1.findElement(By.id("createSubmit")).click();
		Thread.sleep(8000);

		// Join a game in Chrome 2
		FullAppTraversalTests.waitForJQueryProcessing(FullAppTraversalTests.operaDriver2, 15);

		((JavascriptExecutor)FullAppTraversalTests.operaDriver2)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);

		FullAppTraversalTests.operaDriver2.findElement(By.id("joinGameLinkResponsive")).click();
		Thread.sleep(8000);
		FullAppTraversalTests.operaDriver2.findElement(By.id("name")).clear();
		FullAppTraversalTests.operaDriver2.findElement(By.id("name")).sendKeys("Marie");
		new Select(FullAppTraversalTests.operaDriver2.findElement(By.id("sideInput")))
				.selectByVisibleText("ultraviolet");
		new Select(FullAppTraversalTests.operaDriver2.findElement(By.id("decks")))
				.selectByVisibleText("Aura Bant");
		FullAppTraversalTests.operaDriver2.findElement(By.id("gameIdInput")).clear();
		FullAppTraversalTests.operaDriver2.findElement(By.id("gameIdInput")).sendKeys(gameId);

		FullAppTraversalTests.operaDriver2.findElement(By.id("joinSubmit")).click();

		// Assert that no card is present on battlefield
		// The Balduvian Horde is hidden but still there
		// And it contains TWO elements of class magicCard
		Thread.sleep(15000);
		Assert.assertEquals(2,
				FullAppTraversalTests.operaDriver1.findElements(By.cssSelector(".magicCard"))
						.size());
		Assert.assertEquals(2,
				FullAppTraversalTests.operaDriver2.findElements(By.cssSelector(".magicCard"))
						.size());

		// Verify that the hands contains 7 cards
		Assert.assertEquals(7,
				FullAppTraversalTests.operaDriver1.findElements(By.cssSelector(".movers-row"))
						.size());
		Assert.assertEquals(7,
				FullAppTraversalTests.operaDriver2.findElements(By.cssSelector(".movers-row"))
						.size());

		// Find first hand card name of Chrome1
		final String battlefieldCardName = FullAppTraversalTests.operaDriver1
				.findElements(By.cssSelector(".cross-link:nth-child(1) img")).get(0)
				.getAttribute("name");

		// Play a card in Chrome1
		FullAppTraversalTests.operaDriver1.findElement(By.id("playCardLink0")).click();

		// Verify that the hand contains only 6 cards, now
		Thread.sleep(25000);
		Assert.assertEquals(6,
				FullAppTraversalTests.operaDriver1.findElements(By.cssSelector(".movers-row"))
						.size());

		// Verify that card is present on the battlefield
		// Two HTML elements with class "magicCard" are created for each card
		Assert.assertEquals(4,
				FullAppTraversalTests.operaDriver1.findElements(By.cssSelector(".magicCard"))
						.size());
		Assert.assertEquals(4,
				FullAppTraversalTests.operaDriver2.findElements(By.cssSelector(".magicCard"))
						.size());

		// Verify the name of the card on the battlefield
		Assert.assertEquals(battlefieldCardName,
				FullAppTraversalTests.operaDriver1.findElements(By.cssSelector(".magicCard"))
						.get(2).getAttribute("name"));
		Assert.assertEquals(battlefieldCardName,
				FullAppTraversalTests.operaDriver2.findElements(By.cssSelector(".magicCard"))
						.get(2).getAttribute("name"));

		// Verify that the card is untapped
		Assert.assertFalse(FullAppTraversalTests.operaDriver1
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("transform"));
		Assert.assertFalse(FullAppTraversalTests.operaDriver2
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("transform"));

		// Tap card
		((JavascriptExecutor)FullAppTraversalTests.operaDriver1)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_CARD);
		FullAppTraversalTests.operaDriver1.findElement(By.cssSelector("img[id^='tapHandleImage']"))
				.click();
		Thread.sleep(8000);

		// Verify card is tapped
		Assert.assertTrue(FullAppTraversalTests.operaDriver1
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("rotate(90deg)"));
		Assert.assertTrue(FullAppTraversalTests.operaDriver2
				.findElements(By.cssSelector("img[id^='card']")).get(0).getAttribute("style")
				.contains("rotate(90deg)"));

		// Assert that graveyard is not visible
		Assert.assertTrue(FullAppTraversalTests.operaDriver1.findElements(
				By.id("graveyard-page-wrap")).isEmpty());

		// Drag card to graveyard
		WebElement draggable = FullAppTraversalTests.operaDriver1.findElement(By
				.cssSelector("img[id^='handleImage']"));
		WebElement to = FullAppTraversalTests.operaDriver1.findElement(By.id("putToGraveyard"));
		new Actions(FullAppTraversalTests.operaDriver1).dragAndDrop(draggable, to).build()
				.perform();

		Thread.sleep(5000);

		// Assert graveyard is visible and contains one card
		Assert.assertFalse(FullAppTraversalTests.operaDriver1.findElements(
				By.id("graveyard-page-wrap")).isEmpty());
		Assert.assertTrue(FullAppTraversalTests.operaDriver1.findElements(
				By.cssSelector(".graveyard-cross-link")).size() == 1);

		// Verify name of the card in the graveyard
		Assert.assertTrue(battlefieldCardName.equals(FullAppTraversalTests.operaDriver1
				.findElements(By.cssSelector(".graveyard-cross-link:nth-child(1) img")).get(0)
				.getAttribute("name")));

		// Play card from graveyard
		((JavascriptExecutor)FullAppTraversalTests.operaDriver1)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		Thread.sleep(1000);
		((JavascriptExecutor)FullAppTraversalTests.operaDriver1)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RESPONSIVE_MENU);

		FullAppTraversalTests.operaDriver1
				.findElement(By.id("playCardFromGraveyardLinkResponsive")).click();
		Thread.sleep(8000);

		// Verify the name of the card on the battlefield
		Assert.assertEquals(battlefieldCardName,
				FullAppTraversalTests.operaDriver1.findElements(By.cssSelector(".magicCard"))
						.get(2).getAttribute("name"));
		Assert.assertEquals(battlefieldCardName,
				FullAppTraversalTests.operaDriver2.findElements(By.cssSelector(".magicCard"))
						.get(2).getAttribute("name"));

		// Assert that the graveyard is visible and empty
		Assert.assertFalse(FullAppTraversalTests.operaDriver1.findElements(
				By.id("graveyard-page-wrap")).isEmpty());
		Assert.assertTrue(FullAppTraversalTests.operaDriver1.findElements(
				By.cssSelector(".graveyard-cross-link")).isEmpty());

		// Drag card to hand
		draggable = FullAppTraversalTests.operaDriver1.findElement(By
				.cssSelector("img[id^='handleImage']"));
		to = FullAppTraversalTests.operaDriver1.findElement(By.id("putToHand"));
		new Actions(FullAppTraversalTests.operaDriver1).dragAndDrop(draggable, to).build()
				.perform();

		Thread.sleep(3000);

		// Assert that the hand contains 7 cards again
		Assert.assertEquals(7,
				FullAppTraversalTests.operaDriver1.findElements(By.cssSelector(".movers-row"))
						.size());

		// Reveal top card of library
		((JavascriptExecutor)FullAppTraversalTests.operaDriver1)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		Thread.sleep(500);
		((JavascriptExecutor)FullAppTraversalTests.operaDriver1)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_RESPONSIVE_MENU);

		FullAppTraversalTests.operaDriver1.findElement(By.id("revealTopLibraryCardLinkResponsive"))
				.click();
		Thread.sleep(8000);

		// Get top card name
		final String topCardName = FullAppTraversalTests.operaDriver1.findElement(
				By.id("topLibraryCard")).getAttribute("name");

		// Verify that the card name is the same in the second browser
		Assert.assertTrue(topCardName.equals(FullAppTraversalTests.operaDriver2.findElement(
				By.id("topLibraryCard")).getAttribute("name")));

		// Click on the button "Do nothing"
		FullAppTraversalTests.operaDriver1.findElement(By.id("doNothing")).click();
		FullAppTraversalTests.operaDriver2.findElement(By.id("doNothing")).click();
		Thread.sleep(8000);

		// Reveal again
		((JavascriptExecutor)FullAppTraversalTests.operaDriver1)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FullAppTraversalTests.operaDriver1.findElement(By.id("revealTopLibraryCardLinkResponsive"))
				.click();
		Thread.sleep(8000);

		// Assert that the card is the same
		Assert.assertTrue(topCardName.equals(FullAppTraversalTests.operaDriver1.findElement(
				By.id("topLibraryCard")).getAttribute("name")));
		Assert.assertTrue(topCardName.equals(FullAppTraversalTests.operaDriver2.findElement(
				By.id("topLibraryCard")).getAttribute("name")));

		// Put to battlefield
		((JavascriptExecutor)FullAppTraversalTests.operaDriver1)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		((JavascriptExecutor)FullAppTraversalTests.operaDriver2)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);

		FullAppTraversalTests.operaDriver1.findElement(By.id("putToBattlefieldFromModalWindow"))
				.click();
		FullAppTraversalTests.operaDriver2.findElement(By.id("doNothing")).click();

		// Verify that the card is present on the battlefield
		Thread.sleep(8000);
		Assert.assertEquals(4,
				FullAppTraversalTests.operaDriver1.findElements(By.cssSelector(".magicCard"))
						.size());
		Assert.assertEquals(4,
				FullAppTraversalTests.operaDriver2.findElements(By.cssSelector(".magicCard"))
						.size());

		// Assert that the card on the battlefield is the same
		Assert.assertEquals(topCardName,
				FullAppTraversalTests.operaDriver1.findElements(By.cssSelector(".magicCard"))
						.get(2).getAttribute("name"));
		Assert.assertEquals(topCardName,
				FullAppTraversalTests.operaDriver2.findElements(By.cssSelector(".magicCard"))
						.get(2).getAttribute("name"));

		// Reveal top card of library
		((JavascriptExecutor)FullAppTraversalTests.operaDriver1)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FullAppTraversalTests.operaDriver1.findElement(By.id("revealTopLibraryCardLinkResponsive"))
				.click();
		Thread.sleep(8000);

		// Put to hand
		((JavascriptExecutor)FullAppTraversalTests.operaDriver1)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		((JavascriptExecutor)FullAppTraversalTests.operaDriver2)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);

		FullAppTraversalTests.operaDriver1.findElement(By.id("putToHandFromModalWindow")).click();
		FullAppTraversalTests.operaDriver2.findElement(By.id("doNothing")).click();

		Thread.sleep(15000);

		// Assert that the hand contains 8 cards
		Assert.assertEquals(8,
				FullAppTraversalTests.operaDriver1.findElements(By.cssSelector(".movers-row"))
						.size());

		// Verify that there is still two cards on the battlefield
		Assert.assertEquals(4,
				FullAppTraversalTests.operaDriver1.findElements(By.cssSelector(".magicCard"))
						.size());
		Assert.assertEquals(4,
				FullAppTraversalTests.operaDriver2.findElements(By.cssSelector(".magicCard"))
						.size());

		// Reveal again
		((JavascriptExecutor)FullAppTraversalTests.operaDriver1)
				.executeScript(FullAppTraversalTests.SHOW_AND_OPEN_MOBILE_MENUBAR);
		FullAppTraversalTests.operaDriver1.findElement(By.id("revealTopLibraryCardLinkResponsive"))
				.click();
		Thread.sleep(12000);

		// Get top card name
		final String graveyardCardName = FullAppTraversalTests.operaDriver1.findElement(
				By.id("topLibraryCard")).getAttribute("name");

		// Put to graveyard
		((JavascriptExecutor)FullAppTraversalTests.operaDriver1)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);
		((JavascriptExecutor)FullAppTraversalTests.operaDriver2)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_MODAL_WINDOW_BUTTONS);

		FullAppTraversalTests.operaDriver1.findElement(By.id("putToGraveyardFromModalWindow"))
				.click();
		FullAppTraversalTests.operaDriver2.findElement(By.id("doNothing")).click();
		Thread.sleep(8000);

		// Assert graveyard is visible and contains one card
		Assert.assertFalse(FullAppTraversalTests.operaDriver1.findElements(
				By.id("graveyard-page-wrap")).isEmpty());
		Assert.assertTrue(FullAppTraversalTests.operaDriver1.findElements(
				By.cssSelector(".graveyard-cross-link")).size() == 1);

		// Verify name of the card in the graveyard
		Assert.assertTrue(graveyardCardName.equals(FullAppTraversalTests.operaDriver1
				.findElements(By.cssSelector(".graveyard-cross-link:nth-child(1) img")).get(0)
				.getAttribute("name")));

		// Verify that there is still two cards on the battlefield
		Assert.assertEquals(4,
				FullAppTraversalTests.operaDriver1.findElements(By.cssSelector(".magicCard"))
						.size());
		Assert.assertEquals(4,
				FullAppTraversalTests.operaDriver2.findElements(By.cssSelector(".magicCard"))
						.size());

		// Verify the name of the card on the battlefield
		Assert.assertEquals(topCardName,
				FullAppTraversalTests.operaDriver1.findElements(By.cssSelector(".magicCard"))
						.get(2).getAttribute("name"));
		Assert.assertEquals(topCardName,
				FullAppTraversalTests.operaDriver2.findElements(By.cssSelector(".magicCard"))
						.get(2).getAttribute("name"));

		// Verify that the hands contains 8 cards
		Assert.assertEquals(8,
				FullAppTraversalTests.operaDriver1.findElements(By.cssSelector(".movers-row"))
						.size());
		Assert.assertEquals(7,
				FullAppTraversalTests.operaDriver2.findElements(By.cssSelector(".movers-row"))
						.size());

		// Put one card from hand to graveyard
		((JavascriptExecutor)FullAppTraversalTests.operaDriver1)
				.executeScript(FullAppTraversalTests.JAVA_SCRIPT_TO_CENTER_VIEWPORT_AROUND_PUT_TO_ZONE_SUMBIT_BUTTON_FOR_HAND);
		new Select(FullAppTraversalTests.operaDriver1.findElement(By.id("putToZoneSelectForHand")))
				.selectByVisibleText("Graveyard");
		FullAppTraversalTests.operaDriver1.findElement(By.id("moveToZoneSubmitHand")).click();
		Thread.sleep(8000);

		// Verify that there is one more card in the graveyard
		Assert.assertFalse(FullAppTraversalTests.operaDriver1.findElements(
				By.id("graveyard-page-wrap")).isEmpty());
		Assert.assertEquals(
				2,
				FullAppTraversalTests.operaDriver1.findElements(
						By.cssSelector(".graveyard-cross-link")).size());

		// Put current card from hand to exile
		new Select(FullAppTraversalTests.operaDriver1.findElement(By.id("putToZoneSelectForHand")))
				.selectByVisibleText("Exile");
		FullAppTraversalTests.operaDriver1.findElement(By.id("moveToZoneSubmitHand")).click();
		Thread.sleep(8000);

		// Verify that there is one more card in the exile and that it is
		// visible
		Assert.assertFalse(FullAppTraversalTests.operaDriver1
				.findElements(By.id("exile-page-wrap")).isEmpty());
		Assert.assertEquals(1,
				FullAppTraversalTests.operaDriver1
						.findElements(By.cssSelector(".exile-cross-link")).size());

		// Put current card in exile to graveyard
		new Select(FullAppTraversalTests.operaDriver1.findElement(By.id("putToZoneSelectForExile")))
				.selectByVisibleText("Graveyard");
		FullAppTraversalTests.operaDriver1.findElement(By.id("moveToZoneSubmitExile")).click();
		Thread.sleep(8000);

		// Verify that there is one more card in the graveyard
		Assert.assertFalse(FullAppTraversalTests.operaDriver1.findElements(
				By.id("graveyard-page-wrap")).isEmpty());
		Assert.assertEquals(
				3,
				FullAppTraversalTests.operaDriver1.findElements(
						By.cssSelector(".graveyard-cross-link")).size());

		// Get name of the current card in the hand
		final String handCardName = FullAppTraversalTests.operaDriver1
				.findElements(By.cssSelector(".cross-link:nth-child(1) img")).get(0)
				.getAttribute("name");

		// Put current card from hand to exile
		new Select(FullAppTraversalTests.operaDriver1.findElement(By.id("putToZoneSelectForHand")))
				.selectByVisibleText("Exile");
		FullAppTraversalTests.operaDriver1.findElement(By.id("moveToZoneSubmitHand")).click();
		Thread.sleep(8000);

		// Verify that there is one more card in the exile
		Assert.assertFalse(FullAppTraversalTests.operaDriver1
				.findElements(By.id("exile-page-wrap")).isEmpty());
		Assert.assertEquals(1,
				FullAppTraversalTests.operaDriver1
						.findElements(By.cssSelector(".exile-cross-link")).size());

		// Get name of the current card in the exile
		final String exileCardName = FullAppTraversalTests.operaDriver1
				.findElements(By.cssSelector(".exile-cross-link:nth-child(1) img")).get(0)
				.getAttribute("name");

		// Verify that active card in exile is same than card from hand
		Assert.assertEquals(handCardName, exileCardName);

		// Put card from exile to battlefield
		new Select(FullAppTraversalTests.operaDriver1.findElement(By.id("putToZoneSelectForExile")))
				.selectByVisibleText("Battlefield");
		FullAppTraversalTests.operaDriver1.findElement(By.id("moveToZoneSubmitExile")).click();
		Thread.sleep(8000);

		// Verify that there are three cards on the battlefield
		Assert.assertEquals(6,
				FullAppTraversalTests.operaDriver1.findElements(By.cssSelector(".magicCard"))
						.size());
		Assert.assertEquals(6,
				FullAppTraversalTests.operaDriver2.findElements(By.cssSelector(".magicCard"))
						.size());

		// Verify the name of the card on the battlefield
		Assert.assertEquals(exileCardName,
				FullAppTraversalTests.operaDriver1.findElements(By.cssSelector(".magicCard"))
						.get(4).getAttribute("name"));
		Assert.assertEquals(exileCardName,
				FullAppTraversalTests.operaDriver2.findElements(By.cssSelector(".magicCard"))
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
