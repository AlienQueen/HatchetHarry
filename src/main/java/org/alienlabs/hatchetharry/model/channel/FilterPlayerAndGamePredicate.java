package org.alienlabs.hatchetharry.model.channel;

import javax.annotation.Nullable;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

public class FilterPlayerAndGamePredicate implements Predicate<Object>
{
	static final Logger LOGGER = LoggerFactory.getLogger(FilterPlayerAndGamePredicate.class);

	@Override
	public boolean apply(@Nullable final Object input)
	{
		final HatchetHarrySession session = HatchetHarrySession.get();
		final Player p = session.getPlayer();

		if (p != null)
		{
			final String playerFromSession = p.getName();
			final String playerFromEvent = ((PlayCardFromHandCometChannel)input).getPlayerName();
			final long gameFromSession = session.getGameId().longValue();
			final Long gameFromEvent = ((PlayCardFromHandCometChannel)input).getGameId();

			final boolean cond = ((!playerFromSession.equals(playerFromEvent)) && (gameFromSession == gameFromEvent));

			FilterPlayerAndGamePredicate.LOGGER.info("Player name fromSession: "
					+ playerFromSession + " fromEvent: " + playerFromEvent);
			FilterPlayerAndGamePredicate.LOGGER.info("Game id fromSession: " + gameFromSession
					+ " fromEvent: " + gameFromEvent.toString());
			FilterPlayerAndGamePredicate.LOGGER.info("players equal?: "
					+ playerFromSession.equals(playerFromEvent));
			FilterPlayerAndGamePredicate.LOGGER.info("games equal?: "
					+ (gameFromSession == gameFromEvent.longValue()));
			FilterPlayerAndGamePredicate.LOGGER.info("cond: " + cond);
			return playerFromSession.equals("a");
		}

		FilterPlayerAndGamePredicate.LOGGER.info("player is null");
		return false;
	}
}
