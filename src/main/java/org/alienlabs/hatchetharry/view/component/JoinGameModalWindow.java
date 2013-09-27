package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.CollectibleCard;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.Side;
import org.alienlabs.hatchetharry.model.channel.JoinGameNotificationCometChannel;
import org.alienlabs.hatchetharry.model.channel.UpdateDataBoxCometChannel;
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
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class JoinGameModalWindow extends Panel
{
	private static final long serialVersionUID = 1L;

	@SpringBean
	PersistenceService persistenceService;

	static final Logger LOGGER = LoggerFactory.getLogger(JoinGameModalWindow.class);

	final TextField<Long> gameIdInput;
	final WebMarkupContainer dataBoxParent;

	Player player;
	HomePage hp;

	WebMarkupContainer deckParent;
	DropDownChoice<Deck> decks;

	final TextField<String> nameInput;

	final DropDownChoice<String> sideInput;

	final ModalWindow modal;

	public JoinGameModalWindow(final ModalWindow _modal, final String id, final Player _player,
			final WebMarkupContainer _dataBoxParent, final HomePage _hp)
	{
		super(id);
		this.modal = _modal;
		Injector.get().inject(this);

		this.player = _player;
		this.hp = _hp;
		this.dataBoxParent = _dataBoxParent;

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

		this.nameInput.add(new AjaxFormComponentUpdatingBehavior("onfocus")
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target)
			{
				if ((null != target) && (null == JoinGameModalWindow.this.decks.getModelObject()))
				{
					final ArrayList<Deck> _allDecks = (ArrayList<Deck>)JoinGameModalWindow.this.persistenceService
							.getAllDecksFromDeckArchives();
					final Model<ArrayList<Deck>> _decksModel = new Model<ArrayList<Deck>>(_allDecks);
					JoinGameModalWindow.this.decks = new DropDownChoice<Deck>("decks",
							new Model<Deck>(), _decksModel);
					JoinGameModalWindow.this.decks.setOutputMarkupId(true).setMarkupId("decks");

					JoinGameModalWindow.this.deckParent
					.addOrReplace(JoinGameModalWindow.this.decks);
					target.add(JoinGameModalWindow.this.deckParent);
				}
			}
		});

		final Label chooseDeck = new Label("chooseDeck", "Choose a deck: ");

		final ArrayList<Deck> allDecks = (ArrayList<Deck>)this.persistenceService
				.getAllDecksFromDeckArchives();
		final Model<ArrayList<Deck>> decksModel = new Model<ArrayList<Deck>>(allDecks);

		this.deckParent = new WebMarkupContainer("deckParent");
		this.deckParent.setOutputMarkupId(true);
		this.decks = new DropDownChoice<Deck>("decks", new Model<Deck>(), decksModel);
		this.decks.setOutputMarkupId(true).setMarkupId("decks");
		this.deckParent.add(this.decks);

		final Label gameIdLabel = new Label("gameIdLabel",
				"Please provide the game id given by your opponent: ");
		final Model<Long> gameId = new Model<Long>(0l);
		this.gameIdInput = new TextField<Long>("gameIdInput", gameId);
		this.gameIdInput.setOutputMarkupId(true).setMarkupId("gameIdInput");

		final IndicatingAjaxButton submit = new IndicatingAjaxButton("submit", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				if ((null == JoinGameModalWindow.this.nameInput.getModelObject())
						|| ("".equals(JoinGameModalWindow.this.nameInput.getModelObject().trim()))
						|| (null == JoinGameModalWindow.this.decks.getModelObject())
						|| (null == JoinGameModalWindow.this.sideInput
						.getDefaultModelObjectAsString()))
				{
					return;
				}

				final Long _id = Long.valueOf(JoinGameModalWindow.this.gameIdInput
						.getDefaultModelObjectAsString());

				final Game game = JoinGameModalWindow.this.persistenceService.getGame(_id);
				if (null == game)
				{
					JoinGameModalWindow.this.modal.close(target);
					target.appendJavaScript("alert('The selected game (id= "
							+ JoinGameModalWindow.this.gameIdInput.getDefaultModelObjectAsString()
							+ ") does not exist!');");
					return;
				}

				final HatchetHarrySession session = HatchetHarrySession.get();

				JoinGameModalWindow.this.player = JoinGameModalWindow.this.persistenceService
						.getPlayer(session.getPlayer().getId());

				final Game oldGame = session.getPlayer().getGame();

				for (final Side s : oldGame.getSides())
				{
					s.setGame(null);
					JoinGameModalWindow.this.persistenceService.updateSide(s);
				}
				for (final Player p : oldGame.getPlayers())
				{
					p.setGame(null);
					JoinGameModalWindow.this.persistenceService.updatePlayer(p);
				}

				oldGame.getSides().clear();
				oldGame.getPlayers().clear();
				JoinGameModalWindow.this.persistenceService.deleteGame(oldGame);

				JoinGameModalWindow.this.player.setGame(game);
				session.setGameId(_id);
				JoinGameModalWindow.LOGGER.info("~~~ " + _id);

				JoinGameModalWindow.this.persistenceService.clearAllMagicCardsForGameAndDeck(_id,
						JoinGameModalWindow.this.decks.getModelObject().getDeckId());

				final Deck deck = new Deck();
				deck.setPlayerId(HatchetHarrySession.get().getPlayer().getId());
				deck.setDeckArchive(JoinGameModalWindow.this.decks.getModelObject()
						.getDeckArchive());

				final List<CollectibleCard> allCollectibleCardsInDeckArchive = JoinGameModalWindow.this.persistenceService
						.giveAllCollectibleCardsInDeckArchive(deck.getDeckArchive());
				JoinGameModalWindow.LOGGER.error("deck.getDeckArchive().getDeckArchiveId(): "
						+ deck.getDeckArchive().getDeckArchiveId());
				JoinGameModalWindow.LOGGER.error("allCollectibleCardsInDeckArchive.size(): "
						+ allCollectibleCardsInDeckArchive.size());

				final List<MagicCard> allMagicCard = new ArrayList<MagicCard>();

				for (final CollectibleCard cc : allCollectibleCardsInDeckArchive)
				{
					final MagicCard card = new MagicCard("cards/" + cc.getTitle() + "_small.jpg",
							"cards/" + cc.getTitle() + ".jpg", "cards/" + cc.getTitle()
							+ "Thumb.jpg", cc.getTitle(), "",
							JoinGameModalWindow.this.sideInput.getDefaultModelObjectAsString());
					card.setGameId(game.getId());
					card.setDeck(deck);
					card.setUuidObject(UUID.randomUUID());
					card.setZone(CardZone.LIBRARY);
					allMagicCard.add(card);
				}
				deck.setCards(deck.reorderMagicCards(deck.shuffleLibrary(allMagicCard)));

				JoinGameModalWindow.this.persistenceService.saveDeck(deck);
				JoinGameModalWindow.LOGGER.error("deck.cards().size(): " + deck.getCards().size()
						+ ", deckId: " + deck.getDeckId());

				final ArrayList<MagicCard> firstCards = new ArrayList<MagicCard>();

				for (int i = 0; i < 7; i++)
				{
					final MagicCard aCard = deck.getCards().get(i);
					aCard.setZone(CardZone.HAND);
					JoinGameModalWindow.this.persistenceService.updateCard(aCard);
					firstCards.add(aCard);
				}

				JoinGameModalWindow.LOGGER.error("deck.cards().size(): " + deck.getCards().size()
						+ ", deckId: " + deck.getDeckId());

				HatchetHarrySession.get().setFirstCardsInHand(firstCards);
				JoinGameModalWindow.this.player.setDeck(deck);
				JoinGameModalWindow.this.player.setGame(game);
				JoinGameModalWindow.this.persistenceService
				.updatePlayer(JoinGameModalWindow.this.player);
				session.setPlayer(JoinGameModalWindow.this.player);

				JoinGameModalWindow.LOGGER.error("deck.cards().size(): " + deck.getCards().size()
						+ ", deckId: " + deck.getDeckId());

				// Remove Balduvian Horde
				target.appendJavaScript("jQuery('#menutoggleButton249c4f0b_cad0_4606_b5ea_eaee8866a347').remove(); ");
				HatchetHarrySession.get().getAllMagicCardsInBattleField().clear();

				JoinGameModalWindow.this.player.setSide(JoinGameModalWindow.this.sideInput
						.getDefaultModelObjectAsString());
				JoinGameModalWindow.this.player.setName(JoinGameModalWindow.this.nameInput
						.getDefaultModelObjectAsString());
				JoinGameModalWindow.this.player.setGame(game);

				final Set<Player> players = game.getPlayers();
				players.add(JoinGameModalWindow.this.player);
				game.setPlayers(players);

				JoinGameModalWindow.this.persistenceService
				.updatePlayer(JoinGameModalWindow.this.player);
				HatchetHarrySession.get().setPlayer(JoinGameModalWindow.this.player);

				final DataBox dataBox = new DataBox("dataBox",
						Long.valueOf(JoinGameModalWindow.this.gameIdInput
								.getDefaultModelObjectAsString()));
				dataBox.setOutputMarkupId(true);
				JoinGameModalWindow.this.hp.getDataBoxParent().addOrReplace(dataBox);

				if (JoinGameModalWindow.this.player.isHandDisplayed())
				{
					JavaScriptUtils.updateHand(target);
				}

				final StringBuilder buil = new StringBuilder(
						"jQuery.gritter.add({title : \"You have requested to join a game\", text : \"You can start playing right now!\", image : 'image/logoh2.gif', sticky : false, time : ''}); ");
				JoinGameModalWindow.LOGGER.info("close!");

				final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getPage()
						.getRequest();
				final HttpServletRequest request = servletWebRequest.getContainerRequest();
				final String jsessionid = request.getRequestedSessionId();

				final int posX = ("infrared".equals(JoinGameModalWindow.this.sideInput
						.getDefaultModelObjectAsString())) ? 300 : 900;

				JoinGameModalWindow.this.hp.getPlayCardBehavior().setSide(
						JoinGameModalWindow.this.sideInput.getDefaultModelObjectAsString());
				session.setMySidePosX(posX);
				session.setMySidePosY(500);
				JoinGameModalWindow.this.persistenceService.updateGame(game);

				session.setGameCreated();
				_modal.close(target);
				target.add(JoinGameModalWindow.this.hp.getDataBoxParent());
				target.appendJavaScript(buil.toString());

				final Long _gameId = game.getId();
				final JoinGameNotificationCometChannel jgncc = new JoinGameNotificationCometChannel(
						JoinGameModalWindow.this.player.getName(), jsessionid, _gameId);

				final List<BigInteger> allPlayersInGameExceptMe = JoinGameModalWindow.this.persistenceService
						.giveAllPlayersFromGameExceptMe(_gameId,
								JoinGameModalWindow.this.player.getId());
				final UpdateDataBoxCometChannel udbcc = new UpdateDataBoxCometChannel(_gameId);

				// post the DataBox update message to all players in the game,
				// except me
				for (int i = 0; i < allPlayersInGameExceptMe.size(); i++)
				{
					final Long p = allPlayersInGameExceptMe.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(p);
					PlayCardFromHandBehavior.LOGGER.info("pageUuid: " + pageUuid);

					HatchetHarryApplication.get().getEventBus().post(jgncc, pageUuid);
					HatchetHarryApplication.get().getEventBus().post(udbcc, pageUuid);
				}

				session.resetCardsInGraveyard();

				if (JoinGameModalWindow.this.player.isGraveyardDisplayed())
				{
					JavaScriptUtils.updateGraveyard(target);
				}
			}
		};
		submit.setOutputMarkupId(true);
		submit.setMarkupId("joinSubmit");

		form.add(chooseDeck, this.deckParent, sideLabel, nameLabel, this.nameInput, this.sideInput,
				gameIdLabel, this.gameIdInput, submit);

		this.add(form);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
