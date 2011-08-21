package org.alienlabs.hatchetharry.service;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.persistence.dao.MagicCardDao;
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

	@Required
	public void setMagicCardDao(final MagicCardDao _magicCardDao)
	{
		this.magicCardDao = _magicCardDao;
	}

}
