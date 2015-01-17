package org.alienlabs.hatchetharry.model.consolelogstrategy;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class RefuseMulliganConsoleLogStrategy extends ConsoleLogStrategy
{
	private final String player;
	private final Long gameId;
	private final String targetPlayer;

	public RefuseMulliganConsoleLogStrategy(final Long _gameId, final String _player,
			final String _targetPlayer)
	{
		super();
		this.gameId = _gameId;
		this.player = _player;
		this.targetPlayer = _targetPlayer;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		super.logMessage(target, this.player + " disagrees for " + this.targetPlayer
				+ " to do mulligan.", null, this.gameId);
	}

}
