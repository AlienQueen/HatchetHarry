package org.alienlabs.hatchetharry.model.channel.consolelog;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class DrawCardConsoleLogStrategy extends ConsoleLogStrategy {
	private final String player;
	private final Long gameId;

	public DrawCardConsoleLogStrategy(final String _player, final Long _gameId) {
		super();
		this.player = _player;
		this.gameId = _gameId;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target) {
		final String message = this.player + " has drawn a card";
		super.logMessage(target, message, null, this.gameId);
	}

}
