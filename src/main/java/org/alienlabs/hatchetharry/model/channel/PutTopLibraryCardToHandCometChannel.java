package org.alienlabs.hatchetharry.model.channel;


public class PutTopLibraryCardToHandCometChannel
{

	private final Long playerId;
	private final Long deckId;
	private final Long gameId;


	public PutTopLibraryCardToHandCometChannel(final Long _gameId, final Long _playerId,
		final Long _deckId)
	{
		this.gameId = _gameId;
		this.playerId = _playerId;
		this.deckId = _deckId;
	}

	public Long getPlayerId()
	{
		return this.playerId;
	}

	public Long getDeckId()
	{
		return this.deckId;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

}
