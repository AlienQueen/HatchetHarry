package org.alienlabs.hatchetharry.view.component.zone;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.PlayerAndCard;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBase;
import org.alienlabs.hatchetharry.view.component.card.CardPanel;
import org.alienlabs.hatchetharry.view.component.gui.ExileComponent;
import org.alienlabs.hatchetharry.view.component.gui.GraveyardComponent;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by nostromo on 15/11/14.
 */
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
		"classpath:applicationContextTest.xml" })
public class PutToZonePanelTest extends SpringContextLoaderBase
{
	@Test
	public void testPutToZonePanel() throws Exception
	{
		// Start a game and play a card
		super.startAGameAndPlayACard();

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		final CardPanel cardToHandle = (CardPanel)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCardsForSide1:1:cardPanel");

		// Verify
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		String pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();
		final Long gameId = HatchetHarrySession.get().getGameId();

		// We should have one card in the battlefield
		List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		final String cardToVerify = tagTester.get(0).getAttribute("src");

		// Verify that it is not in the hand anymore
		List<MagicCard> allCardsInHand = persistenceService.getAllCardsInHandForAGameAndADeck(
				gameId, HatchetHarrySession.get().getPlayer().getDeck().getDeckId());
		Assert.assertEquals(6, allCardsInHand.size());

		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"handImagePlaceholder", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(6, tagTester.size());

		// Put it to exile
		SpringContextLoaderBase.tester.assertComponent(
				"parentPlaceholder:magicCardsForSide1:1:cardPanel", CardPanel.class);
		final CardPanel card = (CardPanel)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCardsForSide1:1:cardPanel");
		Assert.assertNotNull(card);

		final PutToExileFromBattlefieldBehavior ptefbb = card
				.getPutToExileFromBattlefieldBehavior();
		Assert.assertNotNull(ptefbb);
		SpringContextLoaderBase.tester.executeBehavior(ptefbb);

		// Verify
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

		// Verify that is in exile
		SpringContextLoaderBase.tester.assertComponent("exileParent:exile", ExileComponent.class);
		tagTester = TagTester
				.createTagsByAttribute(pageDocument, "class", "exile-nav-thumb", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));
		Assert.assertEquals(cardToVerify, tagTester.get(0).getAttribute("src").replace("Thumb", ""));

		List<MagicCard> allCardsInExile = persistenceService.getAllCardsInExileForAGameAndAPlayer(
				gameId, HatchetHarrySession.get().getPlayer().getId(), HatchetHarrySession.get()
						.getPlayer().getDeck().getDeckId());
		Assert.assertEquals(1, allCardsInExile.size());
		Assert.assertTrue(cardToVerify.contains(allCardsInExile.get(0).getTitle()));

		// Verify that it is not in battlefield anymore
		allCardsInBattlefield = persistenceService.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertTrue(allCardsInBattlefield.isEmpty());

		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertTrue(tagTester.isEmpty());

		// Put it to graveyard
		SpringContextLoaderBase.tester.assertComponent("exileParent:exile:putToZonePanel",
				PutToZonePanel.class);
		PutToZonePanel putToZonePanel = (PutToZonePanel)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("exileParent:exile:putToZonePanel");
		PutToZoneBehavior ptzb = (PutToZoneBehavior)putToZonePanel.getBehaviors().get(0);
		Assert.assertNotNull(ptzb);
		SpringContextLoaderBase.tester.getRequest().setParameter(
				"card",
				((PlayerAndCard)cardToHandle.getDefaultModelObject()).getCard().getUuidObject()
						.toString());
		SpringContextLoaderBase.tester.getRequest().setParameter("targetZone", "Graveyard");
		SpringContextLoaderBase.tester.executeBehavior(ptzb);

		// Verify
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

