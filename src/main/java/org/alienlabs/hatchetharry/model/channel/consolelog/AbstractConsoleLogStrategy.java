package org.alienlabs.hatchetharry.model.channel.consolelog;

import org.alienlabs.hatchetharry.model.CardZone;

public class AbstractConsoleLogStrategy
{
	public static ConsoleLogStrategy chooseStrategy(final ConsoleLogType type, final CardZone from,
			final CardZone to, final Boolean cond, final String mc, final String player,
			final String counterName, final Long numberOfCounters, final String targetPlayerName,
			final Boolean clearConsole, final Long gameId)
	{
		switch (type)
		{
			case ZONE_MOVE :
				return new ZoneMoveConsoleLogStrategy(from, to, mc, player, gameId);

			case TAP_UNTAP :
				return new TapUntapConsoleLogStrategy(cond, mc, player, clearConsole, gameId);

			case COUNTER_ADD_REMOVE :
				return new CounterConsoleLogStrategy(mc, player, counterName, numberOfCounters,
						targetPlayerName, gameId);

			case DRAW_CARD :
				return new DrawCardConsoleLogStrategy(player, gameId);

			case LIFE_POINTS :
				return new LifePointsConsoleLogStrategy(player, numberOfCounters, gameId);

			case GAME :
				return new GameConsoleLogStrategy(player, cond, gameId);

			default :
				throw new UnsupportedOperationException("Not implementeted!");
		}
	}
}
