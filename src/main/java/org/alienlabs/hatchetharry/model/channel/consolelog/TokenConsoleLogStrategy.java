package org.alienlabs.hatchetharry.model.channel.consolelog;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class TokenConsoleLogStrategy extends ConsoleLogStrategy
{
	private final String player;
	private final Boolean cond;
	private final String counterName;
	private final Long gameId;

	public TokenConsoleLogStrategy(final String _player, final Boolean _cond,
			final String _counterName, final Long _gameId)
	{
		super();
		this.player = _player;
		this.cond = _cond;
		this.counterName = _counterName;
		this.gameId = _gameId;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		final String message = this.player
				+ " has "
				+ (((this.cond != null) && (this.cond.booleanValue()))
						? "put to battlefield"
						: "destroyed") + " a token of type " + this.counterName;
		super.logMessage(target, message, null, this.gameId);
	}

}
