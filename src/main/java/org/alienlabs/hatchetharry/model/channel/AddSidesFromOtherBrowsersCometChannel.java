package org.alienlabs.hatchetharry.model.channel;

import java.util.List;

import org.alienlabs.hatchetharry.model.Player;

public class AddSidesFromOtherBrowsersCometChannel
{
	private final Player player;
	private final List<Player> opponents;

	public AddSidesFromOtherBrowsersCometChannel(final Player _player, final List<Player> _opponents)
	{
		this.player = _player;
		this.opponents = _opponents;

	}

	public Player getPlayer()
	{
		return this.player;
	}

	public List<Player> getOpponents()
	{
		return this.opponents;
	}

}
