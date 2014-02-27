package org.alienlabs.hatchetharry.view.component;

import java.lang.reflect.Method;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.channel.PutToGraveyardCometChannel;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.atmosphere.EventBusMock;
import org.apache.wicket.atmosphere.EventSubscription;
import org.apache.wicket.atmosphere.PageKey;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class CardPanelTest extends SpringContextLoaderBaseTest
{
	@Test
	public void testCardPanel() throws SecurityException, NoSuchMethodException
	{
		// Start a game and play a card
		super.startAGameAndPlayACard(SpringContextLoaderBaseTest.tester,
				SpringContextLoaderBaseTest.CLASS_PATH_XML_APPLICATION_CONTEXT);

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
				"parentPlaceholder:magicCards:1:cardPanel", CardPanel.class);
		final CardPanel card = (CardPanel)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCards:1:cardPanel");
		Assert.assertNotNull(card);

		// Test Atmosphere
		final HomePage homePage = (HomePage)SpringContextLoaderBaseTest.tester
				.getLastRenderedPage();
		final EventBusMock bus = (EventBusMock)(SpringContextLoaderBaseTest.webApp.getEventBus());
		bus.fireRegistration(bus.getResource().uuid(), homePage);
		bus.trackedPages.put(
				bus.getResource().uuid(),
				new PageKey(homePage.getPageId(), (((ServletWebRequest)homePage.getRequest())
						.getContainerRequest().getRequestedSessionId())));
		SpringContextLoaderBaseTest.webApp.resourceRegistered(bus.getResource().uuid(), homePage);
		bus.registerPage((((ServletWebRequest)homePage.getRequest()).getContainerRequest()
				.getRequestedSessionId()), homePage);
		final Method c = HomePage.class.getDeclaredMethod("removeCardFromBattlefield",
				AjaxRequestTarget.class, PutToGraveyardCometChannel.class);
		c.setAccessible(true);
		bus.register(homePage, new EventSubscription(homePage, null, c));

		final PutToGraveyardFromBattlefieldBehavior ptgfbb = card
				.getPutToGraveyardFromBattlefieldBehavior();
		SpringContextLoaderBaseTest.tester.executeBehavior(ptgfbb);

		SpringContextLoaderBaseTest.pageDocument = SpringContextLoaderBaseTest.tester
				.getLastResponse().getDocument();
		System.out.println("### " + SpringContextLoaderBaseTest.pageDocument);
		SpringContextLoaderBaseTest.tester
				.assertComponentOnAjaxResponse("graveyardParent:graveyard:graveyardCardsPlaceholder:graveyardCards:0:wrapper:graveyardImagePlaceholder");

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
