package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.MagicCard;


public class PutToHandFromBattlefieldCometChannel
{
	private final Long gameId;
	private MagicCard mc;
	private String requestingPlayerName;
	private String targetPlayerName;
	private Long targetPlayerId;
	private Long deckId;
	private final boolean shouldUpdateHand;

	public PutToHandFromBattlefieldCometChannel(final Long _gameId, final MagicCard _mc,
			final String _requestingPlayerName, final String _targetPlayerName,
			final Long _targetPlayerId, final Long _deckId, final boolean _shouldUpdateHand)
	{
		this.gameId = _gameId;
		this.mc = _mc;
		this.requestingPlayerName = _requestingPlayerName;
		this.targetPlayerName = _targetPlayerName;
		this.targetPlayerId = _targetPlayerId;
		this.deckId = _deckId;
		this.shouldUpdateHand = _shouldUpdateHand;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public MagicCard getMc()
	{
		return this.mc;
	}

	public void setMc(final MagicCard mc)
	{
		this.mc = mc;
	}

	public String getRequestingPlayerName()
	{
		return this.requestingPlayerName;
	}

	public void setRequestingPlayerName(final String requestingPlayerName)
	{
		this.requestingPlayerName = requestingPlayerName;
	}

	public String getTargetPlayerName()
	{
		return this.targetPlayerName;
	}

	public void setTargetPlayerName(final String targetPlayerName)
	{
		this.targetPlayerName = targetPlayerName;
	}

	public Long getTargetPlayerId()
	{
		return this.targetPlayerId;
	}

	public void setTargetPlayerId(final Long targetPlayerId)
	{
		this.targetPlayerId = targetPlayerId;
	}

	public Long getDeckId()
	{
		return this.deckId;
	}

	public void setDeckId(final Long deckId)
	{
		this.deckId = deckId;
	}

	public boolean isShouldUpdateHand()
	{
		return this.shouldUpdateHand;
	}

}
