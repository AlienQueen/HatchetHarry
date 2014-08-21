package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.Player;

public class ChatCometChannel
{
	private final String user;
	private final Long gameId;
	private final String message;
	private final Player player;

	public ChatCometChannel(final Player _player, final String _user, final Long _gameId,
		final String _message)
	{
		this.player = _player;
		this.user = _user;
		this.gameId = _gameId;
		this.message = _message;
	}

	public Player getPlayer()
	{
		return this.player;
	}

	public String getUser()
	{
		return this.user;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public String getMessage()
	{
		return this.message;
	}

}
