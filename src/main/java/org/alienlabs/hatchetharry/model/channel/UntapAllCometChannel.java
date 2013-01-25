package org.alienlabs.hatchetharry.model.channel;

public class UntapAllCometChannel
{

	private final Long gameId;
	private final Long playerId;
	private final Long deckId;

	public UntapAllCometChannel(final Long _gameId, final Long _playerId, final Long _deckId)
	{
		this.gameId = _gameId;
		this.playerId = _playerId;
		this.deckId = _deckId;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public Long getPlayerId()
	{
		return this.playerId;
	}

	public Long getDeckId()
	{
		return this.deckId;
	}

}
