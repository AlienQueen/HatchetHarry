package org.alienlabs.hatchetharry.view.component.card;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.junit.Assert;
import org.junit.Test;

public class CardMoveBehaviorTest extends SpringContextLoaderBaseTest
{
	@Test
	public void testCardMoveBehavior() throws Exception
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
		CardMoveBehavior cmb = null;

		for (final Behavior b : allCardBehaviors)
		{
			if (b instanceof CardMoveBehavior)
			{
				cmb = (CardMoveBehavior)b;
				break;
			}
		}

		Assert.assertNotNull(cmb);

		// Move the card
		super.tester.assertComponent("parentPlaceholder:magicCards:1:cardPanel", CardPanel.class);
		final CardPanel card = (CardPanel)super.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCards:1:cardPanel");
		Assert.assertNotNull(card);

		super.tester.getRequest().setParameter("card", card.getUuid().toString());
		super.tester.getRequest().setParameter("posX", "128.36");
		super.tester.getRequest().setParameter("posY", "821.63");
		super.tester.executeBehavior(cmb);

		// Verify
		super.tester.startPage(HomePage.class);
		super.tester.assertRenderedPage(HomePage.class);

		final Long gameId = HatchetHarrySession.get().getGameId();
		final List<MagicCard> allCardsInBattlefield = super.persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		final MagicCard mc = allCardsInBattlefield.get(0);
		Assert.assertEquals(128l, mc.getX().longValue());
		Assert.assertEquals(821l, mc.getY().longValue());
	}
}
