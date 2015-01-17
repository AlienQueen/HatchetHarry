package org.alienlabs.hatchetharry.model.consolelogstrategy;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class EndOfTurnConsoleLogStrategy extends ConsoleLogStrategy
{
	private final String player;
	private final Long gameId;

	public EndOfTurnConsoleLogStrategy(final String _player, final Long _gameId)
	{
		super();
		this.player = _player;
		this.gameId = _gameId;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		final String message = this.player + " has put an end to his (her) turn";
		super.logMessage(target, message, null, this.gameId);
	}

}
