package org.alienlabs.hatchetharry.model.consolelogstrategy;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class TapUntapConsoleLogStrategy extends ConsoleLogStrategy
{
	private static final long serialVersionUID = 1L;

	private final Boolean cond;
	private final String mc;
	private final String player;
	private final Boolean clearConsole;
	private final Long gameId;

	public TapUntapConsoleLogStrategy(final Boolean _cond, final String _mc, final String _player,
			final Boolean _clearConsole, final Long _gameId)
	{
		super();
		this.cond = _cond;
		this.mc = _mc;
		this.player = _player;
		this.clearConsole = _clearConsole;
		this.gameId = _gameId;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		String message = "";

		if ((null == this.mc) && (null != this.clearConsole)
				&& (!this.clearConsole))
		{
			message = this.player + " has untapped all his (her) permanents";
			super.logMessage(target, message, Boolean.FALSE, this.gameId);
		}
		else if ((null != this.mc) && (null == this.clearConsole) && (null != this.cond))
		{
			message = this.player + " has " + (this.cond.booleanValue() ? "tapped " : "untapped ")
					+ " permanent " + this.mc;
			super.logMessage(target, message, null, this.gameId);
		}
	}

}
