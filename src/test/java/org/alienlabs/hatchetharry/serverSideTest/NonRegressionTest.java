package org.alienlabs.hatchetharry.serverSideTest;

import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.CardRotateBehavior;
import org.alienlabs.hatchetharry.view.component.HandComponent;
import org.alienlabs.hatchetharry.view.component.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.component.PutToHandFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.TagTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
	private transient WicketTester tester;
	private HatchetHarryApplication webApp;
	static transient ApplicationContext context;

	@Before
	public void setUp()
	{
		this.webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;


			@Override
			public void init()
			{
				NonRegressionTest.context = NonRegressionTest.CLASS_PATH_XML_APPLICATION_CONTEXT;
				this.getComponentInstantiationListeners().add(
						new SpringComponentInjector(this, NonRegressionTest.context, true));
				// We'll ask Emond to enable unit testing in EventBus
				// this.eventBus = new EventBusMock(this);
			}
		};

		this.tester = new WicketTester(this.webApp);

		// start and render the test page
		this.tester.startPage(HomePage.class);

		// assert rendered page class
		this.tester.assertRenderedPage(HomePage.class);
	}

	@After
	public void tearDown()
	{
		NonRegressionTest.context.getBean(PersistenceService.class).resetDb();
	}


	@Test
	/** 
	 * Init: we create a game, we play a card, we tap it, we put it back to hand
	 * Run: we play it again
	 * Verify: the card should be untapped.
	 * 
	 */
	public void testWhenACardIsPlayedAndPutBackToHandAndPlayedAgainItIsUntapped()
	{
		SpringContextLoaderBaseTest.startAGameAndPlayACard(this.tester, NonRegressionTest.context);

		final PersistenceService persistenceService = NonRegressionTest.context
				.getBean(PersistenceService.class);
		final Long gameId = HatchetHarrySession.get().getGameId();

		// We should have one card on the battlefield, untapped
		List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());
		MagicCard card = persistenceService.getCardFromUuid(UUID.fromString(allCardsInBattlefield
				.get(0).getUuid()));
		Assert.assertFalse(card.isTapped());

		// Tap card
		this.tester.startPage(HomePage.class);
		this.tester.assertRenderedPage(HomePage.class);

		final HomePage hp = (HomePage)this.tester.getLastRenderedPage();
		final WebMarkupContainer cardButton = (WebMarkupContainer)hp
				.get("parentPlaceholder:magicCards:1:cardPanel:cardHandle:menutoggleButton");
		Assert.assertNotNull(cardButton);
		final CardRotateBehavior rotateBehavior = cardButton.getBehaviors(CardRotateBehavior.class)
				.get(0);
		Assert.assertNotNull(rotateBehavior);

		this.tester.getRequest().setParameter("uuid", card.getUuid());
		this.tester.executeBehavior(rotateBehavior);

		// Assert card is tapped
		card = persistenceService.getCardFromUuid(UUID.fromString(allCardsInBattlefield.get(0)
				.getUuid()));
		Assert.assertTrue(card.isTapped());

		// Put the first card back to hand
		final PutToHandFromBattlefieldBehavior putToHandFromBattlefieldBehavior = cardButton
				.getBehaviors(PutToHandFromBattlefieldBehavior.class).get(0);
		Assert.assertNotNull(putToHandFromBattlefieldBehavior);
		this.tester.executeBehavior(putToHandFromBattlefieldBehavior);

		// We should have no card on the battlefield
		allCardsInBattlefield = persistenceService.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		// Play card again
		this.tester.startPage(HomePage.class);
		this.tester.assertRenderedPage(HomePage.class);

		this.tester.assertComponent("playCardPlaceholder", WebMarkupContainer.class);
		this.tester.assertComponent("playCardPlaceholder:playCardLink", WebMarkupContainer.class);
		final WebMarkupContainer playCardLink = (WebMarkupContainer)this.tester
				.getComponentFromLastRenderedPage("playCardPlaceholder:playCardLink");

		PlayCardFromHandBehavior pcfhb = (PlayCardFromHandBehavior)playCardLink.getBehaviors().get(
				0);
		pcfhb = (PlayCardFromHandBehavior)playCardLink.getBehaviors().get(0);
		Assert.assertNotNull(pcfhb);

		this.tester.getRequest().setParameter("card", card.getUuid());
		this.tester.executeBehavior(pcfhb);

		// We should have one card on the battlefield, untapped
		allCardsInBattlefield = persistenceService.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		// We'll ask Emond to enable unit testing in EventBus
		// final List<Object> allMessages = EventBusMock.getMessages();
		//
		// boolean success = false;
		// for (int index = allMessages.size() - 1; index >= 0; index--)
		// {
		// if ((allMessages.get(index) instanceof PlayCardFromHandCometChannel))
		// {
		// Assert.assertFalse(persistenceService.getCardFromUuid(
		// ((PlayCardFromHandCometChannel)allMessages.get(index)).getUuidToLookFor())
		// .isTapped());
		// success = true;
		// break;
		// }
		// }
		// Assert.assertTrue("No PlayCardFromHandCometChannel found in EventBus!",
		// success);
	}

	@Test
	public void whenCreatingAGameAndPlayingACardTheTotalNumberOfCardsInAllZonesMustBeTheNumberOfCardsInTheDeck()
	{
		SpringContextLoaderBaseTest.startAGameAndPlayACard(this.tester, NonRegressionTest.context);

		final PersistenceService persistenceService = NonRegressionTest.context
				.getBean(PersistenceService.class);
		final Player p = persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());
	}

	@Test
	public void whenCreatingAGamePlayingACardAndPlayingATokenTheNumberOfCardsInTheHandMustNotChange()
	{
		SpringContextLoaderBaseTest.startAGameAndPlayACard(this.tester, NonRegressionTest.context);

		// 60 cards in the deck?
		final PersistenceService persistenceService = NonRegressionTest.context
				.getBean(PersistenceService.class);
		final Player p = persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());

		// 6 cards in the hand?
		String pageDocument = this.tester.getLastResponse().getDocument();

		this.tester.assertComponent("galleryParent:gallery", HandComponent.class);
		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "class",
				"nav-thumb", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(6, tagTester.size());

		// Do they look OK?
		Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		// Is there really one card on the battlefield?
		this.tester.startPage(HomePage.class);
		this.tester.assertRenderedPage(HomePage.class);
		pageDocument = this.tester.getLastResponse().getDocument();

		tagTester = TagTester.createTagsByAttribute(pageDocument, "class", "magicCard", false);
		Assert.assertNotNull(tagTester);

		// 2 because the Balduvian Horde is still there
		Assert.assertEquals(2, tagTester.size());

		// Play another card
		this.tester.assertComponent("playCardPlaceholder", WebMarkupContainer.class);
		this.tester.assertComponent("playCardPlaceholder:playCardLink", WebMarkupContainer.class);
		final WebMarkupContainer playCardLink = (WebMarkupContainer)this.tester
				.getComponentFromLastRenderedPage("playCardPlaceholder:playCardLink");
		final PlayCardFromHandBehavior pcfhb = (PlayCardFromHandBehavior)playCardLink
				.getBehaviors().get(0);
		this.tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(0).getUuid());
		this.tester.executeBehavior(pcfhb);

		// 6 cards in the hand instead of 5!!!
		this.tester.startPage(HomePage.class);
		this.tester.assertRenderedPage(HomePage.class);
		pageDocument = this.tester.getLastResponse().getDocument();

		this.tester.assertComponent("galleryParent:gallery", HandComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "class", "nav-thumb", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(6, tagTester.size());

		// Do they look OK?
		Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		// Is there really one card on the battlefield? (beware of
		// wicket-quickview)
		this.tester.startPage(HomePage.class);
		this.tester.assertRenderedPage(HomePage.class);
		pageDocument = this.tester.getLastResponse().getDocument();

		tagTester = TagTester.createTagsByAttribute(pageDocument, "class", "magicCard", false);
		Assert.assertNotNull(tagTester);

		// 2 because the Balduvian Horde is still there
		Assert.assertEquals(2, tagTester.size());

		// Verify createTokenWindow
		this.openModalWindow("createTokenWindow", "createTokenLink");

		// open ModalWindow in order to play a token, fill fields and validate
		this.tester.assertComponent("createTokenLink", AjaxLink.class);
		this.tester.clickLink("createTokenLink", true);

		final FormTester createTokenForm = this.tester
				.newFormTester("createTokenWindow:content:form");
		createTokenForm.setValue("type", "Creature");
		createTokenForm.setValue("power", "7");
		createTokenForm.setValue("thoughness", "7");
		createTokenForm.setValue("colors", "Green");
		createTokenForm.setValue("capabilities", "It kills you in three turns");
		createTokenForm.setValue("creatureTypes", "Lurghoyf");
		createTokenForm.setValue("description", "Help!!!");
		createTokenForm.submit();

		// Are there really 2 cards on the battlefield?
		this.tester.startPage(HomePage.class);
		this.tester.assertRenderedPage(HomePage.class);
		pageDocument = this.tester.getLastResponse().getDocument();

		tagTester = TagTester.createTagsByAttribute(pageDocument, "class", "magicCard", false);
		Assert.assertNotNull(tagTester);

		// 2 because: the Balduvian Horde is not there anymore but due to
		// wicket-quickview, there's only one card in the generated HTML at a
		// given moment. Plus, each card contains two nested spans of class
		// "magicCard"
		Assert.assertEquals(2, tagTester.size());

		// Are there still 6 cards in the hand
		pageDocument = this.tester.getLastResponse().getDocument();
		this.tester.assertComponent("galleryParent:gallery", HandComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "class", "nav-thumb", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(6, tagTester.size());

		// Do they still look OK?
		Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		// And now: does the "count cards" modal window display the right
		// result: 5 cards in hand, 2 (but counting as only one because of
		// wicket-quickview) on the battlefield ( + 1 token), 53 in the
		// library, 0
		// in
		// exile & graveyard and 60 in total (beware, there's a token!)

		// Verify CountCardsModalWindow
		this.openModalWindow("countCardsWindow", "countCardsLink");

		// open ModalWindow
		this.tester.assertComponent("countCardsLink", AjaxLink.class);
		this.tester.clickLink("countCardsLink", true);

		this.verifyFieldsOfCountCardsModalWindow("hand", "6");
		this.verifyFieldsOfCountCardsModalWindow("library", "53");
		this.verifyFieldsOfCountCardsModalWindow("graveyard", "0");
		this.verifyFieldsOfCountCardsModalWindow("exile", "0");
		// 1 because of wicket-quickview
		this.verifyFieldsOfCountCardsModalWindow("battlefield", "1");
		this.verifyFieldsOfCountCardsModalWindow("total", "60"); // \O/

	}

	@Test
	public void whenCreatingAndJoiningAGameTheTotalNumberOfCardsInAllZonesMustBeTheNumberOfCardsInTheDeck()
	{
		SpringContextLoaderBaseTest.startAGameAndPlayACard(this.tester, NonRegressionTest.context);

		final PersistenceService persistenceService = NonRegressionTest.context
				.getBean(PersistenceService.class);
		final Player player = persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId()).get(0);
		Assert.assertEquals(60, player.getDeck().getCards().size());

		// Create game
		this.tester.assertComponent("joinGameLink", AjaxLink.class);
		this.tester.clickLink("joinGameLink", true);

		final FormTester joinGameForm = this.tester.newFormTester("joinGameWindow:content:form");
		joinGameForm.setValue("name", "Zala");
		joinGameForm.setValue("sideInput", "1");
		joinGameForm.setValue("deckParent:decks", "1");
		final Long gameId = HatchetHarrySession.get().getGameId();
		joinGameForm.setValue("gameIdInput", String.valueOf(gameId));
		joinGameForm.submit();

		Assert.assertEquals(60, player.getDeck().getCards().size());
	}

	private void openModalWindow(final String _window, final String linkToActivateWindow)
	{
		// assert modal windows are in the page
		this.tester.assertComponent(_window, ModalWindow.class);
		final ModalWindow window = (ModalWindow)this.tester
				.getComponentFromLastRenderedPage(_window);
		this.tester.assertInvisible(window.getPageRelativePath() + ":" + window.getContentId());

		final AjaxLink<Void> link = (AjaxLink<Void>)this.tester
				.getComponentFromLastRenderedPage(linkToActivateWindow);
		Assert.assertNotNull(link);
		this.tester.clickLink(linkToActivateWindow, true);
		this.tester.assertVisible(window.getPageRelativePath() + ":" + window.getContentId());
	}

	private void verifyFieldsOfCountCardsModalWindow(final String fieldId,
			final String expectedFieldContent)
	{
		this.tester.assertComponent("countCardsWindow:content:players", ListView.class);
		this.tester.assertComponent("countCardsWindow:content:players:0:" + fieldId, Label.class);
		final Label actualLabel = (Label)this.tester
				.getComponentFromLastRenderedPage("countCardsWindow:content:players:0:" + fieldId);
		Assert.assertEquals(expectedFieldContent, actualLabel.getDefaultModelObjectAsString());
	}
}
