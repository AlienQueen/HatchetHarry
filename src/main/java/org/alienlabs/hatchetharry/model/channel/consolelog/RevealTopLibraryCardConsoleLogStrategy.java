package org.alienlabs.hatchetharry.model.channel.consolelog;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class RevealTopLibraryCardConsoleLogStrategy extends ConsoleLogStrategy
{
	private final String player;
	private final Long gameId;
	private final String cardName;
	private final Long index;

	public RevealTopLibraryCardConsoleLogStrategy(final String _player, final Long _gameId,
			final String _cardName, final Long _index)
	{
		super();
		this.player = _player;
		this.gameId = _gameId;
		this.cardName = _cardName;
		this.index = _index;
	}

	@Override
	public void logToConsole(final AjaxRequestTarget target)
	{
		final String message = this.player + " reveal his (her) #" + this.index.longValue()
				+ " top library card, and it is: " + this.cardName;
		super.logMessage(target, message, null, this.gameId);
	}

}
