package org.alienlabs.hatchetharry.view.page;

import java.io.IOException;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.card.CardPanel;
import org.alienlabs.hatchetharry.view.component.gui.ChatPanel;
import org.alienlabs.hatchetharry.view.component.gui.ClockPanel;
import org.alienlabs.hatchetharry.view.component.gui.DataBox;
import org.alienlabs.hatchetharry.view.component.gui.ExternalImage;
import org.alienlabs.hatchetharry.view.component.gui.HandComponent;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.TagTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * Tests of basic scenarios of the HomePage using the WicketTester.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
		"classpath:applicationContextTest.xml" })
public class HomePageTest extends SpringContextLoaderBaseTest
{
	private static String pageDocument;

	// Assert dock element is present and contains a .gif
	private static void testDockElement(final String name)
	{
		final String document = SpringContextLoaderBaseTest.tester.getLastResponse().getDocument();
		final List<TagTester> tagTester = TagTester.createTagsByAttribute(document, "title", name,
				false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());

		final TagTester tt = tagTester.get(0);
		Assert.assertNotNull(tt);
		final String attr = tt.getAttribute("alt");
		Assert.assertNotNull(attr);
		Assert.assertTrue(name.equals(attr));
		Assert.assertNotNull(tt.getAttribute("src"));
		Assert.assertTrue(tt.getAttribute("src").contains(".gif"));
	}

	private static void testModalWindow(final String _window, final String linkToActivateWindow)
	{
		// assert modal windows are in the page
		SpringContextLoaderBaseTest.tester.assertComponent(_window, ModalWindow.class);
		final ModalWindow window = (ModalWindow)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage(_window);
		SpringContextLoaderBaseTest.tester.assertInvisible(window.getPageRelativePath() + ":"
				+ window.getContentId());

		final AjaxLink<Void> link = (AjaxLink<Void>)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage(linkToActivateWindow);
		Assert.assertNotNull(link);
		SpringContextLoaderBaseTest.tester.clickLink(linkToActivateWindow, true);
		SpringContextLoaderBaseTest.tester.assertVisible(window.getPageRelativePath() + ":"
				+ window.getContentId());
	}

