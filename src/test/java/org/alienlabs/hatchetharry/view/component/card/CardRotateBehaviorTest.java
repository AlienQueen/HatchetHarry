package org.alienlabs.hatchetharry.view.component.card;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.card.CardRotateBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.junit.Assert;
import org.junit.Test;

public class CardRotateBehaviorTest extends SpringContextLoaderBaseTest
{

	private void clickOnCardHandle(final CardRotateBehavior _crb, final MagicCard _mc)
	{
		super.tester.getRequest().setParameter("uuid", _mc.getUuid().toString());
		super.tester.executeBehavior(_crb);
	}

	private MagicCard giveMagicCard()
	{
		final PersistenceService persistenceService = super.context
				.getBean(PersistenceService.class);
		final Long gameId = HatchetHarrySession.get().getGameId();
		final List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		final MagicCard mc = allCardsInBattlefield.get(0);
		return mc;
	}

	@Test
	public void testCardRotateBehavior()
	{
		// Start a game and play a card
        super.startAGameAndPlayACard();

		// Retrieve the card and the CardMoveBehavior
		super.tester.startPage(HomePage.class);
		super.tester.assertRenderedPage(HomePage.class);

		super.tester.assertComponent(
				"parentPlaceholder:magicCards:1:cardPanel:cardHandle:menutoggleButton",
				WebMarkupContainer.class);
		final WebMarkupContainer button = (WebMarkupContainer)super.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCards:1:cardPanel:cardHandle:menutoggleButton");
		Assert.assertNotNull(button);
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
		MagicCard mc = this.giveMagicCard();
		Assert.assertFalse(mc.isTapped());

		// Rotate the card
		this.clickOnCardHandle(crb, mc);

		// Verify
		mc = this.giveMagicCard();
		Assert.assertTrue(mc.isTapped());


		// Rotate the card again
		this.clickOnCardHandle(crb, mc);

		// Verify again
		mc = this.giveMagicCard();
		Assert.assertFalse(mc.isTapped());
	}

}
