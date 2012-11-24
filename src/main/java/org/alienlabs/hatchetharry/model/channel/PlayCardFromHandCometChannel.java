package org.alienlabs.hatchetharry.model.channel;

import java.util.UUID;

public class PlayCardFromHandCometChannel
{

	UUID uuidToLookFor;
	private final Integer cardPlaceholderId;
	private final String playerName;


	public PlayCardFromHandCometChannel(final UUID _uuidToLookFor, final String _playerName,
			final Integer _cardPlaceholderId)
	{
		this.uuidToLookFor = _uuidToLookFor;
		this.playerName = _playerName;
		this.cardPlaceholderId = _cardPlaceholderId;
	}

	public UUID getUuidToLookFor()
	{
		return this.uuidToLookFor;
	}

	public Integer getCardPlaceholderId()
	{
		return this.cardPlaceholderId;
	}

	public String getPlayerName()
	{
		return this.playerName;
	}

}
