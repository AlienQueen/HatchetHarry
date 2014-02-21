package org.alienlabs.hatchetharry.view.component;

import java.util.List;
import java.util.Set;

import org.alienlabs.hatchetharry.model.Counter;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.serverSideTest.util.EventBusMock;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.atmosphere.PageKey;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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

	@Ignore("We need wicket-atmosphere to be testable for that one")
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
		final EventBusMock bus = ((EventBusMock)SpringContextLoaderBaseTest.webApp.getEventBus());
		bus.fireRegistration(bus.getResource().uuid(), homePage);
		bus.trackedPages.put(
				bus.getResource().uuid(),
				new PageKey(1, (((ServletWebRequest)homePage.getRequest()).getContainerRequest()
						.getRequestedSessionId())));
		SpringContextLoaderBaseTest.webApp.resourceRegistered(bus.getResource().uuid(), homePage);

		final FormTester addCounterForm = SpringContextLoaderBaseTest.tester
				.newFormTester("parentPlaceholder:tooltips:1:cardTooltip:counterPanel:form");
		addCounterForm.setValue("counterAddName", "quest");
		SpringContextLoaderBaseTest.tester.executeAjaxEvent(
				"parentPlaceholder:tooltips:1:cardTooltip:counterPanel:form:submit", "onclick");

		SpringContextLoaderBaseTest.pageDocument = SpringContextLoaderBaseTest.tester
				.getLastResponse().getDocument();
		System.out.println(SpringContextLoaderBaseTest.pageDocument);
		// SpringContextLoaderBaseTest.tester.debugComponentTrees();

		// Then
		// SpringContextLoaderBaseTest.tester
		// .assertComponentOnAjaxResponse("parentPlaceholder:tooltips:1:cardTooltip:counterPanel:form:counterAddName");
		SpringContextLoaderBaseTest.tester
				.assertComponentOnAjaxResponse("parentPlaceholder:tooltips:1:cardTooltip:counterPanel:form:counterAddName");

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