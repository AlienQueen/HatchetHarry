package org.alienlabs.hatchetharry.view.component.card;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.gui.GraveyardComponent;
import org.alienlabs.hatchetharry.view.component.zone.PlayCardFromGraveyardBehavior;
import org.alienlabs.hatchetharry.view.component.zone.PutToGraveyardFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.component.zone.PutToHandFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class CardPanelTest extends SpringContextLoaderBaseTest
{
	@Test
	public void testCardPanel() throws Exception
	{
		// Start a game and play a card
		super.startAGameAndPlayACard();
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// We should have one card in the battlefield
		final Long gameId = HatchetHarrySession.get().getGameId();
		final PersistenceService persistenceService = super.context
				.getBean(PersistenceService.class);
		List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		// Put it to graveyard
		super.tester.assertComponent("parentPlaceholder:magicCardsForSide1:1:cardPanel", CardPanel.class);
		final CardPanel card = (CardPanel)super.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCardsForSide1:1:cardPanel");
		Assert.assertNotNull(card);

		final PutToGraveyardFromBattlefieldBehavior ptgfbb = card
				.getPutToGraveyardFromBattlefieldBehavior();
		super.tester.executeBehavior(ptgfbb);

		// Verify
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		String pageDocument = this.tester.getLastResponse().getDocument();

		SpringContextLoaderBaseTest.tester.assertComponent("graveyardParent:graveyard", GraveyardComponent.class);
		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"thumbPlaceholder", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		allCardsInBattlefield = persistenceService.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		List<MagicCard> allCardsInGraveyard = persistenceService
				.getAllCardsInGraveyardForAGame(gameId);
		Assert.assertEquals(1, allCardsInGraveyard.size());

		// Now, put it back into play
		final PlayCardFromGraveyardBehavior pcfgb = (PlayCardFromGraveyardBehavior)super.tester
				.getComponentFromLastRenderedPage("playCardFromGraveyardLinkDesktop")
				.getBehaviorById(0);
		super.tester.getRequest().setParameter("card", allCardsInGraveyard.get(0).getUuid());
		super.tester.executeBehavior(pcfgb);

		// Verify
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		pageDocument = this.tester.getLastResponse().getDocument();

		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		allCardsInBattlefield = persistenceService.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		allCardsInGraveyard = persistenceService.getAllCardsInGraveyardForAGame(gameId);
		Assert.assertEquals(0, allCardsInGraveyard.size());

		// Put it back to hand
		final PutToHandFromBattlefieldBehavior pthfbb = card.getPutToHandFromBattlefieldBehavior();
		super.tester.executeBehavior(pthfbb);

		// Verify
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		pageDocument = this.tester.getLastResponse().getDocument();

		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertTrue(tagTester.isEmpty());

		allCardsInBattlefield = persistenceService.getAllCardsInBattlefieldForAGame(gameId);
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
