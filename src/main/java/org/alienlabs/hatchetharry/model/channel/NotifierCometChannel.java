package org.alienlabs.hatchetharry.model.channel;

public class NotifierCometChannel
{

	private final NotifierAction action;
	private final Long gameId;
	private final Long playerId;
	private final String playerName;
	private final String side;
	private final String jsessionid;

	public NotifierCometChannel(final NotifierAction _action, final Long _gameId,
			final Long _playerId, final String _playerName, final String _side,
			final String _jsessionid)
	{
		this.action = _action;
		this.gameId = _gameId;
		this.playerId = _playerId;
		this.playerName = _playerName;
		this.side = _side;
		this.jsessionid = _jsessionid;
	}

	public NotifierAction getAction()
	{
		return this.action;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public Long getPlayerId()
	{
		return this.playerId;
	}

	public String getPlayerName()
	{
		return this.playerName;
	}

	public String getSide()
	{
		return this.side;
	}

	public String getJsessionid()
	{
		return this.jsessionid;
	}

}
