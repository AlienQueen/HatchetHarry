package org.alienlabs.hatchetharry.model.channel;

public class AcceptEndTurnCometChannel
{
	private final boolean shouldActivateAcceptEndTurnLink;

	public AcceptEndTurnCometChannel(final boolean _shouldActivateAcceptEndTurnLink)
	{
		this.shouldActivateAcceptEndTurnLink = _shouldActivateAcceptEndTurnLink;
	}

	public boolean isShouldActivateAcceptEndTurnLink()
	{
		return this.shouldActivateAcceptEndTurnLink;
	}

}
