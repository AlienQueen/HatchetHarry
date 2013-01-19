package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.view.component.CardPanel;


public class PutToGraveyardCometChannel
{
	private final Long gameId;
	private final CardPanel card;
	private final MagicCard mc;

	public PutToGraveyardCometChannel(final Long _gameId, final CardPanel _card, final MagicCard _mc)
	{
		this.gameId = _gameId;
		this.card = _card;
		this.mc = _mc;

	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public CardPanel getCardPanel()
	{
		return this.card;
	}

	public MagicCard getMagicCard()
	{
		return this.mc;
	}

}
