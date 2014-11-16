package org.alienlabs.hatchetharry.serverSideTest;

import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.*;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.PlayerAndCard;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.alienlabs.hatchetharry.service.DataGenerator;
import org.alienlabs.hatchetharry.service.ImportDeckService;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.card.*;
import org.alienlabs.hatchetharry.view.component.gui.*;
import org.alienlabs.hatchetharry.view.component.zone.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.component.zone.PutToHandFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.form.*;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.*;
import org.junit.*;

/**
 * Non regression tests using the WicketTester.
 */
public class NonRegressionTest extends SpringContextLoaderBaseTest
{
	@Test
	/**
	 * Init: we create a game, we play a card, we tap it, we put it back to hand
	 * Run: we play it again
	 * Verify: the card should be untapped.
	 *
	 */
	public void testWhenACardIsPlayedAndPutBackToHandAndPlayedAgainItIsUntapped() throws Exception
	{
		this.startAGameAndPlayACard();

		final Long gameId = HatchetHarrySession.get().getGameId();

		// We should have one card on the battlefield, untapped
		List<MagicCard> allCardsInBattlefield = this.persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());
		MagicCard card = this.persistenceService.getCardFromUuid(UUID
				.fromString(allCardsInBattlefield.get(0).getUuid()));
		Assert.assertFalse(card.isTapped());

		// Tap card
		this.tester.startPage(new HomePage(new PageParameters()));
		this.tester.assertRenderedPage(HomePage.class);

		final HomePage page = (HomePage)this.tester.getLastRenderedPage();
		this.tester
				.assertComponent(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:cardHandle:side:menutoggleButton",
						WebMarkupContainer.class);
		final WebMarkupContainer cardButton = (WebMarkupContainer)page
				.get("parentPlaceholder:magicCardsForSide1:1:cardPanel:cardHandle:side:menutoggleButton");
		Assert.assertNotNull(cardButton);
		final CardRotateBehavior rotateBehavior = cardButton.getBehaviors(CardRotateBehavior.class)
				.get(0);
		Assert.assertNotNull(rotateBehavior);

		this.tester.getRequest().setParameter("uuid", card.getUuid());
		this.tester.executeBehavior(rotateBehavior);

		// Assert card is tapped
		card = this.persistenceService.getCardFromUuid(UUID.fromString(allCardsInBattlefield.get(0)
				.getUuid()));
		Assert.assertTrue(card.isTapped());

		// Put the first card back to hand
		final PutToHandFromBattlefieldBehavior putToHandFromBattlefieldBehavior = cardButton
				.getBehaviors(PutToHandFromBattlefieldBehavior.class).get(0);
		Assert.assertNotNull(putToHandFromBattlefieldBehavior);
		this.tester.executeBehavior(putToHandFromBattlefieldBehavior);

		// We should have no card on the battlefield
		allCardsInBattlefield = this.persistenceService.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		// Play card again
		final PlayCardFromHandBehavior pcfhb = SpringContextLoaderBaseTest
				.getFirstPlayCardFromHandBehavior();
		Assert.assertNotNull(pcfhb);

