package org.alienlabs.hatchetharry.model.channel;

public class ReactivateTooltipsCometChannel
{
	private final Long gameId;

	public ReactivateTooltipsCometChannel(final Long _gameId)
	{
		this.gameId = _gameId;
	}

	public Long getGameId()
	{
		return this.gameId;
	}
}