		// Verify that it is in graveyard
		SpringContextLoaderBase.tester.assertComponent("graveyardParent:graveyard",
				GraveyardComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "thumbPlaceholder",
				false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));
		Assert.assertEquals(cardToVerify, tagTester.get(0).getAttribute("src").replace("Thumb", ""));

		List<MagicCard> allCardsInGraveyard = persistenceService
				.getAllCardsInGraveyardForAGame(gameId);
		Assert.assertEquals(1, allCardsInGraveyard.size());
		Assert.assertTrue(cardToVerify.contains(allCardsInGraveyard.get(0).getTitle()));

		// Verify that it is not in exile anymore
		SpringContextLoaderBase.tester.assertComponent("exileParent:exile", ExileComponent.class);
		tagTester = TagTester
				.createTagsByAttribute(pageDocument, "class", "exile-nav-thumb", false);
		Assert.assertNotNull(tagTester);
		Assert.assertTrue(tagTester.isEmpty());

		allCardsInExile = persistenceService.getAllCardsInExileForAGameAndAPlayer(gameId,
				HatchetHarrySession.get().getPlayer().getId(), HatchetHarrySession.get()
						.getPlayer().getDeck().getDeckId());
		Assert.assertTrue(allCardsInExile.isEmpty());

		// Put it back to exile
		SpringContextLoaderBase.tester.assertComponent("graveyardParent:graveyard:putToZonePanel",
				PutToZonePanel.class);
		putToZonePanel = (PutToZonePanel)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("graveyardParent:graveyard:putToZonePanel");
		ptzb = (PutToZoneBehavior)putToZonePanel.getBehaviors().get(0);
		Assert.assertNotNull(ptzb);
		SpringContextLoaderBase.tester.getRequest().setParameter(
				"card",
				((PlayerAndCard)cardToHandle.getDefaultModelObject()).getCard().getUuidObject()
						.toString());
		SpringContextLoaderBase.tester.getRequest().setParameter("targetZone", "Exile");
		SpringContextLoaderBase.tester.executeBehavior(ptzb);

		// Verify
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();
		// Verify that it is in exile
		SpringContextLoaderBase.tester.assertComponent("exileParent:exile", ExileComponent.class);
		tagTester = TagTester
				.createTagsByAttribute(pageDocument, "class", "exile-nav-thumb", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));
		Assert.assertEquals(cardToVerify, tagTester.get(0).getAttribute("src").replace("Thumb", ""));

		// Verify that it is not in battlefield anymore
		allCardsInBattlefield = persistenceService.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertTrue(allCardsInBattlefield.isEmpty());

		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertTrue(tagTester.isEmpty());

		// Verify that it is not in graveyard anymore
		allCardsInGraveyard = persistenceService.getAllCardsInGraveyardForAGame(gameId);
		Assert.assertTrue(allCardsInGraveyard.isEmpty());

		SpringContextLoaderBase.tester.assertComponent("graveyardParent:graveyard",
				GraveyardComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "class", "graveyard-cross-link",
				false);
		Assert.assertNotNull(tagTester);
		Assert.assertTrue(tagTester.isEmpty());

		// Put it back to hand
		SpringContextLoaderBase.tester.assertComponent("exileParent:exile:putToZonePanel",
				PutToZonePanel.class);
		putToZonePanel = (PutToZonePanel)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("exileParent:exile:putToZonePanel");
		ptzb = (PutToZoneBehavior)putToZonePanel.getBehaviors().get(0);
		Assert.assertNotNull(ptzb);
		SpringContextLoaderBase.tester.getRequest().setParameter(
				"card",
				((PlayerAndCard)cardToHandle.getDefaultModelObject()).getCard().getUuidObject()
						.toString());
		SpringContextLoaderBase.tester.getRequest().setParameter("targetZone", "Hand");
		SpringContextLoaderBase.tester.executeBehavior(ptzb);

		// Verify
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

		// Verify that it is in hand
		allCardsInHand = persistenceService.getAllCardsInHandForAGameAndADeck(gameId,
				HatchetHarrySession.get().getPlayer().getDeck().getDeckId());
		Assert.assertEquals(7, allCardsInHand.size());

		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"handImagePlaceholder", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(7, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));
		Assert.assertEquals(cardToVerify, tagTester.get(0).getAttribute("src"));

		// Verify that it is not in exile anymore
		SpringContextLoaderBase.tester.assertComponent("exileParent:exile", ExileComponent.class);
		tagTester = TagTester
				.createTagsByAttribute(pageDocument, "class", "exile-nav-thumb", false);
		Assert.assertNotNull(tagTester);
		Assert.assertTrue(tagTester.isEmpty());

		allCardsInExile = persistenceService.getAllCardsInExileForAGameAndAPlayer(gameId,
				HatchetHarrySession.get().getPlayer().getId(), HatchetHarrySession.get()
						.getPlayer().getDeck().getDeckId());
		Assert.assertTrue(allCardsInExile.isEmpty());

		// Verify that it is not in graveyard anymore
		allCardsInGraveyard = persistenceService.getAllCardsInGraveyardForAGame(gameId);
		Assert.assertTrue(allCardsInGraveyard.isEmpty());

		SpringContextLoaderBase.tester.assertComponent("graveyardParent:graveyard",
				GraveyardComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "class", "graveyard-cross-link",
				false);
		Assert.assertNotNull(tagTester);
		Assert.assertTrue(tagTester.isEmpty());
	}

}
