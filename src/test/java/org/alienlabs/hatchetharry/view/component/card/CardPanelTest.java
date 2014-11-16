package org.alienlabs.hatchetharry.view.component.card;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.PlayerAndCard;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.gui.ExileComponent;
import org.alienlabs.hatchetharry.view.component.gui.GraveyardComponent;
import org.alienlabs.hatchetharry.view.component.zone.PlayCardFromGraveyardBehavior;
import org.alienlabs.hatchetharry.view.component.zone.PutToExileFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.component.zone.PutToGraveyardFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.component.zone.PutToHandFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.markup.html.*;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Assert;
import org.junit.Test;

public class CardPanelTest extends SpringContextLoaderBaseTest
{
	@Test
	public void testPlayCardFromHand() throws Exception
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
		String card = allCardsInBattlefield.get(0).getTitle();

		String pageDocument = this.tester.getLastResponse().getDocument();
		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(card));

		// We should have 6 cards in hand
		final List<MagicCard> allCardsInHand = persistenceService
				.getAllCardsInHandForAGameAndAPlayer(gameId, HatchetHarrySession.get().getPlayer()
						.getId(), HatchetHarrySession.get().getPlayer().getDeck().getDeckId());
		Assert.assertEquals(6, allCardsInHand.size());

		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"handImagePlaceholder", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(6, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));
	}

	@Test
	public void testPutCardToHandFromBattlefield() throws Exception
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

		String pageDocument = this.tester.getLastResponse().getDocument();
		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		String cardToVerify = tagTester.get(0).getAttribute("src");

		// Put it to graveyard
		super.tester.assertComponent("parentPlaceholder:magicCardsForSide1:1:cardPanel",
				CardPanel.class);
		final CardPanel card = (CardPanel)super.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCardsForSide1:1:cardPanel");
		Assert.assertNotNull(card);

		final PutToGraveyardFromBattlefieldBehavior ptgfbb = card
				.getPutToGraveyardFromBattlefieldBehavior();
		super.tester.executeBehavior(ptgfbb);

		// Verify
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		pageDocument = this.tester.getLastResponse().getDocument();

		SpringContextLoaderBaseTest.tester.assertComponent("graveyardParent:graveyard",
				GraveyardComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "thumbPlaceholder",
				false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));
		Assert.assertEquals(cardToVerify, tagTester.get(0).getAttribute("src").replace("Thumb", ""));

		allCardsInBattlefield = persistenceService.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertTrue(allCardsInBattlefield.isEmpty());

		List<MagicCard> allCardsInGraveyard = persistenceService
				.getAllCardsInGraveyardForAGame(gameId);
		Assert.assertEquals(1, allCardsInGraveyard.size());
		Assert.assertTrue(cardToVerify.contains(allCardsInGraveyard.get(0).getTitle()));

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

		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));
		Assert.assertEquals(cardToVerify, tagTester.get(0).getAttribute("src"));

		allCardsInBattlefield = persistenceService.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		allCardsInGraveyard = persistenceService.getAllCardsInGraveyardForAGame(gameId);
		Assert.assertTrue(allCardsInGraveyard.isEmpty());

		// Put it back to hand
		final PutToHandFromBattlefieldBehavior pthfbb = card.getPutToHandFromBattlefieldBehavior();
		super.tester.executeBehavior(pthfbb);

		// Verify
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		pageDocument = this.tester.getLastResponse().getDocument();

		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertTrue(tagTester.isEmpty());

		allCardsInBattlefield = persistenceService.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertTrue(allCardsInBattlefield.isEmpty());

		final List<MagicCard> allCardsInHand = persistenceService
				.getAllCardsInHandForAGameAndAPlayer(gameId, HatchetHarrySession.get().getPlayer()
						.getId(), HatchetHarrySession.get().getPlayer().getDeck().getDeckId());
		Assert.assertEquals(7, allCardsInHand.size());

		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"handImagePlaceholder", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(7, tagTester.size());
		// TODO ensure card is at the beginning or at the end
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));
		Assert.assertEquals(cardToVerify, tagTester.get(0).getAttribute("src"));
	}

	@Test
	public void testPlayCardFromGraveyardBehavior() throws Exception
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

		String pageDocument = this.tester.getLastResponse().getDocument();
		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		String cardToVerify = tagTester.get(0).getAttribute("src");

		// Put it to graveyard
		super.tester.assertComponent("parentPlaceholder:magicCardsForSide1:1:cardPanel",
				CardPanel.class);
		final CardPanel card = (CardPanel)super.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCardsForSide1:1:cardPanel");
		Assert.assertNotNull(card);

		final PutToGraveyardFromBattlefieldBehavior ptgfbb = card
				.getPutToGraveyardFromBattlefieldBehavior();
		super.tester.executeBehavior(ptgfbb);

		// Verify
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		pageDocument = this.tester.getLastResponse().getDocument();

		SpringContextLoaderBaseTest.tester.assertComponent("graveyardParent:graveyard",
				GraveyardComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "thumbPlaceholder",
				false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));
		String cardInGraveyardImage = tagTester.get(0).getAttribute("src").replace("Thumb", "");
		Assert.assertEquals(cardInGraveyardImage, cardToVerify);

		// Play it from graveyard
		SpringContextLoaderBaseTest.tester.assertComponent("playCardFromGraveyardLinkDesktop",
				WebMarkupContainer.class);
		final WebMarkupContainer wmc = (WebMarkupContainer)super.tester
				.getComponentFromLastRenderedPage("playCardFromGraveyardLinkDesktop");
		Assert.assertNotNull(wmc);
		PlayCardFromGraveyardBehavior pcfgb = wmc.getBehaviors(PlayCardFromGraveyardBehavior.class)
				.get(0);
		Assert.assertNotNull(pcfgb);
		this.tester.getRequest().setParameter("card",
				((PlayerAndCard)card.getDefaultModelObject()).getCard().getUuidObject().toString());
		super.tester.executeBehavior(pcfgb);

		// Verify
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		pageDocument = this.tester.getLastResponse().getDocument();

		SpringContextLoaderBaseTest.tester.assertComponent("graveyardParent:graveyard",
				GraveyardComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "thumbPlaceholder",
				false);
		Assert.assertNotNull(tagTester);
		Assert.assertTrue(tagTester.isEmpty());

		allCardsInBattlefield = persistenceService.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		List<MagicCard> allCardsInGraveyard = this.persistenceService
				.getAllCardsInGraveyardForAGame(gameId);
		Assert.assertTrue(allCardsInGraveyard.isEmpty());

		// Verify that there is one card on battlefield
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		String cardInBattlefieldImage = tagTester.get(0).getAttribute("src");
		Assert.assertTrue(cardInBattlefieldImage.contains(allCardsInBattlefield.get(0).getTitle()));

		allCardsInBattlefield = persistenceService.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertNotNull(allCardsInBattlefield);
		Assert.assertEquals(1, allCardsInBattlefield.size());
		Assert.assertTrue(cardToVerify.contains(allCardsInBattlefield.get(0).getTitle()));

		// Verify that there are 6 cards in hand
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"handImagePlaceholder", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(6, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));
	}

	@Test
	public void testPutCardToGraveyardFromBattlefield() throws Exception
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

		String pageDocument = this.tester.getLastResponse().getDocument();
		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		String cardToVerify = tagTester.get(0).getAttribute("src");

		// Put it to graveyard
		super.tester.assertComponent("parentPlaceholder:magicCardsForSide1:1:cardPanel",
				CardPanel.class);
		final CardPanel card = (CardPanel)super.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCardsForSide1:1:cardPanel");
		Assert.assertNotNull(card);

		final PutToGraveyardFromBattlefieldBehavior ptgfbb = card
				.getPutToGraveyardFromBattlefieldBehavior();
		super.tester.executeBehavior(ptgfbb);

		// Verify
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		pageDocument = this.tester.getLastResponse().getDocument();

		SpringContextLoaderBaseTest.tester.assertComponent("graveyardParent:graveyard",
				GraveyardComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "thumbPlaceholder",
				false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));
		Assert.assertEquals(cardToVerify, tagTester.get(0).getAttribute("src").replace("Thumb", ""));

		allCardsInBattlefield = persistenceService.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertTrue(allCardsInBattlefield.isEmpty());

		List<MagicCard> allCardsInGraveyard = persistenceService
				.getAllCardsInGraveyardForAGame(gameId);
		Assert.assertEquals(1, allCardsInGraveyard.size());
		Assert.assertTrue(cardToVerify.contains(allCardsInGraveyard.get(0).getTitle()));
	}

	@Test
	public void testPutCardToExileFromBattlefield() throws Exception
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

		String pageDocument = this.tester.getLastResponse().getDocument();
		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		String cardToVerify = tagTester.get(0).getAttribute("src");

		// Put it to exile
		super.tester.assertComponent("parentPlaceholder:magicCardsForSide1:1:cardPanel",
				CardPanel.class);
		final CardPanel card = (CardPanel)super.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCardsForSide1:1:cardPanel");
		Assert.assertNotNull(card);

		final PutToExileFromBattlefieldBehavior ptefbb = card
				.getPutToExileFromBattlefieldBehavior();
		super.tester.executeBehavior(ptefbb);

		// Verify
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		pageDocument = this.tester.getLastResponse().getDocument();

		SpringContextLoaderBaseTest.tester.assertComponent("exileParent:exile",
				ExileComponent.class);
		tagTester = TagTester
				.createTagsByAttribute(pageDocument, "class", "exile-nav-thumb", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));
		Assert.assertEquals(cardToVerify, tagTester.get(0).getAttribute("src").replace("Thumb", ""));

		allCardsInBattlefield = persistenceService.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertTrue(allCardsInBattlefield.isEmpty());

		List<MagicCard> allCardsInExile = persistenceService.getAllCardsInExileForAGameAndAPlayer(
				gameId, HatchetHarrySession.get().getPlayer().getId(), HatchetHarrySession.get()
						.getPlayer().getDeck().getDeckId());
		Assert.assertEquals(1, allCardsInExile.size());
		Assert.assertTrue(cardToVerify.contains(allCardsInExile.get(0).getTitle()));
	}

}