		this.tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(0).getUuid());
		this.tester.executeBehavior(pcfhb);

		// We should have one card on the battlefield, untapped
		allCardsInBattlefield = this.persistenceService.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());
		Assert.assertFalse(allCardsInBattlefield.get(0).isTapped());
	}

	@Test
	public void whenCreatingAGameAndPlayingACardTheTotalNumberOfCardsInAllZonesMustBeTheNumberOfCardsInTheDeck()
			throws Exception
	{
		this.startAGameAndPlayACard();

		final Player p = this.persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());
	}

	@Test
	public void whenCreatingAGamePlayingACardAndPlayingATokenTheNumberOfCardsInTheHandMustNotChange()
			throws Exception
	{
		this.startAGameAndPlayACard();

		// 60 cards in the deck?
		final Player p = this.persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());

		// 6 cards in the hand?
		this.tester.assertComponent("galleryParent:gallery", HandComponent.class);
		String pageDocument = this.tester.getLastResponse().getDocument();
		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "class",
				"magicCard", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(6, tagTester.size());

		// Do they look OK?
		Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		// Is there really one card on the battlefield?
		final HomePage hp = this.tester.startPage(new HomePage(new PageParameters()));
		this.tester.assertRenderedPage(HomePage.class);
		pageDocument = this.tester.getLastResponse().getDocument();
		tagTester = TagTester.createTagsByAttribute(pageDocument, "class",
				"battlefieldCardContainer", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());

		// Play another card
		final PlayCardFromHandBehavior pcfhb = SpringContextLoaderBaseTest
				.getFirstPlayCardFromHandBehavior();
		Assert.assertNotNull(pcfhb);
		this.tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(0).getUuid());
		this.tester.executeBehavior(pcfhb);

		// 5 cards in the hand instead of 6
		this.tester.startPage(hp);
		this.tester.assertRenderedPage(HomePage.class);
		pageDocument = this.tester.getLastResponse().getDocument();
		this.tester.assertComponent("galleryParent:gallery", HandComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"handImagePlaceholder", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(5, tagTester.size());

		// Do they look OK?
		Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		// Is there really one card on the battlefield? (beware of
		// wicket-quickview)
		this.waTester.switchOnTestMode();
		pageDocument = this.tester.getLastResponse().getDocument();

		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "cardPanel", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());

		// Verify createTokenWindow
		this.openModalWindow("createTokenWindow", "createTokenLink", "topLibraryCard",
				"cards/token_medium.jpg");

		// open ModalWindow in order to play a token, fill fields and validate
		this.tester.assertComponent("createTokenLink", AjaxLink.class);
		this.tester.clickLink("createTokenLink", true);

		final FormTester createTokenForm = this.tester
				.newFormTester("createTokenWindow:content:form");
		createTokenForm.setValue("type", "Creature");
		createTokenForm.setValue("power", "7");
		createTokenForm.setValue("toughness", "7");
		createTokenForm.setValue("colors", "Green");
		createTokenForm.setValue("capabilities", "It kills you in three turns");
		createTokenForm.setValue("creatureTypes", "Lurghoyf");
		createTokenForm.setValue("description", "Help!!!");
		createTokenForm.submit();

		// Are there really 2 cards on the battlefield?
		this.waTester.switchOffTestMode();
		pageDocument = this.waTester.getPushedResponse();

		tagTester = TagTester.createTagsByAttribute(pageDocument, "class", "magicCard", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());

		// Are there still 5 cards in the hand?
		this.tester.startPage(hp);
		this.tester.assertRenderedPage(HomePage.class);
		pageDocument = this.tester.getLastResponse().getDocument();

		this.tester.assertComponent("galleryParent:gallery", HandComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"handImagePlaceholder", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(5, tagTester.size());

		// Do they still look OK?
		Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		// open ModalWindow
		this.tester.assertComponent("countCardsLink", AjaxLink.class);
		this.tester.clickLink("countCardsLink", true);

		// And now: does the "count cards" modal window display the right
		// result: 5 cards in hand, 2 on the battlefield ( + 1 token),
		// 53 in the library, 0 in
		// exile & graveyard and 60 in total (beware, there's a token!)
		this.verifyFieldsOfCountCardsModalWindow(0, "Zala");
		this.verifyFieldsOfCountCardsModalWindow(1, "5");
		this.verifyFieldsOfCountCardsModalWindow(2, "53");
		this.verifyFieldsOfCountCardsModalWindow(3, "0");
		this.verifyFieldsOfCountCardsModalWindow(4, "0");
		this.verifyFieldsOfCountCardsModalWindow(5, "2");
		this.verifyFieldsOfCountCardsModalWindow(6, "60"); // \O/
	}

	@Test
	public void whenCreatingAndJoiningAGameTheTotalNumberOfCardsInAllZonesMustBeTheNumberOfCardsInTheDeck()
			throws Exception
	{
		this.startAGameAndPlayACard();

		final Player player = this.persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId()).get(0);
		Assert.assertEquals(60, player.getDeck().getCards().size());

		// Join game
		this.tester.assertComponent("joinGameLink", AjaxLink.class);
		this.tester.clickLink("joinGameLink", true);

		final FormTester joinGameForm = this.tester.newFormTester("joinGameWindow:content:form");
		joinGameForm.setValue("name", "Zala");
		joinGameForm.setValue("sideInput", "1");
		joinGameForm.setValue("deckParent:decks", "1");
		final Long gameId = HatchetHarrySession.get().getGameId();
		joinGameForm.setValue("gameIdInput", String.valueOf(gameId));
		joinGameForm.submit();

		Assert.assertEquals(60, player.getDeck().getCards().size());
	}

	@Test
	public void testDeckListsShouldNotContainDuplicatesInModalWindows() throws Exception
	{
		// Init
		DataGenerator dataGenerator = context.getBean(DataGenerator.class);
		dataGenerator.afterPropertiesSet();
		Assert.assertEquals(3, this.persistenceService.getAllDecksFromDeckArchives().size());

		this.startAGameAndPlayACard();
		tester.assertComponent("joinGameLink", AjaxLink.class);
		tester.clickLink("joinGameLink", true);

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		this.tester.assertComponent("joinGameLink", AjaxLink.class);
		this.tester.clickLink("joinGameLink", true);
		final TextField nameTextField = (TextField)this.tester
				.getComponentFromLastRenderedPage("joinGameWindow:content:form:name");
		Assert.assertNotNull(nameTextField);

		final AjaxFormComponentUpdatingBehavior nameUpdateBehavior = nameTextField.getBehaviors(
				AjaxFormComponentUpdatingBehavior.class).get(0);
		Assert.assertNotNull(nameUpdateBehavior);

		DropDownChoice choices = (DropDownChoice)this.tester
				.getComponentFromLastRenderedPage("joinGameWindow:content:form:deckParent:decks");
		Assert.assertEquals(3, choices.getChoices().size());

		// Run
		this.tester.executeBehavior(nameUpdateBehavior);

		// Verify
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		this.tester.assertComponent("joinGameLink", AjaxLink.class);
		this.tester.clickLink("joinGameLink", true);

		choices = (DropDownChoice)this.tester
				.getComponentFromLastRenderedPage("joinGameWindow:content:form:deckParent:decks");
		Assert.assertEquals(3, choices.getChoices().size());
		Assert.assertEquals(3, this.persistenceService.getAllDecksFromDeckArchives().size());
	}

	@Test
	public void testBattlefieldOrdersShouldBeOKAfterPuttingACardOutsideOfTheTheBattlefieldAndRearrangingCards()
			throws Exception
	{
		// Start a game and play 5 cards
		super.startAGameAndPlayACard();

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.executeBehavior(SpringContextLoaderBaseTest
				.getFirstPlayCardFromHandBehavior());

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.executeBehavior(SpringContextLoaderBaseTest
				.getFirstPlayCardFromHandBehavior());

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.executeBehavior(SpringContextLoaderBaseTest
				.getFirstPlayCardFromHandBehavior());

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.executeBehavior(SpringContextLoaderBaseTest
				.getFirstPlayCardFromHandBehavior());

		// Retrieve the card and the ReorderCardInBattlefieldBehavior
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		final HomePage page = (HomePage)this.tester.getLastRenderedPage();
		this.tester.assertComponent("parentPlaceholder", WebMarkupContainer.class);
		final WebMarkupContainer parent = (WebMarkupContainer)page.get("parentPlaceholder");
		Assert.assertNotNull(parent);
		ReorderCardInBattlefieldBehavior reorder = parent.getBehaviors(
				ReorderCardInBattlefieldBehavior.class).get(0);
		Assert.assertNotNull(reorder);

		// Get names of the 5 cards, ordered by position on battlefield
		String pageDocument = this.tester.getLastResponse().getDocument();
		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(5, tagTester.size());
		String cardBefore1 = tagTester.get(0).getAttribute("src");
		String cardBefore2 = tagTester.get(1).getAttribute("src");
		String cardBefore3 = tagTester.get(2).getAttribute("src");
		String cardBefore4 = tagTester.get(3).getAttribute("src");
		String cardBefore5 = tagTester.get(4).getAttribute("src");

		// Put the last played card to graveyard
		SpringContextLoaderBaseTest.tester.assertComponent(
				"parentPlaceholder:magicCardsForSide1:5:cardPanel", CardPanel.class);
		final CardPanel card = (CardPanel)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCardsForSide1:5:cardPanel");
		Assert.assertNotNull(card);
		SpringContextLoaderBaseTest.tester.executeBehavior(card
				.getPutToGraveyardFromBattlefieldBehavior());

		// Verify
		final Long gameId = HatchetHarrySession.get().getGameId();
		List<MagicCard> allCardsInGraveyard = this.persistenceService
				.getAllCardsInGraveyardForAGame(gameId);
		Assert.assertTrue(allCardsInGraveyard.size() == 1);
		Assert.assertTrue(cardBefore5.contains(allCardsInGraveyard.get(0).getTitle()));

		// Put the 4th card in first position
		SpringContextLoaderBaseTest.tester.assertComponent(
				"parentPlaceholder:magicCardsForSide1:4:cardPanel", CardPanel.class);
		final CardPanel cardToMove = (CardPanel)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCardsForSide1:4:cardPanel");
		Assert.assertNotNull(card);

		SpringContextLoaderBaseTest.tester.getRequest().setParameter(
				"uuid",
				((PlayerAndCard)cardToMove.getDefaultModelObject()).getCard().getUuidObject()
						.toString());
		SpringContextLoaderBaseTest.tester.getRequest().setParameter("index", "0");
		SpringContextLoaderBaseTest.tester.executeBehavior(reorder);

		// Verify
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		final List<MagicCard> allCardsInBattlefield = SpringContextLoaderBaseTest.persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(4, allCardsInBattlefield.size());

		final MagicCard mc = this.persistenceService.getCardFromUuid(((PlayerAndCard)cardToMove
				.getDefaultModelObject()).getCard().getUuidObject());
		Assert.assertEquals(0, mc.getBattlefieldOrder().intValue());

		// Verify names
		pageDocument = this.tester.getLastResponse().getDocument();
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(4, tagTester.size());

		String cardAfter1 = tagTester.get(0).getAttribute("src");
		String cardAfter2 = tagTester.get(1).getAttribute("src");
		String cardAfter3 = tagTester.get(2).getAttribute("src");
		String cardAfter4 = tagTester.get(3).getAttribute("src");

		Assert.assertEquals(cardBefore4, cardAfter1);
		Assert.assertEquals(cardBefore1, cardAfter2);
		Assert.assertEquals(cardBefore2, cardAfter3);
		Assert.assertEquals(cardBefore3, cardAfter4);
	}

	@Test
	public void testPlayingTokensShouldNotGiveDuplicatesInDb()
	{

	}

	private void openModalWindow(final String _window, final String linkToActivateWindow,
			final String componentIdToCheck, final String valueToCheck)
	{
		final ModalWindow window = this.openModalWindow(_window, linkToActivateWindow);
		final ExternalImage img = (ExternalImage)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage(window.getPageRelativePath() + ":"
						+ window.getContentId() + ":" + componentIdToCheck);
		Assert.assertEquals(valueToCheck, img.getImageUrl());
	}

	private ModalWindow openModalWindow(final String _window, final String linkToActivateWindow)
	{
		// assert modal windows are in the page
		SpringContextLoaderBaseTest.tester.assertComponent(_window, ModalWindow.class);
		final ModalWindow window = (ModalWindow)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage(_window);
		SpringContextLoaderBaseTest.tester.assertInvisible(window.getPageRelativePath() + ":"
				+ window.getContentId());

		@SuppressWarnings("unchecked")
		final AjaxLink<Void> link = (AjaxLink<Void>)SpringContextLoaderBaseTest.tester
				.getComponentFromLastRenderedPage(linkToActivateWindow);
		Assert.assertNotNull(link);
		SpringContextLoaderBaseTest.tester.clickLink(linkToActivateWindow, true);

		return window;
	}

	private void verifyFieldsOfCountCardsModalWindow(final int field,
			final String expectedFieldContent)
	{
		SpringContextLoaderBaseTest.waTester.switchOffTestMode();
		final String pageDocument = SpringContextLoaderBaseTest.waTester.getPushedResponse();

		final List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "class",
				"countedCards", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(7, tagTester.size());
		Assert.assertEquals(expectedFieldContent, tagTester.get(field).getValue());
	}

}