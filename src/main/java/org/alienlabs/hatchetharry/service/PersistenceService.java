package org.alienlabs.hatchetharry.service;

import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.persistence.dao.MagicCardDao;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

public class PersistenceService
{
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


	@Transactional
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
		query.setMaxResults(6);
		@SuppressWarnings("unchecked")
		final List<MagicCard> cards = query.list();

		return cards;
	}

	@Required
	public void setMagicCardDao(final MagicCardDao _magicCardDao)
	{
		this.magicCardDao = _magicCardDao;
	}

}
