package org.alienlabs.hatchetharry.model;

import java.io.Serializable;

/**
 * Created by nostromo on 16/11/14.
 */
public class PlayerAndCard implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final Player player;
	private final MagicCard card;

	public PlayerAndCard(final Player _player, final MagicCard _card)
	{
		this.player = _player;
		this.card = _card;
	}

	public Player getPlayer()
	{
		return this.player;
	}

	public MagicCard getCard()
	{
		return this.card;
	}

}
