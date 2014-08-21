package org.alienlabs.hatchetharry.model.channel.consolelog;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class MulliganConsoleLogStrategy extends ConsoleLogStrategy
{
	private final String player;
	private final Long gameId;

	public MulliganConsoleLogStrategy(final Long _gameId, final String _player)
	{
		super();
		this.gameId = _gameId;
		this.player = _player;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		super.logMessage(target, this.player + " asks for a mulligan. He (she) would like to draw "
			+ this.gameId + " cards.", null, this.gameId);
	}

}
