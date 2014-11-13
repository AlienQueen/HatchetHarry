package org.alienlabs.hatchetharry.view.component.card;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.card.CardRotateBehavior;
import org.alienlabs.hatchetharry.view.component.zone.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.util.tester.TagTester;
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
	public void testCardRotateBehavior() throws Exception
	{
		// Start a game and play a card
		super.startAGameAndPlayACard();

		// Retrieve the card and the CardMoveBehavior
		super.tester.startPage(HomePage.class);
		super.tester.assertRenderedPage(HomePage.class);
		final HomePage page = (HomePage)this.tester.getLastRenderedPage();

		this.tester.assertComponent("parentPlaceholder:magicCardsForSide1:1:cardPanel:cardHandle:side:menutoggleButton", WebMarkupContainer.class);
		final WebMarkupContainer cardButton = (WebMarkupContainer)page.get("parentPlaceholder:magicCardsForSide1:1:cardPanel:cardHandle:side:menutoggleButton");
		Assert.assertNotNull(cardButton);
		CardRotateBehavior crb = (CardRotateBehavior)cardButton.getBehaviors(CardRotateBehavior.class).get(0);
		Assert.assertNotNull(crb);

		// Card default state
		MagicCard mc = this.giveMagicCard();
		Assert.assertFalse(mc.isTapped());
		String pageDocument = this.tester.getLastResponse().getDocument();

		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "class",
				"cardContainer tapped", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(0, tagTester.size());

		// Rotate the card
		this.clickOnCardHandle(crb, mc);
		super.tester.startPage(HomePage.class);
		super.tester.assertRenderedPage(HomePage.class);

		// Verify
		mc = this.giveMagicCard();
		Assert.assertTrue(mc.isTapped());

		pageDocument = this.tester.getLastResponse().getDocument();
		tagTester = TagTester.createTagsByAttribute(pageDocument, "class",
				"cardContainer tapped", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());

		// Rotate the card again
		this.clickOnCardHandle(crb, mc);

		// Verify again
		mc = this.giveMagicCard();
		Assert.assertFalse(mc.isTapped());

		pageDocument = this.tester.getLastResponse().getDocument();
		tagTester = TagTester.createTagsByAttribute(pageDocument, "class",
				"cardContainer tapped", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(0, tagTester.size());

	}

}
