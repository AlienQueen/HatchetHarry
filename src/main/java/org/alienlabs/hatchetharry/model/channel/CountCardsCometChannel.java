package org.alienlabs.hatchetharry.model.channel;

public class CountCardsCometChannel
{
	private final Long gameId;
	private final String requestingPlayerName;

	public CountCardsCometChannel(final Long _gameId, final String _requestingPlayerName)
	{
		this.gameId = _gameId;
		this.requestingPlayerName = _requestingPlayerName;

	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public String getRequestingPlayerName()
	{
		return this.requestingPlayerName;
	}
}
