package org.alienlabs.hatchetharry.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

public class RuntimeDataGenerator implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static final String[] TITLES1 = { "Goblin Guide", "Goblin Guide", "Goblin Guide",
			"Goblin Guide", "Vampire Lacerator", "Vampire Lacerator", "Vampire Lacerator",
			"Vampire Lacerator", "Bloodchief Ascension", "Bloodchief Ascension",
			"Bloodchief Ascension", "Bloodchief Ascension", "Mindcrank", "Mindcrank", "Mindcrank",
			"Lightning Bolt", "Lightning Bolt", "Lightning Bolt", "Lightning Bolt", "Arc Trail",
			"Arc Trail", "Arc Trail", "Arc Trail", "Staggershock", "Staggershock", "Staggershock",
			"Staggershock", "Volt Charge", "Volt Charge", "Volt Charge", "Volt Charge",
			"Tezzeret's Gambit", "Tezzeret's Gambit", "Tezzeret's Gambit", "Tezzeret's Gambit",
			"Hideous End", "Hideous End", "Hideous End", "Blackcleave Cliffs",
			"Blackcleave Cliffs", "Blackcleave Cliffs", "Blackcleave Cliffs", "Mountain",
			"Mountain", "Mountain", "Mountain", "Mountain", "Mountain", "Mountain", "Mountain",
			"Mountain", "Swamp", "Swamp", "Swamp", "Swamp", "Swamp", "Swamp", "Swamp", "Swamp",
			"Swamp" };

	private static final String[] TITLES2 = { "Goblin Guide", "Goblin Guide", "Goblin Guide",
			"Goblin Guide", "Spikeshot Elder", "Spikeshot Elder", "Spikeshot Elder",
			"Spikeshot Elder", "Kiln Fiend", "Kiln Fiend", "Kiln Fiend", "Kiln Fiend",
			"Shrine of Burning Rage", "Shrine of Burning Rage", "Shrine of Burning Rage",
			"Shrine of Burning Rage", "Gut Shot", "Gut Shot", "Gut Shot", "Gut Shot",
			"Lightning Bolt", "Lightning Bolt", "Lightning Bolt", "Lightning Bolt",
			"Burst Lightning", "Burst Lightning", "Burst Lightning", "Burst Lightning",
			"Searing Blaze", "Searing Blaze", "Searing Blaze", "Searing Blaze", "Arc Trail",
			"Arc Trail", "Arc Trail", "Arc Trail", "Staggershock", "Staggershock", "Staggershock",
			"Staggershock", "Teetering Peaks", "Teetering Peaks", "Teetering Peaks",
			"Teetering Peaks", "Mountain", "Mountain", "Mountain", "Mountain", "Mountain",
			"Mountain", "Mountain", "Mountain", "Mountain", "Mountain", "Mountain", "Mountain",
			"Mountain", "Mountain", "Mountain", "Mountain" };

	@SpringBean
	private PersistenceService persistenceService;
	@SpringBean
	private ImportDeckService importDeckService;

	private boolean importDeck;

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

	@Required
	public void setImportDeckService(final ImportDeckService _importDeckService)
	{
		this.importDeckService = _importDeckService;
	}

	@Required
	public void setImportDeck(final boolean _importDeck)
	{
		this.importDeck = _importDeck;

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public Deck generateData(final Long gameId, final Long playerId) throws IOException
	{
		if (null == this.persistenceService.getCardFromUuid(UUID
				.fromString("249c4f0b-cad0-4606-b5ea-eaee8866a347")))
		{
			final MagicCard baldu = new MagicCard("cards/Balduvian Horde_small.jpg",
					"cards/Balduvian Horde.jpg", "cards/Balduvian HordeThumb.jpg",
					"Balduvian Horde", "Isn't it a spoiler?", "", null);
			baldu.setUuidObject(UUID.fromString("249c4f0b-cad0-4606-b5ea-eaee8866a347"));
			final Deck fake = new Deck();
			fake.setDeckArchive(null);
			fake.setCards(null);
			fake.setPlayerId(-1l);

			baldu.setDeck(fake);
			baldu.setGameId(-1l);
			baldu.setX(350l);
			baldu.setY(350l);
			baldu.setZone(CardZone.BATTLEFIELD);
			this.persistenceService.saveDeck(fake);
			this.persistenceService.saveCard(baldu);
		}

		if ((this.importDeck)
				&& (null == this.persistenceService.getDeckArchiveByName("Aura Bant")))
		{
			final File _deck = new File(ResourceBundle.getBundle(
					RuntimeDataGenerator.class.getCanonicalName()).getString("AuraBantDeck"));
			final byte[] content = new byte[475];

			final FileInputStream fis = new FileInputStream(_deck);
			if (fis.read(content) == -1)
			{
				fis.close();
			}
			fis.close();

			final String deckContent = new String(content, "UTF-8");
			this.importDeckService.importDeck(deckContent, "Aura Bant", false);
		}

		final Deck deck1 = new Deck();
		deck1.setPlayerId(playerId);
		deck1.setDeckArchive(this.persistenceService
				.getDeckArchiveByName("aggro-combo Red / Black"));
		this.persistenceService.saveDeck(deck1);

		final Deck deck2 = new Deck();
		deck2.setPlayerId(playerId);
		deck2.setDeckArchive(this.persistenceService.getDeckArchiveByName("burn mono-Red"));
		this.persistenceService.saveDeck(deck2);

		for (int j = 1; j < 3; j++)
		{
			for (int i = 0; i < 60; i++)
			{
				// A CollectibleCard can be duplicated: lands, normal cards
				// which may be present 4 times in a Deck...
				MagicCard card;

				if (j == 1)
				{
					card = new MagicCard("cards/" + RuntimeDataGenerator.TITLES1[i] + "_small.jpg",
							"cards/" + RuntimeDataGenerator.TITLES1[i] + ".jpg", "cards/"
									+ RuntimeDataGenerator.TITLES1[i] + "Thumb.jpg",
							RuntimeDataGenerator.TITLES1[i], "", "", null);
					card.setDeck(deck1);
				}
				else
				{
					card = new MagicCard("cards/" + RuntimeDataGenerator.TITLES2[i] + "_small.jpg",
							"cards/" + RuntimeDataGenerator.TITLES2[i] + ".jpg", "cards/"
									+ RuntimeDataGenerator.TITLES2[i] + "Thumb.jpg",
							RuntimeDataGenerator.TITLES2[i], "", "", null);
					card.setDeck(deck2);
				}

				card.setGameId(gameId);
				card.setUuidObject(UUID.randomUUID());
				card.setX(16l);
				card.setY(16l);
				card.setZone(CardZone.LIBRARY);

				if (j == 1)
				{
					final List<MagicCard> cards = deck1.getCards();
					cards.add(card);
					deck1.setCards(cards);
					this.persistenceService.saveCard(card);
				}
				else
				{
					final List<MagicCard> cards = deck2.getCards();
					cards.add(card);
					deck2.setCards(cards);
					this.persistenceService.saveCard(card);
				}
			}

			if (j == 1)
			{
				this.persistenceService.updateDeck(deck1);
			}
			else
			{
				this.persistenceService.updateDeck(deck2);
			}
		}
		return deck1;
	}

}
