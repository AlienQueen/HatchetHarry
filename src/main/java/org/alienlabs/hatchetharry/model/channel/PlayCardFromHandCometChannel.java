package org.alienlabs.hatchetharry.model.channel;

import java.util.UUID;

public class PlayCardFromHandCometChannel
{

	UUID uuidToLookFor;
	private final String playerName;


	public PlayCardFromHandCometChannel(final UUID _uuidToLookFor, final String _playerName)
	{
		this.uuidToLookFor = _uuidToLookFor;
		this.playerName = _playerName;
	}

	public UUID getUuidToLookFor()
	{
		return this.uuidToLookFor;
	}

	public String getPlayerName()
	{
		return this.playerName;
	}

}
