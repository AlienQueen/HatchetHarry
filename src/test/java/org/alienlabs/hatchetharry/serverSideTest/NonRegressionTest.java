package org.alienlabs.hatchetharry.serverSideTest;

import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.channel.PlayCardFromHandCometChannel;
import org.alienlabs.hatchetharry.serverSideTest.util.EventBusMock;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.CardRotateBehavior;
import org.alienlabs.hatchetharry.view.component.HandComponent;
import org.alienlabs.hatchetharry.view.component.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.component.PutToHandFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Assert;
import org.junit.Test;

/**
 * Non regression tests using the WicketTester.
 */
public class NonRegressionTest extends SpringContextLoaderBaseTest
{
	@Test
	public void testDrawingACardShouldRaiseTheNumberOfCardsInHandToHeight()
	{
		// Init
		// assert hand is present
		SpringContextLoaderBaseTest.tester.assertComponent("galleryParent:gallery",
				HandComponent.class);

		// assert URL of a thumbnail
		List<TagTester> tagTester = TagTester.createTagsByAttribute(
				SpringContextLoaderBaseTest.pageDocument, "class", "nav-thumb", false);
		Assert.assertNotNull(tagTester);

		// assert number of thumbnails
		Assert.assertEquals(7, tagTester.size());

		Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		// Run
		SpringContextLoaderBaseTest.tester.assertComponent("drawCardLink", AjaxLink.class);
		SpringContextLoaderBaseTest.tester.clickLink("drawCardLink", true);

		// Verify
		SpringContextLoaderBaseTest.pageDocument = SpringContextLoaderBaseTest.tester
				.getLastResponse().getDocument();
		final String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ajax-response><component id=\"galleryParent\" ><![CDATA[<span id=\"galleryParent\">";
		final int toRemoveHeader = SpringContextLoaderBaseTest.pageDocument.indexOf(header);
		SpringContextLoaderBaseTest.pageDocument = SpringContextLoaderBaseTest.pageDocument
				.substring(toRemoveHeader + header.length());
		final int toRemoveFooter = SpringContextLoaderBaseTest.pageDocument
				.indexOf("]]></component>");
		SpringContextLoaderBaseTest.pageDocument = SpringContextLoaderBaseTest.pageDocument
				.substring(0, toRemoveFooter);

		tagTester = TagTester.createTagsByAttribute(SpringContextLoaderBaseTest.pageDocument,
				"class", "nav-thumb", false);
		Assert.assertNotNull(tagTester);

		// assert number of thumbnails
		Assert.assertEquals(8, tagTester.size());

		Assert.assertNotNull(tagTester.get(3).getAttribute("src"));
		Assert.assertTrue(tagTester.get(3).getAttribute("src").contains(".jpg"));
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
		PlayCardFromHandBehavior pcfhb = (PlayCardFromHandBehavior)playCardLink.getBehaviors().get(
				0);

		// For the moment, we should have no card in the battlefield
		final Long gameId = HatchetHarrySession.get().getGameId();
		final PersistenceService persistenceService = SpringContextLoaderBaseTest.context
				.getBean(PersistenceService.class);
		List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		// Play a card
		SpringContextLoaderBaseTest.tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(0).getUuid());
		SpringContextLoaderBaseTest.tester.executeBehavior(pcfhb);

		// We should have one card on the battlefield, untapped
		allCardsInBattlefield = persistenceService.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());
		MagicCard card = persistenceService.getCardFromUuid(UUID.fromString(allCardsInBattlefield
				.get(0).getUuid()));
		Assert.assertFalse(card.isTapped());

		// Tap card
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		final HomePage hp = (HomePage)SpringContextLoaderBaseTest.tester.getLastRenderedPage();
		final WebMarkupContainer cardButton = (WebMarkupContainer)hp
				.get("parentPlaceholder:handCards:0:cardPanel:cardHandle:menutoggleButton");
		Assert.assertNotNull(cardButton);
		final CardRotateBehavior rotateBehavior = cardButton.getBehaviors(CardRotateBehavior.class)
				.get(0);
		Assert.assertNotNull(rotateBehavior);

		SpringContextLoaderBaseTest.tester.getRequest().setParameter("uuid", card.getUuid());
		SpringContextLoaderBaseTest.tester.executeBehavior(rotateBehavior);

		// Assert card is tapped
		card = persistenceService.getCardFromUuid(UUID.fromString(allCardsInBattlefield.get(0)
				.getUuid()));
		Assert.assertTrue(card.isTapped());

		// Put the first card back to hand
		final PutToHandFromBattlefieldBehavior putToHandFromBattlefieldBehavior = cardButton
				.getBehaviors(PutToHandFromBattlefieldBehavior.class).get(0);
		Assert.assertNotNull(putToHandFromBattlefieldBehavior);
		SpringContextLoaderBaseTest.tester.executeBehavior(putToHandFromBattlefieldBehavior);

		// We should have no card on the battlefield
		allCardsInBattlefield = persistenceService.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		// Play card again
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		pcfhb = (PlayCardFromHandBehavior)playCardLink.getBehaviors().get(0);
		Assert.assertNotNull(pcfhb);

		SpringContextLoaderBaseTest.tester.getRequest().setParameter("card", card.getUuid());
		SpringContextLoaderBaseTest.tester.executeBehavior(pcfhb);

		// We should have one card on the battlefield, untapped
		allCardsInBattlefield = persistenceService.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());
		final List<Object> allMessages = EventBusMock.getMessages();
		System.out.println(allMessages);
		Assert.assertFalse(persistenceService.getCardFromUuid(
				((PlayCardFromHandCometChannel)allMessages.get(7)).getUuidToLookFor()).isTapped());
	}
}
