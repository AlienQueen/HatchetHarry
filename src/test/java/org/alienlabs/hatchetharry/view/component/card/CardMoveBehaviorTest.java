package org.alienlabs.hatchetharry.view.component.card;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.PlayerAndCard;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBase;
import org.alienlabs.hatchetharry.view.component.gui.ReorderCardInBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = { "classpath:applicationContext.xml",
		"classpath:applicationContextTest.xml" })
public class CardMoveBehaviorTest extends SpringContextLoaderBase
{
	@Test
	public void testCardMoveBehavior() throws Exception
	{
		// Start a game and play 3 cards
		super.startAGameAndPlayACard();

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		SpringContextLoaderBase.tester.executeBehavior(SpringContextLoaderBase
				.getFirstPlayCardFromHandBehavior());

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		SpringContextLoaderBase.tester.executeBehavior(SpringContextLoaderBase
				.getFirstPlayCardFromHandBehavior());

		// Retrieve the card and the ReorderCardInBattlefieldBehavior
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		final HomePage page = (HomePage)SpringContextLoaderBase.tester.getLastRenderedPage();
		SpringContextLoaderBase.tester.assertComponent("parentPlaceholder",
				WebMarkupContainer.class);
		final WebMarkupContainer parent = (WebMarkupContainer)page.get("parentPlaceholder");
		Assert.assertNotNull(parent);
		final ReorderCardInBattlefieldBehavior reorder = parent.getBehaviors(
				ReorderCardInBattlefieldBehavior.class).get(0);
		Assert.assertNotNull(reorder);

		// Get names of the three cards, ordered by position on battlefield
		String pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(3, tagTester.size());
		final String cardBefore1 = tagTester.get(0).getAttribute("src");
		final String cardBefore2 = tagTester.get(1).getAttribute("src");
		final String cardBefore3 = tagTester.get(2).getAttribute("src");

		// Move the last played card
		SpringContextLoaderBase.tester.assertComponent(
				"parentPlaceholder:magicCardsForSide1:3:cardPanel", CardPanel.class);
		final CardPanel card = (CardPanel)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCardsForSide1:3:cardPanel");
		Assert.assertNotNull(card);

		SpringContextLoaderBase.tester.getRequest().setParameter("uuid",
				((PlayerAndCard)card.getDefaultModelObject()).getCard().getUuidObject().toString());
		SpringContextLoaderBase.tester.getRequest().setParameter("index", "0");
		SpringContextLoaderBase.tester.executeBehavior(reorder);

		// Verify
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		final Long gameId = HatchetHarrySession.get().getGameId();
		final List<MagicCard> allCardsInBattlefield = SpringContextLoaderBase.persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(3, allCardsInBattlefield.size());

		final MagicCard mc = SpringContextLoaderBase.persistenceService
				.getCardFromUuid(((PlayerAndCard)card.getDefaultModelObject()).getCard()
						.getUuidObject());
		Assert.assertEquals(0, mc.getBattlefieldOrder().intValue());

		// Verify names
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(3, tagTester.size());

		final String cardAfter1 = tagTester.get(0).getAttribute("src");
		final String cardAfter2 = tagTester.get(1).getAttribute("src");
		final String cardAfter3 = tagTester.get(2).getAttribute("src");

		Assert.assertEquals(cardBefore3, cardAfter1);
		Assert.assertEquals(cardBefore1, cardAfter2);
		Assert.assertEquals(cardBefore2, cardAfter3);
	}
}
