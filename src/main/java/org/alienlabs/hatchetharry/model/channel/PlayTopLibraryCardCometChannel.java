package org.alienlabs.hatchetharry.model.channel;


public class PlayTopLibraryCardCometChannel
{

	private final Long gameId;


	public PlayTopLibraryCardCometChannel(final Long _gameId)
	{
		this.gameId = _gameId;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

}
