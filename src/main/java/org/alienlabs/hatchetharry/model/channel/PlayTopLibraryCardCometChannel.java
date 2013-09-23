package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.MagicCard;

public class PlayTopLibraryCardCometChannel
{
	private final Long gameId;
	private final MagicCard mc;

	public PlayTopLibraryCardCometChannel(final Long _gameId, final MagicCard _mc)
	{
		this.gameId = _gameId;
		this.mc = _mc;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public MagicCard getCard()
	{
		return this.mc;
	}

}
