package org.alienlabs.hatchetharry.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.persistence.dao.CollectibleCardDao;
import org.alienlabs.hatchetharry.persistence.dao.DeckDao;
import org.alienlabs.hatchetharry.persistence.dao.GameDao;
import org.alienlabs.hatchetharry.persistence.dao.MagicCardDao;
import org.alienlabs.hatchetharry.persistence.dao.PlayerDao;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

public class PersistenceService
{
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
		@SuppressWarnings("unchecked")
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
	public Player updatePlayer(final Player p)
	{
		final Session session = this.playerDao.getSession();
		session.merge(p);
		session.flush();
		return p;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Game updateGame(final Game g)
	{
		final Session session = this.gameDao.getSession();
		session.merge(g);
		session.flush();
		return g;
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

	@Transactional
	public boolean getPlayerByGame(final long l)
	{
		final Session session = this.playerDao.getSession();

		final Query query = session.createQuery("from Player player0_ where player0_.gameId=?");
		query.setLong(0, l);

		return query.list().size() > 0;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Player> getAllPlayersOfGame(final long l)
	{
		final Session session = this.playerDao.getSession();

		final Query query = session
				.createQuery("select player0_ from Player player0_ join player0_.game as g where g.gameId=?");
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

	@Transactional
	public Deck getDeck(final long l)
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session.createQuery("from Deck deck0_ where deck0_.playerId=?");
		query.setLong(0, l);

		return (Deck)query.uniqueResult();
	}

	@Transactional
	public List<?> getAllCardsFromDeck(final long l)
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session.createQuery("from MagicCard card0_ where card0_.deck=?");
		query.setLong(0, l);

		return query.list();
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

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Deck> getAllDecks()
	{
		final Session session = this.deckDao.getSession();
		final Query query = session.createQuery("from Deck deck0_ where deck0_.playerId != -1");

		return query.list();
	}

	@Transactional
	public Game createNewGame(final Player player)
	{
		this.gameDao.getSession();

		final Game game = new Game();

		final List<Player> list = new ArrayList<Player>();
		list.add(player);
		game.setPlayers(list);

		return this.gameDao.save(game);
	}

	@Transactional
	public MagicCard findCardByName(final String _name)
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session.createQuery("from MagicCard m where m.title = ?");
		query.setString(0, _name);
		return (MagicCard)query.uniqueResult();

	}

	@Transactional
	public Game getGame(final Long _id)
	{
		return this.gameDao.load(_id);
	}
}
