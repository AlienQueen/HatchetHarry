package org.alienlabs.hatchetharry.serverSideTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.service.ImportDeckService;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ImportDeckServiceTest
{
	static final ClassPathXmlApplicationContext CLASS_PATH_XML_APPLICATION_CONTEXT = new ClassPathXmlApplicationContext(
			new String[] { "applicationContext.xml" });
	protected static transient WicketTester tester;
	protected static HatchetHarryApplication webApp;
	protected static transient ApplicationContext context;

	@BeforeClass
	public static void setUpBeforeClass()
	{
		ImportDeckServiceTest.webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;


			@Override
			public void init()
			{
				ImportDeckServiceTest.context = ImportDeckServiceTest.CLASS_PATH_XML_APPLICATION_CONTEXT;
				this.getComponentInstantiationListeners().add(
						new SpringComponentInjector(this, ImportDeckServiceTest.context, true));
			}
		};
		ImportDeckServiceTest.tester = new WicketTester(ImportDeckServiceTest.webApp);
		ImportDeckServiceTest.context.getBean(PersistenceService.class).resetDb();
	}

	@Test
	public void testImportDeck() throws FileNotFoundException, IOException
	{
		// Init
		final PersistenceService persistenceService = ImportDeckServiceTest.context
				.getBean(PersistenceService.class);
		final ImportDeckService importDeckService = ImportDeckServiceTest.context
				.getBean(ImportDeckService.class);

		final boolean auraBantAlreadyExists = (null != persistenceService
				.getDeckArchiveByName("Aura Bant"));

		final int initialNumberOfDeckArchives = persistenceService.countDeckArchives();
		final int initialNumberOfDecks = persistenceService.countDecks();
		final int initialNumberOfCollectibleCards = persistenceService.countCollectibleCards();
		final int initialNumberOfMagicCards = persistenceService.countMagicCards();

		final File deck = new File("/home/nostromo/Aura Bant.txt");
		final byte[] content = new byte[475];

		final FileInputStream fis = new FileInputStream(deck);
		if (fis.read(content) == -1)
		{
			fis.close();
			Assert.fail("Aura Bant.txt seems to be empty");
		}
		fis.close();

		final String deckContent = new String(content, "UTF-8");

		// Run
		importDeckService.importDeck(deckContent, "Aura Bant", true);

		// Verify
		final int finalNumberOfDeckArchives = persistenceService.countDeckArchives();
		final int finalNumberOfDecks = persistenceService.countDecks();
		final int finalNumberOfCollectibleCards = persistenceService.countCollectibleCards();
		final int finalNumberOfMagicCards = persistenceService.countMagicCards();

		if (auraBantAlreadyExists)
		{
			Assert.assertEquals(initialNumberOfDeckArchives, finalNumberOfDeckArchives);
			Assert.assertEquals(initialNumberOfDecks, finalNumberOfDecks);
			Assert.assertEquals(initialNumberOfCollectibleCards, finalNumberOfCollectibleCards);
			Assert.assertEquals(initialNumberOfMagicCards, finalNumberOfMagicCards);
		}
		else
		{
			Assert.assertEquals(initialNumberOfDeckArchives + 1, finalNumberOfDeckArchives);
			Assert.assertEquals(initialNumberOfDecks + 1, finalNumberOfDecks);
			Assert.assertEquals(initialNumberOfCollectibleCards + 60, finalNumberOfCollectibleCards);
			Assert.assertEquals(initialNumberOfMagicCards + 60, finalNumberOfMagicCards);
		}
	}

}
