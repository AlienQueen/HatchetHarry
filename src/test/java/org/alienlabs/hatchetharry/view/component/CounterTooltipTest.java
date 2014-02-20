package org.alienlabs.hatchetharry.view.component;

import java.util.List;
import java.util.Set;

import org.alienlabs.hatchetharry.model.Counter;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aplombee.QuickView;

public class CounterTooltipTest extends SpringContextLoaderBaseTest
{
	// private List<BigInteger> allPlayersInGame;
	private Long gameId;
	private List<MagicCard> allCardsInBattleField;

	@Before
	// Start a game and play a card
	public void startAGameAndPlayACard()
	{
		this.gameId = super.startAGameAndPlayACard(SpringContextLoaderBaseTest.tester,
				SpringContextLoaderBaseTest.CLASS_PATH_XML_APPLICATION_CONTEXT);

		// this.allPlayersInGame =
		// SpringContextLoaderBaseTest.persistenceService
		// .giveAllPlayersFromGame(this.gameId);

		this.allCardsInBattleField = SpringContextLoaderBaseTest.persistenceService
				.getAllCardsInBattleFieldForAGame(this.gameId);
	}

	@Test
	public void testAddCounter()
	{
		// Given
		Assert.assertEquals(1, this.allCardsInBattleField.size());

		MagicCard firstCard = this.allCardsInBattleField.get(0);
		Assert.assertNotNull(firstCard);

		Set<Counter> allCountersInFirstCard = firstCard.getCounters();
		Assert.assertNotNull(allCountersInFirstCard);
		Assert.assertFalse(allCountersInFirstCard.iterator().hasNext());

		// We put the card on the battlefield with its tooltip
		final HomePage homePage = (HomePage)SpringContextLoaderBaseTest.tester
				.getLastRenderedPage();
		final QuickView<MagicCard> magicCardList = homePage.getAllCardsInBattlefield();
		final QuickView<MagicCard> tooltipList = homePage.getAllTooltips();
		homePage.getAllMagicCardsInBattlefield().add(firstCard);
		magicCardList.addNewItems(firstCard);
		homePage.getAllTooltipsInBattlefield().add(firstCard);
		tooltipList.addNewItems(firstCard);

		// When
		final FormTester addCounterForm = SpringContextLoaderBaseTest.tester
				.newFormTester("parentPlaceholder:tooltips:1:cardTooltip:counterPanel:form");
		addCounterForm.setValue("counterAddName", "quest");
		SpringContextLoaderBaseTest.tester.executeAjaxEvent(
				"parentPlaceholder:tooltips:1:cardTooltip:counterPanel:form:submit", "onclick");

		// Then
		this.allCardsInBattleField = SpringContextLoaderBaseTest.persistenceService
				.getAllCardsInBattleFieldForAGame(this.gameId);
		Assert.assertEquals(1, this.allCardsInBattleField.size());

		firstCard = this.allCardsInBattleField.get(0);
		Assert.assertNotNull(firstCard);

		allCountersInFirstCard = firstCard.getCounters();
		Assert.assertNotNull(allCountersInFirstCard);
		Assert.assertTrue(allCountersInFirstCard.iterator().hasNext());
		final Counter firstCounterInFirstCard = allCountersInFirstCard.iterator().next();
		Assert.assertEquals("quest", firstCounterInFirstCard.getCounterName());

		@SuppressWarnings("rawtypes")
		final TextField counterAddName = (TextField)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:tooltips:1:cardTooltip:counterPanel:form:counterAddName");
		Assert.assertEquals("", counterAddName.getDefaultModelObjectAsString());
	}
}
