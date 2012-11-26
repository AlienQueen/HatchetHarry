package org.alienlabs.hatchetharry.model.channel;

import javax.annotation.Nullable;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Player;

import com.google.common.base.Predicate;

public class FilterPlayerAndGamePredicate implements Predicate<Object>
{
	@Override
	public boolean apply(@Nullable final Object input)
	{
		final Player p = HatchetHarrySession.get().getPlayer();

		if (p != null)
		{
			final String playerFromSession = HatchetHarrySession.get().getPlayer().getName();
			final String playerFromEvent = ((PlayCardFromHandCometChannel)input).getPlayerName();
			final Long gameFromSession = HatchetHarrySession.get().getGameId();
			final Long gameFromEvent = ((PlayCardFromHandCometChannel)input).getGameId();

			final boolean cond = ((!playerFromSession.equals(playerFromEvent)) && (gameFromSession
					.longValue() == gameFromEvent.longValue()));

			System.out.println("Player name fromSession: " + playerFromSession + " fromEvent: "
					+ playerFromEvent);
			System.out.println("Game id fromSession: " + gameFromSession.toString()
					+ " fromEvent: " + gameFromEvent.toString());
			System.out.println("players equal?: " + playerFromSession.equals(playerFromEvent));
			System.out.println("games equal?: "
					+ (gameFromSession.longValue() == gameFromEvent.longValue()));
			System.out.println("cond: " + cond);
			return cond;
		}

		System.out.println("player is null");
		return false;
	}
}
