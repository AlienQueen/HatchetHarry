package org.alienlabs.hatchetharry.service;

import java.util.UUID;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.persistence.dao.MagicCardDao;
import org.alienlabs.hatchetharry.persistence.dao.QueryParam;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.factory.annotation.Required;

public class PersistenceService
{
	@SpringBean
	private MagicCardDao magicCardDao;

	public PersistenceService()
	{
		// InjectorHolder.getInjector().inject(this);
	}

	public MagicCard getFirstCardOfGame()
	{
		final MagicCard c = new MagicCard();
		c.setGameId(1l);

		return this.magicCardDao.load(1l);
	}

	public void saveCard(final MagicCard c)
	{

		this.magicCardDao.save(c);
	}

	public MagicCard getCardFromUuid(final UUID uuid)
	{
		final MagicCard c = new MagicCard();
		c.setUuidObject(uuid);

		return this.magicCardDao.find(new QueryParam(0, 0), c).next();
	}

	@Required
	public void setMagicCardDao(final MagicCardDao _magicCardDao)
	{
		this.magicCardDao = _magicCardDao;
	}

}
