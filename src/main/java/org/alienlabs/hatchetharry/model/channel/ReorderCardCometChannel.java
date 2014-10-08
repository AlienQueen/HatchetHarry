package org.alienlabs.hatchetharry.model.channel;

public class ReorderCardCometChannel
{
	private final Long gameId;
	private final Long playerId;
	private final String playerSide;
	private final Long deckId;

	public ReorderCardCometChannel(final Long _gameId, final Long _playerId, final Long _deckId,
			final String _playerSide)
	{
		this.gameId = _gameId;
		this.playerId = _playerId;
		this.deckId = _deckId;
		this.playerSide = _playerSide;
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

	public String getPlayerSide()
	{
		return this.playerSide;
	}

}
