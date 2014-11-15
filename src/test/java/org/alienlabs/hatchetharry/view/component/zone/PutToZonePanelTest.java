package org.alienlabs.hatchetharry.view.component.zone;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.card.CardPanel;
import org.alienlabs.hatchetharry.view.component.gui.ExileComponent;
import org.alienlabs.hatchetharry.view.component.gui.GraveyardComponent;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by nostromo on 15/11/14.
 */
public class PutToZonePanelTest extends SpringContextLoaderBaseTest
{

	@Test
	public void testPutToZonePanel() throws Exception
	{
		// Start a game and play a card
		super.startAGameAndPlayACard();

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		final CardPanel cardToHandle = (CardPanel)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCardsForSide1:1:cardPanel");

		// Verify
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		String pageDocument = SpringContextLoaderBaseTest.tester.getLastResponse().getDocument();
		final Long gameId = HatchetHarrySession.get().getGameId();

		// We should have one card in the battlefield
		final PersistenceService persistenceService = super.context
				.getBean(PersistenceService.class);
		List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		String cardToVerify = tagTester.get(0).getAttribute("src");

		// Verify that it is not in the hand anymore
		List<MagicCard> allCardsInHand = persistenceService.getAllCardsInHandForAGameAndAPlayer(
				gameId, HatchetHarrySession.get().getPlayer().getId(), HatchetHarrySession.get()
						.getPlayer().getDeck().getDeckId());
		Assert.assertEquals(6, allCardsInHand.size());

		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"handImagePlaceholder", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(6, tagTester.size());

		// Put it to exile
		super.tester.assertComponent("parentPlaceholder:magicCardsForSide1:1:cardPanel",
				CardPanel.class);
		final CardPanel card = (CardPanel)super.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCardsForSide1:1:cardPanel");
		Assert.assertNotNull(card);

		final PutToExileFromBattlefieldBehavior ptefbb = card
				.getPutToExileFromBattlefieldBehavior();
		Assert.assertNotNull(ptefbb);
		super.tester.executeBehavior(ptefbb);

		// Verify
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		pageDocument = this.tester.getLastResponse().getDocument();

		// Verify that is in exile
		SpringContextLoaderBaseTest.tester.assertComponent("exileParent:exile",
				ExileComponent.class);
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
		SpringContextLoaderBaseTest.tester.assertComponent("exileParent:exile:putToZonePanel",
				PutToZonePanel.class);
		PutToZonePanel putToZonePanel = (PutToZonePanel)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("exileParent:exile:putToZonePanel");
		PutToZoneBehavior ptzb = (PutToZoneBehavior)putToZonePanel.getBehaviors().get(0);
		Assert.assertNotNull(ptzb);
		SpringContextLoaderBaseTest.tester.getRequest().setParameter("card",
				cardToHandle.getUuid().toString());
		SpringContextLoaderBaseTest.tester.getRequest().setParameter("targetZone", "Graveyard");
		SpringContextLoaderBaseTest.tester.executeBehavior(ptzb);

		// Verify
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		pageDocument = this.tester.getLastResponse().getDocument();

		// Verify that it is in graveyard
		SpringContextLoaderBaseTest.tester.assertComponent("graveyardParent:graveyard",
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
		SpringContextLoaderBaseTest.tester.assertComponent("exileParent:exile",
				ExileComponent.class);
		tagTester = TagTester
				.createTagsByAttribute(pageDocument, "class", "exile-nav-thumb", false);
		Assert.assertNotNull(tagTester);
		Assert.assertTrue(tagTester.isEmpty());

		allCardsInExile = persistenceService.getAllCardsInExileForAGameAndAPlayer(gameId,
				HatchetHarrySession.get().getPlayer().getId(), HatchetHarrySession.get()
						.getPlayer().getDeck().getDeckId());
		Assert.assertTrue(allCardsInExile.isEmpty());

		// Put it back to exile
		SpringContextLoaderBaseTest.tester.assertComponent(
				"graveyardParent:graveyard:putToZonePanel", PutToZonePanel.class);
		putToZonePanel = (PutToZonePanel)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("graveyardParent:graveyard:putToZonePanel");
		ptzb = (PutToZoneBehavior)putToZonePanel.getBehaviors().get(0);
		Assert.assertNotNull(ptzb);
		SpringContextLoaderBaseTest.tester.getRequest().setParameter("card",
				cardToHandle.getUuid().toString());
		SpringContextLoaderBaseTest.tester.getRequest().setParameter("targetZone", "Exile");
		SpringContextLoaderBaseTest.tester.executeBehavior(ptzb);

		// Verify
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		pageDocument = this.tester.getLastResponse().getDocument();
		// Verify that it is in exile
		SpringContextLoaderBaseTest.tester.assertComponent("exileParent:exile",
				ExileComponent.class);
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

		SpringContextLoaderBaseTest.tester.assertComponent("graveyardParent:graveyard",
				GraveyardComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "class", "graveyard-cross-link",
				false);
		Assert.assertNotNull(tagTester);
		Assert.assertTrue(tagTester.isEmpty());

		// Put it back to hand
		SpringContextLoaderBaseTest.tester.assertComponent("exileParent:exile:putToZonePanel",
				PutToZonePanel.class);
		putToZonePanel = (PutToZonePanel)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("exileParent:exile:putToZonePanel");
		ptzb = (PutToZoneBehavior)putToZonePanel.getBehaviors().get(0);
		Assert.assertNotNull(ptzb);
		SpringContextLoaderBaseTest.tester.getRequest().setParameter("card",
				cardToHandle.getUuid().toString());
		SpringContextLoaderBaseTest.tester.getRequest().setParameter("targetZone", "Hand");
		SpringContextLoaderBaseTest.tester.executeBehavior(ptzb);

		// Verify
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		pageDocument = SpringContextLoaderBaseTest.tester.getLastResponse().getDocument();

		// Verify that it is in hand
		allCardsInHand = persistenceService.getAllCardsInHandForAGameAndAPlayer(gameId,
				HatchetHarrySession.get().getPlayer().getId(), HatchetHarrySession.get()
						.getPlayer().getDeck().getDeckId());
		Assert.assertEquals(7, allCardsInHand.size());

		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"handImagePlaceholder", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(7, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));
		Assert.assertEquals(cardToVerify, tagTester.get(0).getAttribute("src"));

		// Verify that it is not in exile anymore
		SpringContextLoaderBaseTest.tester.assertComponent("exileParent:exile",
				ExileComponent.class);
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

		SpringContextLoaderBaseTest.tester.assertComponent("graveyardParent:graveyard",
				GraveyardComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "class", "graveyard-cross-link",
				false);
		Assert.assertNotNull(tagTester);
		Assert.assertTrue(tagTester.isEmpty());
	}

}
