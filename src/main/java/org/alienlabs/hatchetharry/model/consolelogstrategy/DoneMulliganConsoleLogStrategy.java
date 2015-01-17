package org.alienlabs.hatchetharry.model.consolelogstrategy;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class DoneMulliganConsoleLogStrategy extends ConsoleLogStrategy
{
	private final String player;
	private final Long gameId;

	public DoneMulliganConsoleLogStrategy(final Long _gameId, final String _player)
	{
		super();
		this.gameId = _gameId;
		this.player = _player;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		super.logMessage(target, this.player + " has done mulligan. He (she) has drawn "
				+ this.gameId + " cards.", null, this.gameId);
	}

}
