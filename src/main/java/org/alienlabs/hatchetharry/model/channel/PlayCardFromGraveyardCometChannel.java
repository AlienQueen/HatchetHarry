package org.alienlabs.hatchetharry.model.channel;

import java.util.UUID;

public class PlayCardFromGraveyardCometChannel
{

	UUID uuidToLookFor;
	private final String playerName;
	private final Long gameId;


	public PlayCardFromGraveyardCometChannel(final UUID _uuidToLookFor, final String _playerName,
			final Long _gameId)
	{
		this.uuidToLookFor = _uuidToLookFor;
		this.playerName = _playerName;
		this.gameId = _gameId;
	}

	public UUID getUuidToLookFor()
	{
		return this.uuidToLookFor;
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
