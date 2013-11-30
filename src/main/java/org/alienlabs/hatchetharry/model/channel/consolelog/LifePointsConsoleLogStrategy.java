package org.alienlabs.hatchetharry.model.channel.consolelog;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class LifePointsConsoleLogStrategy extends ConsoleLogStrategy
{
	private final String player;
	private final Long lifePoints;

	public LifePointsConsoleLogStrategy(final String _player, final Long _lifePoints)
	{
		this.player = _player;
		this.lifePoints = _lifePoints;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		final String message = this.player + "'s life points total is now: "
				+ this.lifePoints.longValue();
		super.logMessage(target, message, null);
	}

}
