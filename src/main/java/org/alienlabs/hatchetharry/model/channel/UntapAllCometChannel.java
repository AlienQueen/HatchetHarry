package org.alienlabs.hatchetharry.model.channel;

import java.util.List;

import org.alienlabs.hatchetharry.model.MagicCard;

public class UntapAllCometChannel
{

	private final Long gameId;
	private final Long playerId;
	private final Long deckId;
	private final String playerName;
	private final List<MagicCard> cardsToUntap;

	public UntapAllCometChannel(final Long _gameId, final Long _playerId, final Long _deckId,
			final String _playerName, final List<MagicCard> _cardsToUntap)
	{
		this.gameId = _gameId;
		this.playerId = _playerId;
		this.deckId = _deckId;
		this.playerName = _playerName;
		this.cardsToUntap = _cardsToUntap;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public Long getPlayerId()
	{
		return this.playerId;
	}

	public Long getDeckId()
	{
		return this.deckId;
	}

	public String getPlayerName()
	{
		return this.playerName;
	}

	public List<MagicCard> getCardsToUntap()
	{
		return this.cardsToUntap;
	}

}
