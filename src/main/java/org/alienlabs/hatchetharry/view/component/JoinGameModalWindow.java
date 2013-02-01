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
import org.alienlabs.hatchetharry.model.channel.JoinGameCometChannel;
import org.alienlabs.hatchetharry.model.channel.JoinGameNotificationCometChannel;
import org.alienlabs.hatchetharry.model.channel.UpdateDataBoxCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.JavaScriptUtils;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.atmosphere.EventBus;
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

		final Label nameLabel = new Label("nameLabel", "Choose a name: ");
		final Model<String> nameModel = new Model<String>("");
		this.nameInput = new TextField<String>("name", nameModel);
		this.nameInput.setOutputMarkupId(true);

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
		this.decks.setOutputMarkupId(true);
		this.deckParent.add(this.decks);

		final Label gameIdLabel = new Label("gameIdLabel",
				"Please provide the game id given by your opponent: ");
		final Model<Long> gameId = new Model<Long>(0l);
		this.gameIdInput = new TextField<Long>("gameIdInput", gameId);

		final AjaxButton submit = new AjaxButton("submit", form)
		{
			private static final long serialVersionUID = 561276328198198L;

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
				session.getPlayer().setGame(null);
				oldGame.getPlayers().clear();
				final Set<Side> sides = oldGame.getSides();

				for (final Side s : sides)
				{
					if (oldGame.equals(s.getGame()))
					{
						s.setGame(null);
					}
				}

				session.getPlayer().setGame(game);
				JoinGameModalWindow.this.persistenceService.updatePlayer(session.getPlayer());
				JoinGameModalWindow.this.persistenceService.saveSides(sides);
				sides.clear();
				JoinGameModalWindow.this.persistenceService.deleteGame(oldGame);


				JoinGameModalWindow.this.player.setGame(game);
				session.setGameId(_id);
				JoinGameModalWindow.LOGGER.info("~~~ " + _id);

				final Deck deck = (Deck)JoinGameModalWindow.this.decks.getDefaultModelObject();
				deck.shuffleLibrary();

				final List<MagicCard> cards = new ArrayList<MagicCard>();
				final List<String> allTitles = new ArrayList<String>();

				for (final MagicCard m : deck.getCards())
				{
					allTitles.add(m.getTitle());
				}
				for (final String title : allTitles)
				{
					final CollectibleCard cc = JoinGameModalWindow.this.persistenceService
							.findCollectibleCardByName(title);

					final MagicCard card = new MagicCard("cards/" + cc.getTitle() + "_small.jpg",
							"cards/" + cc.getTitle() + ".jpg", "cards/" + cc.getTitle()
									+ "Thumb.jpg", cc.getTitle(), "");
					card.setGameId(game.getId());
					card.setDeck(deck);
					card.setUuidObject(UUID.randomUUID());
					card.setZone(CardZone.LIBRARY);

					JoinGameModalWindow.this.persistenceService.saveOrUpdateCard(card);
					cards.add(card);
				}

				deck.setCards(cards);
				deck.setPlayerId(HatchetHarrySession.get().getPlayer().getId());
				JoinGameModalWindow.this.persistenceService.saveOrUpdateDeck(deck);

				JoinGameModalWindow.this.player.setDeck(deck);
				session.setPlayer(JoinGameModalWindow.this.player);

				final ArrayList<MagicCard> firstCards = new ArrayList<MagicCard>();

				for (int i = 0; i < 7; i++)
				{
					final MagicCard aCard = cards.get(i);
					aCard.setZone(CardZone.HAND);
					JoinGameModalWindow.this.persistenceService.saveCard(aCard);
					firstCards.add(i, aCard);
					HatchetHarrySession.get().addCardIdInHand(i, i); // TODO
																		// remove
																		// this
					deck.getCards().remove(cards.get(i));
				}

				JoinGameModalWindow.this.persistenceService.saveDeck(deck);

				final List<CardPanel> cardToRemove = HatchetHarrySession.get()
						.getAllCardsInBattleField();
				final StringBuffer javaScript = new StringBuffer();

				if ((null != cardToRemove) && (cardToRemove.size() > 0))
				{
					for (final CardPanel cp : cardToRemove)
					{
						javaScript.append("jQuery('#" + cp.getMarkupId() + "').remove();");
						JoinGameModalWindow.LOGGER.info("cp.getMarkupId(): " + cp.getMarkupId());
						HatchetHarrySession.get().addCardInToRemoveList(cp);
					}
					JoinGameModalWindow.this.persistenceService.deleteAllCardsInBattleField();
					HatchetHarrySession.get().removeAllCardsFromBattleField();
				}

				session.setFirstCardsInHand(firstCards);

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

				if (HatchetHarrySession.get().isHandDisplayed())
				{
					JavaScriptUtils.updateHand(target);
				}

				javaScript
						.append("jQuery('#joyRidePopup0').remove(); jQuery('[id^=\"menutoggleButton\"]').remove(); jQuery.gritter.add({title : \"You have requested to join a game\", text : \"You can start playing right now!\", image : 'image/logoh2.gif', sticky : false, time : ''}); ");
				JoinGameModalWindow.LOGGER.info("close!");

				final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getPage()
						.getRequest();
				final HttpServletRequest request = servletWebRequest.getContainerRequest();
				final String jsessionid = request.getRequestedSessionId();

				final SidePlaceholderPanel spp = new SidePlaceholderPanel("secondSidePlaceholder",
						JoinGameModalWindow.this.sideInput.getDefaultModelObjectAsString(),
						JoinGameModalWindow.this.hp, UUID.randomUUID());
				spp.add(new SidePlaceholderMoveBehavior(spp, spp.getUuid(), jsessionid,
						JoinGameModalWindow.this.hp,
						JoinGameModalWindow.this.hp.getDataBoxParent(), session.getGameId()));
				JoinGameModalWindow.LOGGER.info("gameId in JoinGameModalWindow: "
						+ session.getGameId());
				spp.setOutputMarkupId(true);

				session.putMySidePlaceholderInSesion(JoinGameModalWindow.this.sideInput
						.getDefaultModelObjectAsString());

				JoinGameModalWindow.this.hp.getSecondSidePlaceholderParent().addOrReplace(spp);

				final int posX = ("infrared".equals(JoinGameModalWindow.this.sideInput
						.getDefaultModelObjectAsString())) ? 300 : 900;

				javaScript
						.append("window.setTimeout(function() { var card = jQuery('#sidePlaceholder"
								+ spp.getUuid()
								+ "'); "
								+ "card.css('position', 'absolute'); "
								+ "card.css('left', '"
								+ posX
								+ "px'); "
								+ "card.css('top', '500px'); }, 3000); ");

				final String opponentSide = ("infrared".equals(JoinGameModalWindow.this.sideInput
						.getDefaultModelObjectAsString())) ? "ultraviolet" : "infrared";

				final SidePlaceholderPanel spp2 = new SidePlaceholderPanel("firstSidePlaceholder",
						opponentSide, JoinGameModalWindow.this.hp, UUID.randomUUID());
				spp2.add(new SidePlaceholderMoveBehavior(spp2, spp2.getUuid(), jsessionid,
						JoinGameModalWindow.this.hp,
						JoinGameModalWindow.this.hp.getDataBoxParent(), session.getGameId()));
				spp2.setOutputMarkupId(true);

				JoinGameModalWindow.this.hp.getFirstSidePlaceholderParent().addOrReplace(spp2);

				final int posX2 = (posX == 300) ? 900 : 300;
				javaScript
						.append("window.setTimeout(function() { var card = jQuery('#sidePlaceholder"
								+ spp2.getUuid()
								+ "'); "
								+ "card.css('position', 'absolute'); "
								+ "card.css('left', '"
								+ posX2
								+ "px'); "
								+ "card.css('top', '500px'); }, 3000); ");
				// TODO remove gameId management since we now have Comet
				// channels
				javaScript.append("window.gameId = " + session.getGameId() + "; ");

				JoinGameModalWindow.this.hp.getPlayCardBehavior().setSide(
						JoinGameModalWindow.this.sideInput.getDefaultModelObjectAsString());
				session.setMySidePosX(posX);
				session.setMySidePosY(500);

				final Side s = new Side();
				s.setGame(game);
				s.setSide(JoinGameModalWindow.this.sideInput.getDefaultModelObjectAsString());
				s.setUuid(spp.getUuid().toString());
				s.setWicketId("secondSidePlaceholder");
				s.setX(Long.valueOf(posX));
				s.setY(Long.valueOf(500));
				JoinGameModalWindow.this.persistenceService.updateGame(game); // TODO
				JoinGameModalWindow.this.persistenceService.saveSide(s);

				spp.setPosX(posX);
				spp.setPosY(500);
				session.setMySidePlaceholder(spp);
				spp2.setPosX(posX2);
				spp2.setPosY(500);
				session.setMySidePlaceholder(spp2);

				session.setGameCreated();

				_modal.close(target);
				target.add(JoinGameModalWindow.this.hp.getDataBoxParent());
				target.add(JoinGameModalWindow.this.hp.getSecondSidePlaceholderParent());
				target.add(JoinGameModalWindow.this.hp.getFirstSidePlaceholderParent());
				target.appendJavaScript(javaScript.toString());

				final JoinGameCometChannel jgcc = new JoinGameCometChannel(
						JoinGameModalWindow.this.sideInput.getDefaultModelObjectAsString(),
						jsessionid, spp.getUuid(), Long.valueOf(posX), 500l);
				EventBus.get().post(jgcc);

				final Long _gameId = game.getId();
				final JoinGameNotificationCometChannel jgncc = new JoinGameNotificationCometChannel(
						JoinGameModalWindow.this.player.getName(), jsessionid, _gameId);
				EventBus.get().post(jgncc);

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
					EventBus.get().post(udbcc, pageUuid);
				}

				session.resetCardsInGraveyard();

				if (session.isGraveyardDisplayed())
				{
					JavaScriptUtils.updateGraveyard(target);
				}
			}

			@Override
			protected void onError(final AjaxRequestTarget target, final Form<?> _form)
			{
			}
		};
		submit.setOutputMarkupId(true);
		submit.setMarkupId("joinSubmit" + _player.getId());

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
