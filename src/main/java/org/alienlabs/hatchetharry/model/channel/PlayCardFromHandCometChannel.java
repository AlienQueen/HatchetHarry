package org.alienlabs.hatchetharry.model.channel;

import java.util.UUID;

public class PlayCardFromHandCometChannel
{

	UUID uuidToLookFor;
	private final String cardWicketId;
	private final String playerName;


	public PlayCardFromHandCometChannel(final UUID _uuidToLookFor, final String _playerName,
			final String _cardWicketId)
	{
		this.uuidToLookFor = _uuidToLookFor;
		this.playerName = _playerName;
		this.cardWicketId = _cardWicketId;
	}

	public UUID getUuidToLookFor()
	{
		return this.uuidToLookFor;
	}

	public String getCardWicketId()
	{
		return this.cardWicketId;
	}

	public String getPlayerName()
	{
		return this.playerName;
	}

}
