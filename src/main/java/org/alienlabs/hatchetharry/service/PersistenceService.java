package org.alienlabs.hatchetharry.service;

import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.persistence.dao.CollectibleCardDao;
import org.alienlabs.hatchetharry.persistence.dao.DeckDao;
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

	public PersistenceService()
	{
	}

	public MagicCard getFirstCardOfGame()
	{
		final MagicCard c = new MagicCard();
		c.setGameId(1l);
		c.setUuidObject(UUID.randomUUID());
		return this.magicCardDao.load(1l);
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
	public MagicCard saveCardByGeneratingItsUuid(final MagicCard _c)
	{
		final MagicCard c = _c;
		c.setUuid(UUID.randomUUID().toString());
		c.setGameId(1l);

		final Session session = this.magicCardDao.getSession();
		final Long id = (Long)session.save(c);
		c.setId(id);
		return c;

	}

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
	public List<MagicCard> getFirstHand()
	{
		final Session session = this.magicCardDao.getSession();

		final Query query = session
				.createQuery("from MagicCard magiccard0_ where magiccard0_.gameId=?");
		query.setLong(0, 1);
		query.setFirstResult(1);
		query.setMaxResults(7);
		@SuppressWarnings("unchecked")
		final List<MagicCard> cards = query.list();

		return cards;
	}

	@Transactional
	public Long savePlayer(final Player p)
	{
		final Session session = this.playerDao.getSession();
		final Long id = (Long)session.save(p);
		p.setId(id);
		return id;
	}

	@Transactional
	public Deck saveDeck(final Deck d)
	{
		final Session session = this.deckDao.getSession();
		final Long id = (Long)session.save(d);
		d.setId(id);
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
		return query.list().size() > 0;
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


}
