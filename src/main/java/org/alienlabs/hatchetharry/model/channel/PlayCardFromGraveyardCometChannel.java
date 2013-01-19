package org.alienlabs.hatchetharry.model.channel;

import java.util.UUID;

public class PlayCardFromGraveyardCometChannel
{

	UUID uuidToLookFor;
	private final Long cardPlaceholderId;
	private final String playerName;
	private final Long gameId;


	public PlayCardFromGraveyardCometChannel(final UUID _uuidToLookFor, final String _playerName,
			final Long _gameId, final Long _cardPlaceholderId)
	{
		this.uuidToLookFor = _uuidToLookFor;
		this.playerName = _playerName;
		this.gameId = _gameId;
		this.cardPlaceholderId = _cardPlaceholderId;
	}

	public UUID getUuidToLookFor()
	{
		return this.uuidToLookFor;
	}

	public Long getCardPlaceholderId()
	{
		return this.cardPlaceholderId;
	}

	public String getPlayerName()
	{
		return this.playerName;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

}
