package org.alienlabs.hatchetharry.model.channel;



public class PutToHandFromBattlefieldCometChannel
{
	private final Long gameId;

	public PutToHandFromBattlefieldCometChannel(final Long _gameId)
	{
		this.gameId = _gameId;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

}
