package org.alienlabs.hatchetharry.model.channel;

import java.util.UUID;

public class PlayCardFromHandCometChannel
{

	UUID uuidToLookFor;
	private final String playerName;
	private final Integer placeholderId;


	public PlayCardFromHandCometChannel(final UUID _uuidToLookFor, final String _playerName,
			final Integer _placeholderId)
	{
		this.uuidToLookFor = _uuidToLookFor;
		this.playerName = _playerName;
		this.placeholderId = _placeholderId;
	}

	public UUID getUuidToLookFor()
	{
		return this.uuidToLookFor;
	}

	public String getPlayerName()
	{
		return this.playerName;
	}

	public Integer getPlaceholderId()
	{
		return this.placeholderId;
	}

}
