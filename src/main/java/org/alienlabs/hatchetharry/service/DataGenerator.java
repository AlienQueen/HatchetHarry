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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.alienlabs.hatchetharry.model.CardCollection;
import org.alienlabs.hatchetharry.model.CardCollectionRootElement;
import org.alienlabs.hatchetharry.model.CollectibleCard;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.DeckArchive;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.persistence.dao.CardCollectionDao;
import org.alienlabs.hatchetharry.persistence.dao.CollectibleCardDao;
import org.alienlabs.hatchetharry.persistence.dao.DeckArchiveDao;
import org.alienlabs.hatchetharry.persistence.dao.DeckDao;
import org.alienlabs.hatchetharry.persistence.dao.MagicCardDao;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author ivaynberg
 * @author Kare Nuorteva
 */
public class DataGenerator implements InitializingBean
{
	private static final String[] TITLES1 = { "Goblin Guide", "Goblin Guide", "Goblin Guide",
			"Goblin Guide", "Vampire Lacerator", "Vampire Lacerator", "Vampire Lacerator",
			"Vampire Lacerator", "Bloodchief Ascension", "Bloodchief Ascension",
			"Bloodchief Ascension", "Bloodchief Ascension", "Mindcrank", "Mindcrank", "Mindcrank",
			"Lightning Bolt", "Lightning Bolt", "Lightning Bolt", "Lightning Bolt", "Arc Trail",
			"Arc Trail", "Arc Trail", "Arc Trail", "Staggershock", "Staggershock", "Staggershock",
			"Staggershock", "Volt Charge", "Volt Charge", "Volt Charge", "Volt Charge",
			"Tezzeret s Gambit", "Tezzeret's Gambit", "Tezzeret's Gambit", "Tezzeret's Gambit",
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
	private DeckDao deckDao;
	@SpringBean
	private DeckArchiveDao deckArchiveDao;
	@SpringBean
	private CollectibleCardDao collectibleCardDao;
	@SpringBean
	private MagicCardDao magicCardDao;
	@SpringBean
	private PersistenceService persistenceService;
	@SpringBean
	private CardCollectionDao cardCollectionDao;

	@SpringBean
	private transient boolean generateData;

	@SpringBean
	private transient boolean generateCardCollection;

	@Required
	public void setDeckDao(final DeckDao _deckDao)
	{
		this.deckDao = _deckDao;
	}

	@Required
	public void setDeckArchiveDao(final DeckArchiveDao _deckArchiveDao)
	{
		this.deckArchiveDao = _deckArchiveDao;
	}

	@Required
	public void setCollectibleCardDao(final CollectibleCardDao _collectibleCardDao)
	{
		this.collectibleCardDao = _collectibleCardDao;
	}

	@Required
	public void setMagicCardDao(final MagicCardDao _magicCardDao)
	{
		this.magicCardDao = _magicCardDao;
	}

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

	@Override
	public void afterPropertiesSet()
	{
		if ((this.generateCardCollection) && (this.cardCollectionDao.count() == 0))
		{
			try
			{

				final JAXBContext context = JAXBContext
						.newInstance(CardCollectionRootElement.class);
				final Unmarshaller um = context.createUnmarshaller();
				final CardCollectionRootElement cardCollection = (CardCollectionRootElement)um
						.unmarshal(new File(
								"/home/nostromo/hatchetharry/src/main/java/org/alienlabs/hatchetharry/service/return_to_ravnica.xml"));

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

		if (this.generateData)
		{
			Deck deck1 = new Deck();
			deck1.setPlayerId(1l);

			final DeckArchive deckArchive1 = new DeckArchive();
			deckArchive1.setDeckName("aggro-combo Red / Black");
			deck1.setDeckArchive(deckArchive1);

			deck1 = this.deckDao.save(deck1);

			Deck deck2 = new Deck();
			deck2.setPlayerId(2l);

			final DeckArchive deckArchive2 = new DeckArchive();
			deckArchive2.setDeckName("burn mono-Red");
			deck2.setDeckArchive(deckArchive2);

			deck2 = this.deckDao.save(deck2);

			final List<Deck> decks = new ArrayList<Deck>();
			decks.add(0, deck1);
			decks.add(1, deck2);

			for (int j = 1; j < 3; j++)
			{
				for (int i = 0; i < 60; i++)
				{

					final CollectibleCard c = new CollectibleCard();
					c.setTitle((j == 1 ? DataGenerator.TITLES1[i] : DataGenerator.TITLES2[i]));
					c.setDeckArchiveId(j == 1 ? deckArchive1.getDeckArchiveId() : deckArchive2
							.getDeckArchiveId());
					if (!this.persistenceService.doesCollectibleCardAlreadyExistsInDb(c.getTitle()))
					{
						this.deckArchiveDao.save(j == 1 ? deckArchive1 : deckArchive2);
						this.collectibleCardDao.save(c);
					}

					if (j == 1l)
					{
						MagicCard card = new MagicCard("cards/" + DataGenerator.TITLES1[i]
								+ "_small.jpg", "cards/" + DataGenerator.TITLES1[i] + ".jpg",
								"cards/" + DataGenerator.TITLES1[i] + "Thumb.jpg",
								DataGenerator.TITLES1[i], "");
						card.setGameId(1l);
						card.setDeck(decks.get(j - 1));
						card.setUuidObject(UUID.randomUUID());
						card = this.magicCardDao.save(card);

						final List<MagicCard> cards = decks.get(j - 1).getCards();
						cards.add(card);
						decks.get(j - 1).setCards(cards);
					}
					else
					{
						MagicCard card = new MagicCard("cards/" + DataGenerator.TITLES1[i]
								+ "_small.jpg", "cards/" + DataGenerator.TITLES1[i] + ".jpg",
								"cards/" + DataGenerator.TITLES1[i] + "Thumb.jpg",
								DataGenerator.TITLES1[i], "");
						card.setGameId(1l);
						card.setDeck(decks.get(j - 1));
						card.setUuidObject(UUID.randomUUID());
						card.setX(16l);
						card.setY(16l);
						card = this.magicCardDao.save(card);

						final List<MagicCard> cards = decks.get(j - 1).getCards();
						cards.add(card);
						decks.get(j - 1).setCards(cards);
					}
				}
			}

			final MagicCard card = new MagicCard("image/BalduvianHorde_small.jpg",
					"image/BalduvianHorde.jpg", "image/BalduvianHordeThumb.jpg", "Balduvian Horde",
					"Isn't it a spoiler?");
			card.setUuidObject(UUID.randomUUID());
			final Deck fake = new Deck();
			fake.setPlayerId(-1l);
			card.setDeck(fake);
			card.setGameId(-1l);
			this.magicCardDao.save(card);
		}
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

}
