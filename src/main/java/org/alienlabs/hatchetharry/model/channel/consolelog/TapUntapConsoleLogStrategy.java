package org.alienlabs.hatchetharry.model.channel.consolelog;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class TapUntapConsoleLogStrategy extends ConsoleLogStrategy
{
	private final Boolean cond;
	private final String mc;
	private final String player;

	public TapUntapConsoleLogStrategy(final Boolean _cond, final String _mc, final String _player)
	{
		this.cond = _cond;
		this.mc = _mc;
		this.player = _player;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		String message = "";

		if (null == this.mc)
		{
			message = this.player + " has untapped all his (her) permanents";
		}

		super.logMessage(target, message);
	}

}
