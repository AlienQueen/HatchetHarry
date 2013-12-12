package org.alienlabs.hatchetharry.model.channel.consolelog;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class CounterConsoleLogStrategy extends ConsoleLogStrategy
{
	private final String mc;
	private final String player;
	private final String counterName;
	private final Long numberOfCounters;
	private final String targetPlayerName;
	private final Long gameId;

	public CounterConsoleLogStrategy(final String _mc, final String _player,
			final String _counterName, final Long _numberOfCounters,
			final String _targetPlayerName, final Long _gameId)
	{
		super();
		this.mc = _mc;
		this.player = _player;
		this.counterName = _counterName;
		this.numberOfCounters = _numberOfCounters;
		this.targetPlayerName = _targetPlayerName;
		this.gameId = _gameId;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		String message = this.player + " has ";

		if (this.numberOfCounters.longValue() != 0l)
		{
			message += "put " + this.numberOfCounters.longValue() + " '" + this.counterName
					+ "' counter(s) ";
		}
		else
		{
			message += "cleared '" + this.counterName + "' counters ";
		}

		message += "on " + this.targetPlayerName + "'s card or token: " + this.mc;

		super.logMessage(target, message, null, this.gameId);
	}

}
