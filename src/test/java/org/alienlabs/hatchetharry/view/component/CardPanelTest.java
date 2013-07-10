package org.alienlabs.hatchetharry.view.component;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.CardPanel;
import org.alienlabs.hatchetharry.view.component.PlayCardFromGraveyardBehavior;
import org.alienlabs.hatchetharry.view.component.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.component.PutToGraveyardFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.component.PutToHandFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class CardPanelTest extends SpringContextLoaderBaseTest
{
	@Test
	public void testCardPanel()
	{
		// Start a game and play a card
		CardPanelTest.startAGameAndPlayACard();

		// We should have one card in the battlefield
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		final Long gameId = HatchetHarrySession.get().getGameId();
		final PersistenceService persistenceService = SpringContextLoaderBaseTest.context
				.getBean(PersistenceService.class);
		List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		// Put it to graveyard
		SpringContextLoaderBaseTest.tester.assertComponent(
				"parentPlaceholder:handCards:0:cardPanel", CardPanel.class);
		final CardPanel card = (CardPanel)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:handCards:0:cardPanel");
		Assert.assertNotNull(card);

		final PutToGraveyardFromBattlefieldBehavior ptgfbb = card
				.getPutToGraveyardFromBattlefieldBehavior();
		SpringContextLoaderBaseTest.tester.executeBehavior(ptgfbb);

		// Verify
		allCardsInBattlefield = persistenceService.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		List<MagicCard> allCardsInGraveyard = persistenceService
				.getAllCardsInGraveyardForAGame(gameId);
		Assert.assertEquals(1, allCardsInGraveyard.size());

		// Now, put it back in play
		final PlayCardFromGraveyardBehavior pcfgb = (PlayCardFromGraveyardBehavior)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("playCardFromGraveyardLinkDesktop")
				.getBehaviorById(0);
		SpringContextLoaderBaseTest.tester.getRequest().setParameter("card",
				allCardsInGraveyard.get(0).getUuid());
		SpringContextLoaderBaseTest.tester.executeBehavior(pcfgb);
		// Verify
		allCardsInBattlefield = persistenceService.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		allCardsInGraveyard = persistenceService.getAllCardsInGraveyardForAGame(gameId);
		Assert.assertEquals(0, allCardsInGraveyard.size());

		// Put it back to hand
		final PutToHandFromBattlefieldBehavior pthfbb = card.getPutToHandFromBattlefieldBehavior();
		// SpringContextLoaderBaseTest.tester.getRequest().setParameter("card",
		// allCardsInGraveyard.get(0).getUuid());
		SpringContextLoaderBaseTest.tester.executeBehavior(pthfbb);

		// Verify
		allCardsInBattlefield = persistenceService.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		final List<MagicCard> allCardsInHand = persistenceService
				.getAllCardsInHandForAGameAndAPlayer(gameId, HatchetHarrySession.get().getPlayer()
						.getId(), HatchetHarrySession.get().getPlayer().getDeck().getDeckId());
		Assert.assertEquals(7, allCardsInHand.size());
	}

	@Test
	@Ignore
	public void testCardMoveBehavior()
	{
	}

	@Test
	@Ignore
	public void testPlayCardFromHandBehavior()
	{
	}

	@Test
	@Ignore
	public void testPlayCardFromGraveyardBehavior()
	{
	}

	private static void startAGameAndPlayACard()
	{
		// Create game
		SpringContextLoaderBaseTest.tester.assertComponent("createGameLink", AjaxLink.class);
		SpringContextLoaderBaseTest.tester.clickLink("createGameLink", true);

		final FormTester createGameForm = SpringContextLoaderBaseTest.tester
				.newFormTester("createGameWindow:content:form");
		createGameForm.setValue("name", "Zala");
		createGameForm.setValue("sideInput", "0");
		createGameForm.setValue("deckParent:decks", "0");
		createGameForm.submit();

		// Retrieve PlayCardFromHandBehavior
		SpringContextLoaderBaseTest.tester.assertComponent("playCardPlaceholder",
				WebMarkupContainer.class);
		SpringContextLoaderBaseTest.tester.assertComponent("playCardPlaceholder:playCardLink",
				WebMarkupContainer.class);
		final WebMarkupContainer playCardLink = (WebMarkupContainer)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("playCardPlaceholder:playCardLink");
		final PlayCardFromHandBehavior pcfhb = (PlayCardFromHandBehavior)playCardLink
				.getBehaviors().get(0);

		// For the moment, we should have no card in the battlefield
		final Long gameId = HatchetHarrySession.get().getGameId();
		final PersistenceService persistenceService = SpringContextLoaderBaseTest.context
				.getBean(PersistenceService.class);
		final List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		// Play a card
		SpringContextLoaderBaseTest.tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(0).getUuid());
		SpringContextLoaderBaseTest.tester.executeBehavior(pcfhb);
	}

}
