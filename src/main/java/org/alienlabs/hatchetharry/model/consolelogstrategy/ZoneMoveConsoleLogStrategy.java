package org.alienlabs.hatchetharry.model.consolelogstrategy;

import org.alienlabs.hatchetharry.model.CardZone;
import org.apache.wicket.ajax.AjaxRequestTarget;

public class ZoneMoveConsoleLogStrategy extends ConsoleLogStrategy
{
	private final CardZone from;
	private final CardZone to;
	private final String mc;
	private final String player;
	private final Long gameId;

	public ZoneMoveConsoleLogStrategy(final CardZone _from, final CardZone _to, final String _mc,
			final String _player, final Long _gameId)
	{
		super();
		this.from = _from;
		this.to = _to;
		this.mc = _mc;
		this.player = _player;
		this.gameId = _gameId;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		final String message = this.player + " has put " + this.mc + " from "
				+ this.from.toString() + " to " + this.to.toString();
		super.logMessage(target, message, null, this.gameId);
	}

}
