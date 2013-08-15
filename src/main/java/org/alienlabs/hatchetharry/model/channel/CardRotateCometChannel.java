package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.MagicCard;

public class CardRotateCometChannel
{
	private final Long gameId;
	private final String cardUuid;
	private final boolean tapped;
	private final MagicCard mc;

	public CardRotateCometChannel(final Long _gameId, final MagicCard _mc, final String _cardUuid,
			final boolean _tapped)
	{
		this.gameId = _gameId;
		this.mc = _mc;
		this.cardUuid = _cardUuid;
		this.tapped = _tapped;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public String getCardUuid()
	{
		return this.cardUuid;
	}

	public boolean isTapped()
	{
		return this.tapped;
	}

	public MagicCard getMc()
	{
		return this.mc;
	}
}
