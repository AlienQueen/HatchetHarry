package org.alienlabs.hatchetharry.model.channel;

public class CountCardsCometChannel
{
	private final Long gameId;

	public CountCardsCometChannel(final Long _gameId)
	{
		this.gameId = _gameId;

	}

	public Long getGameId()
	{
		return this.gameId;
	}
}
