package org.alienlabs.hatchetharry.model.channel.consolelog;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class RevealHandConsoleLogStrategy extends ConsoleLogStrategy
{
	private final String player;
	private final Long gameId;

	public RevealHandConsoleLogStrategy(final Long _gameId, final String _player)
	{
		super();
		this.gameId = _gameId;
		this.player = _player;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		final String message = this.player + " reveals his (her) hand";
		super.logMessage(target, message, null, this.gameId);
	}

}
