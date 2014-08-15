package org.alienlabs.hatchetharry.view.component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.CollectibleCard;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.Side;
import org.alienlabs.hatchetharry.model.channel.AddSideCometChannel;
import org.alienlabs.hatchetharry.model.channel.ConsoleLogCometChannel;
import org.alienlabs.hatchetharry.model.channel.consolelog.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogType;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.JavaScriptUtils;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class CreateGameModalWindow extends Panel {
	private static final long serialVersionUID = -5432292812819537705L;

	@SpringBean
	PersistenceService persistenceService;

	static final Logger LOGGER = LoggerFactory.getLogger(CreateGameModalWindow.class);

	Player player;
	final Game game;
	final WebMarkupContainer sidePlaceholderParent;

	final HomePage homePage;

	WebMarkupContainer deckParent;
	DropDownChoice<Deck> decks;

	final TextField<String> nameInput;

	final DropDownChoice<String> sideInput;

	final ModalWindow modal;

	public CreateGameModalWindow(final ModalWindow _modal, final String id, final Player _player,
								 final WebMarkupContainer _sidePlaceholderParent, final HomePage hp) {
		super(id);
		Injector.get().inject(this);

		this.modal = _modal;
		this.player = _player;
		this.sidePlaceholderParent = _sidePlaceholderParent;
		this.homePage = hp;

		final Form<String> form = new Form<String>("form");

		final ArrayList<String> allSides = new ArrayList<String>();
		allSides.add("infrared");
		allSides.add("ultraviolet");
		final Model<ArrayList<String>> sidesModel = new Model<ArrayList<String>>(allSides);
		final Label sideLabel = new Label("sideLabel", "Choose your side: ");
		this.sideInput = new DropDownChoice<String>("sideInput", new Model<String>(), sidesModel);
		this.sideInput.setOutputMarkupId(true).setMarkupId("sideInput");

		final Label nameLabel = new Label("nameLabel", "Choose a name: ");
		final Model<String> nameModel = new Model<String>("");
		this.nameInput = new TextField<String>("name", nameModel);
		this.nameInput.setOutputMarkupId(true).setMarkupId("name");

		this.nameInput.add(new AjaxFormComponentUpdatingBehavior("onfocus") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				if ((null != target) && (null == CreateGameModalWindow.this.decks.getModelObject())) {
					final ArrayList<Deck> _allDecks = (ArrayList<Deck>) CreateGameModalWindow.this.persistenceService
																				.getAllDecksFromDeckArchives();
					final Model<ArrayList<Deck>> _decksModel = new Model<ArrayList<Deck>>(_allDecks);
					CreateGameModalWindow.this.decks = new DropDownChoice<Deck>("decks",
																					   new Model<Deck>(), _decksModel);
					CreateGameModalWindow.this.decks.setOutputMarkupId(true).setMarkupId("decks");

					CreateGameModalWindow.this.deckParent
							.addOrReplace(CreateGameModalWindow.this.decks);
					target.add(CreateGameModalWindow.this.deckParent);
				}
			}
		});

		final Label chooseDeck = new Label("chooseDeck", "Choose a deck: ");

		final ArrayList<Deck> allDecks = (ArrayList<Deck>) this.persistenceService
																   .getAllDecksFromDeckArchives();
		final Model<ArrayList<Deck>> decksModel = new Model<ArrayList<Deck>>(allDecks);

		this.deckParent = new WebMarkupContainer("deckParent");
		this.deckParent.setOutputMarkupId(true);
		this.decks = new DropDownChoice<Deck>("decks", new Model<Deck>(), decksModel);
		this.decks.setOutputMarkupId(true).setMarkupId("decks");
		this.deckParent.add(this.decks);

		this.game = this.player.getGame();
		final Label beforeGameId = new Label("beforeGameId", "The id of this game is: ");

		final Label gameId = new Label("gameId", this.game.getId());
		gameId.setOutputMarkupId(true).setMarkupId("gameId");

		final Label afterGameId = new Label("afterGameId",
												   ". You'll have to provide it to your opponent(s).");
		HatchetHarrySession.get().setGameId(this.game.getId());


		final IndicatingAjaxButton submit = new IndicatingAjaxButton("submit", form) {
			private static final long serialVersionUID = 5612763286127668L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form) {
				if ((null == CreateGameModalWindow.this.nameInput.getModelObject())
							|| ("".equals(CreateGameModalWindow.this.nameInput.getModelObject().trim()))
							|| (null == CreateGameModalWindow.this.decks.getModelObject())
							|| (null == CreateGameModalWindow.this.sideInput
												.getDefaultModelObjectAsString())) {
					return;
				}

				CreateGameModalWindow.this.modal.close(target);

				final Game g = CreateGameModalWindow.this.persistenceService
									   .getGame(CreateGameModalWindow.this.game.getId());

				CreateGameModalWindow.this.player.setGame(g);

				CreateGameModalWindow.this.player.getSide().setSideName(
																			   CreateGameModalWindow.this.sideInput.getDefaultModelObjectAsString());
				CreateGameModalWindow.this.player.setName(CreateGameModalWindow.this.nameInput
																  .getDefaultModelObjectAsString());

				CreateGameModalWindow.this.persistenceService.updateGame(g);

				CreateGameModalWindow.this.persistenceService.clearAllMagicCardsForGameAndDeck(
																									  CreateGameModalWindow.this.game.getId(), CreateGameModalWindow.this.player
																																					   .getDeck().getDeckId());

				final Deck deck = new Deck();
				deck.setPlayerId(HatchetHarrySession.get().getPlayer().getId());
				deck.setDeckArchive(CreateGameModalWindow.this.decks.getModelObject()
											.getDeckArchive());
				CreateGameModalWindow.this.persistenceService.saveDeck(deck);

				final List<CollectibleCard> allCollectibleCardsInDeckArchive = CreateGameModalWindow.this.persistenceService
																					   .giveAllCollectibleCardsInDeckArchive(deck.getDeckArchive());
				CreateGameModalWindow.LOGGER.error("allCollectibleCardsInDeckArchive.size(): "
														   + allCollectibleCardsInDeckArchive.size());

				final List<MagicCard> allMagicCards = new ArrayList<MagicCard>();

				for (final CollectibleCard cc : allCollectibleCardsInDeckArchive) {
					final MagicCard card = new MagicCard("cards/" + cc.getTitle() + "_small.jpg",
																"cards/" + cc.getTitle() + ".jpg", "cards/" + cc.getTitle()
																										   + "Thumb.jpg", cc.getTitle(), "",
																CreateGameModalWindow.this.sideInput.getDefaultModelObjectAsString(),
																null);
					card.setGameId(g.getId());
					card.setDeck(deck);
					card.setUuidObject(UUID.randomUUID());
					card.setZone(CardZone.LIBRARY);
					allMagicCards.add(card);
				}

				CreateGameModalWindow.LOGGER.error("allMagicCard.size(): " + allMagicCards.size());
				deck.setCards(deck.reorderMagicCards(deck.shuffleLibrary(allMagicCards)));

				CreateGameModalWindow.LOGGER.error("deck.cards().size(): " + deck.getCards().size()
														   + ", deckId: " + deck.getDeckId());


				final ArrayList<MagicCard> firstCards = new ArrayList<MagicCard>();

				for (int i = 0; i < 7; i++) {
					final MagicCard aCard = deck.getCards().get(i);
					aCard.setZone(CardZone.HAND);
					firstCards.add(aCard);
				}

				CreateGameModalWindow.this.persistenceService.saveDeckOrUpdate(deck);
				CreateGameModalWindow.LOGGER.error("deck.cards().size(): " + deck.getCards().size()
														   + ", deckId: " + deck.getDeckId());

				HatchetHarrySession.get().setFirstCardsInHand(firstCards);
				CreateGameModalWindow.this.player.setDeck(deck);
				CreateGameModalWindow.this.player.setGame(g);
				CreateGameModalWindow.this.persistenceService
						.mergePlayer(CreateGameModalWindow.this.player);
				HatchetHarrySession.get().setPlayer(CreateGameModalWindow.this.player);

				CreateGameModalWindow.LOGGER.error("deck.cards().size(): " + deck.getCards().size()
														   + ", deckId: " + deck.getDeckId());

				// Remove Balduvian Horde
				target.appendJavaScript("jQuery('#menutoggleButton249c4f0b_cad0_4606_b5ea_eaee8866a347').remove(); ");
				HatchetHarrySession.get().getAllMagicCardsInBattleField().clear();

				final StringBuilder buil = new StringBuilder(
																	"jQuery.gritter.add({title : \"You've created a game\", text : \"As soon as a player is connected, you'll be able to play.\", image : 'image/logoh2.gif', sticky : false, time : ''}); ");

				CreateGameModalWindow.LOGGER.info("close!");
				final HatchetHarrySession session = HatchetHarrySession.get();

				final int posX = ("infrared".equals(CreateGameModalWindow.this.sideInput
															.getDefaultModelObjectAsString())) ? 300 : 900;
				target.appendJavaScript(buil.toString());

				CreateGameModalWindow.this.homePage.getPlayCardBehavior().setSide(
																						 CreateGameModalWindow.this.sideInput.getDefaultModelObjectAsString());

				session.getPlayer()
						.getSide()
						.setSideName(
											CreateGameModalWindow.this.sideInput
													.getDefaultModelObjectAsString());
				session.getPlayer().setName(
												   CreateGameModalWindow.this.nameInput.getDefaultModelObjectAsString());
				session.setMySidePosX(posX);
				session.setMySidePosY(500);
				final Side s = CreateGameModalWindow.this.player.getSide();
				s.setUuid(UUID.randomUUID().toString());
				s.setX(Long.valueOf(posX));
				s.setY(500l);

				final DataBox db = new DataBox("dataBox", g.getId());
				CreateGameModalWindow.this.homePage.getDataBoxParent().addOrReplace(db);
				db.setOutputMarkupId(true);
				target.add(CreateGameModalWindow.this.homePage.getDataBoxParent());

				session.setGameCreated();
				session.resetCardsInGraveyard();

				if (CreateGameModalWindow.this.player.isHandDisplayed()) {
					JavaScriptUtils.updateHand(target);
				}

				if ((CreateGameModalWindow.this.player.isGraveyardDisplayed() != null)
							&& CreateGameModalWindow.this.player.isGraveyardDisplayed()) {
					JavaScriptUtils.updateGraveyard(target);
				}

				CreateGameModalWindow.this.player.setSideUuid(s.getUuid());
				CreateGameModalWindow.this.persistenceService.updateSide(s);
				CreateGameModalWindow.this.persistenceService
						.mergePlayer(CreateGameModalWindow.this.player);

				final AddSideCometChannel ascc = new AddSideCometChannel(
																				CreateGameModalWindow.this.player);

				final String pageUuid = HatchetHarryApplication.getCometResources().get(
																							   CreateGameModalWindow.this.player.getId());

				HatchetHarryApplication.get().getEventBus().post(ascc, pageUuid);

				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
																								   ConsoleLogType.GAME, null, null, true, null, HatchetHarrySession.get()
																																						.getPlayer().getName(), null, g.getId(), null, null, g.getId());
				HatchetHarryApplication.get().getEventBus()
						.post(new ConsoleLogCometChannel(logger), pageUuid);

				target.appendJavaScript("document.getElementById('userName').value = '"
												+ CreateGameModalWindow.this.player.getName() + "'; ");
			}

			@Override
			protected void onError(final AjaxRequestTarget target, final Form<?> _form) {
				CreateGameModalWindow.LOGGER.error("ERROR");
			}
		};
		submit.setOutputMarkupId(true);
		submit.setMarkupId("createSubmit");

		form.add(chooseDeck, this.deckParent, beforeGameId, gameId, afterGameId, sideLabel,
						nameLabel, this.nameInput, this.sideInput, submit);

		this.add(form);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService) {
		this.persistenceService = _persistenceService;
	}

}
