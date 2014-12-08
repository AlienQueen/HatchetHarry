package org.alienlabs.hatchetharry.view.component.card;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.serversidetest.util.SpringContextLoaderBase;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = { "classpath:applicationContext.xml",
		"classpath:applicationContextTest.xml" })
public class CardRotateBehaviorTest extends SpringContextLoaderBase
{

	private void clickOnCardHandle(final CardRotateBehavior _crb, final MagicCard _mc)
	{
		SpringContextLoaderBase.tester.getRequest().setParameter("uuid", _mc.getUuid().toString());
		SpringContextLoaderBase.tester.executeBehavior(_crb);
	}

	private MagicCard giveMagicCard()
	{
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
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		final HomePage page = (HomePage)SpringContextLoaderBase.tester.getLastRenderedPage();

		SpringContextLoaderBase.tester
				.assertComponent(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:cardHandle:side:menutoggleButton",
						WebMarkupContainer.class);
		final WebMarkupContainer cardButton = (WebMarkupContainer)page
				.get("parentPlaceholder:magicCardsForSide1:1:cardPanel:cardHandle:side:menutoggleButton");
		Assert.assertNotNull(cardButton);
		final CardRotateBehavior crb = cardButton.getBehaviors(CardRotateBehavior.class).get(0);
		Assert.assertNotNull(crb);

		// Card default state
		MagicCard mc = this.giveMagicCard();
		Assert.assertFalse(mc.isTapped());
		String pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "class",
				"cardContainer tapped", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(0, tagTester.size());

		// Rotate the card
		this.clickOnCardHandle(crb, mc);
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Verify
		mc = this.giveMagicCard();
		Assert.assertTrue(mc.isTapped());

		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();
		tagTester = TagTester.createTagsByAttribute(pageDocument, "class", "cardContainer tapped",
				false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());

		// Rotate the card again
		this.clickOnCardHandle(crb, mc);

		// Verify again
		mc = this.giveMagicCard();
		Assert.assertFalse(mc.isTapped());

		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();
		tagTester = TagTester.createTagsByAttribute(pageDocument, "class", "cardContainer tapped",
				false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(0, tagTester.size());

	}

}
