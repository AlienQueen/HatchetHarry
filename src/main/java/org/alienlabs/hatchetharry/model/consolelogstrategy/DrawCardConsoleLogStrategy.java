package org.alienlabs.hatchetharry.model.consolelogstrategy;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class DrawCardConsoleLogStrategy extends ConsoleLogStrategy
{
	private static final long serialVersionUID = 1L;

	private final String player;
	private final Long gameId;

	public DrawCardConsoleLogStrategy(final String _player, final Long _gameId)
	{
		super();
		this.player = _player;
		this.gameId = _gameId;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		final String message = this.player + " has drawn a card";
		super.logMessage(target, message, null, this.gameId);
	}

}
