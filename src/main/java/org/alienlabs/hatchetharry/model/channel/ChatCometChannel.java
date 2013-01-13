package org.alienlabs.hatchetharry.model.channel;

public class ChatCometChannel
{
	private final Long gameId;
	private final String message;

	public ChatCometChannel(final Long _gameId, final String _message)
	{
		this.gameId = _gameId;
		this.message = _message;
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
