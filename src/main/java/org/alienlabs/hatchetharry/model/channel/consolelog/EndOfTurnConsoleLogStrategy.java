package org.alienlabs.hatchetharry.model.channel.consolelog;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;

public class EndOfTurnConsoleLogStrategy extends ConsoleLogStrategy
{
	private final String player;
	private final Long gameId;

	public EndOfTurnConsoleLogStrategy(final String _player, final Long _gameId)
	{
		super();
		this.player = _player;
		this.gameId = _gameId;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		final String message = this.player + " has put an end to his (her) turn";
		super.logMessage(target, message, null, this.gameId);

		final WebMarkupContainer dummy = new WebMarkupContainer("dummy");
		dummy.setOutputMarkupId(true);
		dummy.setParent(target.getPage());
		target.add(dummy);
	}

}