	@Test
	public void testRenderHand() throws IOException
	{
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		// assert hand is present
		SpringContextLoaderBaseTest.tester.assertComponent("galleryParent:gallery",
				HandComponent.class);

		// assert URL of a thumbnail
		final String document = SpringContextLoaderBaseTest.tester.getLastResponse().getDocument();
		final List<TagTester> tagTester = TagTester.createTagsByAttribute(document, "class",
				"magicCard", false);
		Assert.assertNotNull(tagTester);

		// assert number of thumbnails
		Assert.assertEquals(7, tagTester.size());

		Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));
	}

	@Test
	public void testRenderMyPage()
	{
		// assert rendered label component
		final Label message = (Label)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("message1");
		Assert.assertTrue(message.getDefaultModelObjectAsString().contains("version"));
		Assert.assertTrue(message.getDefaultModelObjectAsString().contains("release"));
	}

	@Test
	public void testRenderClock() throws IOException
	{
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		// assert clock is present
		SpringContextLoaderBaseTest.tester.assertComponent("clockPanel", ClockPanel.class);

		// assert clock content
		final ClockPanel clock = (ClockPanel)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("clockPanel");
		Assert.assertTrue(clock.getTime().getObject().contains("###"));
	}

	@Test
	public void testRenderMenuBar() throws IOException
	{
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		// Assert menubar
		final String document = SpringContextLoaderBaseTest.tester.getLastResponse().getDocument();
		final List<TagTester> tagTester = TagTester.createTagsByAttribute(document, "class",
				"shift-bottom", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(3, tagTester.size());

		// Assert 'Import a deck' entry exists
		boolean containsText = false;
		for (final TagTester tt : tagTester)
		{
			if (((null != tt.getMarkup()) && tt.getMarkup().contains("Game")))
			{
				containsText = true;
				break;
			}
		}
		Assert.assertTrue(containsText);
	}

	@Test
	public void testRenderChat() throws IOException
	{
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		// assert chat is present
		SpringContextLoaderBaseTest.tester.assertComponent("chatPanel", ChatPanel.class);
		SpringContextLoaderBaseTest.tester.assertComponent("chatPanel:chatForm:user",
				RequiredTextField.class);
		SpringContextLoaderBaseTest.tester.assertComponent("chatPanel:chatForm:message",
				RequiredTextField.class);
	}

	@Test
	public void testRenderDock() throws IOException
	{
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		// Assert hand
		HomePageTest.testDockElement("Hand");

		// Assert graveyard
		HomePageTest.testDockElement("Graveyard");

		// Assert exiled
		HomePageTest.testDockElement("Exile");

		// Assert battlefield
		HomePageTest.testDockElement("Battlefield");

		// Assert library
		HomePageTest.testDockElement("Library");

		// The first click must hide the hand
		SpringContextLoaderBaseTest.tester.assertComponent("handLink", AjaxLink.class);
		SpringContextLoaderBaseTest.tester.clickLink("handLink", true);

		WebMarkupContainer handParent = (WebMarkupContainer)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("galleryParent");
		SpringContextLoaderBaseTest.tester.assertComponentOnAjaxResponse(handParent);
		Component gallery = handParent.get("gallery");
		Assert.assertNotNull(gallery);
		Assert.assertFalse(gallery instanceof HandComponent);

		// The second click must show the hand
		SpringContextLoaderBaseTest.tester.assertComponent("handLink", AjaxLink.class);
		SpringContextLoaderBaseTest.tester.clickLink("handLink", true);

		handParent = (WebMarkupContainer)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("galleryParent");
		SpringContextLoaderBaseTest.tester.assertComponentOnAjaxResponse(handParent);
		gallery = handParent.get("gallery");
		Assert.assertNotNull(gallery);
		Assert.assertTrue(gallery instanceof HandComponent);

		// The hand must be contain 7 cards again
		Assert.assertEquals(7, ((HandComponent)gallery).getAllCards().size());

		final String allMarkup = SpringContextLoaderBaseTest.tester.getLastResponseAsString();
		final String markupAfterOpeningCdata = allMarkup.split("<!\\[CDATA\\[")[1];
		final String markupWithoutCdata = markupAfterOpeningCdata.split("<!\\[CDATA\\[")[0];

		final List<TagTester> tagTester = TagTester.createTagsByAttribute(markupWithoutCdata,
				"class", "magicCard", false);
		Assert.assertNotNull(tagTester);

		// assert number of thumbnails
		Assert.assertEquals(7, tagTester.size());

		// assert URL of two thumbnail
		Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		Assert.assertNotNull(tagTester.get(1).getAttribute("src"));
		Assert.assertTrue(tagTester.get(1).getAttribute("src").contains(".jpg"));
	}

	@Test
	public void testRenderModalWindows()
	{
		// Re-init because of testRenderModalWindowsInMobileMenu()
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		HomePageTest.testModalWindow("aboutWindow", "aboutLink");
		HomePageTest.testModalWindow("teamInfoWindow", "teamInfoLink");
		HomePageTest.testModalWindow("createGameWindow", "createGameLink");
		HomePageTest.testModalWindow("joinGameWindow", "joinGameLink");
		HomePageTest.testModalWindow("createTokenWindow", "createTokenLink");
	}

	@Test
	public void testRenderModalWindowsInMobileMenu()
	{
		// Re-init because of testRenderModalWindows()
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		HomePageTest.testModalWindow("aboutWindow", "aboutLinkResponsive");
		HomePageTest.testModalWindow("teamInfoWindow", "teamInfoLinkResponsive");
		HomePageTest.testModalWindow("createGameWindow", "createGameLinkResponsive");
		HomePageTest.testModalWindow("joinGameWindow", "joinGameLinkResponsive");
		HomePageTest.testModalWindow("createTokenWindow", "createTokenLinkResponsive");
	}

	@Test
	public void testRenderToolbar()
	{
		// Test the toolbar at the bottom of the screen
		SpringContextLoaderBaseTest.tester.assertComponent("drawCardLink", AjaxLink.class);
		SpringContextLoaderBaseTest.tester
				.assertComponent("playCardLink", WebMarkupContainer.class);
		SpringContextLoaderBaseTest.tester.assertComponent("endTurnPlaceholder",
				WebMarkupContainer.class);
		SpringContextLoaderBaseTest.tester.assertComponent("endTurnPlaceholder:endTurnLink",
				AjaxLink.class);
	}

	/**
	 * When drawing a card, it should appear at the left of the hand cards list,
	 * hence be visible in the hand component
	 */
	@Test
	public void testGenerateDrawCardLink() throws Exception
	{
		this.startAGameAndPlayACard();

		final PersistenceService persistenceService = this.context
				.getBean(PersistenceService.class);
		final HatchetHarrySession session = HatchetHarrySession.get();
		Assert.assertTrue(persistenceService.getAllCardsInLibraryForDeckAndPlayer(
				session.getGameId(), session.getPlayer().getId(),
				session.getPlayer().getDeck().getDeckId()).size() > 0);

		// assert hand is present
		SpringContextLoaderBaseTest.tester.assertComponent("galleryParent:gallery",
				HandComponent.class);

		// assert presence of a hand cards
		HomePageTest.pageDocument = SpringContextLoaderBaseTest.tester.getLastResponse()
				.getDocument();
		List<TagTester> tagTester = TagTester.createTagsByAttribute(HomePageTest.pageDocument,
				"wicket:id", "handImagePlaceholder", false);
		Assert.assertNotNull(tagTester);
		Assert.assertFalse(tagTester.isEmpty());

		// assert number of hand cards
		Assert.assertEquals(6, tagTester.size());

		// assert id of hand cards
		Assert.assertNotNull(tagTester.get(0).getAttribute("id"));
		Assert.assertTrue(tagTester.get(0).getAttribute("id").contains("placeholder"));

		final String cardNameBeforeDraw = tagTester.get(0).getAttribute("id");

		// Draw a card
		SpringContextLoaderBaseTest.tester.assertComponent("drawCardLink", AjaxLink.class);
		final AjaxLink<String> drawCardLink = (AjaxLink<String>)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("drawCardLink");
		SpringContextLoaderBaseTest.tester.executeAjaxEvent(drawCardLink, "onclick");

		// assert presence of hand cards
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertComponent("galleryParent:gallery",
				HandComponent.class);
		HomePageTest.pageDocument = SpringContextLoaderBaseTest.tester.getLastResponse()
				.getDocument();
		tagTester = TagTester.createTagsByAttribute(HomePageTest.pageDocument, "wicket:id",
				"handImagePlaceholder", false);
		Assert.assertNotNull(tagTester);

		// assert number of hand cards
		Assert.assertEquals(7, tagTester.size());

		// assert id of hand cards
		Assert.assertNotNull(tagTester.get(0).getAttribute("id"));
		Assert.assertTrue(tagTester.get(0).getAttribute("id").contains("placeholder"));

		// Drawing card successful?
		// TODO ensure card is at the beginning or at the end
		final String firstCardIdAfterDraw = tagTester.get(1).getAttribute("id");
		Assert.assertTrue("The second thumb of the hand component has changed!",
				cardNameBeforeDraw.equals(firstCardIdAfterDraw));

		Assert.assertNotNull(tagTester.get(1).getAttribute("id"));
		Assert.assertTrue(tagTester.get(1).getAttribute("id").contains("placeholder"));
		final String secondCardIdAfterDraw = tagTester.get(1).getAttribute("id");
		Assert.assertEquals(cardNameBeforeDraw, secondCardIdAfterDraw);
	}

}
