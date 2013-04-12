package org.alienlabs.hatchetharry.clientSideTest;

import java.io.Serializable;

import org.alienlabs.hatchetharry.persistence.dao.CardCollectionDao;
import org.alienlabs.hatchetharry.persistence.dao.CollectibleCardDao;
import org.alienlabs.hatchetharry.persistence.dao.DeckArchiveDao;
import org.alienlabs.hatchetharry.persistence.dao.DeckDao;
import org.alienlabs.hatchetharry.persistence.dao.GameDao;
import org.alienlabs.hatchetharry.persistence.dao.MagicCardDao;
import org.alienlabs.hatchetharry.persistence.dao.PlayerDao;
import org.alienlabs.hatchetharry.persistence.dao.SideDao;
import org.alienlabs.hatchetharry.serverSideTest.SpringContextLoaderBaseTest;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Required;

public class BasicDatabaseTest extends SpringContextLoaderBaseTest implements Serializable
{
	@SpringBean
	private CardCollectionDao cardCollectionDao;
	@SpringBean
	private CollectibleCardDao collectibleCardDao;
	@SpringBean
	private DeckArchiveDao deckArchiveDao;
	@SpringBean
	private DeckDao deckDao;
	@SpringBean
	private GameDao gameDao;
	@SpringBean
	private MagicCardDao magicCardDao;
	@SpringBean
	private PlayerDao playerDao;
	@SpringBean
	private SideDao sideDao;

	private static final long serialVersionUID = 1L;

	@Before
	public void setUp()
	{
		Injector.get().inject(this);
	}

	@Test
	public void testDependencyInjection()
	{
		Assert.assertNotNull(this.cardCollectionDao);
		Assert.assertNotNull(this.collectibleCardDao);
		Assert.assertNotNull(this.deckArchiveDao);
		Assert.assertNotNull(this.deckDao);
		Assert.assertNotNull(this.gameDao);
		Assert.assertNotNull(this.magicCardDao);
		Assert.assertNotNull(this.playerDao);
		Assert.assertNotNull(this.sideDao);
	}

	@Test
	public void testDatabaseConnection()
	{
		Assert.assertTrue(this.gameDao.count() > 0);
	}

	@Required
	public void setCardCollectionDao(final CardCollectionDao _cardCollectionDao)
	{
		this.cardCollectionDao = _cardCollectionDao;
	}

	@Required
	public void setCollectibleCardDao(final CollectibleCardDao _collectibleCardDao)
	{
		this.collectibleCardDao = _collectibleCardDao;
	}

	@Required
	public void setDeckArchiveDao(final DeckArchiveDao _deckArchiveDao)
	{
		this.deckArchiveDao = _deckArchiveDao;
	}

	@Required
	public void setDeckDao(final DeckDao _deckDao)
	{
		this.deckDao = _deckDao;
	}

	@Required
	public void setGameDao(final GameDao _gameDao)
	{
		this.gameDao = _gameDao;
	}

	@Required
	public void setMagicCardDao(final MagicCardDao _magicCardDao)
	{
		this.magicCardDao = _magicCardDao;
	}

	@Required
	public void setPlayerDao(final PlayerDao _playerDao)
	{
		this.playerDao = _playerDao;
	}

	@Required
	public void setSideDao(final SideDao _sideDao)
	{
		this.sideDao = _sideDao;
	}

}
