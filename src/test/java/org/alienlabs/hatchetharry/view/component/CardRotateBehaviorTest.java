package org.alienlabs.hatchetharry.view.component;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.junit.Assert;
import org.junit.Test;

public class CardRotateBehaviorTest extends SpringContextLoaderBaseTest
{

	@Test
	public void testCardRotateBehavior()
	{
		// Start a game and play a card
		super.startAGameAndPlayACard(SpringContextLoaderBaseTest.tester,
				SpringContextLoaderBaseTest.CLASS_PATH_XML_APPLICATION_CONTEXT);

		// Retrieve the card and the CardMoveBehavior
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		SpringContextLoaderBaseTest.tester.assertComponent(
				"parentPlaceholder:magicCards:1:cardPanel:cardHandle:menutoggleButton",
				WebMarkupContainer.class);
		final WebMarkupContainer button = (WebMarkupContainer)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCards:1:cardPanel:cardHandle:menutoggleButton");
		Assert.assertNotNull(button);
		@SuppressWarnings("unchecked")
		final List<Behavior> allCardBehaviors = (List<Behavior>)button.getBehaviors();
		CardRotateBehavior crb = null;

		for (final Behavior b : allCardBehaviors)
		{
			if (b instanceof CardRotateBehavior)
			{
				crb = (CardRotateBehavior)b;
				break;
			}
		}

		Assert.assertNotNull(crb);

		// Card default state
		MagicCard mc = CardRotateBehaviorTest.giveMagicCard();
		Assert.assertFalse(mc.isTapped());

		// Rotate the card
		CardRotateBehaviorTest.clickOnCardHandle(crb, mc);

		// Verify
		mc = CardRotateBehaviorTest.giveMagicCard();
		Assert.assertTrue(mc.isTapped());


		// Rotate the card again
		CardRotateBehaviorTest.clickOnCardHandle(crb, mc);

		// Verify again
		mc = CardRotateBehaviorTest.giveMagicCard();
		Assert.assertFalse(mc.isTapped());
	}

	private static void clickOnCardHandle(final CardRotateBehavior _crb, final MagicCard _mc)
	{
		SpringContextLoaderBaseTest.tester.getRequest().setParameter("uuid",
				_mc.getUuid().toString());
		SpringContextLoaderBaseTest.tester.executeBehavior(_crb);
	}

	private static MagicCard giveMagicCard()
	{
		final PersistenceService persistenceService = SpringContextLoaderBaseTest.context
				.getBean(PersistenceService.class);
		final Long gameId = HatchetHarrySession.get().getGameId();
		final List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		final MagicCard mc = allCardsInBattlefield.get(0);
		return mc;
	}

}
