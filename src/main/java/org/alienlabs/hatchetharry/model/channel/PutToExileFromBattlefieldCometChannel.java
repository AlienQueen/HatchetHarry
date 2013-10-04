package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.MagicCard;


public class PutToExileFromBattlefieldCometChannel
{
	private final Long gameId;
	private MagicCard mc;
	private String requestingPlayerName;
	private String targetPlayerName;
	private Long targetPlayerId;
	private Long deckId;
	private final boolean shouldUpdateExile;

	public PutToExileFromBattlefieldCometChannel(final Long _gameId, final MagicCard _mc,
			final String _requestingPlayerName, final String _targetPlayerName,
			final Long _targetPlayerId, final Long _deckId, final boolean _shouldUpdateExile)
	{
		this.gameId = _gameId;
		this.mc = _mc;
		this.requestingPlayerName = _requestingPlayerName;
		this.targetPlayerName = _targetPlayerName;
		this.targetPlayerId = _targetPlayerId;
		this.deckId = _deckId;
		this.shouldUpdateExile = _shouldUpdateExile;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public MagicCard getMc()
	{
		return this.mc;
	}

	public void setMc(final MagicCard _mc)
	{
		this.mc = _mc;
	}

	public String getRequestingPlayerName()
	{
		return this.requestingPlayerName;
	}

	public void setRequestingPlayerName(final String _requestingPlayerName)
	{
		this.requestingPlayerName = _requestingPlayerName;
	}

	public String getTargetPlayerName()
	{
		return this.targetPlayerName;
	}

	public void setTargetPlayerName(final String _targetPlayerName)
	{
		this.targetPlayerName = _targetPlayerName;
	}

	public Long getTargetPlayerId()
	{
		return this.targetPlayerId;
	}

	public void setTargetPlayerId(final Long _targetPlayerId)
	{
		this.targetPlayerId = _targetPlayerId;
	}

	public Long getDeckId()
	{
		return this.deckId;
	}

	public void setDeckId(final Long _deckId)
	{
		this.deckId = _deckId;
	}

	public boolean isShouldUpdateExile()
	{
		return this.shouldUpdateExile;
	}

}