package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.MagicCard;

public class CardMoveCometChannel
{

	private final Long gameId;
	private final String uniqueid;
	private final Long playerId;
	private final MagicCard mc;

	/**
	 * @param _gameId
	 * @param _mc
	 * @param _uniqueid
	 * @param _playerId
	 */
	public CardMoveCometChannel(final Long _gameId, final MagicCard _mc, final String _uniqueid, final Long _playerId)
	{
		this.gameId = _gameId;
		this.mc = _mc;
		this.uniqueid = _uniqueid;
		this.playerId = _playerId;
	}

	public Long getGameId()
	{
		return this.gameId;
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
