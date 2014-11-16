package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Side;

public class PutTokenOnBattlefieldCometChannel
{
	private final Long gameId;
	private final MagicCard mc;
	private final Side side;

	public PutTokenOnBattlefieldCometChannel(final Long _gameId, final MagicCard _mc,
			final Side _side)
	{
		this.gameId = _gameId;
		this.mc = _mc;
		this.side = _side;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public MagicCard getMagicCard()
	{
		return this.mc;
	}

	public Side getSide()
	{
		return this.side;
	}

}
