/*
 * $Id: DataGenerator.java 366 2005-10-11 16:06:21 -0700 (Tue, 11 Oct 2005) ivaynberg $
 * $Revision: 366 $
 * $Date: 2005-10-11 16:06:21 -0700 (Tue, 11 Oct 2005) $
 *
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.alienlabs.hatchetharry.service;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.alienlabs.hatchetharry.model.CardCollection;
import org.alienlabs.hatchetharry.model.CardCollectionRootElement;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.CollectibleCard;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.DeckArchive;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.persistence.dao.CardCollectionDao;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author ivaynberg
 * @author Kare Nuorteva
 */
public class DataGenerator implements InitializingBean, Serializable
{
	private static final long serialVersionUID = 1L;
	static final Logger LOGGER = LoggerFactory.getLogger(DataGenerator.class);

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
	private CardCollectionDao cardCollectionDao;

	@SpringBean
	private transient boolean generateData;

	@SpringBean
	private transient boolean generateCardCollection;

	@SpringBean
	private ImportDeckService importDeckService;

	@Required
	public void setCardCollectionDao(final CardCollectionDao _cardCollectionDao)
	{
		this.cardCollectionDao = _cardCollectionDao;
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

	@Required
	public void setGenerateData(final boolean _generateData)
	{
		this.generateData = _generateData;
	}

	@Required
	public void setGenerateCardCollection(final boolean _generateCardCollection)
	{
		this.generateCardCollection = _generateCardCollection;
	}

	@Required
	public void setImportDeckService(final ImportDeckService _importDeckService)
	{
		this.importDeckService = _importDeckService;
	}

	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "PRMC_POSSIBLY_REDUNDANT_METHOD_CALLS", justification = "the difference is in fake.setDeckArchive()")
	@Override
	public void afterPropertiesSet() throws Exception
	{
		if ((this.generateCardCollection) && (this.cardCollectionDao.count() == 0))
		{
			try
			{
				final JAXBContext context = JAXBContext
					.newInstance(CardCollectionRootElement.class);
				final Unmarshaller um = context.createUnmarshaller();
				final CardCollectionRootElement cardCollection = (CardCollectionRootElement)um
					.unmarshal(new File(ResourceBundle.getBundle(
						DataGenerator.class.getCanonicalName()).getString(
						"AllCardsInCollectionUntilReturnToRavnica")));

				final Object[] array = cardCollection.getCardCollectionList().toArray();

				for (int i = 0; i < array.length; i++)
				{
					this.cardCollectionDao.save((CardCollection)array[i]);
				}
			}
			catch (final JAXBException e)
			{
				e.printStackTrace();
			}
		}

		UUID uuid = UUID.fromString("249c4f0b-cad0-4606-b5ea-eaee8866a347");
		if (null == this.persistenceService.getCardFromUuid(uuid))
		{
			final MagicCard baldu = new MagicCard("cards/Balduvian Horde_small.jpg",
				"cards/Balduvian Horde.jpg", "cards/Balduvian HordeThumb.jpg", "Balduvian Horde",
				"Isn't it a spoiler?", "", null);
			baldu.setUuidObject(uuid);
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

		if ((this.generateData)
			&& (null == this.persistenceService.getDeckArchiveByName("Aura Bant")))
		{
			Path path = Paths.get(ResourceBundle.getBundle(DataGenerator.class.getCanonicalName())
                    .getString("AuraBantDeck"));
			final byte[] content = Files.readAllBytes(path);
			final String deckContent = new String(content, "UTF-8");
			this.importDeckService.importDeck(deckContent, "Aura Bant", false);
		}

		DataGenerator.LOGGER.info("preparing to import decks");

		if ((null == this.persistenceService.getDeckArchiveByName("aggro-combo Red / Black") && (null == this.persistenceService
			.getDeckArchiveByName("burn mono-Red")))
			|| ((this.persistenceService.getDeckByDeckArchiveName("aggro-combo Red / Black")
				.getCards().isEmpty()) && (this.persistenceService.getDeckByDeckArchiveName(
				"burn mono-Red").getCards().isEmpty())))
		{
			DataGenerator.LOGGER.info("importing decks");


			DeckArchive da1 = new DeckArchive();
			da1.setDeckName("aggro-combo Red / Black");
			this.persistenceService.saveDeckArchive(da1);

			DeckArchive da2 = new DeckArchive();
			da2.setDeckName("burn mono-Red");
			this.persistenceService.saveDeckArchive(da2);

			final Deck deck1 = new Deck();
			deck1.setPlayerId(1l);
			deck1.setDeckArchive(da1);
			this.persistenceService.saveDeck(deck1);

			final Deck deck2 = new Deck();
			deck2.setPlayerId(2l);
			deck2.setDeckArchive(da2);
			this.persistenceService.saveDeck(deck2);

			for (int j = 1; j < 3; j++)
			{
				for (int i = 0; i < 60; i++)
				{
					// A CollectibleCard can be duplicated: lands, normal cards
					//
					System.out.print(".");
					MagicCard card;

					final CollectibleCard cc = new CollectibleCard();
					cc.setTitle(DataGenerator.TITLES1[i]);
					cc.setDeckArchiveId(j == 1 ? da1.getDeckArchiveId() : da2.getDeckArchiveId());

					// A CollectibleCard can be duplicated: lands, normal cards
					// which may be present 4 times in a Deck...
					this.persistenceService.saveCollectibleCard(cc);

					if (j == 1)
					{
						card = new MagicCard("cards/" + DataGenerator.TITLES1[i] + "_small.jpg",
							"cards/" + DataGenerator.TITLES1[i] + ".jpg", "cards/"
								+ DataGenerator.TITLES1[i] + "Thumb.jpg", DataGenerator.TITLES1[i],
							"", "", null);
						card.setDeck(deck1);
					}
					else
					{
						card = new MagicCard("cards/" + DataGenerator.TITLES2[i] + "_small.jpg",
							"cards/" + DataGenerator.TITLES2[i] + ".jpg", "cards/"
								+ DataGenerator.TITLES2[i] + "Thumb.jpg", DataGenerator.TITLES2[i],
							"", "", null);
						card.setDeck(deck2);
					}

					card.setGameId(-1l);
					card.setUuidObject(UUID.randomUUID());
					card.setX(16l);
					card.setY(16l);
					card.setZone(CardZone.LIBRARY);

					if (j == 1)
					{
						final List<MagicCard> cards = deck1.getCards();
						cards.add(card);
						deck1.setCards(cards);
						// this.persistenceService.saveCard(card);
					}
					else
					{
						final List<MagicCard> cards = deck2.getCards();
						cards.add(card);
						deck2.setCards(cards);
						// this.persistenceService.saveCard(card);
					}
				}

				System.out.println("");
				if (j == 1)
				{
					this.persistenceService.updateDeckArchive(da1);
					this.persistenceService.updateDeck(deck1);
					DataGenerator.LOGGER.info("updating deck 1");
				}
				else
				{
					this.persistenceService.updateDeckArchive(da2);
					this.persistenceService.updateDeck(deck2);
					DataGenerator.LOGGER.info("updating deck 2");
				}
			}
		}
	}

}
