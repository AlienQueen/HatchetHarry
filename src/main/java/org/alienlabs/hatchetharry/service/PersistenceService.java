package org.alienlabs.hatchetharry.service;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.CollectibleCard;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.DeckArchive;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.Side;
import org.alienlabs.hatchetharry.persistence.dao.CardCollectionDao;
import org.alienlabs.hatchetharry.persistence.dao.CollectibleCardDao;
import org.alienlabs.hatchetharry.persistence.dao.DeckArchiveDao;
import org.alienlabs.hatchetharry.persistence.dao.DeckDao;
import org.alienlabs.hatchetharry.persistence.dao.GameDao;
import org.alienlabs.hatchetharry.persistence.dao.MagicCardDao;
import org.alienlabs.hatchetharry.persistence.dao.PlayerDao;
import org.alienlabs.hatchetharry.persistence.dao.SideDao;
import org.alienlabs.hatchetharry.view.component.CardPanel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

public class PersistenceService implements Serializable
{
	private static final long serialVersionUID = 1L;

	static final Logger LOGGER = LoggerFactory.getLogger(PersistenceService.class);

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
	private CardCollectionDao cardCollectionDao;

	public PersistenceService()
	{
	}

	@Transactional
	public MagicCard getFirstCardOfGame(final long gameId)
	{
		return this.magicCardDao.load(gameId);
	}

	@Transactional
	public MagicCard getNthCardOfGame(final Long index)
	{
		final Session session = this.magicCardDao.getSession();
		final Query query = session
				.createQuery("from MagicCard magiccard0_ where magiccard0_.id=?");
		query.setLong(0, index);
		final MagicCard c = (MagicCard)query.uniqueResult();

		return c;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public MagicCard saveCardByGeneratingItsUuid(final MagicCard _c, final long gameId)
	{
		final MagicCard c = _c;
		c.setUuid(UUID.randomUUID().toString());
		c.setGameId(gameId);

		final Session session = this.magicCardDao.getSession();
		final Long id = (Long)session.save(c);
		c.setId(id);

		return c;
	}

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public void saveCard(final MagicCard c)
	{
		this.magicCardDao.save(c);
	}

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public void saveOrUpdateCard(final MagicCard c)
	{
		this.magicCardDao.getSession().saveOrUpdate(c);
	}

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public void updateCard(final MagicCard c)
	{
		this.magicCardDao.getSession().update(c);
	}

	@Transactional
	public MagicCard getCardFromUuid(final UUID uuid)
	{
		final Session session = this.magicCardDao.getSession();
		final Query query = session
				.createQuery("from MagicCard magiccard0_ where magiccard0_.uuid=?");
		query.setString(0, uuid.toString());
		PersistenceService.LOGGER.debug("card UUID: " + uuid.toString());
		final MagicCard c = (MagicCard)query.uniqueResult();

		return c;
	}

	@Transactional
	public List<MagicCard> getFirstHand(final long gameId)
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session
				.createQuery("from MagicCard magiccard0_ where magiccard0_.gameId=?");
		query.setLong(0, gameId);
		query.setFirstResult(0);
		query.setMaxResults(7);
		final List<MagicCard> cards = query.list();

		return cards;
	}

