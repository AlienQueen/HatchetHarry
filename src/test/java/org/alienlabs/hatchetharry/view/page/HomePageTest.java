package org.alienlabs.hatchetharry.view.page;

import java.io.IOException;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.serversidetest.util.SpringContextLoaderBase;
import org.alienlabs.hatchetharry.view.component.gui.ChatPanel;
import org.alienlabs.hatchetharry.view.component.gui.ClockPanel;
import org.alienlabs.hatchetharry.view.component.gui.HandComponent;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;

/**
 * Tests of basic scenarios of the HomePage using the WicketTester.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
"classpath:applicationContextTest.xml" })
public class HomePageTest extends SpringContextLoaderBase
{
	private static String pageDocument;

	// Assert dock element is present and contains a .gif
	private static void testDockElement(final String name)
	{
		final String document = SpringContextLoaderBase.tester.getLastResponse().getDocument();
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
		SpringContextLoaderBase.tester.assertComponent(_window, ModalWindow.class);
		final ModalWindow window = (ModalWindow)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage(_window);
		SpringContextLoaderBase.tester.assertInvisible(window.getPageRelativePath() + ":"
				+ window.getContentId());

		final AjaxLink<Void> link = (AjaxLink<Void>)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage(linkToActivateWindow);
		Assert.assertNotNull(link);
		SpringContextLoaderBase.tester.clickLink(linkToActivateWindow, true);
		SpringContextLoaderBase.tester.assertVisible(window.getPageRelativePath() + ":"
				+ window.getContentId());
	}

	@Test
	public void testRenderHand() throws IOException
	{
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		// assert hand is present
		SpringContextLoaderBase.tester
		.assertComponent("galleryParent:gallery", HandComponent.class);

		// assert URL of a thumbnail
		final String document = SpringContextLoaderBase.tester.getLastResponse().getDocument();
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
		final Label message = (Label)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("message1");
		Assert.assertTrue(message.getDefaultModelObjectAsString().contains("version"));
		Assert.assertTrue(message.getDefaultModelObjectAsString().contains("release"));
	}

	@Test
	public void testRenderClock()
	{
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		// assert clock is present
		SpringContextLoaderBase.tester.assertComponent("clockPanel", ClockPanel.class);

		// assert clock content
		final ClockPanel clock = (ClockPanel)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("clockPanel");
		Assert.assertTrue(clock.getTime().getObject().contains("###"));
	}

	@Test
	public void testRenderMenuBar() throws IOException
	{
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		// Assert menubar
		final String document = SpringContextLoaderBase.tester.getLastResponse().getDocument();
		final List<TagTester> tagTester = TagTester.createTagsByAttribute(document, "class",
				"shift-bottom", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(3, tagTester.size());

		// Assert 'Import a deck' entry exists
		boolean containsText = false;
		for (final TagTester tt : tagTester)
		{
			if (((null != tt.getMarkup()) && tt.getMarkup().contains("Match")))
			{
				containsText = true;
				break;
			}
		}
		Assert.assertTrue(containsText);
	}

	@Test
	public void testRenderChat()
	{
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		// assert chat is present
		SpringContextLoaderBase.tester.assertComponent("chatPanel", ChatPanel.class);
		SpringContextLoaderBase.tester.assertComponent("chatPanel:chatForm:user",
				RequiredTextField.class);
		SpringContextLoaderBase.tester.assertComponent("chatPanel:chatForm:message",
				RequiredTextField.class);
	}

	@Test
	public void testRenderDock()
	{
		SpringContextLoaderBase.tester.startPage(HomePage.class);
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
		SpringContextLoaderBase.tester.assertComponent("handLink", AjaxLink.class);
		SpringContextLoaderBase.tester.clickLink("handLink", true);

		WebMarkupContainer handParent = (WebMarkupContainer)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("galleryParent");
		SpringContextLoaderBase.tester.assertComponentOnAjaxResponse(handParent);
		Component gallery = handParent.get("gallery");
		Assert.assertNotNull(gallery);
		Assert.assertFalse(gallery instanceof HandComponent);

		// The second click must show the hand
		SpringContextLoaderBase.tester.assertComponent("handLink", AjaxLink.class);
		SpringContextLoaderBase.tester.clickLink("handLink", true);

		handParent = (WebMarkupContainer)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("galleryParent");
		SpringContextLoaderBase.tester.assertComponentOnAjaxResponse(handParent);
		gallery = handParent.get("gallery");
		Assert.assertNotNull(gallery);
		Assert.assertTrue(gallery instanceof HandComponent);

		// The hand must be contain 7 cards again
		Assert.assertEquals(7, ((HandComponent)gallery).getAllCards().size());

		final String allMarkup = SpringContextLoaderBase.tester.getLastResponseAsString();
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
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		HomePageTest.testModalWindow("aboutWindow", "aboutLink");
		HomePageTest.testModalWindow("teamInfoWindow", "teamInfoLink");
		HomePageTest.testModalWindow("createMatchWindow", "createMatchLink");
		HomePageTest.testModalWindow("joinMatchWindow", "joinMatchLink");
		HomePageTest.testModalWindow("createTokenWindow", "createTokenLink");
	}

	@Test
	public void testRenderModalWindowsInMobileMenu()
	{
		// Re-init because of testRenderModalWindows()
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		HomePageTest.testModalWindow("aboutWindow", "aboutLinkResponsive");
		HomePageTest.testModalWindow("teamInfoWindow", "teamInfoLinkResponsive");
		HomePageTest.testModalWindow("createMatchWindow", "createMatchLinkResponsive");
		HomePageTest.testModalWindow("joinMatchWindow", "joinMatchLinkResponsive");
		HomePageTest.testModalWindow("createTokenWindow", "createTokenLinkResponsive");
	}

	@Test
	public void testRenderToolbar()
	{
		// Test the toolbar at the bottom of the screen
		SpringContextLoaderBase.tester.assertComponent("drawCardLink", AjaxLink.class);
		SpringContextLoaderBase.tester.assertComponent("playCardLink", WebMarkupContainer.class);
		SpringContextLoaderBase.tester.assertComponent("endTurnPlaceholder",
				WebMarkupContainer.class);
		SpringContextLoaderBase.tester.assertComponent("endTurnPlaceholder:endTurnLink",
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

		final HatchetHarrySession session = HatchetHarrySession.get();
		List<MagicCard> allCardsInLibrary = persistenceService
				.getAllCardsInLibraryForDeckAndPlayer(session.getGameId(), session.getPlayer()
						.getId(), session.getPlayer().getDeck().getDeckId());
		Assert.assertFalse(allCardsInLibrary.isEmpty());

		// assert hand is present
		SpringContextLoaderBase.tester
		.assertComponent("galleryParent:gallery", HandComponent.class);

		// assert presence of a hand cards
		HomePageTest.pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();
		List<TagTester> tagTester = TagTester.createTagsByAttribute(HomePageTest.pageDocument,
				"wicket:id", "handImagePlaceholder", false);
		Assert.assertNotNull(tagTester);
		Assert.assertFalse(tagTester.isEmpty());

		// assert number of hand cards
		Assert.assertEquals(6, tagTester.size());

		// get name of card to draw
		String cardToDraw = allCardsInLibrary.get(0).getTitle();

		// Draw a card
		SpringContextLoaderBase.tester.assertComponent("drawCardLink", AjaxLink.class);
		final AjaxLink<String> drawCardLink = (AjaxLink<String>)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("drawCardLink");
		SpringContextLoaderBase.tester.executeAjaxEvent(drawCardLink, "onclick");

		// assert presence of hand cards
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		SpringContextLoaderBase.tester
		.assertComponent("galleryParent:gallery", HandComponent.class);
		HomePageTest.pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();
		tagTester = TagTester.createTagsByAttribute(HomePageTest.pageDocument, "wicket:id",
				"handImagePlaceholder", false);
		Assert.assertNotNull(tagTester);

		// assert number of hand cards
		Assert.assertEquals(7, tagTester.size());

		// assert src of hand card
		final String drawnCardSrc = tagTester.get(6).getAttribute("src");

		Assert.assertNotNull(drawnCardSrc);
		Assert.assertTrue(drawnCardSrc.contains("cards/"));

		// Drawing card successful?
		Assert.assertTrue(drawnCardSrc.contains(cardToDraw));
	}

}
