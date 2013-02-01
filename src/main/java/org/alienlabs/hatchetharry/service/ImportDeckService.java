package org.alienlabs.hatchetharry.service;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

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

	public void importDeck(final String fileContent, final String deckName, final boolean testDeck)
	{
		Deck deck;
		DeckArchive deckArchive;

		if (testDeck)
		{
			deckArchive = this.persistenceService.getDeckArchiveByName(deckName);
			if (null != deckArchive)
			{
				return;
			}
		}
		deckArchive = new DeckArchive();
		deckArchive.setDeckName(deckName);
		this.persistenceService.saveDeckArchive(deckArchive);

		deck = new Deck();
		deck.setPlayerId(1l);
		deck.setDeckArchive(deckArchive);
		final List<MagicCard> allMagicCards = deck.getCards();

		deck = this.persistenceService.saveDeck(deck);

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

				this.persistenceService.saveCollectibleCard(cc);

				final MagicCard card = new MagicCard("cards/" + cardName + "_small.jpg", "cards/"
						+ cardName + ".jpg", "cards/" + cardName + "Thumb.jpg", cardName, "");
				card.setGameId(1l);
				card.setDeck(deck);
				card.setUuidObject(UUID.randomUUID());

				allMagicCards.add(card);

				this.persistenceService.updateDeck(deck);
				this.persistenceService.saveOrUpdateCard(card);
			}
		}
		this.persistenceService.saveOrUpdateDeckArchive(deckArchive);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
