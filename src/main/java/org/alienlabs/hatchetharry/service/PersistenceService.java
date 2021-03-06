package org.alienlabs.hatchetharry.service;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Arrow;
import org.alienlabs.hatchetharry.model.CardConstants;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.ChatMessage;
import org.alienlabs.hatchetharry.model.CollectibleCard;
import org.alienlabs.hatchetharry.model.ConsoleLogMessage;
import org.alienlabs.hatchetharry.model.Counter;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.DeckArchive;
import org.alienlabs.hatchetharry.model.Format;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.Side;
import org.alienlabs.hatchetharry.model.Token;
import org.alienlabs.hatchetharry.model.User;
import org.alienlabs.hatchetharry.persistence.dao.ArrowDao;
import org.alienlabs.hatchetharry.persistence.dao.ChatMessageDao;
import org.alienlabs.hatchetharry.persistence.dao.CollectibleCardDao;
import org.alienlabs.hatchetharry.persistence.dao.ConsoleLogMessageDao;
import org.alienlabs.hatchetharry.persistence.dao.CounterDao;
import org.alienlabs.hatchetharry.persistence.dao.DeckArchiveDao;
import org.alienlabs.hatchetharry.persistence.dao.DeckDao;
import org.alienlabs.hatchetharry.persistence.dao.GameDao;
import org.alienlabs.hatchetharry.persistence.dao.MagicCardDao;
import org.alienlabs.hatchetharry.persistence.dao.PlayerDao;
import org.alienlabs.hatchetharry.persistence.dao.SideDao;
import org.alienlabs.hatchetharry.persistence.dao.TokenDao;
import org.alienlabs.hatchetharry.persistence.dao.UserDao;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class PersistenceService implements Serializable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceService.class);
	private static final long serialVersionUID = 1L;

	@SpringBean
	private PlayerDao playerDao;
	@SpringBean
	private DeckDao deckDao;
	@SpringBean
	private DeckArchiveDao deckArchiveDao;
	@SpringBean
	private CollectibleCardDao collectibleCardDao;
	@SpringBean
	private MagicCardDao magicCardDao;
	@SpringBean
	private GameDao gameDao;
	@SpringBean
	private SideDao sideDao;
	@SpringBean
	private CounterDao counterDao;
	@SpringBean
	private TokenDao tokenDao;
	@SpringBean
	private ArrowDao arrowDao;
	@SpringBean
	private ConsoleLogMessageDao consoleLogMessageDao;
	@SpringBean
	private UserDao userDao;
	@SpringBean
	private ChatMessageDao chatMessageDao;

	public PersistenceService()
	{
	}

	@Transactional(readOnly = true)
	public MagicCard getNthCardOfGame(final Long index)
	{
		final Session session = this.magicCardDao.getSession();
		final Query query = session
			.createQuery("from MagicCard magiccard0_ where magiccard0_.id=:id");
		query.setLong("id", index.longValue());

		return (MagicCard)query.uniqueResult();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public MagicCard saveCardByGeneratingItsUuid(final MagicCard _c, final long gameId)
	{
		final MagicCard c = _c;
		c.setUuid(UUID.randomUUID().toString());
		c.setGameId(Long.valueOf(gameId));

		final Session session = this.magicCardDao.getSession();
		final Long id = (Long)session.save(c);
		c.setId(id);

		return c;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public void saveCard(final MagicCard c)
	{
		this.magicCardDao.getSession().save(c);

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public void saveTokenAndDeck(final MagicCard c, final Token t)
	{
		this.tokenDao.getSession().save(t);
		this.magicCardDao.getSession().save(c);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveToken(final Token t)
	{
		this.tokenDao.getSession().save(t);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveOrUpdateAllMagicCards(final List<MagicCard> allMagicCards)
	{
		for (final MagicCard card : allMagicCards)
		{
			this.magicCardDao.getSession().saveOrUpdate(this.magicCardDao.getSession().merge(card));
		}
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveAllMagicCards(final List<MagicCard> allMagicCards)
	{
		for (final MagicCard card : allMagicCards)
		{
			this.magicCardDao.getSession().saveOrUpdate(card);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public void updateAllMagicCards(final List<MagicCard> allMagicCards)
	{
		for (final MagicCard card : allMagicCards)
		{
			this.magicCardDao.getSession().saveOrUpdate(this.magicCardDao.getSession().merge(card));
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public void updateCard(final MagicCard c)
	{
		this.magicCardDao.getSession().saveOrUpdate(this.magicCardDao.getSession().merge(c));
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public void updateCardWithoutMerge(final MagicCard c)
	{
		this.magicCardDao.getSession().update(c);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void updateToken(final Token t)
	{
		this.tokenDao.getSession().update(t);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public void deleteCardAndToken(final MagicCard c)
	{
		final Token token = c.getToken();
		final Player p = token.getPlayer();
		token.setPlayer(null);
		final Set<Counter> counters = c.getCounters();

		final List<MagicCard> magicCardCards = c.getDeck().getCards();
		magicCardCards.remove(c);
		c.getDeck().setCards(magicCardCards);

		final Deck d = c.getDeck();
		p.setDeck(d);
		token.setPlayer(p);

		final Long magicCardId = c.getId();

		this.deckDao.getSession().merge(d);

		Session session = this.magicCardDao.getSession();
		Query query = session.createSQLQuery("DELETE FROM MagicCard WHERE magicCardId = :id ");
		query.setLong("id", magicCardId.longValue());
		query.executeUpdate();

		session = this.tokenDao.getSession();
		query = session
			.createSQLQuery("DELETE t.* FROM Card_Counter cc, Token t, MagicCard mc WHERE t.tokenId = mc.token_tokenId AND cc.counterId = t.tokenId AND mc.magicCardId = :id");
		query.setLong("id", magicCardId.longValue());
		query.executeUpdate();

		session = this.magicCardDao.getSession();
		query = session
			.createSQLQuery("DELETE cc.* FROM Card_Counter cc, Counter c, MagicCard mc WHERE c.card = mc.magicCardId AND cc.counterId = c.counterId AND mc.magicCardId = :id");
		query.setLong("id", magicCardId.longValue());
		query.executeUpdate();

		for (final Counter counter : counters)
		{
			session = this.counterDao.getSession();
			query = session.createSQLQuery("DELETE FROM Counter WHERE counterId = :id");
			query.setLong("id", counter.getId().longValue());
			query.executeUpdate();
		}
	}


	@Transactional(readOnly = true)
	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "PRMC_POSSIBLY_REDUNDANT_METHOD_CALLS", justification = "So you're saying I can't log the same thing in two different methods????")
	public MagicCard getCardFromUuid(final UUID uuid)
	{
		final Session session = this.magicCardDao.getSession();
		final Query query = session
			.createQuery("from MagicCard magiccard0_ where magiccard0_.uuid= :uuid ");
		query.setString("uuid", uuid.toString());
		query.setCacheable(true);
		PersistenceService.LOGGER.info("card UUID: " + uuid.toString());

		try
		{
			if ((query.list() != null) && (query.list().size() > 1))
			{
				return (MagicCard)query.list().get(0);
			}

			return (MagicCard)query.uniqueResult();
		}
		catch (final ObjectNotFoundException e)
		{
			LOGGER.error("MagiCard uuid: " + uuid.toString() + " not found!", e);
			return null;
		}
	}

	@Transactional(readOnly = true)
	public Token getTokenFromUuid(final String uuid)
	{
		final Session session = this.tokenDao.getSession();
		final Query query = session.createQuery("from Token t0_ where t0_.uuid= :uuid ");
		query.setString("uuid", uuid);
		query.setCacheable(true);
		PersistenceService.LOGGER.debug("token UUID: ");

		if (query.list().size() > 1)
		{
			return (Token)query.list().get(0);
		}

		return (Token)query.uniqueResult();
	}

	@Transactional(readOnly = true)
	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "PRMC_POSSIBLY_REDUNDANT_METHOD_CALLS", justification = "So you're saying I can't log the same thing in two different methods????")
	public Token getTokenFromUuid(final UUID uuid)
	{
		final Session session = this.tokenDao.getSession();
		final Query query = session.createQuery("from Token token0 where token0.uuid= :uuid ");
		query.setString("uuid", uuid.toString());
		PersistenceService.LOGGER.debug("token UUID: " + uuid.toString());

		if (query.list().size() > 1)
		{
			return (Token)query.list().get(0);
		}

		return (Token)query.uniqueResult();
	}

	@Transactional(readOnly = true)
	public List<MagicCard> getFirstHand(final long gameId)
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session
			.createQuery("from MagicCard magiccard0_ where magiccard0_.gameId=:id");
		query.setLong("id", gameId);
		query.setFirstResult(0);
		query.setMaxResults(CardConstants.INITIAL_NUMBER_OF_CARDS_IN_HAND);

		return query.list();
	}

	@Transactional(readOnly = true)
	public Player getFirstPlayer()
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session.createQuery("from Player player0_");
		query.setFirstResult(0);
		query.setMaxResults(1);
		final Object mc = query.uniqueResult();

		return (mc == null ? null : (Player)mc);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void mergePlayer(final Player p)
	{
		final Session session = this.playerDao.getSession();
		session.saveOrUpdate(session.merge(p));
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public void updatePlayer(final Player p)
	{
		final Session session = this.playerDao.getSession();
		session.update(session.merge(p));
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void updatePlayerWithoutMerge(final Player p)
	{
		final Session session = this.playerDao.getSession();
		session.update(p);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void savePlayer(final Player p)
	{
		this.deckDao.getSession().saveOrUpdate(p.getDeck());
		final Session session = this.playerDao.getSession();
		session.save(p);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void updateSide(final Side s)
	{
		final Session session = this.sideDao.getSession();
		session.merge(s);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveOrUpdateCounter(final Counter c)
	{
		final Session session = this.counterDao.getSession();
		session.saveOrUpdate(c);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveGame(final Game g)
	{
		final Session session = this.gameDao.getSession();
		session.save(g);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void updateGame(final Game g)
	{
		this.gameDao.getSession().update(g);
	}

	@Transactional(readOnly = true)
	public int countPlayers(final long l)
	{
		final Session session = this.playerDao.getSession();

		final Query query = session.createQuery("from Player player0_ where player0_.gameId=:id");
		query.setLong("id", l);

		return query.list().size();
	}

	@Transactional(readOnly = true)
	public int countDeckArchives()
	{
		final Session session = this.deckArchiveDao.getSession();

		final Query query = session.createQuery("from DeckArchive");

		return query.list().size();
	}

	@Transactional(readOnly = true)
	public int countCollectibleCards()
	{
		final Session session = this.collectibleCardDao.getSession();

		final Query query = session.createQuery("from CollectibleCard");

		return query.list().size();
	}

	@Transactional(readOnly = true)
	public int countCollectibleCardsInDeckArchive(final DeckArchive deckArchive)
	{
		final Session session = this.collectibleCardDao.getSession();

		final Query query = session
			.createQuery("from CollectibleCard cc where cc.deckArchiveId = :id");
		query.setLong("id", deckArchive.getDeckArchiveId().longValue());

		return query.list().size();
	}

	@Transactional(readOnly = true)
	public List<CollectibleCard> giveAllCollectibleCardsInDeckArchive(final DeckArchive deckArchive)
	{
		final Session session = this.collectibleCardDao.getSession();

		final Query query = session
			.createQuery("from CollectibleCard cc where cc.deckArchiveId = :id");
		query.setLong("id", deckArchive.getDeckArchiveId().longValue());

		return query.list();
	}

	@Transactional(readOnly = true)
	public int countMagicCards()
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session.createQuery("from MagicCard");

		return query.list().size();
	}

	@Transactional(readOnly = true)
	public int countDecks()
	{
		final Session session = this.deckDao.getSession();

		final Query query = session.createQuery("from Deck");

		return query.list().size();
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void updateDeck(final Deck d)
	{
		final Session deckSession = this.deckDao.getSession();
		deckSession.update(d);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public Deck saveOrUpdateDeck(final Deck d)
	{
		final Session session = this.deckDao.getSession();
		session.saveOrUpdate(d);
		return d;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public Deck mergeDeck(final Deck d)
	{
		final Session session = this.deckDao.getSession();
		return (Deck)session.merge(d);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public void saveOrUpdateDeckWithoutMerge(final Deck d)
	{
		final Session session = this.deckDao.getSession();
		session.saveOrUpdate(d);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Deck saveDeck(final Deck d)
	{
		final Session cardSession = this.magicCardDao.getSession();
		if (null != d.getCards())
		{
			for (final MagicCard c : d.getCards())
			{
				cardSession.save(c);
			}
		}
		this.deckDao.getSession().save(d);
		return d;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveDeckOrUpdate(final Deck d)
	{
		final Session cardSession = this.magicCardDao.getSession();
		if (null != d.getCards())
		{
			for (final MagicCard c : d.getCards())
			{
				cardSession.saveOrUpdate(c);
			}
		}
		this.deckDao.getSession().saveOrUpdate(d);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Side saveSide(final Side s)
	{
		this.sideDao.getSession().save(s);
		return s;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveSides(final Set<Side> sides)
	{
		for (final Side s : sides)
		{
			this.sideDao.getSession().save(s);
		}
	}

	@Transactional(readOnly = true)
	public Player getPlayer(final Long l)
	{
		return (Player)this.playerDao.getSession().get(Player.class, l);
	}

	@Transactional(readOnly = true)
	public boolean getPlayerByGame(final long l)
	{
		final Session session = this.playerDao.getSession();

		final Query query = session.createQuery("from Player player0_ where player0_.gameId=:id");
		query.setLong("id", l);

		return !query.list().isEmpty();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public List<Player> getAllPlayersOfGame(final long l)
	{
		final Session session = this.playerDao.getSession();

		final Query query = session
			.createQuery("from Player player0_ where player0_.game = :game ");
		query.setLong("game", l);

		final List<Player> all = query.list();
		for (final Player p : all)
		{
			if (null == p.getDeck())
			{
				final Deck d = this.getDeckByDeckArchiveName("aggro-combo Red / Black");
				p.setDeck(d);
			}
			final List<MagicCard> cards = this.getAllCardsFromDeck(p.getDeck().getDeckArchive()
				.getDeckName());
			p.getDeck().setCards(cards);
			this.updatePlayer(p);
		}

		return all;
	}

	// Isolation level chosen to be consistent with
	// RuntimeDataGenerator#generateData()
	@Transactional(readOnly = true)
	public DeckArchive getDeckArchiveByName(final String name)
	{
		final Session session = this.deckDao.getSession();

		final SQLQuery query = session
			.createSQLQuery("SELECT da.* FROM Deck d, DeckArchive da WHERE da.deckName=:deckName AND da.deckArchiveId = d.Deck_DeckArchive");
		query.addEntity(DeckArchive.class);
		query.setString("deckName", name);

		@SuppressWarnings("rawtypes")
		final List results = query.list();
		return (!results.isEmpty() ? (DeckArchive)query.list().get(0) : null);
	}

	@Transactional(readOnly = true)
	public boolean doesCollectibleCardAlreadyExistsInDb(final String title)
	{
		final Session session = this.collectibleCardDao.getSession();

		final Query query = session
			.createQuery("from CollectibleCard cc0_ where cc0_.title=:title");
		query.setString("title", title);
		final List<?> list = query.list();
		return ((list != null) && (!list.isEmpty()));
	}

	@Transactional(readOnly = true)
	public Deck getDeck(final long deckId)
	{
		final Session session = this.deckDao.getSession();

		final Query query = session.createQuery("from Deck deck0_ where deck0_.deckId=:id");
		query.setLong("id", deckId);
		query.setMaxResults(1);

		return (Deck)query.uniqueResult();
	}

	@Transactional(readOnly = true)
	public Deck getDeckByDeckArchiveName(final String deckArchiveName)
	{
		final Session session = this.deckDao.getSession();

		final SQLQuery query = session
			.createSQLQuery("SELECT d.* FROM DeckArchive da, Deck d WHERE da.deckName=:deckArchiveName AND da.deckArchiveId=d.Deck_DeckArchive");
		query.addEntity(Deck.class);
		query.setString("deckArchiveName", deckArchiveName);
		query.setMaxResults(1);

		return (Deck)query.uniqueResult();
	}

	@Transactional(readOnly = true)
	public List<MagicCard> getAllCardsFromDeck(final String name)
	{
		final Session session = this.magicCardDao.getSession();

		final SQLQuery query = session
			.createSQLQuery("SELECT mc.* FROM MagicCard mc, Deck d, DeckArchive da WHERE mc.card_deck=d.deckId AND d.Deck_DeckArchive=da.deckArchiveId AND da.deckName=:deck");
		query.addEntity(MagicCard.class);
		query.setString("deck", name);

		List<MagicCard> list = new ArrayList<>();
		try
		{
			list = query.list();
		}
		catch (final ObjectNotFoundException e)
		{
			PersistenceService.LOGGER.error("error!", e);
		}
		return list;
	}

	@Transactional(readOnly = true)
	public List<MagicCard> getAllCardsInLibraryForDeckAndPlayer(final Long gameId,
		final Long playerId, final Long deckId)
	{
		final Session session = this.magicCardDao.getSession();

		final SQLQuery query = session
			.createSQLQuery("SELECT mc.* FROM MagicCard mc, Deck d WHERE mc.gameId = :gameId AND mc.zone = :zoneName AND d.playerId = :playerId AND mc.card_deck = d.deckId AND d.deckId = :deckId ORDER BY mc.zoneOrder");
		query.addEntity(MagicCard.class);
		query.setLong("gameId", gameId.longValue());
		query.setString("zoneName", CardZone.LIBRARY.toString().toUpperCase(Locale.ENGLISH));
		query.setLong("playerId", playerId.longValue());
		query.setLong("deckId", deckId.longValue());

		List<MagicCard> list = new ArrayList<>();
		try
		{
			list = query.list();
		}
		catch (final ObjectNotFoundException e)
		{
			PersistenceService.LOGGER.error("error!", e);
		}
		return list;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveCollectibleCard(final CollectibleCard cc)
	{
		this.collectibleCardDao.getSession().save(cc);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public DeckArchive saveOrUpdateDeckArchive(final DeckArchive da)
	{
		final Session session = this.deckArchiveDao.getSession();

		final SQLQuery query = session
			.createSQLQuery("SELECT da.* FROM Deck d, DeckArchive da WHERE da.deckName=:deck AND da.deckArchiveId = d.Deck_DeckArchive");
		query.addEntity(DeckArchive.class);
		query.setString("deck", da.getDeckName());

		if (!query.list().isEmpty())
		{
			return ((DeckArchive)query.list().get(0));
		}

		session.saveOrUpdate(da);
		return da;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public DeckArchive updateDeckArchive(final DeckArchive da)
	{
		final Session session = this.deckArchiveDao.getSession();
		session.update(da);
		return da;
	}

	@Required
	public void setPlayerDao(final PlayerDao _playerDao)
	{
		this.playerDao = _playerDao;
	}

	@Required
	public void setDeckDao(final DeckDao _deckDao)
	{
		this.deckDao = _deckDao;
	}

	@Required
	public void setDeckArchiveDao(final DeckArchiveDao _deckArchiveDao)
	{
		this.deckArchiveDao = _deckArchiveDao;
	}

	@Required
	public void setSideDao(final SideDao _sideDao)
	{
		this.sideDao = _sideDao;
	}

	@Required
	public void setCollectibleCardDao(final CollectibleCardDao _collectibleCardDao)
	{
		this.collectibleCardDao = _collectibleCardDao;
	}

	@Required
	public void setMagicCardDao(final MagicCardDao _magicCardDao)
	{
		this.magicCardDao = _magicCardDao;
	}

	@Required
	public void setGameDao(final GameDao _gameDao)
	{
		this.gameDao = _gameDao;
	}

	@Required
	public void setCounterDao(final CounterDao _counterDao)
	{
		this.counterDao = _counterDao;
	}

	@Required
	public void setTokenDao(final TokenDao _tokenDao)
	{
		this.tokenDao = _tokenDao;
	}

	@Required
	public void setArrowDao(final ArrowDao _arrowDao)
	{
		this.arrowDao = _arrowDao;
	}

	@Required
	public void setConsoleLogMessageDao(final ConsoleLogMessageDao _consoleLogMessageDao)
	{
		this.consoleLogMessageDao = _consoleLogMessageDao;
	}

	@Required
	public void setUserDao(final UserDao _userDao)
	{
		this.userDao = _userDao;
	}

	@Required
	public void setChatMessageDao(final ChatMessageDao _chatMessageDao)
	{
		this.chatMessageDao = _chatMessageDao;
	}

	@Transactional(readOnly = true)
	public List<MagicCard> getCardsByDeckId(final long deckId)
	{
		final Session session = this.magicCardDao.getSession();
		final Query query = session
			.createQuery("select card0_ from MagicCard card0_ , Deck deck0_ where card0_.deck  = deck0_.deckId  and deck0_.deckId = :deck");
		query.setLong("deck", deckId);

		return query.list();
	}

	@Transactional(readOnly = true)
	public List<Deck> getAllDecks()
	{
		final Session session = this.deckDao.getSession();
		final Query query = session.createQuery("from Deck deck0_ where deck0_.playerId != -1");

		return query.list();
	}

	@Transactional(readOnly = true)
	public List<DeckArchive> getAllDeckArchives()
	{
		final Session session = this.deckDao.getSession();
		final Query query = session.createQuery("from DeckArchive d where d.deckName != null");

		return query.list();
	}

	@Transactional(readOnly = true)
	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "BC_UNCONFIRMED_CAST_OF_RETURN_VALUE", justification = " Hibernate returns ArrayList, for the moment at least")
	public ArrayList<Deck> getAllDecksFromDeckArchives()
	{
		final Session session = this.deckDao.getSession();
		final SQLQuery query = session
			.createSQLQuery("SELECT dd.* FROM Deck dd WHERE dd.Deck_DeckArchive IN (SELECT DISTINCT da.deckArchiveId FROM  Deck de, DeckArchive da WHERE de.Deck_DeckArchive = da.deckArchiveId AND da.deckName IS NOT NULL) GROUP BY dd.Deck_DeckArchive");
		query.addEntity(Deck.class);

		return (ArrayList<Deck>)query.list();
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Game createGameAndPlayer(final Game game, final Player player)
	{
		final Set<Player> set = game.getPlayers();
		set.add(player);
		game.setPlayers(set);
		game.setPending(false);

		player.setGame(game);

		this.playerDao.getSession().save(player);

		this.gameDao.getSession().save(game);
		return game;
	}

	@Transactional(readOnly = true)
	public MagicCard findCardByName(final String _name)
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session.createQuery("from MagicCard m where m.title = :title");
		query.setString("title", _name);
		query.setMaxResults(1);
		try
		{
			return (MagicCard)query.list().get(0);
		}
		catch (final IndexOutOfBoundsException e)
		{
			PersistenceService.LOGGER.error("error!", e);
			return null;
		}

	}

	@Transactional(readOnly = true)
	public CollectibleCard findCollectibleCardByName(final String title)
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session.createQuery("from CollectibleCard cc where cc.title = :title");
		query.setString("title", title);
		query.setMaxResults(1);
		try
		{
			return (CollectibleCard)query.uniqueResult();
		}
		catch (final ObjectNotFoundException e)
		{
			PersistenceService.LOGGER.error("error!", e);
			return null;
		}
	}

	@Transactional(readOnly = true)
	public Game getGame(final Long _id)
	{
		return (Game)this.gameDao.getSession().get(Game.class, _id);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public void deleteMagicCard(final MagicCard mc)
	{
		this.magicCardDao.getSession().delete(mc);
	}

	@Transactional(readOnly = true)
	public List<MagicCard> getAllCardsInHandForAGameAndADeck(final Long gameId, final Long deckId)
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session
			.createQuery("from MagicCard mc where mc.gameId = :gameId and mc.zone = :zoneName and mc.deck = :deckId order by mc.zoneOrder");
		query.setLong("gameId", gameId.longValue());
		query.setParameter("zoneName", CardZone.HAND);
		query.setLong("deckId", deckId.longValue());

		try
		{
			return query.list();
		}
		catch (final ObjectNotFoundException e)
		{
			PersistenceService.LOGGER.error("Error retrieving cards in hand for game: " + gameId
				+ " => no result found", e);
			return null;
		}
	}

	@Transactional(readOnly = true)
	public List<MagicCard> getAllCardsInBattlefieldForAGameAndADeck(final Long gameId,
		final Long deckId)
	{
		final Session session = this.magicCardDao.getSession();


		final Query query = session
			.createQuery("select mc from MagicCard mc where mc.gameId = :gameId and mc.zone = :zone and mc.deck = :deckId");
		query.setLong("gameId", gameId.longValue());
		query.setParameter("zone", CardZone.BATTLEFIELD);
		query.setLong("deckId", deckId.longValue());
		query.setCacheable(true);

		try
		{
			return query.list();
		}
		catch (final ObjectNotFoundException e)
		{
			PersistenceService.LOGGER.error("Error retrieving cards in battlefield for game: "
				+ gameId + " => no result found", e);
			return null;
		}
	}

	@Transactional(readOnly = true)
	public List<MagicCard> getAllCardsInBattlefieldForAGame(final Long gameId)
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session
			.createQuery("select m from MagicCard m where m.gameId = :gameId and m.zone = :zone");
		query.setLong("gameId", gameId.longValue());
		query.setParameter("zone", CardZone.BATTLEFIELD);
		query.setCacheable(true);

		try
		{
			return query.list();
		}
		catch (final ObjectNotFoundException e)
		{
			PersistenceService.LOGGER.error("Error retrieving cards in battlefield for game: "
				+ gameId + " => no result found", e);
			return null;
		}
	}

	@Transactional(readOnly = true)
	public List<MagicCard> getAllCardsAndTokensInBattlefieldForAGameAndAPlayer(final Long gameId,
		final Long playerId, final Long deckId)
	{
		final Session session = this.magicCardDao.getSession();
		final List<MagicCard> all = new ArrayList<>();

		final List<MagicCard> allMagicCards = this.getAllCardsInBattlefieldForAGameAndADeck(gameId,
			deckId);
		if (null != allMagicCards)
		{
			all.addAll(allMagicCards);
		}

		final SQLQuery query = session
			.createSQLQuery("SELECT t.* FROM Token t WHERE t.gameId = :gameId AND t.Player_Token = :playerId ");
		query.addEntity(Token.class);
		query.setLong("gameId", gameId.longValue());
		query.setLong("playerId", playerId.longValue());

		try
		{
			final List<Token> allTokens = query.list();
			if (null != allTokens)
			{
				for (final Token t : allTokens)
				{
					final MagicCard card = this.getCardFromUuid(UUID.fromString(t.getUuid()));

					if ((null != card) && (!all.contains(card)))
					{
						all.add(card);
					}
				}
			}
		}
		catch (final ObjectNotFoundException e)
		{
			PersistenceService.LOGGER.error("Error retrieving tokens in battlefield for game: "
				+ gameId + " => no result found", e);
		}

		Collections.sort(all);
		return all;
	}

	// Used only in tests, for the moment
	@Transactional(readOnly = true)
	public List<MagicCard> getAllCardsInGraveyardForAGame(final Long gameId)
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session
			.createQuery("from MagicCard m where m.gameId = :gameId and m.zone = :zone");
		query.setLong("gameId", gameId.longValue());
		query.setParameter("zone", CardZone.GRAVEYARD);
		query.setCacheable(true);

		try
		{
			return query.list();
		}
		catch (final ObjectNotFoundException e)
		{
			PersistenceService.LOGGER.error("Error retrieving cards in graveyard for game: "
				+ gameId + " => no result found", e);
			return null;
		}
	}

	@Transactional(readOnly = true)
	public List<MagicCard> getAllCardsInExileForAGameAndAPlayer(final Long gameId,
		final Long playerId, final Long deckId)
	{
		return this.getAllCardsInZoneForAGameAndAPlayer(CardZone.EXILE, gameId, playerId, deckId);
	}

	@Transactional(readOnly = true)
	public List<MagicCard> getAllCardsInGraveyardForAGameAndAPlayer(final Long gameId,
		final Long playerId, final Long deckId)
	{
		return this.getAllCardsInZoneForAGameAndAPlayer(CardZone.GRAVEYARD, gameId, playerId,
			deckId);
	}

	// Maybe it will be public one day
	@Transactional(readOnly = true)
	private List<MagicCard> getAllCardsInZoneForAGameAndAPlayer(final CardZone zone,
		final Long gameId, final Long playerId, final Long deckId)
	{
		final Session session = this.magicCardDao.getSession();

		final SQLQuery query = session
			.createSQLQuery("SELECT mc.* FROM MagicCard mc, Deck d WHERE mc.gameId = :gameId AND mc.zone = :zoneName AND d.playerId = :playerId AND mc.card_deck = d.deckId AND d.deckId = :deckId");
		query.addEntity(MagicCard.class);
		query.setLong("gameId", gameId.longValue());
		query.setString("zoneName", zone.toString().toUpperCase(Locale.ENGLISH));
		query.setLong("playerId", playerId.longValue());
		query.setLong("deckId", deckId.longValue());

		try
		{
			return query.list();
		}
		catch (final ObjectNotFoundException e)
		{
			PersistenceService.LOGGER.error("Error retrieving cards in graveyard for game: "
				+ gameId + " => no result found", e);
			return null;
		}
	}


	@Transactional(readOnly = true)
	public List<Side> getSidesFromGame(final Game game)
	{
		final Session session = this.sideDao.getSession();
		final Query query = session.createQuery("from Side s where s.game=:game");
		query.setEntity("game", game);

		return query.list();
	}

	@Transactional(readOnly = true)
	public List<BigInteger> giveAllPlayersFromGameExceptMe(final Long gameId, final Long me)
	{
		final Session session = this.gameDao.getSession();

		final Query query = session
			.createSQLQuery("SELECT playerId FROM Player_Game pg WHERE pg.gameId = :gameId AND pg.playerId <> :playerId");
		query.setLong("gameId", gameId.longValue());
		query.setLong("playerId", me.longValue());

		return query.list();
	}

	@Transactional(readOnly = true)
	public List<Player> giveAllPlayersFromGameExceptMeAsPlayers(final Long gameId, final Long me)
	{
		final Session session = this.gameDao.getSession();

		final SQLQuery query = session
			.createSQLQuery("SELECT p.* FROM Player p, Player_Game pg WHERE pg.gameId = :gameId AND pg.playerId <> :playerId AND pg.playerId = p.playerId");
		query.setLong("gameId", gameId.longValue());
		query.setLong("playerId", me.longValue());
		query.addEntity(Player.class);

		return query.list();
	}

	@Transactional(readOnly = true)
	public List<BigInteger> giveAllPlayersFromGame(final Long gameId)
	{
		final Session session = this.gameDao.getSession();

		final Query query = session
			.createSQLQuery("SELECT playerId FROM Player_Game pg WHERE pg.gameId = :gameId");
		query.setLong("gameId", gameId.longValue());

		return query.list();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public void deleteGame(final Game oldGame)
	{
		this.gameDao.getSession().delete(oldGame);
	}

	@Transactional
	public void clearAllMagicCardsForGameAndDeck(final Long gameId, final Long deckId)
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session
			.createSQLQuery("DELETE FROM MagicCard WHERE gameId = :gameId AND card_deck = :deck");
		query.setLong("gameId", gameId.longValue());
		query.setLong("deck", deckId.longValue());

		query.executeUpdate();
	}

	@Transactional
	public void resetDb()
	{
		final Session session = this.gameDao.getSession();

		session.createSQLQuery("DELETE FROM Player_Game").executeUpdate();
		session.createSQLQuery("DELETE FROM Card_Counter").executeUpdate();
		session.createSQLQuery("DELETE FROM Counter").executeUpdate();
		session.createSQLQuery("DELETE FROM MagicCard").executeUpdate();
		session.createSQLQuery("DELETE FROM Token").executeUpdate();
		session.createSQLQuery("DELETE FROM Counter").executeUpdate();
		session.createSQLQuery("DELETE FROM CollectibleCard").executeUpdate();
		session.createSQLQuery("DELETE FROM Arrow").executeUpdate();
		session.createSQLQuery("DELETE FROM CardCollection").executeUpdate();
		session.createSQLQuery("DELETE FROM ChatMessage").executeUpdate();
		session.createSQLQuery("DELETE FROM ConsoleLogMessage").executeUpdate();
		session.createSQLQuery("DELETE FROM User").executeUpdate();
		session.createSQLQuery("DELETE FROM Player").executeUpdate();
		session.createSQLQuery("DELETE FROM Deck").executeUpdate();
		session.createSQLQuery("DELETE FROM DeckArchive").executeUpdate();
		session.createSQLQuery("DELETE FROM Side").executeUpdate();
		session.createSQLQuery("DELETE FROM Game").executeUpdate();
	}

	@Transactional(readOnly = true)
	public Integer getNumberOfCardsInACertainZoneForAGameAndADeck(final CardZone zone,
		final Long gameId, final Long deckId)
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session
			.createQuery("from MagicCard where zone = :zoneName and gameId = :gameId and card_deck = :deckId");
		query.setString("zoneName", zone.toString().toUpperCase(Locale.ENGLISH));
		query.setLong("gameId", gameId.longValue());
		query.setLong("deckId", deckId.longValue());

		final List<MagicCard> cards = query.list();
		return (cards == null) ? Integer.valueOf(0) : Integer.valueOf(cards.size());
	}

	// The MagicCard or Counter are updated by cascade
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public void deleteCounter(final Counter counter, final MagicCard card, final Token token)
	{
		if ((card != null) && (card.getCounters() != null) && (!card.getCounters().isEmpty())
			&& (token == null))
		{
			for (final MagicCard mc : HatchetHarrySession.get().getPlayer().getDeck().getCards())
			{
				if (mc.equals(card))
				{
					this.deckDao.getSession().saveOrUpdate(
						this.getDeck(counter.getCard().getDeck().getDeckId().longValue()));
					this.playerDao.getSession().saveOrUpdate(
						this.getPlayer(counter.getCard().getDeck().getPlayerId()));
					counter.getCard().setDeck(null);
					counter.setCard(null);

					final Set<Counter> set = new HashSet<>();
					for (final Counter c : card.getCounters())
					{
						if (c.getId().longValue() != counter.getId().longValue())
						{
							set.add(c);
						}
						else
						{
							LOGGER.info("true: " + counter);
						}
					}
					mc.setCounters(set);
					mc.setZone(CardZone.BATTLEFIELD);
					this.magicCardDao.getSession().saveOrUpdate(
						this.magicCardDao.getSession().merge(mc));

					this.counterDao.getSession()
						.delete(this.counterDao.getSession().merge(counter));
					break;
				}
			}
		}
		else if ((token != null) && (token.getCounters() != null)
			&& (!token.getCounters().isEmpty()))
		{
			token.getCounters().remove(counter);
			this.updatePlayer(HatchetHarrySession.get().getPlayer());
			this.updateToken(token);
			this.counterDao.getSession().delete(this.counterDao.getSession().merge(counter));
		}
	}

	@Transactional
	public void updateCounter(final Counter counter)
	{
		final Query query = this.counterDao.getSession().createSQLQuery(
			"UPDATE Counter SET numberOfCounters = :counters WHERE counterId = :id");
		query.setLong("counters", counter.getNumberOfCounters().longValue());
		query.setLong("id", counter.getId().longValue());
		query.executeUpdate();
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveOrUpdateArrow(final Arrow arrow)
	{
		this.arrowDao.getSession().saveOrUpdate(arrow);
	}


	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void deleteArrow(final Arrow arrow)
	{
		this.arrowDao.getSession().delete(arrow);
	}

	@Transactional(readOnly = true)
	public List<Arrow> loadAllArrowsForAGame(final Long gameId)
	{
		final Session session = this.arrowDao.getSession();

		final Query query = session.createQuery("from Arrow where gameId = :id");
		query.setLong("id", gameId.longValue());

		return query.list();
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void deleteAllArrowsForAGame(final Long gameId)
	{
		final List<Arrow> allArrows = this.loadAllArrowsForAGame(gameId);
		final Session session = this.arrowDao.getSession();

		for (final Arrow arrow : allArrows)
		{
			session.delete(arrow);
		}
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveMessageWithoutDuplicate(final ConsoleLogMessage consoleLogMessage)
	{
		if ((consoleLogMessage.getGameId() != null) && (consoleLogMessage.getMessage() != null))
		{
			this.consoleLogMessageDao.getSession().save(consoleLogMessage);
		}
	}

	@Transactional(readOnly = true)
	public List<ConsoleLogMessage> loadAllConsoleLogMessagesForAGame(final Long gameId)
	{
		final Session session = this.consoleLogMessageDao.getSession();

		final Query query = session.createQuery("from ConsoleLogMessage where gameId = :id");
		query.setLong("id", gameId.longValue());

		return query.list();
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void deleteAllMessagesForAGame(final Long gameId)
	{
		final List<ConsoleLogMessage> allMessages = this.loadAllConsoleLogMessagesForAGame(gameId);
		final Session session = this.consoleLogMessageDao.getSession();

		for (final ConsoleLogMessage consoleLogMessage : allMessages)
		{
			session.delete(consoleLogMessage);
		}
	}

	@Transactional(readOnly = true)
	public User getUser(final String username)
	{
		return this.userDao.load(username);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveOrUpdateUser(final User user)
	{
		this.userDao.getSession().saveOrUpdate(user);
	}


	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveChatMessage(final ChatMessage msg)
	{
		this.chatMessageDao.save(msg);
	}

	@Transactional(readOnly = true)
	public List<ChatMessage> loadAllChatMessagesForAGame(final Long gameId)
	{
		final Session session = this.chatMessageDao.getSession();

		final Query query = session.createQuery("from ChatMessage where gameId = :id order by id");
		query.setLong("id", gameId.longValue());

		return query.list();
	}

	@Transactional(readOnly = true)
	public Long getPendingGame(final Format desiredFormat, final int desiredPlayers)
	{
		final Session session = this.gameDao.getSession();

		// < (and not <=) because when joining, there is only the player who
		// have created the game
		final Query query = session
			.createQuery("from Game g where g.pending is true and desiredFormat = :desiredFormat and size(g.players) < :desiredPlayers and g.desiredNumberOfPlayers = :desiredPlayers and g.gameId <> :gameIdToExclude");
		query.setString("desiredFormat", desiredFormat.name());
		query.setInteger("desiredPlayers", desiredPlayers);
		query.setLong("gameIdToExclude", HatchetHarrySession.get().getGameId().longValue());
		query.setMaxResults(1);

		final Game g = (Game)query.uniqueResult();
		if (null == g)
		{
			return null;
		}
		return g.getId();
	}

}
