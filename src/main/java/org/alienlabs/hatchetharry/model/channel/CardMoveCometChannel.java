package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.MagicCard;

public class CardMoveCometChannel
{

	private final Long gameId;
	private final String mouseX;
	private final String mouseY;
	private final String uniqueid;
	private final Long playerId;
	private final MagicCard mc;

	/**
	 * @param _gameId
	 * @param _mc
	 * @param _mouseX
	 * @param _mouseY
	 * @param _uniqueid
	 * @param _playerId
	 */
	public CardMoveCometChannel(final Long _gameId, final MagicCard _mc, final String _mouseX,
		final String _mouseY, final String _uniqueid, final Long _playerId)
	{
		this.gameId = _gameId;
		this.mc = _mc;
		this.mouseX = _mouseX;
		this.mouseY = _mouseY;
		this.uniqueid = _uniqueid;
		this.playerId = _playerId;
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

	public Long getPlayerId()
	{
		return this.playerId;
	}

	public MagicCard getMc()
	{
		return this.mc;
	}

}
