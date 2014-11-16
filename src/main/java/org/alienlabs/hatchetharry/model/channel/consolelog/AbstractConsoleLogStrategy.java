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

			case TOKEN_CREATION_DESTRUCTION :
				return new TokenConsoleLogStrategy(player, cond, counterName, gameId);

			case INSERT_DIVISION :
				return new InsertDivisionConsoleLogStrategy(player, gameId);

			case SHUFFLE_LIBRARY :
				return new ShuffleLibraryConsoleLogStrategy(player, gameId);

			case END_OF_TURN :
				return new EndOfTurnConsoleLogStrategy(player, gameId);

			case COMBAT :
				return new CombatConsoleLogStrategy(player, gameId, cond);

			case REVEAL_TOP_CARD_OF_LIBRARY :
				return new RevealTopLibraryCardConsoleLogStrategy(player, gameId, mc,
						numberOfCounters);

			case REVEAL_HAND :
				return new RevealHandConsoleLogStrategy(gameId, player, targetPlayerName);

			case DISCARD_AT_RANDOM :
				return new DiscardAtRandomConsoleLogStrategy(gameId, player, mc);

			case ASK_FOR_MULLIGAN :
				return new MulliganConsoleLogStrategy(gameId, player);

			case OK_FOR_MULLIGAN :
				return new OkForMulliganConsoleLogStrategy(gameId, player, targetPlayerName);

			case DONE_MULLIGAN :
				return new DoneMulliganConsoleLogStrategy(gameId, player);

			case OK_FOR_MULLIGAN_BUT_ONE_LESS :
				return new OkForMulliganButOneLessConsoleLogStrategy(gameId, player,
						targetPlayerName);

			case REFUSE_MULLIGAN :
				return new RefuseMulliganConsoleLogStrategy(gameId, player, targetPlayerName);

			default :
				throw new UnsupportedOperationException("Not implementeted!");
		}
	}
}