	@Transactional
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
	public void updatePlayer(final Player p)
	{
		final Session session = this.playerDao.getSession();
		if (p.getId() != null)
		{
			session.merge(p);
			return;
		}
		session.saveOrUpdate(p);
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void saveGame(final Game g)
	{
		final Session session = this.gameDao.getSession();
		session.save(g);
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void saveOrUpdateGame(final Game g)
	{
		final Session session = this.gameDao.getSession();
		session.saveOrUpdate(g);
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void updateGame(final Game g)
	{
		this.gameDao.save(g);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Long savePlayer(final Player p)
	{
		final Session session = this.playerDao.getSession();
		final Long l = (Long)session.save(p);
		return (l);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveOrUpdatePlayer(final Player p)
	{
		final Session session = this.playerDao.getSession();
		session.saveOrUpdate(p);
	}

	@Transactional
	public int countPlayers(final long l)
	{
		final Session session = this.playerDao.getSession();

		final Query query = session.createQuery("from Player player0_ where player0_.gameId=?");
		query.setLong(0, l);

		return query.list().size();
	}

	@Transactional
	public int countDeckArchives()
	{
		final Session session = this.deckArchiveDao.getSession();

		final Query query = session.createQuery("from DeckArchive");

		return query.list().size();
	}

	@Transactional
	public int countCollectibleCards()
	{
		final Session session = this.collectibleCardDao.getSession();

		final Query query = session.createQuery("from CollectibleCard");

		return query.list().size();
	}

	@Transactional
	public int countCollectibleCardsInDeckArchive(final DeckArchive deckArchive)
	{
		final Session session = this.collectibleCardDao.getSession();

		final Query query = session
				.createQuery("from CollectibleCard cc where cc.deckArchiveId = ?");
		query.setLong(0, deckArchive.getDeckArchiveId());

		return query.list().size();
	}

	@Transactional
	public List<CollectibleCard> giveAllCollectibleCardsInDeckArchive(final DeckArchive deckArchive)
	{
		final Session session = this.collectibleCardDao.getSession();

		final Query query = session
				.createQuery("from CollectibleCard cc where cc.deckArchiveId = ?");
		query.setLong(0, deckArchive.getDeckArchiveId());

		return query.list();
	}

	@Transactional
	public int countMagicCards()
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session.createQuery("from MagicCard");

		return query.list().size();
	}

	@Transactional
	public int countDecks()
	{
		final Session session = this.deckDao.getSession();

		final Query query = session.createQuery("from Deck");

		return query.list().size();
	}

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public Deck updateDeck(final Deck d)
	{
		final Session session = this.deckDao.getSession();
		session.saveOrUpdate(d);

		return d;
	}

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public Deck saveDeck(final Deck d)
	{
		final Session session = this.deckDao.getSession();
		session.update(d);

		return d;
	}

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public Deck saveOrUpdateDeck(final Deck d)
	{
		final Session session = this.deckDao.getSession();
		session.saveOrUpdate(d);

		return d;
	}

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public Side saveSide(final Side s)
	{
		return this.sideDao.save(s);
	}

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public void saveSides(final Set<Side> sides)
	{
		for (final Side s : sides)
		{
			this.sideDao.save(s);
		}
	}

	@Transactional
	public Player getPlayer(final Long l)
	{
		return this.playerDao.load(l);
	}

	@Transactional
	public boolean getPlayerByGame(final long l)
	{
		final Session session = this.playerDao.getSession();

		final Query query = session.createQuery("from Player player0_ where player0_.gameId=?");
		query.setLong(0, l);

		return query.list().size() > 0;
	}

	@Transactional
	public List<Player> getAllPlayersOfGame(final long l)
	{
		final Session session = this.playerDao.getSession();

		final SQLQuery query = session
				.createSQLQuery("select player0_.* from Player player0_ where player0_.game_gameId=?");
		query.addEntity(Player.class);
		query.setLong(0, l);
		return query.list();
	}

	@Transactional
	public DeckArchive getDeckArchiveByName(final String name)
	{
		final Session session = this.deckDao.getSession();

		final SQLQuery query = session
				.createSQLQuery("select da.* from Deck d, DeckArchive da where da.deckName=? and da.deckArchiveId = d.Deck_DeckArchive");
		query.addEntity(DeckArchive.class);
		query.setString(0, name);
		return (DeckArchive)query.uniqueResult();
	}

	@Transactional
	public boolean getPlayerByJsessionId(final String jsessionid)
	{
		final Session session = this.playerDao.getSession();

		final Query query = session.createQuery("from Player player0_ where player0_.jsessionid=?");
		query.setString(0, jsessionid);

		return query.list().size() > 0;
	}

	@Transactional
	public boolean doesCollectibleCardAlreadyExistsInDb(final String title)
	{
		final Session session = this.collectibleCardDao.getSession();

		final Query query = session.createQuery("from CollectibleCard cc0_ where cc0_.title=?");
		query.setString(0, title);
		final List<?> list = query.list();
		return ((list != null) && (list.size() > 0));
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public Deck getDeck(final long deckId)
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session.createQuery("from Deck deck0_ where deck0_.deckId=?");
		query.setLong(0, deckId);
		query.setMaxResults(1);
		return (Deck)query.uniqueResult();
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public List<MagicCard> getAllCardsFromDeck(final long l)
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session.createQuery("from MagicCard card0_ where card0_.deck=?");
		query.setLong(0, l);

		List<MagicCard> list = new ArrayList<MagicCard>();
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

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public List<MagicCard> getAllCardsInLibraryForDeckAndPlayer(final Long gameId,
			final Long playerId, final Long deckId)
	{
		final Session session = this.magicCardDao.getSession();

		final SQLQuery query = session
				.createSQLQuery("select mc.* from MagicCard mc, Deck d where mc.gameId = ? and mc.zone = ? and d.playerId = ? and mc.card_deck = d.deckId and d.deckId = ?");
		query.addEntity(MagicCard.class);
		query.setLong(0, gameId);
		query.setString(1, CardZone.LIBRARY.toString());
		query.setLong(2, playerId);
		query.setLong(3, deckId);

		List<MagicCard> list = new ArrayList<MagicCard>();
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

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public void saveCollectibleCard(final CollectibleCard cc)
	{
		this.collectibleCardDao.save(cc);
	}

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public void saveDeckArchive(final DeckArchive da)
	{
		this.deckArchiveDao.save(da);
	}

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public void saveOrUpdateDeckArchive(final DeckArchive da)
	{
		this.deckArchiveDao.getSession().saveOrUpdate(da);
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
	public void setCardCollectionDao(final CardCollectionDao _cardCollectionDao)
	{
		this.cardCollectionDao = _cardCollectionDao;
	}

	@Transactional
	public List<?> getCardsByDeckId(final long gameId)
	{
		final Session session = this.magicCardDao.getSession();
		final Query query = session
				.createQuery("select card0_ from MagicCard card0_ , Deck deck0_ where card0_.deck  = deck0_.deckId  and deck0_.deckId = ?");
		query.setLong(0, gameId);

		return query.list();
	}

	@Transactional
	public List<Deck> getAllDecks()
	{
		final Session session = this.deckDao.getSession();
		final Query query = session.createQuery("from Deck deck0_ where deck0_.playerId != -1");

		return query.list();
	}

	@Transactional
	public List<DeckArchive> getAllDeckArchives()
	{
		final Session session = this.deckDao.getSession();
		final Query query = session.createQuery("from DeckArchive d where d.deckName != null");

		return query.list();
	}

	@Transactional
	public Deck getFirstDeckFromDeckArchiveForPlayer(final Long playerId) // TODO:
																			// remove
																			// this
	{
		final Session session = this.deckDao.getSession();
		final SQLQuery query = session
				.createSQLQuery("select d.deckId, d.playerId, distinct(d.Deck_DeckArchive) from Deck d, DeckArchive da where d.Deck_DeckArchive = da.deckArchiveId");
		query.addEntity(Deck.class);

		return (Deck)query.list().get(0);
	}

	@Transactional
	public List<Deck> getAllDecksFromDeckArchives()
	{
		final Session session = this.deckDao.getSession();
		final SQLQuery query = session
				.createSQLQuery("select dd.* from Deck dd where dd.Deck_DeckArchive in (select distinct da.deckArchiveId from  Deck de, DeckArchive da where de.Deck_DeckArchive = da.deckArchiveId and da.deckName is not null) group by dd.Deck_DeckArchive");
		query.addEntity(Deck.class);

		return query.list();
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public Game createGameAndPlayer(final Game game, final Player player)
	{
		final Set<Player> set = game.getPlayers();
		set.add(player);
		game.setPlayers(set);
		
		player.setGame(game);
		
		this.playerDao.getSession().save(player);

		this.gameDao.getSession().save(game);
		return game;
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public MagicCard findCardByName(final String _name)
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session.createQuery("from MagicCard m where m.title = ?");
		query.setString(0, _name);
		query.setMaxResults(1);
		try
		{
			return (MagicCard)query.list().get(0);
		}
		catch (final ObjectNotFoundException e)
		{
			PersistenceService.LOGGER.error("error!", e);
			return null;
		}

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public CollectibleCard findCollectibleCardByName(final String title)
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session.createQuery("from CollectibleCard cc where cc.title = ?");
		query.setString(0, title);
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

	@Transactional
	public Game getGame(final Long _id)
	{
		return this.gameDao.load(_id);
	}

	@Transactional
	public void deleteMagicCard(final MagicCard mc)
	{
		this.magicCardDao.delete(mc.getId());
	}

	@Transactional
	public void deleteAllCardsInBattleField()
	{
		final List<CardPanel> inBattleField = HatchetHarrySession.get().getAllCardsInBattleField();

		if ((null != inBattleField) && (inBattleField.size() > 0))
		{
			for (int i = 0; i < inBattleField.size(); i++)
			{
				final CardPanel cp = inBattleField.get(i);
				if (null != cp)
				{
					try
					{
						PersistenceService.LOGGER.debug("card uuid= " + cp.getUuid());
						final MagicCard mc = this.getCardFromUuid(cp.getUuid());
						if (null != mc)
						{
							this.deleteMagicCard(mc);
						}
					}
					catch (final ObjectNotFoundException e)
					{
						PersistenceService.LOGGER.error("card doesn't exist", e);
					}
				}
			}
		}
	}

	@Transactional
	public List<MagicCard> getAllCardsInHandForAGameAndAPlayer(final Long gameId,
			final Long playerId, final Long deckId)
	{
		final Session session = this.magicCardDao.getSession();

		final SQLQuery query = session
				.createSQLQuery("select mc.* from MagicCard mc, Deck d where mc.gameId = ? and mc.zone = ? and d.playerId = ? and mc.card_deck = d.deckId and d.deckId = ?");
		query.addEntity(MagicCard.class);
		query.setLong(0, gameId);
		query.setString(1, CardZone.HAND.toString());
		query.setLong(2, playerId);
		query.setLong(3, deckId);

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

	@Transactional
	public List<MagicCard> getAllCardsInBattleFieldForAGame(final Long gameId)
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session
				.createQuery("select m from MagicCard m where m.gameId = :gameId and m.zone = :zone");
		query.setLong("gameId", gameId);
		query.setParameter("zone", CardZone.BATTLEFIELD);

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


	@Transactional
	public List<MagicCard> getAllCardsInBattlefieldForAGameAndAPlayer(final Long gameId,
			final Long playerId, final Long deckId)
	{
		final Session session = this.magicCardDao.getSession();

		final SQLQuery query = session
				.createSQLQuery("select mc.* from MagicCard mc, Deck d where mc.gameId = ? and mc.zone = ? and d.playerId = ? and mc.card_deck = d.deckId  and d.deckId = ?");
		query.addEntity(MagicCard.class);
		query.setLong(0, gameId);
		query.setString(1, CardZone.BATTLEFIELD.toString());
		query.setLong(2, playerId);
		query.setLong(3, deckId);

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

	// TODO remove this
	@Transactional
	public List<MagicCard> getAllCardsInGraveyardForAGame(final Long gameId)
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session
				.createQuery("select m from MagicCard m where m.gameId = :gameId and m.zone = :zone");
		query.setLong("gameId", gameId);
		query.setParameter("zone", CardZone.GRAVEYARD);

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

	@Transactional
	public List<MagicCard> getAllCardsInGraveyardForAGameAndAPlayer(final Long gameId,
			final Long playerId, final Long deckId)
	{
		final Session session = this.magicCardDao.getSession();

		final SQLQuery query = session
				.createSQLQuery("select mc.* from MagicCard mc, Deck d where mc.gameId = ? and mc.zone = ? and d.playerId = ? and mc.card_deck = d.deckId and d.deckId = ?");
		query.addEntity(MagicCard.class);
		query.setLong(0, gameId);
		query.setString(1, CardZone.GRAVEYARD.toString());
		query.setLong(2, playerId);
		query.setLong(3, deckId);

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

	@Transactional
	public List<Side> getSidesFromGame(final Game game)
	{
		final Session session = this.sideDao.getSession();
		final Query query = session.createQuery("from Side s where s.game=?");
		query.setEntity(0, game);
		final List<Side> s = query.list();

		return s;
	}

	@Transactional
	public List<BigInteger> giveAllPlayersFromGameExceptMe(final Long gameId, final Long me)
	{
		final Session session = this.gameDao.getSession();

		final Query query = session
				.createSQLQuery("select playerId from Player_Game pg where pg.gameId = ? and pg.playerId <> ?");
		query.setLong(0, gameId);
		query.setLong(1, me);

		final List<BigInteger> l = query.list();

		return l;
	}

	@Transactional
	public List<BigInteger> giveAllPlayersFromGame(final Long gameId)
	{
		final Session session = this.gameDao.getSession();

		final Query query = session
				.createSQLQuery("select playerId from Player_Game pg where pg.gameId = ?");
		query.setLong(0, gameId);

		final List<BigInteger> l = query.list();

		return l;
	}

	@Transactional
	public void deleteGame(final Game oldGame)
	{
		this.gameDao.delete(oldGame.getId());
	}

	@Transactional
	public void resetDb()
	{
		final Session session = this.gameDao.getSession();

		session.createSQLQuery("truncate table Player").executeUpdate();
		session.createSQLQuery("truncate table Game").executeUpdate();
		session.createSQLQuery("truncate table Player_Game").executeUpdate();
		session.createSQLQuery("truncate table Side").executeUpdate();
		session.createSQLQuery("truncate table Deck").executeUpdate();
		session.createSQLQuery("truncate table MagicCard").executeUpdate();
		session.createSQLQuery("truncate table MagicCard__cardPlaceholderId").executeUpdate();
		session.createSQLQuery("truncate table DeckArchive").executeUpdate();
		session.createSQLQuery("truncate table CollectibleCard").executeUpdate();
		session.createSQLQuery("truncate table Game_Side").executeUpdate();
	}

}
