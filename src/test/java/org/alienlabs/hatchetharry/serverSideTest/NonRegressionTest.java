package org.alienlabs.hatchetharry.serverSideTest;

import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.PlayCardFromHandCometChannel;
import org.alienlabs.hatchetharry.serverSideTest.util.EventBusMock;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.CardRotateBehavior;
import org.alienlabs.hatchetharry.view.component.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.component.PutToHandFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.FormTester;
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
			new String[] { "applicationContext.xml" });
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
				this.eventBus = new EventBusMock(this);
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
		this.startAGameAndPlayACard();

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
				.get("parentPlaceholder:handCards:0:cardPanel:cardHandle:menutoggleButton");
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
		final List<Object> allMessages = EventBusMock.getMessages();

		boolean success = false;
		for (int index = allMessages.size() - 1; index >= 0; index--)
		{
			if ((allMessages.get(index) instanceof PlayCardFromHandCometChannel))
			{
				Assert.assertFalse(persistenceService.getCardFromUuid(
						((PlayCardFromHandCometChannel)allMessages.get(index)).getUuidToLookFor())
						.isTapped());
				success = true;
				break;
			}
		}
		Assert.assertTrue("No PlayCardFromHandCometChannel found in EventBus!", success);
	}

	@Test
	public void whenCreatingAGameAndPlayingACardTheTotalNumberOfCardsInAllZonesMustBeTheNumberOfCardsInTheDeck()
	{
		this.startAGameAndPlayACard();

		final PersistenceService persistenceService = NonRegressionTest.context
				.getBean(PersistenceService.class);
		final Player p = persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());
	}

	@Test
	public void whenCreatingAndJoiningAGameTheTotalNumberOfCardsInAllZonesMustBeTheNumberOfCardsInTheDeck()
	{
		this.startAGameAndPlayACard();

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

	private void startAGameAndPlayACard()
	{
		// Create game
		this.tester.assertComponent("createGameLink", AjaxLink.class);
		this.tester.clickLink("createGameLink", true);

		final FormTester createGameForm = this.tester
				.newFormTester("createGameWindow:content:form");
		createGameForm.setValue("name", "Zala");
		createGameForm.setValue("sideInput", "0");
		createGameForm.setValue("deckParent:decks", "0");
		createGameForm.submit();

		// Retrieve PlayCardFromHandBehavior
		this.tester.assertComponent("playCardPlaceholder", WebMarkupContainer.class);
		this.tester.assertComponent("playCardPlaceholder:playCardLink", WebMarkupContainer.class);
		final WebMarkupContainer playCardLink = (WebMarkupContainer)this.tester
				.getComponentFromLastRenderedPage("playCardPlaceholder:playCardLink");
		final PlayCardFromHandBehavior pcfhb = (PlayCardFromHandBehavior)playCardLink
				.getBehaviors().get(0);

		// For the moment, we should have no card in the battlefield
		final Long gameId = HatchetHarrySession.get().getGameId();
		final PersistenceService persistenceService = NonRegressionTest.context
				.getBean(PersistenceService.class);
		final List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		// Play a card
		this.tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(0).getUuid());
		this.tester.executeBehavior(pcfhb);
	}

}
