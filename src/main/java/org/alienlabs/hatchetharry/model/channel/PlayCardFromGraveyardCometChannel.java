package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Side;

public class PlayCardFromGraveyardCometChannel
{
	private final String playerName;
	private final Long gameId;
	private final Side side;
	private final MagicCard mc;


	public PlayCardFromGraveyardCometChannel(final MagicCard _mc, final String _playerName,
			final Long _gameId, final Side _side)
	{
		this.mc = _mc;
		this.playerName = _playerName;
		this.gameId = _gameId;
		this.side = _side;
	}

	public String getPlayerName()
	{
		return this.playerName;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public Side getSide()
	{
		return this.side;
	}

	public MagicCard getMagicCard()
	{
		return this.mc;
	}

}
