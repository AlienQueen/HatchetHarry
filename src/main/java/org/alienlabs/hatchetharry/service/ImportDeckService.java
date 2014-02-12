package org.alienlabs.hatchetharry.service;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.CollectibleCard;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.DeckArchive;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class ImportDeckService implements Serializable
{
	private static final long serialVersionUID = 1L;
	static final Logger LOGGER = LoggerFactory.getLogger(ImportDeckService.class);

	@SpringBean
	private PersistenceService persistenceService;

	public boolean importDeck(final String fileContent, final String deckName,
			final boolean testDeck)
	{
		DeckArchive deckArchive;

		if (testDeck)
		{
			deckArchive = this.persistenceService.getDeckArchiveByName(deckName);
			if (null != deckArchive)
			{
				return false;
			}
		}
		deckArchive = new DeckArchive();
		deckArchive.setDeckName(deckName);
		deckArchive = this.persistenceService.saveDeckArchive(deckArchive);

		Deck deck = new Deck();
		deck.setPlayerId(1l);
		deck.setDeckArchive(deckArchive);
		deck.setDeckId(-1l);
		final List<MagicCard> allMagicCards = deck.getCards();

		deck = this.persistenceService.saveDeck(deck);

		if (fileContent.split("\n").length < 3)
		{
			return false;
		}

		for (final String line : fileContent.split("\n"))
		{
			ImportDeckService.LOGGER.info("line: " + line);

			if ("".equals(line.trim()))
			{
				break;
			}

			final String numberOfItemsAsString = line.split(" ")[0];
			final int numberOfItems = Integer.parseInt(numberOfItemsAsString);
			final int indexOfSpace = line.indexOf(" ");
			final String cardName = line.substring(indexOfSpace + 1, line.length());


			ImportDeckService.LOGGER.info(numberOfItems + " x " + cardName);

			for (int i = 0; i < numberOfItems; i++)
			{
				final CollectibleCard cc = new CollectibleCard();
				cc.setTitle(cardName);
				cc.setDeckArchiveId(deckArchive.getDeckArchiveId());

				// A CollectibleCard can be duplicated: lands, normal cards
				// which may be present 4 times in a Deck...
				this.persistenceService.saveCollectibleCard(cc);

				final MagicCard card = new MagicCard("cards/" + cardName + "_small.jpg", "cards/"
						+ cardName + ".jpg", "cards/" + cardName + "Thumb.jpg", cardName, "", "",
						null);
				card.setGameId(1l);
				card.setDeck(deck);
				card.setZone(CardZone.LIBRARY);
				card.setUuidObject(UUID.randomUUID());

				allMagicCards.add(card);

				deck.setDeckArchive(deckArchive);
				// updateDeck is enough to save allMagicCards by cascade
				this.persistenceService.updateDeck(deck);
			}
		}
		this.persistenceService.updateDeckArchive(deckArchive);
		return true;
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
