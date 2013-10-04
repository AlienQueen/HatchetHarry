package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.MagicCard;

public class CardZoneMoveCometChannel
{
	private final CardZone sourceZone, targetZone;
	private final MagicCard card;
	private final Long playerId, gameId, deckId;

	public CardZoneMoveCometChannel(final CardZone _sourceZone, final CardZone _targetZone,
			final MagicCard _card, final Long _playerId, final Long _gameId, final Long _deckId)
	{
		this.sourceZone = _sourceZone;
		this.targetZone = _targetZone;
		this.card = _card;
		this.playerId = _playerId;
		this.gameId = _gameId;
		this.deckId = _deckId;
	}

	public CardZone getSourceZone()
	{
		return this.sourceZone;
	}

	public CardZone getTargetZone()
	{
		return this.targetZone;
	}

	public MagicCard getCard()
	{
		return this.card;
	}

	public Long getPlayerId()
	{
		return this.playerId;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public Long getDeckId()
	{
		return this.deckId;
	}

}
