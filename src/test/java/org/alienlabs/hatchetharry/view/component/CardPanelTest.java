package org.alienlabs.hatchetharry.view.component;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class CardPanelTest extends SpringContextLoaderBaseTest
{
	@Test
	public void testCardPanel() throws SecurityException, NoSuchMethodException
	{
		// Start a game and play a card
		super.startAGameAndPlayACard();

		// We should have one card in the battlefield
		//		super.tester.startPage(HomePage.class);
		//		super.tester.assertRenderedPage(HomePage.class);

		final Long gameId = HatchetHarrySession.get().getGameId();
		final PersistenceService persistenceService = super.context
				.getBean(PersistenceService.class);
		List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		// Put it to graveyard
		super.tester.assertComponent("parentPlaceholder:magicCards:1:cardPanel", CardPanel.class);
		final CardPanel card = (CardPanel)super.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCards:1:cardPanel");
		Assert.assertNotNull(card);

		super.tester.getLastRenderedPage();

		final PutToGraveyardFromBattlefieldBehavior ptgfbb = card
				.getPutToGraveyardFromBattlefieldBehavior();
		super.tester.executeBehavior(ptgfbb);

		super.pageDocument = super.tester.getLastResponse().getDocument();
		// SpringContextLoaderBaseTest.tester
		// .assertComponentOnAjaxResponse("graveyardParent:graveyard:graveyardCardsPlaceholder:graveyardCards:0:wrapper:graveyardImagePlaceholder");

		// Verify
		allCardsInBattlefield = persistenceService.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		List<MagicCard> allCardsInGraveyard = persistenceService
				.getAllCardsInGraveyardForAGame(gameId);
		Assert.assertEquals(1, allCardsInGraveyard.size());

		// Now, put it back in play
		final PlayCardFromGraveyardBehavior pcfgb = (PlayCardFromGraveyardBehavior)super.tester
				.getComponentFromLastRenderedPage("playCardFromGraveyardLinkDesktop")
				.getBehaviorById(0);
		super.tester.getRequest().setParameter("card", allCardsInGraveyard.get(0).getUuid());
		super.tester.executeBehavior(pcfgb);
		// Verify
		allCardsInBattlefield = persistenceService.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		allCardsInGraveyard = persistenceService.getAllCardsInGraveyardForAGame(gameId);
		Assert.assertEquals(0, allCardsInGraveyard.size());

		// Put it back to hand
		final PutToHandFromBattlefieldBehavior pthfbb = card.getPutToHandFromBattlefieldBehavior();
		// super.tester.getRequest().setParameter("card",
		// allCardsInGraveyard.get(0).getUuid());
		super.tester.executeBehavior(pthfbb);

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
	public void testPlayCardFromHandBehavior()
	{
		// For later
	}

	@Test
	@Ignore
	public void testPlayCardFromGraveyardBehavior()
	{
		// For later
	}

}
