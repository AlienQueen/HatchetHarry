package org.alienlabs.hatchetharry.model.channel;

public class CardRotateCometChannel
{
	private final Long gameId;
	private final String cardUuid;
	private final boolean tapped;

	public CardRotateCometChannel(final Long _gameId, final String _cardUuid, final boolean _tapped)
	{
		this.gameId = _gameId;
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
}
