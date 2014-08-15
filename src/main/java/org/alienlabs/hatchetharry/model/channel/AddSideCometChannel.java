package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.Player;

public class AddSideCometChannel {
	private final Player player;

	public AddSideCometChannel(final Player _player) {
		this.player = _player;
	}

	public Player getPlayer() {
		return this.player;
	}

}
