package org.alienlabs.hatchetharry.model.channel;

public class UntapAllCometChannel
{

	private final Long gameId;
	private final Long playerId;

	public UntapAllCometChannel(final Long _gameId, final Long _playerId)
	{
		this.gameId = _gameId;
		this.playerId = _playerId;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public Long getPlayerId()
	{
		return this.playerId;
	}

}
