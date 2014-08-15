package org.alienlabs.hatchetharry.model.channel.consolelog;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class RevealHandConsoleLogStrategy extends ConsoleLogStrategy {
	private final String player;
	private final Long gameId;
	private final String targetPlayerName;

	public RevealHandConsoleLogStrategy(final Long _gameId, final String _player,
										final String _targetPlayerName) {
		super();
		this.gameId = _gameId;
		this.player = _player;
		this.targetPlayerName = _targetPlayerName;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target) {
		final String message;
		if (this.targetPlayerName != null) {
			message = this.targetPlayerName + " stops looking at " + this.player + "'s hand";
		} else {
			message = this.player + " reveals his (her) hand";
		}
		super.logMessage(target, message, null, this.gameId);
	}

}
