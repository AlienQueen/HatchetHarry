package org.alienlabs.hatchetharry.serverSideTest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.CardRotateBehavior;
import org.alienlabs.hatchetharry.view.component.ExternalImage;
import org.alienlabs.hatchetharry.view.component.HandComponent;
import org.alienlabs.hatchetharry.view.component.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.component.PutToHandFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.atmosphere.tester.AtmosphereTester;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.TagTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Non regression tests using the WicketTester.
 */
public class NonRegressionTest
{
	static final ClassPathXmlApplicationContext CLASS_PATH_XML_APPLICATION_CONTEXT = new ClassPathXmlApplicationContext(
			new String[] { "applicationContext.xml", "applicationContextTest.xml" });
	static transient ApplicationContext context;
	private static AtmosphereTester waTester;
	private static transient WicketTester tester;
	private static HatchetHarryApplication webApp;
	private static PersistenceService persistenceService;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException
	{
		NonRegressionTest.webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void init()
			{
				super.init();
				NonRegressionTest.context = NonRegressionTest.CLASS_PATH_XML_APPLICATION_CONTEXT;
				this.getComponentInstantiationListeners().add(
						new SpringComponentInjector(this, NonRegressionTest.context, true));
			}
		};

		// start and render the test page
		NonRegressionTest.tester = new WicketTester(NonRegressionTest.webApp);
		NonRegressionTest.persistenceService = NonRegressionTest.context
				.getBean(PersistenceService.class);
		NonRegressionTest.waTester = new AtmosphereTester(NonRegressionTest.tester, new HomePage(
				NonRegressionTest.pageParameters()));
	}

	@After
	public void tearDown()
	{
		NonRegressionTest.persistenceService.resetDb();
		HatchetHarrySession.get().reinitSession();
	}

	public void newHomePage() throws IOException
	{
		final PageParameters pp = NonRegressionTest.pageParameters();
		pp.add("test", "test");
		NonRegressionTest.tester.startPage(new HomePage(pp));
	}

	public void startAGameAndPlayACard(final WicketTester _tester) throws IOException
	{
		// Create game
		this.newHomePage();
		NonRegressionTest.tester.assertRenderedPage(HomePage.class);

		_tester.assertComponent("createGameLink", AjaxLink.class);
		_tester.clickLink("createGameLink", true);

		final FormTester createGameForm = _tester.newFormTester("createGameWindow:content:form");
		createGameForm.setValue("name", "Zala");
		createGameForm.setValue("sideInput", "0");
		createGameForm.setValue("deckParent:decks", "0");
		createGameForm.submit();

		Player p = NonRegressionTest.persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());


		// Retrieve PlayCardFromHandBehavior
		_tester.assertComponent("playCardLink", WebMarkupContainer.class);
		final WebMarkupContainer playCardLink = (WebMarkupContainer)_tester
				.getComponentFromLastRenderedPage("playCardLink");
		final PlayCardFromHandBehavior pcfhb = (PlayCardFromHandBehavior)playCardLink
				.getBehaviors().get(0);

		// For the moment, we should have no card in the battlefield
		final Long gameId = HatchetHarrySession.get().getGameId();
		final List<MagicCard> allCardsInBattlefield = NonRegressionTest.persistenceService
				.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		// Play a card
		_tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(0).getUuid());
		_tester.executeBehavior(pcfhb);


		// We still should not have more cards that the number of cards in the
		// deck
		p = NonRegressionTest.persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());
	}

	@Test
	/**
	 * Init: we create a game, we play a card, we tap it, we put it back to hand
	 * Run: we play it again
	 * Verify: the card should be untapped.
	 *
	 */
	public void testWhenACardIsPlayedAndPutBackToHandAndPlayedAgainItIsUntapped()
			throws IOException
	{
		this.startAGameAndPlayACard(NonRegressionTest.tester);

		final Long gameId = HatchetHarrySession.get().getGameId();

		// We should have one card on the battlefield, untapped
		List<MagicCard> allCardsInBattlefield = NonRegressionTest.persistenceService
				.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());
		MagicCard card = NonRegressionTest.persistenceService.getCardFromUuid(UUID
				.fromString(allCardsInBattlefield.get(0).getUuid()));
		Assert.assertFalse(card.isTapped());

		// Tap card
		NonRegressionTest.tester.startPage(new HomePage(NonRegressionTest.pageParameters()));
		NonRegressionTest.tester.assertRenderedPage(HomePage.class);

		final HomePage page = (HomePage)NonRegressionTest.tester.getLastRenderedPage();
		final WebMarkupContainer cardButton = (WebMarkupContainer)page
				.get("parentPlaceholder:magicCards:1:cardPanel:cardHandle:menutoggleButton");
		Assert.assertNotNull(cardButton);
		final CardRotateBehavior rotateBehavior = cardButton.getBehaviors(CardRotateBehavior.class)
				.get(0);
		Assert.assertNotNull(rotateBehavior);

		NonRegressionTest.tester.getRequest().setParameter("uuid", card.getUuid());
		NonRegressionTest.tester.executeBehavior(rotateBehavior);

		// Assert card is tapped
		card = NonRegressionTest.persistenceService.getCardFromUuid(UUID
				.fromString(allCardsInBattlefield.get(0).getUuid()));
		Assert.assertTrue(card.isTapped());

		// Put the first card back to hand
		final PutToHandFromBattlefieldBehavior putToHandFromBattlefieldBehavior = cardButton
				.getBehaviors(PutToHandFromBattlefieldBehavior.class).get(0);
		Assert.assertNotNull(putToHandFromBattlefieldBehavior);
		NonRegressionTest.tester.executeBehavior(putToHandFromBattlefieldBehavior);

		// We should have no card on the battlefield
		allCardsInBattlefield = NonRegressionTest.persistenceService
				.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		// Play card again
		NonRegressionTest.tester.startPage(new HomePage(NonRegressionTest.pageParameters()));
		NonRegressionTest.tester.assertRenderedPage(HomePage.class);

		NonRegressionTest.tester.assertComponent("playCardLink", WebMarkupContainer.class);
		final WebMarkupContainer playCardLink = (WebMarkupContainer)NonRegressionTest.tester
				.getComponentFromLastRenderedPage("playCardLink");

		PlayCardFromHandBehavior pcfhb = (PlayCardFromHandBehavior)playCardLink.getBehaviors().get(
				0);
		Assert.assertNotNull(pcfhb);

		NonRegressionTest.tester.getRequest().setParameter("card", card.getUuid());
		NonRegressionTest.tester.executeBehavior(pcfhb);

		// We should have one card on the battlefield, untapped
		allCardsInBattlefield = NonRegressionTest.persistenceService
				.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());
	}

	@Test
	public void whenCreatingAGameAndPlayingACardTheTotalNumberOfCardsInAllZonesMustBeTheNumberOfCardsInTheDeck()
			throws IOException
	{
		this.startAGameAndPlayACard(NonRegressionTest.tester);

		final Player p = NonRegressionTest.persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());
	}

	@Test
	public void whenCreatingAGamePlayingACardAndPlayingATokenTheNumberOfCardsInTheHandMustNotChange()
			throws IOException
	{
		this.startAGameAndPlayACard(NonRegressionTest.tester);

		// 60 cards in the deck?
		final Player p = NonRegressionTest.persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());

		// 6 cards in the hand?
		NonRegressionTest.tester.assertComponent("galleryParent:gallery", HandComponent.class);
        String pageDocument = NonRegressionTest.tester.getLastResponse().getDocument()
                .replace("<![CDATA[", "");
		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "class",
				"nav-thumb", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(6, tagTester.size());

		// Do they look OK?
		Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		// Is there really one card on the battlefield?
		final HomePage hp = NonRegressionTest.tester.startPage(new HomePage(NonRegressionTest
				.pageParameters()));
		NonRegressionTest.tester.assertRenderedPage(HomePage.class);
        pageDocument = NonRegressionTest.tester.getLastResponse().getDocument()
                .replace("<![CDATA[", "");

		tagTester = TagTester.createTagsByAttribute(pageDocument, "class", "magicCard", false);
		Assert.assertNotNull(tagTester);

		Assert.assertEquals(1, tagTester.size());

		// Play another card
		NonRegressionTest.tester.assertComponent("playCardLink", WebMarkupContainer.class);
		final WebMarkupContainer playCardLink = (WebMarkupContainer)NonRegressionTest.tester
				.getComponentFromLastRenderedPage("playCardLink");
		final PlayCardFromHandBehavior pcfhb = (PlayCardFromHandBehavior)playCardLink
				.getBehaviors().get(0);
		NonRegressionTest.tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(0).getUuid());
		NonRegressionTest.tester.executeBehavior(pcfhb);

		// 6 cards in the hand instead of 5!!!
		NonRegressionTest.tester.startPage(hp);
		NonRegressionTest.tester.assertRenderedPage(HomePage.class);
        pageDocument = NonRegressionTest.tester.getLastResponse().getDocument()
                .replace("<![CDATA[", "");

		NonRegressionTest.tester.assertComponent("galleryParent:gallery", HandComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "class", "nav-thumb", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(6, tagTester.size());

		// Do they look OK?
		Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		// Is there really one card on the battlefield? (beware of
		// wicket-quickview)
		NonRegressionTest.waTester.switchOnTestMode();
		pageDocument = NonRegressionTest.tester.getLastResponse().getDocument()
				.replace("<![CDATA[", "");

		tagTester = TagTester.createTagsByAttribute(pageDocument, "class", "magicCard", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());

		// Verify createTokenWindow
		this.openModalWindow("createTokenWindow", "createTokenLink", "topLibraryCard",
				"cards/token_medium.jpg");

		// open ModalWindow in order to play a token, fill fields and validate
		NonRegressionTest.tester.assertComponent("createTokenLink", AjaxLink.class);
		NonRegressionTest.tester.clickLink("createTokenLink", true);

		final FormTester createTokenForm = NonRegressionTest.tester
				.newFormTester("createTokenWindow:content:form");
		createTokenForm.setValue("type", "Creature");
		createTokenForm.setValue("power", "7");
		createTokenForm.setValue("toughness", "7");
		createTokenForm.setValue("colors", "Green");
		createTokenForm.setValue("capabilities", "It kills you in three turns");
		createTokenForm.setValue("creatureTypes", "Lurghoyf");
		createTokenForm.setValue("description", "Help!!!");
		createTokenForm.submit();

		// Are there really 2 cards on the battlefield?
		NonRegressionTest.waTester.switchOffTestMode();
		pageDocument = NonRegressionTest.waTester.getPushedResponse().replace("<![CDATA[", "");

		tagTester = TagTester.createTagsByAttribute(pageDocument, "class", "magicCard", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());

		// Are there still 6 cards in the hand
		NonRegressionTest.tester.startPage(hp);
		NonRegressionTest.tester.assertRenderedPage(HomePage.class);
		pageDocument = NonRegressionTest.tester.getLastResponse().getDocument()
				.replace("<![CDATA[", "");

		NonRegressionTest.tester.assertComponent("galleryParent:gallery", HandComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "class", "nav-thumb", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(6, tagTester.size());

		// Do they still look OK?
		Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		// open ModalWindow
		NonRegressionTest.tester.assertComponent("countCardsLink", AjaxLink.class);
		NonRegressionTest.tester.clickLink("countCardsLink", true);

		// And now: does the "count cards" modal window display the right
		// result: 6 cards in hand, 1 on the battlefield ( + 1 token),
		// 53 in the library, 0 in
		// exile & graveyard and 60 in total (beware, there's a token!)
		this.verifyFieldsOfCountCardsModalWindow(0, "infrared");
		this.verifyFieldsOfCountCardsModalWindow(1, "6");
		this.verifyFieldsOfCountCardsModalWindow(2, "53");
		this.verifyFieldsOfCountCardsModalWindow(3, "0");
		this.verifyFieldsOfCountCardsModalWindow(4, "0");
		this.verifyFieldsOfCountCardsModalWindow(5, "1");
		this.verifyFieldsOfCountCardsModalWindow(6, "60"); // \O/
	}

	private static PageParameters pageParameters()
	{
		final PageParameters pp = new PageParameters();
		pp.add("test", "test");
		return pp;
	}

	@Test
	public void whenCreatingAndJoiningAGameTheTotalNumberOfCardsInAllZonesMustBeTheNumberOfCardsInTheDeck()
			throws IOException
	{
		this.startAGameAndPlayACard(NonRegressionTest.tester);

		final Player player = NonRegressionTest.persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId()).get(0);
		Assert.assertEquals(60, player.getDeck().getCards().size());

		// Create game
		NonRegressionTest.tester.assertComponent("joinGameLink", AjaxLink.class);
		NonRegressionTest.tester.clickLink("joinGameLink", true);

		final FormTester joinGameForm = NonRegressionTest.tester
				.newFormTester("joinGameWindow:content:form");
		joinGameForm.setValue("name", "Zala");
		joinGameForm.setValue("sideInput", "1");
		joinGameForm.setValue("deckParent:decks", "1");
		final Long gameId = HatchetHarrySession.get().getGameId();
		joinGameForm.setValue("gameIdInput", String.valueOf(gameId));
		joinGameForm.submit();

		Assert.assertEquals(60, player.getDeck().getCards().size());
	}

	private void openModalWindow(final String _window, final String linkToActivateWindow,
			final String componentIdToCheck, final String valueToCheck)
	{
		// assert modal windows are in the page
		NonRegressionTest.tester.assertComponent(_window, ModalWindow.class);
		final ModalWindow window = (ModalWindow)NonRegressionTest.tester
				.getComponentFromLastRenderedPage(_window);
		NonRegressionTest.tester.assertInvisible(window.getPageRelativePath() + ":"
				+ window.getContentId());

		@SuppressWarnings("unchecked")
		final AjaxLink<Void> link = (AjaxLink<Void>)NonRegressionTest.tester
		.getComponentFromLastRenderedPage(linkToActivateWindow);
		Assert.assertNotNull(link);
		NonRegressionTest.tester.clickLink(linkToActivateWindow, true);
		NonRegressionTest.waTester.switchOnTestMode();

		final ExternalImage img = (ExternalImage)NonRegressionTest.tester
				.getComponentFromLastRenderedPage(window.getPageRelativePath() + ":"
						+ window.getContentId() + ":" + componentIdToCheck);
		Assert.assertEquals(valueToCheck, img.getImageUrl());
	}

	private void verifyFieldsOfCountCardsModalWindow(final int field,
			final String expectedFieldContent)
	{
		NonRegressionTest.waTester.switchOffTestMode();
		final String pageDocument = NonRegressionTest.waTester.getPushedResponse().replace(
				"<![CDATA[", "");

		final List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "class",
				"countedCards", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(7, tagTester.size());
		Assert.assertEquals(expectedFieldContent, tagTester.get(field).getValue());
	}

}