package org.alienlabs.hatchetharry.model.channel;


public class RevealTopLibraryCardCometChannel
{
	private final String playerName;

	public RevealTopLibraryCardCometChannel(final String _playerName)
	{
		this.playerName = _playerName;
	}

	public String getPlayerName()
	{
		return this.playerName;
	}

}
