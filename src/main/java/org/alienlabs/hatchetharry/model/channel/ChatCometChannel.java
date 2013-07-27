package org.alienlabs.hatchetharry.model.channel;

public class ChatCometChannel
{
    private final String user;
    private final Long gameId;
	private final String message;

	public ChatCometChannel(final String _user, final Long _gameId, final String _message)
	{
        this.user = _user;
		this.gameId = _gameId;
		this.message = _message;
	}

    public String getUser() {
        return user;
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
