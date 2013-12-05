package org.alienlabs.hatchetharry.model.channel.consolelog;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class GameConsoleLogStrategy extends ConsoleLogStrategy
{
	private final String player;
	private final Long gameId;
	private final Boolean created;

	public GameConsoleLogStrategy(final String _player, final Long _gameId, final Boolean _created)
	{
		this.player = _player;
		this.gameId = _gameId;
		this.created = _created;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		final String action = ((this.created != null) && (this.created.booleanValue() == true))
				? "created "
				: "joined ";

		final String message = this.player + " has " + action + "game #" + this.gameId.longValue();
		super.logMessage(target, message, null);
	}

}
