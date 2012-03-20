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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.model.CollectibleCard;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.persistence.dao.CollectibleCardDao;
import org.alienlabs.hatchetharry.persistence.dao.DeckDao;
import org.alienlabs.hatchetharry.persistence.dao.MagicCardDao;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

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
			"Tezzeret s Gambit", "Tezzeret s Gambit", "Tezzeret s Gambit", "Tezzeret s Gambit",
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
	private  DeckDao deckDao;
	@SpringBean
	private  CollectibleCardDao collectibleCardDao;
	@SpringBean
	private  MagicCardDao magicCardDao;
	@SpringBean
	private  PersistenceService persistenceService;

	@SpringBean
	private transient boolean generateData;

	@Required
	public void setDeckDao(final DeckDao _deckDao)
	{
		this.deckDao = _deckDao;
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
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

	@Override
	@Transactional
	public void afterPropertiesSet()
	{
		if (!this.generateData)
		{
			return;
		}
		Deck deck1 = new Deck();
		deck1.setPlayerId(1l);
		deck1.setDeckName("aggro-combo Red / Black");
		deck1 = this.deckDao.save(deck1);

		Deck deck2 = new Deck();
		deck2.setPlayerId(2l);
		deck2.setDeckName("burn mono-Red");
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
				if (!this.persistenceService.doesCollectibleCardAlreadyExistsInDb(c.getTitle()))
				{
					this.collectibleCardDao.save(c);
				}

				if (j == 1l)
				{
					MagicCard card = new MagicCard("image/"
							+ DataGenerator.TITLES1[i].replace(" ", "") + "_small.jpg", "image/"
							+ DataGenerator.TITLES1[i].replace(" ", "") + ".jpg", "image/"
							+ DataGenerator.TITLES1[i].replace(" ", "") + "Thumb.jpg",
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
					MagicCard card = new MagicCard("image/"
							+ DataGenerator.TITLES2[i].replace(" ", "") + "_small.jpg", "image/"
							+ DataGenerator.TITLES2[i].replace(" ", "") + ".jpg", "image/"
							+ DataGenerator.TITLES2[i].replace(" ", "") + "Thumb.jpg",
							DataGenerator.TITLES2[i], "");
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
		fake.setDeckName("fake");
		fake.setPlayerId(-1l);
		card.setDeck(fake);
		card.setGameId(-1l);
		this.magicCardDao.save(card);
	}

	public void setGenerateData(final boolean _generateData)
	{
		this.generateData = _generateData;
	}

}
