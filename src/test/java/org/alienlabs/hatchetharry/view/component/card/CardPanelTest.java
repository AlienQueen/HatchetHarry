package org.alienlabs.hatchetharry.view.component.card;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.PlayerAndCard;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBase;
import org.alienlabs.hatchetharry.view.component.gui.ExileComponent;
import org.alienlabs.hatchetharry.view.component.gui.GraveyardComponent;
import org.alienlabs.hatchetharry.view.component.zone.PlayCardFromGraveyardBehavior;
import org.alienlabs.hatchetharry.view.component.zone.PutToExileFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.component.zone.PutToGraveyardFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.component.zone.PutToHandFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = { "classpath:applicationContext.xml",
		"classpath:applicationContextTest.xml" })
public class CardPanelTest extends SpringContextLoaderBase
{
	@Test
	public void testPlayCardFromHand() throws Exception
	{
		// Start a game and play a card
		super.startAGameAndPlayACard();
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// We should have one card in the battlefield
		final Long gameId = HatchetHarrySession.get().getGameId();
		final List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());
		final String card = allCardsInBattlefield.get(0).getTitle();

		final String pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();
		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(card));

		// We should have 6 cards in hand
		final List<MagicCard> allCardsInHand = persistenceService
				.getAllCardsInHandForAGameAndADeck(gameId, HatchetHarrySession.get().getPlayer().getDeck().getDeckId());
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
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// We should have one card in the battlefield
		final Long gameId = HatchetHarrySession.get().getGameId();
		List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		String pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();
		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		final String cardToVerify = tagTester.get(0).getAttribute("src");

		// Put it to graveyard
		SpringContextLoaderBase.tester.assertComponent(
				"parentPlaceholder:magicCardsForSide1:1:cardPanel", CardPanel.class);
		final CardPanel card = (CardPanel)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCardsForSide1:1:cardPanel");
		Assert.assertNotNull(card);

		final PutToGraveyardFromBattlefieldBehavior ptgfbb = card
				.getPutToGraveyardFromBattlefieldBehavior();
		SpringContextLoaderBase.tester.executeBehavior(ptgfbb);

		// Verify
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

		SpringContextLoaderBase.tester.assertComponent("graveyardParent:graveyard",
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
		final PlayCardFromGraveyardBehavior pcfgb = (PlayCardFromGraveyardBehavior)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("playCardFromGraveyardLinkDesktop")
				.getBehaviorById(0);
		SpringContextLoaderBase.tester.getRequest().setParameter("card",
				allCardsInGraveyard.get(0).getUuid());
		SpringContextLoaderBase.tester.executeBehavior(pcfgb);

		// Verify
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

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
		SpringContextLoaderBase.tester.executeBehavior(pthfbb);

		// Verify
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertTrue(tagTester.isEmpty());

		allCardsInBattlefield = persistenceService.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertTrue(allCardsInBattlefield.isEmpty());

		final List<MagicCard> allCardsInHand = persistenceService
				.getAllCardsInHandForAGameAndADeck(gameId, HatchetHarrySession.get().getPlayer().getDeck().getDeckId());
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
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// We should have one card in the battlefield
		final Long gameId = HatchetHarrySession.get().getGameId();
		List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		String pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();
		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		final String cardToVerify = tagTester.get(0).getAttribute("src");

		// Put it to graveyard
		SpringContextLoaderBase.tester.assertComponent(
				"parentPlaceholder:magicCardsForSide1:1:cardPanel", CardPanel.class);
		final CardPanel card = (CardPanel)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCardsForSide1:1:cardPanel");
		Assert.assertNotNull(card);

		final PutToGraveyardFromBattlefieldBehavior ptgfbb = card
				.getPutToGraveyardFromBattlefieldBehavior();
		SpringContextLoaderBase.tester.executeBehavior(ptgfbb);

		// Verify
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

		SpringContextLoaderBase.tester.assertComponent("graveyardParent:graveyard",
				GraveyardComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "thumbPlaceholder",
				false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));
		final String cardInGraveyardImage = tagTester.get(0).getAttribute("src")
				.replace("Thumb", "");
		Assert.assertEquals(cardInGraveyardImage, cardToVerify);

		// Play it from graveyard
		SpringContextLoaderBase.tester.assertComponent("playCardFromGraveyardLinkDesktop",
				WebMarkupContainer.class);
		final WebMarkupContainer wmc = (WebMarkupContainer)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("playCardFromGraveyardLinkDesktop");
		Assert.assertNotNull(wmc);
		final PlayCardFromGraveyardBehavior pcfgb = wmc.getBehaviors(
				PlayCardFromGraveyardBehavior.class).get(0);
		Assert.assertNotNull(pcfgb);
		SpringContextLoaderBase.tester.getRequest().setParameter("card",
				((PlayerAndCard)card.getDefaultModelObject()).getCard().getUuidObject().toString());
		SpringContextLoaderBase.tester.executeBehavior(pcfgb);

		// Verify
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

		SpringContextLoaderBase.tester.assertComponent("graveyardParent:graveyard",
				GraveyardComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "thumbPlaceholder",
				false);
		Assert.assertNotNull(tagTester);
		Assert.assertTrue(tagTester.isEmpty());

		allCardsInBattlefield = persistenceService.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		final List<MagicCard> allCardsInGraveyard = SpringContextLoaderBase.persistenceService
				.getAllCardsInGraveyardForAGame(gameId);
		Assert.assertTrue(allCardsInGraveyard.isEmpty());

		// Verify that there is one card on battlefield
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		final String cardInBattlefieldImage = tagTester.get(0).getAttribute("src");
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
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// We should have one card in the battlefield
		final Long gameId = HatchetHarrySession.get().getGameId();
		List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		String pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();
		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		final String cardToVerify = tagTester.get(0).getAttribute("src");

		// Put it to graveyard
		SpringContextLoaderBase.tester.assertComponent(
				"parentPlaceholder:magicCardsForSide1:1:cardPanel", CardPanel.class);
		final CardPanel card = (CardPanel)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCardsForSide1:1:cardPanel");
		Assert.assertNotNull(card);

		final PutToGraveyardFromBattlefieldBehavior ptgfbb = card
				.getPutToGraveyardFromBattlefieldBehavior();
		SpringContextLoaderBase.tester.executeBehavior(ptgfbb);

		// Verify
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

		SpringContextLoaderBase.tester.assertComponent("graveyardParent:graveyard",
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

		final List<MagicCard> allCardsInGraveyard = persistenceService
				.getAllCardsInGraveyardForAGame(gameId);
		Assert.assertEquals(1, allCardsInGraveyard.size());
		Assert.assertTrue(cardToVerify.contains(allCardsInGraveyard.get(0).getTitle()));
	}

	@Test
	public void testPutCardToExileFromBattlefield() throws Exception
	{
		// Start a game and play a card
		super.startAGameAndPlayACard();
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// We should have one card in the battlefield
		final Long gameId = HatchetHarrySession.get().getGameId();
		List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		String pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();
		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		final String cardToVerify = tagTester.get(0).getAttribute("src");

		// Put it to exile
		SpringContextLoaderBase.tester.assertComponent(
				"parentPlaceholder:magicCardsForSide1:1:cardPanel", CardPanel.class);
		final CardPanel card = (CardPanel)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCardsForSide1:1:cardPanel");
		Assert.assertNotNull(card);

		final PutToExileFromBattlefieldBehavior ptefbb = card
				.getPutToExileFromBattlefieldBehavior();
		SpringContextLoaderBase.tester.executeBehavior(ptefbb);

		// Verify
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

		SpringContextLoaderBase.tester.assertComponent("exileParent:exile", ExileComponent.class);
		tagTester = TagTester
				.createTagsByAttribute(pageDocument, "class", "exile-nav-thumb", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));
		Assert.assertEquals(cardToVerify, tagTester.get(0).getAttribute("src").replace("Thumb", ""));

		allCardsInBattlefield = persistenceService.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertTrue(allCardsInBattlefield.isEmpty());

		final List<MagicCard> allCardsInExile = persistenceService
				.getAllCardsInExileForAGameAndAPlayer(gameId, HatchetHarrySession.get().getPlayer()
						.getId(), HatchetHarrySession.get().getPlayer().getDeck().getDeckId());
		Assert.assertEquals(1, allCardsInExile.size());
		Assert.assertTrue(cardToVerify.contains(allCardsInExile.get(0).getTitle()));
	}

}
