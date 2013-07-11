package org.alienlabs.hatchetharry.model;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class DeckTest
{

	@Test
	@Ignore("refactor me")
	public void testReorderMagicCards()
	{
		// Init
		final MagicCard mc1 = new MagicCard("smallImageFilename5", "bigImageFilename",
				"thumbnailFilename", "title", "description");
		final MagicCard mc2 = new MagicCard("smallImageFilename4", "bigImageFilename",
				"thumbnailFilename", "title", "description");
		final MagicCard mc3 = new MagicCard("smallImageFilename3", "bigImageFilename",
				"thumbnailFilename", "title", "description");
		final MagicCard mc4 = new MagicCard("smallImageFilename2", "bigImageFilename",
				"thumbnailFilename", "title", "description");
		final MagicCard mc5 = new MagicCard("smallImageFilename1", "bigImageFilename",
				"thumbnailFilename", "title", "description");

		mc1.setZone(CardZone.HAND);
		mc2.setZone(CardZone.HAND);
		mc3.setZone(CardZone.HAND);
		mc4.setZone(CardZone.HAND);
		mc5.setZone(CardZone.HAND);

		mc1.setZoneOrder(0l);
		mc2.setZoneOrder(1l);
		mc3.setZoneOrder(2l);
		mc4.setZoneOrder(3l);
		mc5.setZoneOrder(4l);

		final List<MagicCard> initial = Arrays.asList(new MagicCard[] { mc1, mc2, mc3, mc4, mc5 });
		final MagicCard toAdd = new MagicCard("smallImageFilename0", "bigImageFilename0",
				"thumbnailFilename0", "title0", "description0");

		// Run
		final List<MagicCard> ordered = new Deck().reorderMagicCards(initial);
		ordered.add(0, toAdd);

		// Verify
		Assert.assertEquals("smallImageFilename0", ordered.get(0).getSmallImageFilename());
		Assert.assertEquals(0l, ordered.get(0).getZoneOrder().longValue());

		Assert.assertEquals("smallImageFilename5", ordered.get(1).getSmallImageFilename());
		Assert.assertEquals(1l, ordered.get(1).getZoneOrder().longValue());

		Assert.assertEquals("smallImageFilename4", ordered.get(2).getSmallImageFilename());
		Assert.assertEquals(2l, ordered.get(2).getZoneOrder().longValue());

		Assert.assertEquals("smallImageFilename3", ordered.get(3).getSmallImageFilename());
		Assert.assertEquals(3l, ordered.get(3).getZoneOrder().longValue());

		Assert.assertEquals("smallImageFilename2", ordered.get(4).getSmallImageFilename());
		Assert.assertEquals(4l, ordered.get(4).getZoneOrder().longValue());

		Assert.assertEquals("smallImageFilename1", ordered.get(5).getSmallImageFilename());
		Assert.assertEquals(5l, ordered.get(5).getZoneOrder().longValue());
	}

}
