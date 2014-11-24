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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ivaynberg
 * @author Kare Nuorteva
 */
public class DataGenerator implements InitializingBean, Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(DataGenerator.class);

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
	private boolean generateData;

	@SpringBean
	private boolean generateCardCollection;

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

	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = { "PATH_TRAVERSAL_IN",
			"PRMC_POSSIBLY_REDUNDANT_METHOD_CALLS" }, justification = "the difference is in fake.setDeckArchive() ")
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
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

				for (final Object element : array)
				{
					this.cardCollectionDao.save((CardCollection)element);
				}
			}
			catch (final JAXBException e)
			{
				LOGGER.error("Error while generating card collection!", e);
			}
		}

		if ((this.generateData)
				&& (null == this.persistenceService.getDeckArchiveByName("Aura Bant")))
		{
			final Path path = Paths.get(ResourceBundle.getBundle(
					DataGenerator.class.getCanonicalName()).getString("AuraBantDeck"));
			final byte[] content = Files.readAllBytes(path);
			final String deckContent = new String(content, "UTF-8");
			this.importDeckService.importDeck(deckContent, "Aura Bant", false);
		}

		DataGenerator.LOGGER.info("preparing to import decks");

		if (((null == this.persistenceService.getDeckArchiveByName("aggro-combo Red / Black")) && (null == this.persistenceService
				.getDeckArchiveByName("burn mono-Red")))
				|| ((this.persistenceService.getDeckByDeckArchiveName("aggro-combo Red / Black")
						.getCards().isEmpty()) || (this.persistenceService
						.getDeckByDeckArchiveName("burn mono-Red").getCards().isEmpty())))
		{
			DataGenerator.LOGGER.info("importing decks");

			final DeckArchive da1;
			final DeckArchive da2;
			final Deck deck1;
			final Deck deck2;

			if (null == this.persistenceService.getDeckArchiveByName("aggro-combo Red / Black"))
			{
				da1 = new DeckArchive();
				da1.setDeckName("aggro-combo Red / Black");
				this.persistenceService.saveOrUpdateDeckArchive(da1);
				deck1 = new Deck();
				deck1.setPlayerId(Long.valueOf(1l));
				deck1.setDeckArchive(da1);
			}
			else
			{
				da1 = this.persistenceService.getDeckArchiveByName("aggro-combo Red / Black");
				deck1 = this.persistenceService.getDeckByDeckArchiveName("aggro-combo Red / Black");
			}

			if (null == this.persistenceService.getDeckArchiveByName("burn mono-Red"))
			{
				da2 = new DeckArchive();
				da2.setDeckName("burn mono-Red");
				this.persistenceService.saveOrUpdateDeckArchive(da2);
				deck2 = new Deck();
				deck2.setPlayerId(Long.valueOf(2l));
				deck2.setDeckArchive(da2);
			}
			else
			{
				da2 = this.persistenceService.getDeckArchiveByName("burn mono-Red");
				deck2 = this.persistenceService.getDeckByDeckArchiveName("burn mono-Red");
			}

			for (int j = 1; j < 3; j++)
			{
				if (((j == 1) && ((deck1.getCards() == null) || (deck1.getCards().isEmpty())))
						|| (((j == 2) && ((deck2.getCards() == null) || (deck2.getCards().isEmpty())))))
				{
					for (int i = 0; i < 60; i++)
					{
						// A CollectibleCard can be duplicated: lands, normal
						// cards
						//
						System.out.print(".");
						MagicCard card = null;

						final CollectibleCard cc = new CollectibleCard();
						cc.setTitle(j == 1 ? DataGenerator.TITLES1[i] : DataGenerator.TITLES2[i]);
						cc.setDeckArchiveId(j == 1 ? da1.getDeckArchiveId() : da2
								.getDeckArchiveId());

						// A CollectibleCard can be duplicated: lands, normal
						// cards
						// which may be present 4 times in a Deck...
						this.persistenceService.saveCollectibleCard(cc);

						if (j == 1)
						{
							card = new MagicCard(
									"cards/" + DataGenerator.TITLES1[i] + "_small.jpg", "cards/"
											+ DataGenerator.TITLES1[i] + ".jpg", "cards/"
											+ DataGenerator.TITLES1[i] + "Thumb.jpg",
									DataGenerator.TITLES1[i], "", "", null, Integer.valueOf(0));
							card.setDeck(deck1);
						}
						else
						{
							card = new MagicCard(
									"cards/" + DataGenerator.TITLES2[i] + "_small.jpg", "cards/"
											+ DataGenerator.TITLES2[i] + ".jpg", "cards/"
											+ DataGenerator.TITLES2[i] + "Thumb.jpg",
									DataGenerator.TITLES2[i], "", "", null, Integer.valueOf(0));
							card.setDeck(deck2);
						}

						card.setGameId(Long.valueOf(-1l));
						card.setUuidObject(UUID.randomUUID());
						card.setX(Long.valueOf(16l));
						card.setY(Long.valueOf(16l));
						card.setZone(CardZone.LIBRARY);

						if (j == 1)
						{
							final List<MagicCard> cards = deck1.getCards();
							cards.add(card);
							deck1.setCards(cards);
						}
						else
						{
							final List<MagicCard> cards = deck2.getCards();
							cards.add(card);
							deck2.setCards(cards);
						}
					}
				}
				else
				{
					if (j == 1)
					{
						final List<MagicCard> cards = this.persistenceService
								.getAllCardsFromDeck("aggro-combo Red / Black");
						deck1.setCards(cards);
					}
					else
					{
						final List<MagicCard> cards = this.persistenceService
								.getAllCardsFromDeck("burn mono-Red");
						deck2.setCards(cards);
					}
				}

				System.out.println("");
				if (j == 1)
				{
					this.persistenceService.saveOrUpdateDeck(deck1);
					this.persistenceService.saveOrUpdateDeckArchive(da1);
					DataGenerator.LOGGER.info("updated deck 1");
				}
				else
				{
					this.persistenceService.saveOrUpdateDeck(deck2);
					this.persistenceService.saveOrUpdateDeckArchive(da2);
					DataGenerator.LOGGER.info("updated deck 2");
				}
			}
		}
	}

}
