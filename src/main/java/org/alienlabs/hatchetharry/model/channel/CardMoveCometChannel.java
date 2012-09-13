package org.alienlabs.hatchetharry.model.channel;

public class CardMoveCometChannel
{

	private final Long gameId;
	private final String mouseX;
	private final String mouseY;
	private final String uniqueid;

	public CardMoveCometChannel(final Long _gameId, final String _mouseX, final String _mouseY,
			final String _uniqueid)
	{
		this.gameId = _gameId;
		this.mouseX = _mouseX;
		this.mouseY = _mouseY;
		this.uniqueid = _uniqueid;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public String getMouseX()
	{
		return this.mouseX;
	}

	public String getMouseY()
	{
		return this.mouseY;
	}

	public String getUniqueid()
	{
		return this.uniqueid;
	}

}
