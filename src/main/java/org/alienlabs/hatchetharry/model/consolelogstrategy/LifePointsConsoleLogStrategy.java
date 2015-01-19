package org.alienlabs.hatchetharry.model.consolelogstrategy;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class LifePointsConsoleLogStrategy extends ConsoleLogStrategy
{
	private static final long serialVersionUID = 1L;

	private final String player;
	private final Long gameId;
	private final Long lifePoints;

	public LifePointsConsoleLogStrategy(final String _player, final Long _lifePoints,
			final Long _gameId)
	{
		super();
		this.player = _player;
		this.lifePoints = _lifePoints;
		this.gameId = _gameId;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		final String message = this.player + "'s life points total is now: "
				+ this.lifePoints.longValue();
		super.logMessage(target, message, null, this.gameId);
	}

}
