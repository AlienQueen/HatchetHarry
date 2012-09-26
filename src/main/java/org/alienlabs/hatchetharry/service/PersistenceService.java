package org.alienlabs.hatchetharry.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.Side;
import org.alienlabs.hatchetharry.persistence.dao.CollectibleCardDao;
import org.alienlabs.hatchetharry.persistence.dao.DeckDao;
import org.alienlabs.hatchetharry.persistence.dao.GameDao;
import org.alienlabs.hatchetharry.persistence.dao.MagicCardDao;
import org.alienlabs.hatchetharry.persistence.dao.PlayerDao;
import org.alienlabs.hatchetharry.persistence.dao.SideDao;
import org.alienlabs.hatchetharry.view.component.CardPanel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
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
	private CollectibleCardDao collectibleCardDao;
	@SpringBean
	private MagicCardDao magicCardDao;
	@SpringBean
	private GameDao gameDao;
	@SpringBean
	private SideDao sideDao;

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

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void saveOrUpdateGame(final Game g)
	{
		final Session session = this.gameDao.getSession();
		session.saveOrUpdate(g);
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

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public Deck saveDeck(final Deck d)
	{
		final Session session = this.deckDao.getSession();
		session.update(d);

		return d;
	}

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public Side saveSide(final Side s)
	{
		final Session session = this.sideDao.getSession();
		session.save(s);

		return s;
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

		final Query query = session
				.createQuery("select player0_ from Player player0_ join player0_.games as g where g.gameId=?");
		query.setLong(0, l);
		return query.list();
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
		return list.size() > 0;
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public Deck getDeck(final long l)
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session.createQuery("from Deck deck0_ where deck0_.playerId=?");
		query.setLong(0, l);
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

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public Game createGame(final Player player, final Long gameId)
	{
		this.gameDao.getSession();

		Game game = this.gameDao.load(gameId);
		if (null == game)
		{
			game = new Game();
		}

		final Set<Player> set = game.getPlayers();
		set.add(player);
		game.setPlayers(set);
		game.setId(gameId);

		return this.gameDao.save(game);
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
	public List<MagicCard> getAllCardsInBattleFieldForAPlayer(final Long playerId)
	{
		final Session session = this.magicCardDao.getSession();

		final List<CardPanel> allCardsInBattleField = HatchetHarrySession.get()
				.getAllCardsInBattleField();
		final List<String> allUuidsOfCardsInBattleField = new ArrayList<String>();
		for (final CardPanel cp : allCardsInBattleField)
		{
			allUuidsOfCardsInBattleField.add(cp.getUuid().toString());
		}

		final Query query = session
				.createQuery("select m from MagicCard m join m.deck as mcd where m.uuid in (:uuids) and mcd.playerId = :playerId ");
		query.setParameterList("uuids", allUuidsOfCardsInBattleField);
		query.setLong("playerId", playerId);
		try
		{
			return query.list();
		}
		catch (final ObjectNotFoundException e)
		{
			PersistenceService.LOGGER.error("Error retrieving cards in battlefield for player: "
					+ playerId + " => no result found", e);
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

}
