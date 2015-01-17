package org.alienlabs.hatchetharry.model.consolelogstrategy;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class GameConsoleLogStrategy extends ConsoleLogStrategy
{
	private final String player;
	private final Boolean created;
	private final Long gameId;

	public GameConsoleLogStrategy(final String _player, final Boolean _created, final Long _gameId)
	{
		super();
		this.player = _player;
		this.created = _created;
		this.gameId = _gameId;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		final String action;

		if (this.created == null)
		{
			action = "put an end to ";
		}
		else if (this.created)
		{
			action = "created ";
		}
		else
		{
			action = "joined ";
		}

		final String message = this.player + " has " + action + "game #" + this.gameId.longValue();
		super.logMessage(target, message, null, this.gameId);
	}

}
