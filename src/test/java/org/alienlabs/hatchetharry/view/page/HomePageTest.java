package org.alienlabs.hatchetharry.view.page;

import java.io.IOException;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.CardPanel;
import org.alienlabs.hatchetharry.view.component.ChatPanel;
import org.alienlabs.hatchetharry.view.component.ClockPanel;
import org.alienlabs.hatchetharry.view.component.DataBox;
import org.alienlabs.hatchetharry.view.component.ExternalImage;
import org.alienlabs.hatchetharry.view.component.HandComponent;
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
import org.junit.runners.MethodSorters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Tests of basic scenarios of the HomePage using the WicketTester.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HomePageTest
{
	public static final ClassPathXmlApplicationContext CLASS_PATH_XML_APPLICATION_CONTEXT = new ClassPathXmlApplicationContext(
			new String[] { "applicationContext.xml", "applicationContextTest.xml" });
	public static transient ApplicationContext context;
	protected static transient WicketTester tester;
	protected static HatchetHarryApplication webApp;
	protected static String pageDocument;
	protected static PersistenceService persistenceService;

	@BeforeClass
	public static void setUpBeforeClassWithoutMocks() throws IOException
	{
		// Init the EventBus
		HomePageTest.webApp = new HatchetHarryApplication()
		{
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void init()
			{
				super.init();
				HomePageTest.context = HomePageTest.CLASS_PATH_XML_APPLICATION_CONTEXT;
				this.getComponentInstantiationListeners().add(
						new SpringComponentInjector(this, HomePageTest.context, true));
			}
		};
		HomePageTest.tester = new WicketTester(HomePageTest.webApp);
		HomePageTest.persistenceService = HomePageTest.context.getBean(PersistenceService.class);
	}

	// Assert dock element is present and contains a .gif
	private static void testDockElement(final String name)
	{
		final String document = HomePageTest.tester.getLastResponse().getDocument();
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
		HomePageTest.tester.assertComponent(_window, ModalWindow.class);
		final ModalWindow window = (ModalWindow)HomePageTest.tester
				.getComponentFromLastRenderedPage(_window);
		HomePageTest.tester.assertInvisible(window.getPageRelativePath() + ":"
				+ window.getContentId());

		final AjaxLink<Void> link = (AjaxLink<Void>)HomePageTest.tester
				.getComponentFromLastRenderedPage(linkToActivateWindow);
		Assert.assertNotNull(link);
		HomePageTest.tester.clickLink(linkToActivateWindow, true);
		HomePageTest.tester.assertVisible(window.getPageRelativePath() + ":"
				+ window.getContentId());
	}

	@Test
	// First test of the suite, otherwise baldu is a WebMarkupContainer
	public void testFirstRenderBaldu() throws IOException
	{
		// Test the baldu and its different children
		HomePageTest.tester.startPage(HomePage.class);
		HomePageTest.tester.assertComponent("balduParent:baldu", CardPanel.class);
		final CardPanel baldu = (CardPanel)HomePageTest.tester
				.getComponentFromLastRenderedPage("balduParent:baldu");
		final ExternalImage tapHandleImage = (ExternalImage)baldu
				.get("cardHandle:menutoggleButton:form:tapHandleImage");
		Assert.assertNotNull(tapHandleImage);
		final ExternalImage handleImage = (ExternalImage)baldu
				.get("cardHandle:menutoggleButton:form:handleImage");
		Assert.assertNotNull(handleImage);
		final ExternalImage cardImage = (ExternalImage)baldu
				.get("cardHandle:menutoggleButton:form:cardImage");
		Assert.assertNotNull(cardImage);
	}

	@Test
	public void testRenderHand() throws IOException
	{
		HomePageTest.tester.startPage(HomePage.class);
		// assert hand is present
		HomePageTest.tester.assertComponent("galleryParent:gallery", HandComponent.class);

		// assert URL of a thumbnail
		final String document = HomePageTest.tester.getLastResponse().getDocument();
		final List<TagTester> tagTester = TagTester.createTagsByAttribute(document, "class",
				"nav-thumb", false);
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
		final Label message = (Label)HomePageTest.tester
				.getComponentFromLastRenderedPage("message1");
		Assert.assertTrue(message.getDefaultModelObjectAsString().contains("version"));
		Assert.assertTrue(message.getDefaultModelObjectAsString().contains("release"));
	}

	@Test
	public void testRenderClock() throws IOException
	{
		HomePageTest.tester.startPage(HomePage.class);
		// assert clock is present
		HomePageTest.tester.assertComponent("clockPanel", ClockPanel.class);

		// assert clock content
		final ClockPanel clock = (ClockPanel)HomePageTest.tester
				.getComponentFromLastRenderedPage("clockPanel");
		Assert.assertTrue(clock.getTime().getObject().contains("###"));
	}

	@Test
	public void testRenderMenuBar() throws IOException
	{
		HomePageTest.tester.startPage(HomePage.class);
		// Assert menubar
		final String document = HomePageTest.tester.getLastResponse().getDocument();
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
	public void testRenderDataBox() throws IOException
	{
		HomePageTest.tester.startPage(HomePage.class);
		// assert DataBox is present
		HomePageTest.tester.assertComponent("dataBoxParent:dataBox", DataBox.class);

		// assert DataBox content
		HomePageTest.tester.assertComponent(
				"dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints",
				AjaxEditableLabel.class);
		final AjaxEditableLabel<String> lifePoints = (AjaxEditableLabel<String>)HomePageTest.tester
				.getComponentFromLastRenderedPage("dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints");
		Assert.assertEquals("20", lifePoints.getDefaultModelObject().toString());
	}

	@Test
	public void testRenderChat() throws IOException
	{
		HomePageTest.tester.startPage(HomePage.class);
		// assert chat is present
		HomePageTest.tester.assertComponent("chatPanel", ChatPanel.class);
		HomePageTest.tester.assertComponent("chatPanel:chatForm:user", RequiredTextField.class);
		HomePageTest.tester.assertComponent("chatPanel:chatForm:message", RequiredTextField.class);
	}

	@Test
	public void testRenderDock() throws IOException
	{
		HomePageTest.tester.startPage(HomePage.class);
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
		HomePageTest.tester.assertComponent("handLink", AjaxLink.class);
		HomePageTest.tester.clickLink("handLink", true);

		WebMarkupContainer handParent = (WebMarkupContainer)HomePageTest.tester
				.getComponentFromLastRenderedPage("galleryParent");
		HomePageTest.tester.assertComponentOnAjaxResponse(handParent);
		Component gallery = handParent.get("gallery");
		Assert.assertNotNull(gallery);
		Assert.assertFalse(gallery instanceof HandComponent);

		// The second click must show the hand
		HomePageTest.tester.assertComponent("handLink", AjaxLink.class);
		HomePageTest.tester.clickLink("handLink", true);

		handParent = (WebMarkupContainer)HomePageTest.tester
				.getComponentFromLastRenderedPage("galleryParent");
		HomePageTest.tester.assertComponentOnAjaxResponse(handParent);
		gallery = handParent.get("gallery");
		Assert.assertNotNull(gallery);
		Assert.assertTrue(gallery instanceof HandComponent);

		// The hand must be contain 7 cards again
		Assert.assertEquals(7, ((HandComponent)gallery).getAllCards().size());

		final String allMarkup = HomePageTest.tester.getLastResponseAsString();
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

	@Test
	public void testRenderModalWindows()
	{
		// Re-init because of testRenderModalWindowsInMobileMenu()
		HomePageTest.tester.startPage(HomePage.class);
		HomePageTest.tester.assertRenderedPage(HomePage.class);

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
		HomePageTest.tester.startPage(HomePage.class);
		HomePageTest.tester.assertRenderedPage(HomePage.class);

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
		HomePageTest.tester.assertComponent("drawCardLink", AjaxLink.class);
		HomePageTest.tester.assertComponent("playCardLink", WebMarkupContainer.class);
		HomePageTest.tester.assertComponent("endTurnPlaceholder", WebMarkupContainer.class);
		HomePageTest.tester.assertComponent("endTurnPlaceholder:endTurnLink", AjaxLink.class);
	}

	/**
	 * When drawing a card, it should appear at the left of the hand thumb list, hence be visible in
	 * the hand component
	 */
	@Test
	@Ignore
	public void testGenerateDrawCardLink()
	{
		// this.startAGameAndPlayACard();

		final PersistenceService persistenceService = HomePageTest.context
				.getBean(PersistenceService.class);
		final HatchetHarrySession session = HatchetHarrySession.get();
		Assert.assertTrue(persistenceService.getAllCardsInLibraryForDeckAndPlayer(
				session.getGameId(), session.getPlayer().getId(),
				session.getPlayer().getDeck().getDeckId()).size() > 0);
		// assert hand is present
		HomePageTest.tester.assertComponent("galleryParent:gallery", HandComponent.class);

		// assert presence of a thumbnail
		HomePageTest.pageDocument = HomePageTest.tester.getLastResponse().getDocument();
		List<TagTester> tagTester = TagTester.createTagsByAttribute(HomePageTest.pageDocument,
				"class", "nav-thumb", false);
		Assert.assertNotNull(tagTester);

		// assert number of thumbnails
		Assert.assertEquals(6, tagTester.size());

		// assert id of thumbnails
		Assert.assertNotNull(tagTester.get(0).getAttribute("id"));
		Assert.assertTrue(tagTester.get(0).getAttribute("id").contains("placeholder"));

		final String firstCardIdBeforeDraw = tagTester.get(0).getAttribute("id");

		HomePageTest.pageDocument = HomePageTest.tester.getLastResponse().getDocument();
		// Draw a card
		HomePageTest.tester.assertComponent("drawCardLink", AjaxLink.class);
		final AjaxLink<String> drawCardLink = (AjaxLink<String>)HomePageTest.tester
				.getComponentFromLastRenderedPage("drawCardLink");
		HomePageTest.tester.executeAjaxEvent(drawCardLink, "onclick");

		// assert presence of a thumbnail
		HomePageTest.tester.startPage(HomePage.class);
		HomePageTest.tester.assertRenderedPage(HomePage.class);
		HomePageTest.tester.assertComponent("galleryParent:gallery", HandComponent.class);
		HomePageTest.pageDocument = HomePageTest.tester.getLastResponse().getDocument();
		tagTester = TagTester.createTagsByAttribute(HomePageTest.pageDocument, "class",
				"nav-thumb", false);
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
