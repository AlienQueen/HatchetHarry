package org.alienlabs.hatchetharry.model.channel;

import java.util.UUID;

public class PlayCardFromHandCometChannel
{

	UUID uuidToLookFor;
	String side;


	public PlayCardFromHandCometChannel(final UUID _uuidToLookFor, final String _side)
	{
		this.uuidToLookFor = _uuidToLookFor;
		this.side = _side;
	}

	public UUID getUuidToLookFor()
	{
		return this.uuidToLookFor;
	}

	public String getSide()
	{
		return this.side;
	}

}
