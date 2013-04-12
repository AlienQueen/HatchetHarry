package org.alienlabs.hatchetharry.serverSideTest;

import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.CardMoveBehavior;
import org.alienlabs.hatchetharry.view.component.HandComponent;
import org.alienlabs.hatchetharry.view.component.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.component.PutToGraveyardFromBattlefieldBehavior;
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
	 * Init: we create a game, we play two cards, we move them to know positions
	 * Run: then, we put the first card to the graveyard
	 * Verify: the second card shall not have moved.
	 * 
	 */
	public void testPuttingACardToGraveyardShouldNotMoveCardsInBattlefield()
	{
		// Create game
		SpringContextLoaderBaseTest.tester.assertComponent("createGameLink", AjaxLink.class);
		SpringContextLoaderBaseTest.tester.clickLink("createGameLink", true);

		SpringContextLoaderBaseTest.pageDocument = SpringContextLoaderBaseTest.tester
				.getLastResponse().getDocument();

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
		List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		// Play two cards
		SpringContextLoaderBaseTest.tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(0).getUuid());
		SpringContextLoaderBaseTest.tester.executeBehavior(pcfhb);

		SpringContextLoaderBaseTest.tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(1).getUuid());
		SpringContextLoaderBaseTest.tester.executeBehavior(pcfhb);

		// We should have two cards on the battlefield
		// Of coordinates (100l + currentPlaceholderId*16) & (100l +
		// (currentPlaceholderId+1)*16)
		allCardsInBattlefield = persistenceService.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(2, allCardsInBattlefield.size());

		final MagicCard firstCard = persistenceService.getCardFromUuid(UUID
				.fromString(allCardsInBattlefield.get(0).getUuid()));
		Assert.assertTrue((firstCard.getX().longValue() == 116l)
				|| (firstCard.getX().longValue() == 132l) || (firstCard.getX().longValue() == 148l));
		Assert.assertTrue((firstCard.getY().longValue() == 116l)
				|| (firstCard.getY().longValue() == 132l) || (firstCard.getY().longValue() == 148l));

		final MagicCard secondCard = persistenceService.getCardFromUuid(UUID
				.fromString(allCardsInBattlefield.get(1).getUuid()));
		Assert.assertTrue((secondCard.getX().longValue() == 116l)
				|| (secondCard.getX().longValue() == 132l)
				|| (secondCard.getX().longValue() == 148l));
		Assert.assertTrue((secondCard.getY().longValue() == 116l)
				|| (secondCard.getY().longValue() == 132l)
				|| (secondCard.getY().longValue() == 148l));

		// Move the two cards
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		final HomePage hp = (HomePage)SpringContextLoaderBaseTest.tester.getLastRenderedPage();
		final WebMarkupContainer firstCardButton = (WebMarkupContainer)hp
				.get("parentPlaceholder:handCards:0:cardPanel:cardHandle:menutoggleButton");
		Assert.assertNotNull(firstCardButton);
		final CardMoveBehavior firstMoveBehavior = firstCardButton.getBehaviors(
				CardMoveBehavior.class).get(0);
		Assert.assertNotNull(firstMoveBehavior);

		final WebMarkupContainer secondCardPanelButton = (WebMarkupContainer)hp
				.get("parentPlaceholder:handCards:1:cardPanel:cardHandle:menutoggleButton");
		Assert.assertNotNull(secondCardPanelButton);
		final CardMoveBehavior secondMoveBehavior = secondCardPanelButton.getBehaviors(
				CardMoveBehavior.class).get(0);
		Assert.assertNotNull(secondMoveBehavior);

		SpringContextLoaderBaseTest.tester.getRequest().setParameter("posX", "100");
		SpringContextLoaderBaseTest.tester.getRequest().setParameter("posY", "100");
		SpringContextLoaderBaseTest.tester.executeBehavior(firstMoveBehavior);

		SpringContextLoaderBaseTest.tester.getRequest().setParameter("posX", "200");
		SpringContextLoaderBaseTest.tester.getRequest().setParameter("posY", "200");
		SpringContextLoaderBaseTest.tester.executeBehavior(secondMoveBehavior);

		final MagicCard _firstCard = persistenceService.getCardFromUuid(UUID
				.fromString(allCardsInBattlefield.get(0).getUuid()));
		Assert.assertTrue(_firstCard.getX().longValue() == 100l);
		Assert.assertTrue(_firstCard.getY().longValue() == 100l);

		MagicCard _secondCard = persistenceService.getCardFromUuid(UUID
				.fromString(allCardsInBattlefield.get(1).getUuid()));
		Assert.assertTrue((_secondCard.getX().longValue() == 200l));
		Assert.assertTrue((_secondCard.getY().longValue() == 200l));

		// Put the first card to graveyard
		final PutToGraveyardFromBattlefieldBehavior graveyardBehavior = firstCardButton
				.getBehaviors(PutToGraveyardFromBattlefieldBehavior.class).get(0);
		Assert.assertNotNull(graveyardBehavior);
		SpringContextLoaderBaseTest.tester.executeBehavior(graveyardBehavior);

		// We expect the second card not to move
		_secondCard = persistenceService.getCardFromUuid(UUID.fromString(allCardsInBattlefield.get(
				1).getUuid()));
		Assert.assertTrue((_secondCard.getX().longValue() == 200l));
		Assert.assertTrue((_secondCard.getY().longValue() == 200l));
	}
}
