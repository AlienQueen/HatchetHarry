package org.alienlabs.hatchetharry.serverSideTest;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.PlayerAndCard;
import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBase;
import org.alienlabs.hatchetharry.service.DataGenerator;
import org.alienlabs.hatchetharry.view.component.card.CardPanel;
import org.alienlabs.hatchetharry.view.component.card.CardRotateBehavior;
import org.alienlabs.hatchetharry.view.component.gui.ExternalImage;
import org.alienlabs.hatchetharry.view.component.gui.GraveyardComponent;
import org.alienlabs.hatchetharry.view.component.gui.HandComponent;
import org.alienlabs.hatchetharry.view.component.gui.ReorderCardInBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.component.zone.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.component.zone.PutToHandFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

/**
 * Non regression tests using the WicketTester.
 */
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
		"classpath:applicationContextTest.xml" })
public class NonRegressionTest extends SpringContextLoaderBase
{
	/**
	 * Init: we create a game, we play a card, we tap it, we put it back to hand
	 * Run: we play it again Verify: the card should be untapped.
	 * 
	 */
	@Test
	public void testWhenACardIsPlayedAndPutBackToHandAndPlayedAgainItIsUntapped() throws Exception
	{
		this.startAGameAndPlayACard();

		final Long gameId = HatchetHarrySession.get().getGameId();

		// We should have one card on the battlefield, untapped
		List<MagicCard> allCardsInBattlefield = SpringContextLoaderBase.persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());
		MagicCard card = SpringContextLoaderBase.persistenceService.getCardFromUuid(UUID
				.fromString(allCardsInBattlefield.get(0).getUuid()));
		Assert.assertFalse(card.isTapped());

		// Tap card
		SpringContextLoaderBase.tester.startPage(new HomePage(new PageParameters()));
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		final HomePage page = (HomePage)SpringContextLoaderBase.tester.getLastRenderedPage();
		SpringContextLoaderBase.tester
				.assertComponent(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:cardHandle:side:menutoggleButton",
						WebMarkupContainer.class);
		final WebMarkupContainer cardButton = (WebMarkupContainer)page
				.get("parentPlaceholder:magicCardsForSide1:1:cardPanel:cardHandle:side:menutoggleButton");
		Assert.assertNotNull(cardButton);
		final CardRotateBehavior rotateBehavior = cardButton.getBehaviors(CardRotateBehavior.class)
				.get(0);
		Assert.assertNotNull(rotateBehavior);

		SpringContextLoaderBase.tester.getRequest().setParameter("uuid", card.getUuid());
		SpringContextLoaderBase.tester.executeBehavior(rotateBehavior);

		// Assert card is tapped
		card = SpringContextLoaderBase.persistenceService.getCardFromUuid(UUID
				.fromString(allCardsInBattlefield.get(0).getUuid()));
		Assert.assertTrue(card.isTapped());

		// Put the first card back to hand
		final PutToHandFromBattlefieldBehavior putToHandFromBattlefieldBehavior = cardButton
				.getBehaviors(PutToHandFromBattlefieldBehavior.class).get(0);
		Assert.assertNotNull(putToHandFromBattlefieldBehavior);
		SpringContextLoaderBase.tester.executeBehavior(putToHandFromBattlefieldBehavior);

		// We should have no card on the battlefield
		allCardsInBattlefield = SpringContextLoaderBase.persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		// Play card again
		final PlayCardFromHandBehavior pcfhb = SpringContextLoaderBase
				.getFirstPlayCardFromHandBehavior();
		Assert.assertNotNull(pcfhb);

