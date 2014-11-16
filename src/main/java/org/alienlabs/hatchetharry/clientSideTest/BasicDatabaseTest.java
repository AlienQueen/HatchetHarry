package org.alienlabs.hatchetharry.clientSideTest;

import java.io.Serializable;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.persistence.dao.CardCollectionDao;
import org.alienlabs.hatchetharry.persistence.dao.CollectibleCardDao;
import org.alienlabs.hatchetharry.persistence.dao.DeckArchiveDao;
import org.alienlabs.hatchetharry.persistence.dao.DeckDao;
import org.alienlabs.hatchetharry.persistence.dao.GameDao;
import org.alienlabs.hatchetharry.persistence.dao.MagicCardDao;
import org.alienlabs.hatchetharry.persistence.dao.PlayerDao;
import org.alienlabs.hatchetharry.persistence.dao.SideDao;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BasicDatabaseTest implements Serializable
{
	static final ClassPathXmlApplicationContext CLASS_PATH_XML_APPLICATION_CONTEXT = new ClassPathXmlApplicationContext(
			new String[] { "applicationContext.xml" });
	private static final long serialVersionUID = 1L;
	static transient WicketTester tester;
	static HatchetHarryApplication webApp;
	static transient ApplicationContext context;
	static String pageDocument;
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

	@BeforeClass
	public static void setUpBeforeClass()
	{
		BasicDatabaseTest.webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;

			@Override
			@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "No choice as @BeforeClass is static and HatchetHarryApplication isn't")
			public void init()
			{
				BasicDatabaseTest.context = BasicDatabaseTest.CLASS_PATH_XML_APPLICATION_CONTEXT;
				this.getComponentInstantiationListeners().add(
						new SpringComponentInjector(this, BasicDatabaseTest.context, true));
			}
		};

		BasicDatabaseTest.tester = new WicketTester(BasicDatabaseTest.webApp);

		// start and render the test page
		BasicDatabaseTest.tester.startPage(HomePage.class);

		// assert rendered page class
		BasicDatabaseTest.tester.assertRenderedPage(HomePage.class);

		BasicDatabaseTest.pageDocument = BasicDatabaseTest.tester.getLastResponse().getDocument();
	}

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
