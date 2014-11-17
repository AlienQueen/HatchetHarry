package org.alienlabs.hatchetharry.view.component.card;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.PlayerAndCard;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTests;
import org.alienlabs.hatchetharry.view.component.gui.ReorderCardInBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = { "classpath:applicationContext.xml",
		"classpath:applicationContextTest.xml" })
public class CardMoveBehaviorTests extends SpringContextLoaderBaseTests
{
	@Test
	public void testCardMoveBehavior() throws Exception
	{
		// Start a game and play 3 cards
		super.startAGameAndPlayACard();

		SpringContextLoaderBaseTests.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTests.tester.assertRenderedPage(HomePage.class);
		SpringContextLoaderBaseTests.tester.executeBehavior(SpringContextLoaderBaseTests
				.getFirstPlayCardFromHandBehavior());

		SpringContextLoaderBaseTests.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTests.tester.assertRenderedPage(HomePage.class);
		SpringContextLoaderBaseTests.tester.executeBehavior(SpringContextLoaderBaseTests
				.getFirstPlayCardFromHandBehavior());

		// Retrieve the card and the ReorderCardInBattlefieldBehavior
		SpringContextLoaderBaseTests.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTests.tester.assertRenderedPage(HomePage.class);

		final HomePage page = (HomePage)this.tester.getLastRenderedPage();
		this.tester.assertComponent("parentPlaceholder", WebMarkupContainer.class);
		final WebMarkupContainer parent = (WebMarkupContainer)page.get("parentPlaceholder");
		Assert.assertNotNull(parent);
		ReorderCardInBattlefieldBehavior reorder = parent.getBehaviors(
				ReorderCardInBattlefieldBehavior.class).get(0);
		Assert.assertNotNull(reorder);

		// Get names of the three cards, ordered by position on battlefield
		String pageDocument = this.tester.getLastResponse().getDocument();

		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(3, tagTester.size());
		String cardBefore1 = tagTester.get(0).getAttribute("src");
		String cardBefore2 = tagTester.get(1).getAttribute("src");
		String cardBefore3 = tagTester.get(2).getAttribute("src");

		// Move the last played card
		SpringContextLoaderBaseTests.tester.assertComponent(
				"parentPlaceholder:magicCardsForSide1:3:cardPanel", CardPanel.class);
		final CardPanel card = (CardPanel)SpringContextLoaderBaseTests.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCardsForSide1:3:cardPanel");
		Assert.assertNotNull(card);

		SpringContextLoaderBaseTests.tester.getRequest().setParameter("uuid",
				((PlayerAndCard)card.getDefaultModelObject()).getCard().getUuidObject().toString());
		SpringContextLoaderBaseTests.tester.getRequest().setParameter("index", "0");
		SpringContextLoaderBaseTests.tester.executeBehavior(reorder);

		// Verify
		SpringContextLoaderBaseTests.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTests.tester.assertRenderedPage(HomePage.class);

		final Long gameId = HatchetHarrySession.get().getGameId();
		final List<MagicCard> allCardsInBattlefield = SpringContextLoaderBaseTests.persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(3, allCardsInBattlefield.size());

		final MagicCard mc = this.persistenceService.getCardFromUuid(((PlayerAndCard)card
				.getDefaultModelObject()).getCard().getUuidObject());
		Assert.assertEquals(0, mc.getBattlefieldOrder().intValue());

		// Verify names
		pageDocument = this.tester.getLastResponse().getDocument();
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(3, tagTester.size());

		String cardAfter1 = tagTester.get(0).getAttribute("src");
		String cardAfter2 = tagTester.get(1).getAttribute("src");
		String cardAfter3 = tagTester.get(2).getAttribute("src");

		Assert.assertEquals(cardBefore3, cardAfter1);
		Assert.assertEquals(cardBefore1, cardAfter2);
		Assert.assertEquals(cardBefore2, cardAfter3);
	}
}
