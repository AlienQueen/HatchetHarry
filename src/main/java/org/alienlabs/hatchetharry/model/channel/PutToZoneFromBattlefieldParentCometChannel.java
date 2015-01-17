package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.MagicCard;

/**
 * Created by nostromo on 17/01/15.
 */
public class PutToZoneFromBattlefieldParentCometChannel
{
	protected final Long gameId;
	protected final boolean shouldUpdateZone;
	protected final MagicCard mc;
	protected final String requestingPlayerName;
	protected final Long targetPlayerId;
	protected final Long deckId;

	protected PutToZoneFromBattlefieldParentCometChannel(final Long _gameId, final MagicCard _mc,
			final String _requestingPlayerName, final Long _targetPlayerId, final Long _deckId,
			final boolean _shouldUpdateZone)
	{
		this.gameId = _gameId;
		this.mc = _mc;
		this.requestingPlayerName = _requestingPlayerName;
		this.targetPlayerId = _targetPlayerId;
		this.deckId = _deckId;
		this.shouldUpdateZone = _shouldUpdateZone;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public MagicCard getMagicCard()
	{
		return this.mc;
	}

	public String getRequestingPlayerName()
	{
		return this.requestingPlayerName;
	}

	public Long getTargetPlayerId()
	{
		return this.targetPlayerId;
	}

	public Long getDeckId()
	{
		return this.deckId;
	}

	public boolean isShouldUpdateZone()
	{
		return this.shouldUpdateZone;
	}

}
