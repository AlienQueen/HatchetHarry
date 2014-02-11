package org.alienlabs.hatchetharry.model.channel.consolelog;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class DiscardAtRandomConsoleLogStrategy extends ConsoleLogStrategy
{
	private final String player;
	private final Long gameId;
	private final String discardedCardName;

	public DiscardAtRandomConsoleLogStrategy(final Long _gameId, final String _player,
			final String _discardedCardName)
	{
		super();
		this.gameId = _gameId;
		this.player = _player;
		this.discardedCardName = _discardedCardName;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		super.logMessage(target, this.player
				+ " discards a card at random form his hand, and it is: " + this.discardedCardName,
				null, this.gameId);
	}

}
