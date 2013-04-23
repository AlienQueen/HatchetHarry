package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.MagicCard;


public class RevealTopLibraryCardCometChannel
{
	private final String playerName;
	private final MagicCard card;

	public RevealTopLibraryCardCometChannel(final String _playerName, final MagicCard _card)
	{
		this.playerName = _playerName;
		this.card = _card;
	}

	public String getPlayerName()
	{
		return this.playerName;
	}

	public MagicCard getCard()
	{
		return this.card;
	}

}
