package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.consolelogstrategy.ConsoleLogStrategy;

public class ConsoleLogCometChannel
{
	private final ConsoleLogStrategy logger;

	public ConsoleLogCometChannel(final ConsoleLogStrategy _logger)
	{
		this.logger = _logger;
	}

	public ConsoleLogStrategy getLogger()
	{
		return this.logger;
	}

}
