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

		if (this.generateData)
		{
			DeckArchive deckArchive1 = this.persistenceService
					.getDeckArchiveByName("aggro-combo Red / Black");
			if (null == deckArchive1)
			{
				deckArchive1 = new DeckArchive();
				deckArchive1.setDeckName("aggro-combo Red / Black");
			}

			Deck deck1 = this.persistenceService.getDeck(1l);
			if (null == deck1)
			{
				deck1 = new Deck();
				deck1.setPlayerId(1l);
				deck1.setDeckArchive(deckArchive1);
			}

			DeckArchive deckArchive2 = this.persistenceService
					.getDeckArchiveByName("burn mono-Red");
			if (null == deckArchive2)
			{
				deckArchive2 = new DeckArchive();
				deckArchive2.setDeckName("burn mono-Red");
			}

			Deck deck2 = this.persistenceService.getDeck(1l);
			if (null == deck2)
			{
				deck2 = new Deck();
				deck2.setPlayerId(2l);
				deck2.setDeckArchive(deckArchive2);
			}

			final List<Deck> decks = new ArrayList<Deck>();
			this.persistenceService.saveDeckArchive(deckArchive1);
			deck1 = this.persistenceService.saveDeck(deck1);
			this.persistenceService.saveDeckArchive(deckArchive2);
			deck2 = this.persistenceService.saveDeck(deck2);
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
					// A CollectibleCard can be duplicated: lands, normal cards
					// which may be present 4 times in a Deck...
					this.persistenceService.saveCollectibleCard(c);

					if (j == 1l)
					{
						final MagicCard card = new MagicCard("cards/" + DataGenerator.TITLES1[i]
								+ "_small.jpg", "cards/" + DataGenerator.TITLES1[i] + ".jpg",
								"cards/" + DataGenerator.TITLES1[i] + "Thumb.jpg",
								DataGenerator.TITLES1[i], "");
						card.setGameId(1l);
						card.setDeck(decks.get(j - 1));
						card.setUuidObject(UUID.randomUUID());
						card.setZone(CardZone.LIBRARY);
						this.persistenceService.saveCard(card);

						final List<MagicCard> cards = decks.get(j - 1).getCards();
						cards.add(card);
						decks.get(j - 1).setCards(cards);
					}
					else
					{
						final MagicCard card = new MagicCard("cards/" + DataGenerator.TITLES1[i]
								+ "_small.jpg", "cards/" + DataGenerator.TITLES1[i] + ".jpg",
								"cards/" + DataGenerator.TITLES1[i] + "Thumb.jpg",
								DataGenerator.TITLES1[i], "");
						card.setGameId(1l);
						card.setDeck(decks.get(j - 1));
						card.setUuidObject(UUID.randomUUID());
						card.setX(16l);
						card.setY(16l);
						card.setZone(CardZone.LIBRARY);
						this.persistenceService.saveOrUpdateDeck(decks.get(j - 1));
						this.persistenceService.saveCard(card);

						final List<MagicCard> cards = decks.get(j - 1).getCards();
						cards.add(card);
						decks.get(j - 1).setCards(cards);
					}
				}
				this.persistenceService.updateDeck(decks.get(j - 1));
			}

			decks.get(0).setDeckArchive(deckArchive1);
			decks.get(1).setDeckArchive(deckArchive2);
			this.persistenceService.updateDeck(decks.get(0));
			this.persistenceService.updateDeck(decks.get(1));
			this.persistenceService.updateDeckArchive(deckArchive1);
			this.persistenceService.updateDeckArchive(deckArchive2);

			if (null == this.persistenceService.getCardFromUuid(UUID
					.fromString("249c4f0b-cad0-4606-b5ea-eaee8866a347")))
			{
				final MagicCard card = new MagicCard("cards/Balduvian Horde_small.jpg",
						"cards/Balduvian Horde.jpg", "cards/Balduvian HordeThumb.jpg",
						"Balduvian Horde", "Isn't it a spoiler?");
				card.setUuidObject(UUID.fromString("249c4f0b-cad0-4606-b5ea-eaee8866a347"));
				final Deck fake = new Deck();
				fake.setPlayerId(-1l);
				fake.setDeckArchive(deckArchive1);
				card.setDeck(fake);
				card.setGameId(-1l);
				card.setZone(CardZone.BATTLEFIELD);
				this.persistenceService.saveCard(card);
			}
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
