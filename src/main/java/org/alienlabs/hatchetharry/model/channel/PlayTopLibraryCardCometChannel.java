package org.alienlabs.hatchetharry.model.channel;

import java.util.UUID;

public class PlayTopLibraryCardCometChannel
{
	private final Long gameId;
	private final UUID uuidToLookFor;

	public PlayTopLibraryCardCometChannel(final Long _gameId, final UUID _uuidToLookFor)
	{
		this.gameId = _gameId;
		this.uuidToLookFor = _uuidToLookFor;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public UUID getUuidToLookFor()
	{
		return this.uuidToLookFor;
	}

}
