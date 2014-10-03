package org.alienlabs.hatchetharry.model.channel;

public class JoinGameNotificationCometChannel
{
	private final String playerName;
	private final Long gameId;

	public JoinGameNotificationCometChannel(final String _playerName, final Long _gameId)
	{
		this.playerName = _playerName;
		this.gameId = _gameId;
	}

	public String getPlayerName()
	{
		return this.playerName;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

}
