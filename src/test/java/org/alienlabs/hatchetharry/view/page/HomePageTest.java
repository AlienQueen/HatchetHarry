package org.alienlabs.hatchetharry.view.page;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.CardPanel;
import org.alienlabs.hatchetharry.view.component.ChatPanel;
import org.alienlabs.hatchetharry.view.component.ClockPanel;
import org.alienlabs.hatchetharry.view.component.DataBox;
import org.alienlabs.hatchetharry.view.component.ExternalImage;
import org.alienlabs.hatchetharry.view.component.HandComponent;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests of basic scenarios of the HomePage using the WicketTester.
 */
public class HomePageTest extends SpringContextLoaderBaseTest
{
	@Test
	public void testRenderHand()
	{
		// assert hand is present
		SpringContextLoaderBaseTest.tester.assertComponent("galleryParent:gallery",
				HandComponent.class);

		// assert URL of a thumbnail
		final List<TagTester> tagTester = TagTester.createTagsByAttribute(
				SpringContextLoaderBaseTest.pageDocument, "class", "nav-thumb", false);
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
	public void testRenderClock()
	{
		// assert clock is present
		SpringContextLoaderBaseTest.tester.assertComponent("clockPanel", ClockPanel.class);

		// assert clock content
		final ClockPanel clock = (ClockPanel)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("clockPanel");
		Assert.assertTrue(clock.getTime().getObject().contains("###"));
	}

	@Test
	public void testRenderMenuBar()
	{
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		SpringContextLoaderBaseTest.pageDocument = SpringContextLoaderBaseTest.tester
				.getLastResponse().getDocument();

		// Assert menubar
		final List<TagTester> tagTester = TagTester.createTagsByAttribute(
				SpringContextLoaderBaseTest.pageDocument, "class", "fNiv", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(3, tagTester.size());

		// Assert 'Import a deck' entry exists
		boolean containsText = false;
		for (final TagTester tt : tagTester)
		{
			if (((null != tt.getMarkup()) && tt.getMarkup().contains("Import a deck")))
			{
				containsText = true;
				break;
			}
		}
		Assert.assertTrue(containsText);
	}

	@Test
	public void testRenderDataBox()
	{
		// assert DataBox is present
		SpringContextLoaderBaseTest.tester.assertComponent("dataBoxParent:dataBox", DataBox.class);

		// assert DataBox content
		SpringContextLoaderBaseTest.tester.assertLabel(
				"dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints",
				"20 life points");
	}

	@Test
	public void testRenderChat()
	{
		// assert chat is present
		SpringContextLoaderBaseTest.tester.assertComponent("chatPanel", ChatPanel.class);
		SpringContextLoaderBaseTest.tester.assertComponent("chatPanel:chatForm:user",
				RequiredTextField.class);
		SpringContextLoaderBaseTest.tester.assertComponent("chatPanel:chatForm:message",
				RequiredTextField.class);
	}

	@Test
	public void testRenderDock()
	{
		super.tearDownAfterClass();
		super.setUpBeforeClass();

		// Assert hand
		HomePageTest.testDockElement("Hand");

		// Assert graveyard
		HomePageTest.testDockElement("Graveyard");

		// Assert exiled
		HomePageTest.testDockElement("Exiled");

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
				"class", "nav-thumb", false);
		Assert.assertNotNull(tagTester);

		// assert number of thumbnails
		Assert.assertEquals(7, tagTester.size());

		// assert URL of two thumbnail
		Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		Assert.assertNotNull(tagTester.get(1).getAttribute("src"));
		Assert.assertTrue(tagTester.get(1).getAttribute("src").contains(".jpg"));
	}

	// Assert dock element is present and contains a .gif
	private static void testDockElement(final String name)
	{
		final List<TagTester> tagTester = TagTester.createTagsByAttribute(
				SpringContextLoaderBaseTest.tester.getLastResponse().getDocument(), "title", name,
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
	}

	private static void testModalWindow(final String _window, final String linkToActivateWindow)
	{
		// assert modal windows are in the page
		SpringContextLoaderBaseTest.tester.assertComponent(_window, ModalWindow.class);
		final ModalWindow window = (ModalWindow)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage(_window);
		SpringContextLoaderBaseTest.tester.assertInvisible(window.getPageRelativePath() + ":"
				+ window.getContentId());

		final AjaxLink<Void> aboutLink = (AjaxLink<Void>)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage(linkToActivateWindow);
		Assert.assertNotNull(aboutLink);
		SpringContextLoaderBaseTest.tester.clickLink(linkToActivateWindow, true);
		SpringContextLoaderBaseTest.tester.assertVisible(window.getPageRelativePath() + ":"
				+ window.getContentId());
	}

	@Test
	public void testRenderBaldu()
	{
		// Test the baldu and its different children
		SpringContextLoaderBaseTest.tester.assertComponent("balduParent:baldu", CardPanel.class);
		final CardPanel baldu = (CardPanel)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("balduParent:baldu");
		final Image tapHandleImage = (Image)baldu
				.get("cardHandle:menutoggleButton:form:tapHandleImage");
		Assert.assertNotNull(tapHandleImage);
		final Image handleImage = (Image)baldu.get("cardHandle:menutoggleButton:form:handleImage");
		Assert.assertNotNull(handleImage);
		final ExternalImage cardImage = (ExternalImage)baldu
				.get("cardHandle:menutoggleButton:form:cardImage");
		Assert.assertNotNull(cardImage);
	}

	@Test
	public void testRenderToolbar()
	{
		// Test the toolbar at the bottom of the screen
		SpringContextLoaderBaseTest.tester.assertComponent("drawCardLink", AjaxLink.class);
		SpringContextLoaderBaseTest.tester.assertComponent("playCardPlaceholder",
				WebMarkupContainer.class);
		SpringContextLoaderBaseTest.tester.assertComponent("playCardPlaceholder:playCardLink",
				WebMarkupContainer.class);
		SpringContextLoaderBaseTest.tester.assertComponent("endTurnPlaceholder",
				WebMarkupContainer.class);
		SpringContextLoaderBaseTest.tester.assertComponent("endTurnPlaceholder:endTurnLink",
				AjaxLink.class);
	}

	/**
	 * When drawing a card, it should appear at the left of the hand thumb list,
	 * hence be visible in the hand component
	 * 
	 */
	@Test
	public void testGenerateDrawCardLink()
	{
		SpringContextLoaderBaseTest.startAGameAndPlayACard(SpringContextLoaderBaseTest.tester,
				SpringContextLoaderBaseTest.context);

		final PersistenceService persistenceService = SpringContextLoaderBaseTest.context
				.getBean(PersistenceService.class);
		final HatchetHarrySession session = HatchetHarrySession.get();
		Assert.assertTrue(persistenceService.getAllCardsInLibraryForDeckAndPlayer(
				session.getGameId(), session.getPlayer().getId(),
				session.getPlayer().getDeck().getDeckId()).size() > 0);
		// assert hand is present
		SpringContextLoaderBaseTest.tester.assertComponent("galleryParent:gallery",
				HandComponent.class);

		// assert presence of a thumbnail
		SpringContextLoaderBaseTest.pageDocument = SpringContextLoaderBaseTest.tester
				.getLastResponse().getDocument();
		List<TagTester> tagTester = TagTester.createTagsByAttribute(
				SpringContextLoaderBaseTest.pageDocument, "class", "nav-thumb", false);
		Assert.assertNotNull(tagTester);

		// assert number of thumbnails
		Assert.assertEquals(6, tagTester.size());

		// assert id of thumbnails
		Assert.assertNotNull(tagTester.get(0).getAttribute("id"));
		Assert.assertTrue(tagTester.get(0).getAttribute("id").contains("placeholder"));

		final String firstCardIdBeforeDraw = tagTester.get(0).getAttribute("id");

		SpringContextLoaderBaseTest.pageDocument = SpringContextLoaderBaseTest.tester
				.getLastResponse().getDocument();

		// Draw a card
		SpringContextLoaderBaseTest.tester.assertComponent("drawCardLink", AjaxLink.class);
		final AjaxLink<String> drawCardLink = (AjaxLink<String>)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("drawCardLink");
		SpringContextLoaderBaseTest.tester.executeAjaxEvent(drawCardLink, "onclick");

		// assert presence of a thumbnail
		SpringContextLoaderBaseTest.tester.assertComponent("galleryParent:gallery",
				HandComponent.class);
		SpringContextLoaderBaseTest.pageDocument = SpringContextLoaderBaseTest.tester
				.getLastResponse().getDocument();
		tagTester = TagTester.createTagsByAttribute(SpringContextLoaderBaseTest.pageDocument,
				"class", "nav-thumb", false);
		Assert.assertNotNull(tagTester);

		// assert number of thumbnails
		Assert.assertEquals(7, tagTester.size());

		// assert id of thumbnails
		Assert.assertNotNull(tagTester.get(0).getAttribute("id"));
		Assert.assertTrue(tagTester.get(0).getAttribute("id").contains("placeholder"));

		// Drawing card successful?
		final String firstCardIdAfterDraw = tagTester.get(0).getAttribute("id");
		Assert.assertFalse("The first thumb of the hand component has not changed!",
				firstCardIdBeforeDraw.equals(firstCardIdAfterDraw));

		Assert.assertNotNull(tagTester.get(1).getAttribute("id"));
		Assert.assertTrue(tagTester.get(1).getAttribute("id").contains("placeholder"));
		final String secondCardIdAfterDraw = tagTester.get(1).getAttribute("id");
		Assert.assertTrue(firstCardIdBeforeDraw.equals(secondCardIdAfterDraw));
	}
}
