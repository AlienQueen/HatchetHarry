package org.alienlabs.hatchetharry.serverSideTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;

import org.alienlabs.hatchetharry.service.ImportDeckService;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.junit.Test;

public class ImportDeckServiceTest extends TestParent
{
	@Test
	public void testImportDeck() throws FileNotFoundException, IOException
	{
		// Init
		final PersistenceService persistenceService = TestParent.context
				.getBean(PersistenceService.class);
		final ImportDeckService importDeckService = TestParent.context
				.getBean(ImportDeckService.class);

		final int initialNumberOfDeckArchives = persistenceService.countDeckArchives();
		final int initialNumberOfDecks = persistenceService.countDecks();
		final int initialNumberOfCollectibleCards = persistenceService.countCollectibleCards();
		final int initialNumberOfMagicCards = persistenceService.countMagicCards();

		final File deck = new File("/home/nostromo/Aura Bant.txt");
		final byte[] content = new byte[475];
		new FileInputStream(deck).read(content);

		final String deckContent = new String(content, "UTF-8");

		// Run
		importDeckService.importDeck(deckContent, "Aura Bant");

		// Verify
		final int finalNumberOfDeckArchives = persistenceService.countDeckArchives();
		final int finalNumberOfDecks = persistenceService.countDecks();
		final int finalNumberOfCollectibleCards = persistenceService.countCollectibleCards();
		final int finalNumberOfMagicCards = persistenceService.countMagicCards();

		Assert.assertEquals(initialNumberOfDeckArchives + 1, finalNumberOfDeckArchives);
		Assert.assertEquals(initialNumberOfDecks + 1, finalNumberOfDecks);
		Assert.assertEquals(initialNumberOfCollectibleCards + 60, finalNumberOfCollectibleCards);
		Assert.assertEquals(initialNumberOfMagicCards + 60, finalNumberOfMagicCards);
	}
}