		SpringContextLoaderBase.tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(0).getUuid());
		SpringContextLoaderBase.tester.executeBehavior(pcfhb);

		// We should have one card on the battlefield, untapped
		allCardsInBattlefield = SpringContextLoaderBase.persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());
		Assert.assertFalse(allCardsInBattlefield.get(0).isTapped());
	}

	@Test
	public void whenCreatingAGameAndPlayingACardTheTotalNumberOfCardsInAllZonesMustBeTheNumberOfCardsInTheDeck()
			throws Exception
	{
		this.startAGameAndPlayACard();

		final Player p = SpringContextLoaderBase.persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId().longValue()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());
	}

	@Test
	public void whenCreatingAGamePlayingACardAndPlayingATokenTheNumberOfCardsInTheHandMustNotChange()
			throws Exception
	{
		this.startAGameAndPlayACard();

		// 60 cards in the deck?
		final Player p = SpringContextLoaderBase.persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId().longValue()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());

		// 6 cards in the hand?
		SpringContextLoaderBase.tester
				.assertComponent("galleryParent:gallery", HandComponent.class);
		String pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();
		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "class",
				"magicCard", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(6, tagTester.size());

		// Do they look OK?
		Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		// Is there really one card on the battlefield?
		final HomePage hp = SpringContextLoaderBase.tester.startPage(new HomePage(
				new PageParameters()));
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

		final Long gameId = HatchetHarrySession.get().getGameId();
		final List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(1, allCardsInBattlefield.size());

		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());

		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").endsWith(".jpg"));

		// Play another card
		final PlayCardFromHandBehavior pcfhb = SpringContextLoaderBase
				.getFirstPlayCardFromHandBehavior();
		Assert.assertNotNull(pcfhb);
		SpringContextLoaderBase.tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(0).getUuid());
		SpringContextLoaderBase.tester.executeBehavior(pcfhb);

		// 5 cards in the hand instead of 6
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

		SpringContextLoaderBase.tester
				.assertComponent("galleryParent:gallery", HandComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"handImagePlaceholder", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(5, tagTester.size());

		// Do they look OK?
		Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		// Are there really two cards on the battlefield?
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(2, tagTester.size());

		Assert.assertTrue(tagTester.get(0).getAttribute("src").startsWith("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").endsWith(".jpg"));
		Assert.assertTrue(tagTester.get(1).getAttribute("src").startsWith("cards/"));
		Assert.assertTrue(tagTester.get(1).getAttribute("src").endsWith(".jpg"));

		// Verify createTokenWindow
		this.openModalWindow("createTokenWindow", "createTokenLink", "topLibraryCard",
				"cards/token_medium.jpg");

		// open ModalWindow in order to play a token, fill fields and validate
		SpringContextLoaderBase.tester.assertComponent("createTokenLink", AjaxLink.class);
		SpringContextLoaderBase.tester.clickLink("createTokenLink", true);

		final FormTester createTokenForm = SpringContextLoaderBase.tester
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
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(2, tagTester.size());

		// Are there still 5 cards in the hand?
		SpringContextLoaderBase.tester.startPage(hp);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

		SpringContextLoaderBase.tester
				.assertComponent("galleryParent:gallery", HandComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"handImagePlaceholder", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(5, tagTester.size());

		// Do they still look OK?
		Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		// open ModalWindow
		SpringContextLoaderBase.tester.assertComponent("countCardsLink", AjaxLink.class);
		SpringContextLoaderBase.tester.clickLink("countCardsLink", true);

		// And now: does the "count cards" modal window display the right
		// result: 5 cards in hand, 2 on the battlefield ( + 1 token),
		// 53 in the library, 0 in
		// exile & graveyard and 60 in total (we should not count the token!)
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

		final Player player = SpringContextLoaderBase.persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId().longValue()).get(0);
		Assert.assertEquals(60, player.getDeck().getCards().size());

		// Join game
		SpringContextLoaderBase.tester.assertComponent("joinGameLink", AjaxLink.class);
		SpringContextLoaderBase.tester.clickLink("joinGameLink", true);

		final FormTester joinGameForm = SpringContextLoaderBase.tester
				.newFormTester("joinGameWindow:content:form");
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
		final DataGenerator dataGenerator = this.context.getBean(DataGenerator.class);
		dataGenerator.afterPropertiesSet();
		Assert.assertEquals(3, SpringContextLoaderBase.persistenceService
				.getAllDecksFromDeckArchives().size());

		this.startAGameAndPlayACard();
		tester.assertComponent("joinGameLink", AjaxLink.class);
		tester.clickLink("joinGameLink", true);

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		SpringContextLoaderBase.tester.assertComponent("joinGameLink", AjaxLink.class);
		SpringContextLoaderBase.tester.clickLink("joinGameLink", true);
		final TextField<String> nameTextField = (TextField<String>)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("joinGameWindow:content:form:name");
		Assert.assertNotNull(nameTextField);

		final AjaxFormComponentUpdatingBehavior nameUpdateBehavior = nameTextField.getBehaviors(
				AjaxFormComponentUpdatingBehavior.class).get(0);
		Assert.assertNotNull(nameUpdateBehavior);

		DropDownChoice<Deck> choices = (DropDownChoice<Deck>)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("joinGameWindow:content:form:deckParent:decks");
		Assert.assertEquals(3, choices.getChoices().size());

		// Run
		SpringContextLoaderBase.tester.executeBehavior(nameUpdateBehavior);

		// Verify
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		SpringContextLoaderBase.tester.assertComponent("joinGameLink", AjaxLink.class);
		SpringContextLoaderBase.tester.clickLink("joinGameLink", true);

		choices = (DropDownChoice<Deck>)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("joinGameWindow:content:form:deckParent:decks");
		Assert.assertEquals(3, choices.getChoices().size());
		Assert.assertEquals(3, SpringContextLoaderBase.persistenceService
				.getAllDecksFromDeckArchives().size());
	}

	@Test
	// TODO: test card reordering by dragging a card to the right + multiple reorderings
	public void testBattlefieldOrdersShouldBeOKAfterPuttingACardOutsideOfTheTheBattlefieldAndRearrangingCards()
			throws Exception
	{
		// Start a game and play 5 cards
		super.startAGameAndPlayACard();

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		SpringContextLoaderBase.tester.executeBehavior(SpringContextLoaderBase
				.getFirstPlayCardFromHandBehavior());

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		SpringContextLoaderBase.tester.executeBehavior(SpringContextLoaderBase
				.getFirstPlayCardFromHandBehavior());

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

		// Get names of the 5 cards, ordered by position on battlefield
		String pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();
		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(5, tagTester.size());
		final String cardBefore1 = tagTester.get(0).getAttribute("src");
		final String cardBefore2 = tagTester.get(1).getAttribute("src");
		final String cardBefore3 = tagTester.get(2).getAttribute("src");
		final String cardBefore4 = tagTester.get(3).getAttribute("src");
		final String cardBefore5 = tagTester.get(4).getAttribute("src");

		// Put the last played card to graveyard
		SpringContextLoaderBase.tester.assertComponent(
				"parentPlaceholder:magicCardsForSide1:5:cardPanel", CardPanel.class);
		final CardPanel card = (CardPanel)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCardsForSide1:5:cardPanel");
		Assert.assertNotNull(card);
		SpringContextLoaderBase.tester.executeBehavior(card
				.getPutToGraveyardFromBattlefieldBehavior());

		// Verify
		final Long gameId = HatchetHarrySession.get().getGameId();
		final List<MagicCard> allCardsInGraveyard = SpringContextLoaderBase.persistenceService
				.getAllCardsInGraveyardForAGame(gameId);
		Assert.assertTrue(allCardsInGraveyard.size() == 1);
		Assert.assertTrue(cardBefore5.contains(allCardsInGraveyard.get(0).getTitle()));

		// Put the 4th card in first position
		SpringContextLoaderBase.tester.assertComponent(
				"parentPlaceholder:magicCardsForSide1:4:cardPanel", CardPanel.class);
		final CardPanel cardToMove = (CardPanel)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:magicCardsForSide1:4:cardPanel");
		Assert.assertNotNull(card);

		SpringContextLoaderBase.tester.getRequest().setParameter(
				"uuid",
				((PlayerAndCard)cardToMove.getDefaultModelObject()).getCard().getUuidObject()
						.toString());
		SpringContextLoaderBase.tester.getRequest().setParameter("index", "0");
		SpringContextLoaderBase.tester.executeBehavior(reorder);

		// Verify
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

		final List<MagicCard> allCardsInBattlefield = SpringContextLoaderBase.persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(4, allCardsInBattlefield.size());

		final MagicCard mc = SpringContextLoaderBase.persistenceService
				.getCardFromUuid(((PlayerAndCard)cardToMove.getDefaultModelObject()).getCard()
						.getUuidObject());
		Assert.assertEquals(0, mc.getBattlefieldOrder().intValue());

		// Verify names
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "cardImage", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(4, tagTester.size());

		final String cardAfter1 = tagTester.get(0).getAttribute("src");
		final String cardAfter2 = tagTester.get(1).getAttribute("src");
		final String cardAfter3 = tagTester.get(2).getAttribute("src");
		final String cardAfter4 = tagTester.get(3).getAttribute("src");

		Assert.assertEquals(cardBefore4, cardAfter1);
		Assert.assertEquals(cardBefore1, cardAfter2);
		Assert.assertEquals(cardBefore2, cardAfter3);
		Assert.assertEquals(cardBefore3, cardAfter4);

		List<MagicCard> cards = SpringContextLoaderBase.persistenceService.getAllCardsInBattlefieldForAGame(gameId);
		Collections.sort(cards);
		Assert.assertEquals(cardBefore4.replaceAll("cards/ ", ""), cards.get(0).getBigImageFilename());
		Assert.assertEquals(cardBefore1.replaceAll("cards/ ", ""), cards.get(1).getBigImageFilename());
		Assert.assertEquals(cardBefore2.replaceAll("cards/ ", ""), cards.get(2).getBigImageFilename());
		Assert.assertEquals(cardBefore3.replaceAll("cards/ ", ""), cards.get(3).getBigImageFilename());
	}

	@Test
	public void testPlayingTokensShouldNotGiveDuplicatesInDb()
	{

	}

	@Test
	public void testPlayingATokenShouldNotImpactTheNumberOfCountedCards()
	{

	}

	@Test
	public void testDiscardingAcardAtRandomShouldRemoveACardFromHand() throws Exception
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

		// Verify that there are 4 cards in hand
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		final Long gameId = HatchetHarrySession.get().getGameId();

		List<MagicCard> allCardsInHand = persistenceService.getAllCardsInHandForAGameAndAPlayer(
				gameId, HatchetHarrySession.get().getPlayer().getId(), HatchetHarrySession.get()
						.getPlayer().getDeck().getDeckId());
		Assert.assertEquals(4, allCardsInHand.size());

		String pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();
		List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"handImagePlaceholder", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(4, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		// Verify that graveyard is empty
		SpringContextLoaderBase.tester.clickLink("graveyardLink", true);
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

		SpringContextLoaderBase.tester.assertComponent("graveyardParent:graveyard",
				GraveyardComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "thumbPlaceholder",
				false);
		Assert.assertNotNull(tagTester);
		Assert.assertTrue(tagTester.isEmpty());

		// Run: discard a card at random
		SpringContextLoaderBase.tester.clickLink("discardAtRandomLink", true);

		// Verify that there is a card in graveyard
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);
		pageDocument = SpringContextLoaderBase.tester.getLastResponse().getDocument();

		SpringContextLoaderBase.tester.assertComponent("graveyardParent:graveyard",
				GraveyardComponent.class);
		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id", "thumbPlaceholder",
				false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		final List<MagicCard> allCardsInGraveyard = persistenceService
				.getAllCardsInGraveyardForAGameAndAPlayer(gameId, HatchetHarrySession.get()
						.getPlayer().getId(), HatchetHarrySession.get().getPlayer().getDeck()
						.getDeckId());
		Assert.assertEquals(1, allCardsInGraveyard.size());

		// Verify that there are 3 cards in hand
		allCardsInHand = persistenceService.getAllCardsInHandForAGameAndAPlayer(gameId,
				HatchetHarrySession.get().getPlayer().getId(), HatchetHarrySession.get()
						.getPlayer().getDeck().getDeckId());
		Assert.assertEquals(3, allCardsInHand.size());

		tagTester = TagTester.createTagsByAttribute(pageDocument, "wicket:id",
				"handImagePlaceholder", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(3, tagTester.size());
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains("cards/"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		// Verify that the deck still contains 60 cards
		Assert.assertEquals(
				60,
				persistenceService.getAllCardsFromDeck(
						HatchetHarrySession.get().getPlayer().getDeck().getDeckArchive()
								.getDeckName()).size());
	}

	private void openModalWindow(final String _window, final String linkToActivateWindow,
			final String componentIdToCheck, final String valueToCheck)
	{
		final ModalWindow window = this.openModalWindow(_window, linkToActivateWindow);
		final ExternalImage img = (ExternalImage)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage(window.getPageRelativePath() + ":"
						+ window.getContentId() + ":" + componentIdToCheck);
		Assert.assertEquals(valueToCheck, img.getImageUrl());
	}

	private ModalWindow openModalWindow(final String _window, final String linkToActivateWindow)
	{
		// assert modal windows are in the page
		SpringContextLoaderBase.tester.assertComponent(_window, ModalWindow.class);
		final ModalWindow window = (ModalWindow)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage(_window);
		SpringContextLoaderBase.tester.assertInvisible(window.getPageRelativePath() + ":"
				+ window.getContentId());

		final AjaxLink<Void> link = (AjaxLink<Void>)SpringContextLoaderBase.tester
				.getComponentFromLastRenderedPage(linkToActivateWindow);
		Assert.assertNotNull(link);
		SpringContextLoaderBase.tester.clickLink(linkToActivateWindow, true);

		return window;
	}

	private void verifyFieldsOfCountCardsModalWindow(final int field,
			final String expectedFieldContent)
	{
		SpringContextLoaderBase.waTester.switchOffTestMode();
		final String pageDocument = SpringContextLoaderBase.waTester.getPushedResponse();

		final List<TagTester> tagTester = TagTester.createTagsByAttribute(pageDocument, "class",
				"countedCards", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(7, tagTester.size());
		Assert.assertEquals(expectedFieldContent, tagTester.get(field).getValue());
	}

}