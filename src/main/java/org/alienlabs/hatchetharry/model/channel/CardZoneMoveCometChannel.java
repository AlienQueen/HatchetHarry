package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Side;

public class CardZoneMoveCometChannel
{
	private final CardZone sourceZone, targetZone;
	private final MagicCard card;
	private final Long requestingPlayerId, gameId;
	private final Deck deck;
	private final Side side;
	private final Long targetPlayerId;
	private final boolean isReveal;

	public CardZoneMoveCometChannel(final CardZone _sourceZone, final CardZone _targetZone,
		final MagicCard _card, final Long _requestingPlayerId, final Long _targetPlayerId,
		final Long _gameId, final Deck _deck, final Side _side, final boolean _isReveal)
	{
		this.sourceZone = _sourceZone;
		this.targetZone = _targetZone;
		this.card = _card;
		this.requestingPlayerId = _requestingPlayerId;
		this.targetPlayerId = _targetPlayerId;
		this.gameId = _gameId;
		this.deck = _deck;
		this.side = _side;
		this.isReveal = _isReveal;
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

	public Long getGameId()
	{
		return this.gameId;
	}

	public Deck getDeck()
	{
		return this.deck;
	}

	public Side getSide()
	{
		return this.side;
	}

	public Long getRequestingPlayerId()
	{
		return this.requestingPlayerId;
	}

	public Long getTargetPlayerId()
	{
		return this.targetPlayerId;
	}

	public boolean isReveal()
	{
		return this.isReveal;
	}

}
