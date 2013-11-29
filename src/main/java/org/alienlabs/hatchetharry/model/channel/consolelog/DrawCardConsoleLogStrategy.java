package org.alienlabs.hatchetharry.model.channel.consolelog;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class DrawCardConsoleLogStrategy extends ConsoleLogStrategy
{
	private final String player;

	public DrawCardConsoleLogStrategy(final String _player)
	{
		this.player = _player;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		final String message = this.player + " has drawn a card";
		super.logMessage(target, message, null);
	}

}
