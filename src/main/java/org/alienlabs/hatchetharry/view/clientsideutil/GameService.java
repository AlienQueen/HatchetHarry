package org.alienlabs.hatchetharry.view.clientsideutil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.CollectibleCard;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.Side;
import org.alienlabs.hatchetharry.model.channel.AddSideCometChannel;
import org.alienlabs.hatchetharry.model.channel.AddSidesFromOtherBrowsersCometChannel;
import org.alienlabs.hatchetharry.model.channel.ConsoleLogCometChannel;
import org.alienlabs.hatchetharry.model.channel.JoinGameNotificationCometChannel;
import org.alienlabs.hatchetharry.model.channel.UpdateDataBoxCometChannel;
import org.alienlabs.hatchetharry.model.consolelogstrategy.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.consolelogstrategy.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.consolelogstrategy.ConsoleLogType;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);

	private GameService()
	{
	}

	public static void joinGame(final PersistenceService persistenceService,
			final ModalWindow _modal, final AjaxRequestTarget target, final Long gameId,
			final Deck deck, final String side, final String playerName, final HomePage hp)
	{
		final Game game = persistenceService.getGame(gameId);
		if (null == game)
		{
			target.appendJavaScript("alert('The selected match (id= " + gameId.toString()
					+ ") does not exist!');");
			return;
		}

		final HatchetHarrySession session = HatchetHarrySession.get();

		final Player player = persistenceService.getPlayer(session.getPlayer().getId());

		final Game oldGame = session.getPlayer().getGame();

		for (final Player p : oldGame.getPlayers())
		{
			p.setGame(null);
			final Deck d = p.getDeck();
			p.setDeck(null);
			d.setPlayerId(-1L);
			persistenceService.updatePlayer(p);
		}

		oldGame.getPlayers().clear();
		persistenceService.deleteGame(oldGame);

		player.setGame(game);
		session.setGameId(gameId);
		LOGGER.info("~~~ " + gameId);

		persistenceService.clearAllMagicCardsForGameAndDeck(gameId, deck.getDeckId());

		final Deck _deck = new Deck();
		_deck.setPlayerId(player.getId());
		_deck.setDeckArchive(deck.getDeckArchive());
		player.setDeck(_deck);
		persistenceService.saveDeck(_deck);

		final List<CollectibleCard> allCollectibleCardsInDeckArchive = persistenceService
				.giveAllCollectibleCardsInDeckArchive(_deck.getDeckArchive());
		LOGGER.info("deck.getDeckArchive().getDeckArchiveId(): "
				+ _deck.getDeckArchive().getDeckArchiveId());
		LOGGER.info("allCollectibleCardsInDeckArchive.size(): "
				+ allCollectibleCardsInDeckArchive.size());

		final List<MagicCard> allMagicCards = new ArrayList<>();

		for (final CollectibleCard cc : allCollectibleCardsInDeckArchive)
		{
			final MagicCard card = new MagicCard("cards/" + cc.getTitle() + "_small.jpg", "cards/"
					+ cc.getTitle() + ".jpg", "cards/" + cc.getTitle() + "Thumb.jpg",
					cc.getTitle(), "", side, null, Integer.valueOf(0));
			card.setGameId(gameId);
			card.setDeck(_deck);
			card.setUuidObject(UUID.randomUUID());
			card.setZone(CardZone.LIBRARY);
			allMagicCards.add(card);
		}
		_deck.setCards(_deck.reorderMagicCards(_deck.shuffleLibrary(allMagicCards)));

		LOGGER.info("deck.cards().size(): " + _deck.getCards().size() + ", deckId: "
				+ deck.getDeckId());

		final ArrayList<MagicCard> firstCards = new ArrayList<>();

		for (int i = 0; i < 7; i++)
		{
			final MagicCard aCard = _deck.getCards().get(i);
			aCard.setZone(CardZone.HAND);
			firstCards.add(aCard);
		}

		persistenceService.updateDeck(_deck);
		LOGGER.info("_deck.cards().size(): " + _deck.getCards().size() + ", deckId: "
				+ _deck.getDeckId());

		HatchetHarrySession.get().setFirstCardsInHand(firstCards);
		player.setGame(game);
		player.getSide().setSideName(side);
		player.setName(playerName);

		persistenceService.mergePlayer(player);
		session.setPlayer(player);

		LOGGER.info("_deck.cards().size(): " + _deck.getCards().size() + ", deckId: "
				+ _deck.getDeckId());

		HatchetHarrySession.get().getAllMagicCardsInBattleField().clear();
		final Set<Player> players = game.getPlayers();
		players.add(player);
		game.setPlayers(players);

		if (player.isHandDisplayed().booleanValue())
		{
			BattlefieldService.updateHand(target);
		}

		final StringBuilder buil = new StringBuilder(
				"jQuery.gritter.add({title : \"You have requested to join a match\", text : \"You can start playing right now!\", image : 'image/logoh2.gif', sticky : false, time : ''}); ");
		LOGGER.info("close!");

		final int posX = ("infrared".equals(side)) ? 300 : 900;

		session.setMySidePosX(posX);
		session.setMySidePosY(300);

		final Side s = player.getSide();
		s.setUuid(UUID.randomUUID().toString());
		s.setX(Long.valueOf(posX));
		s.setY(500L);

		persistenceService.updateGame(game);

		session.setGameCreated();
		_modal.close(target);
		target.add(hp.getDataBoxParent());
		target.appendJavaScript(buil.toString());

		final Long _gameId = game.getId();
		final JoinGameNotificationCometChannel jgncc = new JoinGameNotificationCometChannel(
				player.getName(), _gameId);

		final List<BigInteger> allPlayersInGameExceptMe = persistenceService
				.giveAllPlayersFromGameExceptMe(_gameId, player.getId());

		final UpdateDataBoxCometChannel udbcc = new UpdateDataBoxCometChannel(_gameId);

		final List<BigInteger> allPlayersInGame = persistenceService
				.giveAllPlayersFromGame(_gameId);

		persistenceService.updateSide(s);
		player.setSideUuid(s.getUuid());
		persistenceService.updatePlayer(player);

		final AddSideCometChannel ascc = new AddSideCometChannel(player);
		final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
				ConsoleLogType.GAME, null, null, Boolean.FALSE, null, HatchetHarrySession.get()
						.getPlayer().getName(), null, _gameId, null, null, _gameId);

		// post the DataBox update message to all players in the game,
		// except me
		EventBusPostService.post(allPlayersInGameExceptMe, jgncc);

		EventBusPostService.post(allPlayersInGame, udbcc, ascc, new ConsoleLogCometChannel(logger));

		// In order to display the opponents' sides
		final List<Player> giveAllPlayersFromGameExceptMeAsPlayers = persistenceService
				.giveAllPlayersFromGameExceptMeAsPlayers(_gameId, player.getId());
		final AddSidesFromOtherBrowsersCometChannel asfobcc = new AddSidesFromOtherBrowsersCometChannel(
				player, giveAllPlayersFromGameExceptMeAsPlayers);
		EventBusPostService.post(allPlayersInGame, asfobcc);

		session.resetCardsInGraveyard();

		if ((player.isGraveyardDisplayed() != null) && player.isGraveyardDisplayed().booleanValue())
		{
			BattlefieldService.updateGraveyard(target);
		}

		target.appendJavaScript("document.getElementById('userName').value = '" + player.getName()
				+ "'; ");
	}
}
