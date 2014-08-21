package org.alienlabs.hatchetharry.model.channel;

public class JoinGameNotificationCometChannel
{
	private final String playerName;
	private final String jsessionid;
	private final Long gameId;

	public JoinGameNotificationCometChannel(final String _playerName, final String _jsessionid,
		final Long _gameId)
	{
		this.playerName = _playerName;
		this.jsessionid = _jsessionid;
		this.gameId = _gameId;
	}

	public String getPlayerName()
	{
		return this.playerName;
	}

	public String getJsessionid()
	{
		return this.jsessionid;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

}
