package org.alienlabs.hatchetharry.model.consolelogstrategy;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class DiscardAtRandomConsoleLogStrategy extends ConsoleLogStrategy
{
	private static final long serialVersionUID = 1L;

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
				+ " discards a card at random from his (her) hand, and it is: &&&"
				, null, this.gameId, this.discardedCardName);
	}

}
