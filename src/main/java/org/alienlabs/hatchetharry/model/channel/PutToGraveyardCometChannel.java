package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.view.component.CardPanel;


public class PutToGraveyardCometChannel
{
	private final Long gameId;
	private final CardPanel card;

	public PutToGraveyardCometChannel(final Long _gameId, final CardPanel _card)
	{
		this.gameId = _gameId;
		this.card = _card;

	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public CardPanel getCard()
	{
		return this.card;
	}


}
