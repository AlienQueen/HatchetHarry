package org.alienlabs.hatchetharry.model.consolelogstrategy;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class CombatConsoleLogStrategy extends ConsoleLogStrategy
{
	private final String player;
	private final Long gameId;
	private final Boolean cond;

	public CombatConsoleLogStrategy(final String _player, final Long _gameId, final Boolean _cond)
	{
		super();
		this.player = _player;
		this.gameId = _gameId;
		this.cond = _cond;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		final String message = this.player
				+ ((this.cond != null) && (this.cond)
						? " is declaring"
						: " has finished") + " combat";
		super.logMessage(target, message, null, this.gameId);
	}

}
