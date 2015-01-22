package org.alienlabs.hatchetharry.view.component.modalwindow;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.CollectibleCard;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Format;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.Side;
import org.alienlabs.hatchetharry.model.channel.AddSideCometChannel;
import org.alienlabs.hatchetharry.model.channel.ConsoleLogCometChannel;
import org.alienlabs.hatchetharry.model.consolelogstrategy.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.consolelogstrategy.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.consolelogstrategy.ConsoleLogType;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.BattlefieldService;
import org.alienlabs.hatchetharry.view.clientsideutil.EventBusPostService;
import org.alienlabs.hatchetharry.view.component.gui.DataBox;
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "SE_INNER_CLASS", justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class CreateGameModalWindow extends Panel
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateGameModalWindow.class);
	private static final long serialVersionUID = 1L;

	final Game game;
	final HomePage homePage;
	final TextField<String> nameInput;
	final DropDownChoice<String> sideInput;
	final ModalWindow modal;

	final Player player;
	WebMarkupContainer deckParent;
	DropDownChoice<Deck> decks;
	final DropDownChoice<Format> formats;
	final TextField<String> numberOfPlayers;

	@SpringBean
	PersistenceService persistenceService;

	public CreateGameModalWindow(final ModalWindow _modal, final String id, final Player _player,
			final HomePage hp)
	{
		super(id);
		Injector.get().inject(this);

		this.modal = _modal;
		this.player = _player;
		this.homePage = hp;

		final Form<String> form = new Form<>("form");

		final ArrayList<String> allSides = new ArrayList<>();
		allSides.add("infrared");
		allSides.add("ultraviolet");
		final Model<ArrayList<String>> sidesModel = new Model<>(allSides);
		final Label sideLabel = new Label("sideLabel", "Choose your side: ");
		this.sideInput = new DropDownChoice<>("sideInput", new Model<String>(), sidesModel);
		this.sideInput.setOutputMarkupId(true).setMarkupId("sideInput");

		final Label nameLabel = new Label("nameLabel", "Choose a name: ");
		final Model<String> nameModel = new Model<>("");
		this.nameInput = new TextField<>("name", nameModel);
		this.nameInput.setOutputMarkupId(true).setMarkupId("name");

		this.nameInput.add(new AjaxFormComponentUpdatingBehavior("onfocus")
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target)
			{
				if ((null != target) && (null == CreateGameModalWindow.this.decks.getModelObject()))
				{
					final ArrayList<Deck> _allDecks = CreateGameModalWindow.this.persistenceService
							.getAllDecksFromDeckArchives();
					final Model<ArrayList<Deck>> _decksModel = new Model<>(_allDecks);
					CreateGameModalWindow.this.decks = new DropDownChoice<>("decks",
							new Model<Deck>(), _decksModel);
					CreateGameModalWindow.this.decks.setOutputMarkupId(true).setMarkupId("decks");

					CreateGameModalWindow.this.deckParent
							.addOrReplace(CreateGameModalWindow.this.decks);
					target.add(CreateGameModalWindow.this.deckParent);
				}
			}
		});

		final Label chooseDeck = new Label("chooseDeck", "Choose a deck: ");

		final ArrayList<Deck> allDecks = this.persistenceService.getAllDecksFromDeckArchives();
		final Model<ArrayList<Deck>> decksModel = new Model<>(allDecks);

		this.deckParent = new WebMarkupContainer("deckParent");
		this.deckParent.setOutputMarkupId(true);
		this.decks = new DropDownChoice<>("decks", new Model<Deck>(), decksModel);
		this.decks.setOutputMarkupId(true).setMarkupId("decks");
		this.deckParent.add(this.decks);

		this.game = this.player.getGame();
		final Label beforeGameId = new Label("beforeGameId", "The id of this game is: ");

		final Label gameId = new Label("gameId", this.game.getId());
		gameId.setOutputMarkupId(true).setMarkupId("gameId");

		final Label afterGameId = new Label("afterGameId",
				". You'll have to provide it to your opponent(s).");
		HatchetHarrySession.get().setGameId(this.game.getId());

		final ArrayList<Format> allFormats = new ArrayList<>();
		allFormats.addAll(Arrays.asList(Format.values()));
		final Model<ArrayList<Format>> formatsModel = new Model<>(allFormats);
		this.formats = new DropDownChoice<>("formats", new Model<Format>(), formatsModel);
		this.formats.setOutputMarkupId(true).setMarkupId("formats");

		final Model<String> numberOfPlayersModel = new Model<>("");
		this.numberOfPlayers = new TextField<>("numberOfPlayers", numberOfPlayersModel);
		this.numberOfPlayers.setOutputMarkupId(true).setMarkupId("numberOfPlayers");

		final IndicatingAjaxButton submit = new IndicatingAjaxButton("submit", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				CreateGameModalWindow.LOGGER.info("CreateGameModalWindow");

				if ((null == CreateGameModalWindow.this.nameInput.getModelObject())
						|| ("".equals(CreateGameModalWindow.this.nameInput.getModelObject().trim()))
						|| (null == CreateGameModalWindow.this.decks.getModelObject())
						|| (null == CreateGameModalWindow.this.formats.getModelObject())
						|| (null == CreateGameModalWindow.this.sideInput
								.getDefaultModelObjectAsString())
						|| (null == CreateGameModalWindow.this.numberOfPlayers.getModelObject())
						|| ("".equals(CreateGameModalWindow.this.numberOfPlayers
								.getDefaultModelObjectAsString())))
				{
					return;
				}

				Integer players;
				try
				{
					players = Integer.valueOf(CreateGameModalWindow.this.numberOfPlayers
							.getDefaultModelObjectAsString());
				}
				catch (final NumberFormatException e)
				{
					LOGGER.error(
							"invalid integer: "
									+ CreateGameModalWindow.this.numberOfPlayers
											.getDefaultModelObjectAsString(), e);
					return;
				}

				if ((players.intValue() < 2) || (players.intValue() > 10))
				{
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

				g.setDesiredNumberOfPlayers(players);
				g.setPending(true);
				g.setDesiredFormat(CreateGameModalWindow.this.formats.getModelObject());
				LOGGER.info("desiredFormat: " + g.getDesiredFormat());

				CreateGameModalWindow.this.persistenceService.updateGame(g);

				CreateGameModalWindow.this.persistenceService.clearAllMagicCardsForGameAndDeck(
						CreateGameModalWindow.this.game.getId(), CreateGameModalWindow.this.player
								.getDeck().getDeckId());

				final Deck deck = new Deck();
				deck.setPlayerId(HatchetHarrySession.get().getPlayer().getId());
				deck.setDeckArchive(CreateGameModalWindow.this.decks.getModelObject()
						.getDeckArchive());

				final List<CollectibleCard> allCollectibleCardsInDeckArchive = CreateGameModalWindow.this.persistenceService
						.giveAllCollectibleCardsInDeckArchive(deck.getDeckArchive());
				CreateGameModalWindow.LOGGER.error("allCollectibleCardsInDeckArchive.size(): "
						+ allCollectibleCardsInDeckArchive.size());

				final List<MagicCard> allMagicCards = new ArrayList<>();

				for (final CollectibleCard cc : allCollectibleCardsInDeckArchive)
				{
					final MagicCard card = new MagicCard("cards/" + cc.getTitle() + "_small.jpg",
							"cards/" + cc.getTitle() + ".jpg", "cards/" + cc.getTitle()
									+ "Thumb.jpg", cc.getTitle(), "",
							CreateGameModalWindow.this.sideInput.getDefaultModelObjectAsString(),
							null, Integer.valueOf(0));
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


				final ArrayList<MagicCard> firstCards = new ArrayList<>();

				for (int i = 0; i < 7; i++)
				{
					final MagicCard aCard = deck.getCards().get(i);
					aCard.setZone(CardZone.HAND);
					firstCards.add(aCard);
				}

				CreateGameModalWindow.this.persistenceService.saveDeck(deck);
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

				HatchetHarrySession.get().getAllMagicCardsInBattleField().clear();
				final StringBuilder buil = new StringBuilder(
						"jQuery.gritter.add({title : \"You've created a game\", text : \"As soon as a player is connected, you'll be able to play.\", image : 'image/logoh2.gif', sticky : false, time : ''}); ");

				CreateGameModalWindow.LOGGER.info("close!");
				final HatchetHarrySession session = HatchetHarrySession.get();

				final int posX = ("infrared".equals(CreateGameModalWindow.this.sideInput
						.getDefaultModelObjectAsString())) ? 300 : 900;
				target.appendJavaScript(buil.toString());

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
				s.setY(500L);

				final DataBox db = new DataBox("dataBox", g.getId().longValue());
				CreateGameModalWindow.this.homePage.getDataBoxParent().addOrReplace(db);
				db.setOutputMarkupId(true);
				target.add(CreateGameModalWindow.this.homePage.getDataBoxParent());

				session.setGameCreated();
				session.resetCardsInGraveyard();

				BattlefieldService.updateHand(target);

				if ((CreateGameModalWindow.this.player.isGraveyardDisplayed() != null)
						&& CreateGameModalWindow.this.player.isGraveyardDisplayed().booleanValue())
				{
					BattlefieldService.updateGraveyard(target);
				}

				final Label gameIdLabel = new Label("gameId", "Game id: "
						+ CreateGameModalWindow.this.game.getId().longValue());
				gameIdLabel.setOutputMarkupId(true);
				final WebMarkupContainer gameIdParent = ((HomePage)target.getPage())
						.getGameIdParent();
				gameIdParent.addOrReplace(gameIdLabel);
				target.add(gameIdParent);

				CreateGameModalWindow.this.player.setSideUuid(s.getUuid());
				CreateGameModalWindow.this.persistenceService.updateSide(s);
				CreateGameModalWindow.this.persistenceService
						.updatePlayer(CreateGameModalWindow.this.player);

				final AddSideCometChannel ascc = new AddSideCometChannel(
						CreateGameModalWindow.this.player);
				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.GAME, null, null, Boolean.TRUE, null, HatchetHarrySession
								.get().getPlayer().getName(), null, g.getId(), null, null,
						g.getId());

				final List<BigInteger> allPlayersInGame = new ArrayList<>();
				allPlayersInGame.add(BigInteger.valueOf(CreateGameModalWindow.this.player.getId()
						.longValue()));
				EventBusPostService
						.post(allPlayersInGame, ascc, new ConsoleLogCometChannel(logger));

				target.appendJavaScript("document.getElementById('userName').value = '"
						+ CreateGameModalWindow.this.player.getName() + "'; ");
			}

			@Override
			protected void onError(final AjaxRequestTarget target, final Form<?> _form)
			{
				CreateGameModalWindow.LOGGER.error("ERROR");
			}
		};
		submit.setOutputMarkupId(true);
		submit.setMarkupId("createSubmit");

		form.add(chooseDeck, this.deckParent, beforeGameId, gameId, afterGameId, sideLabel,
				nameLabel, this.nameInput, this.sideInput, this.formats, this.numberOfPlayers,
				submit);

		this.add(form);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
