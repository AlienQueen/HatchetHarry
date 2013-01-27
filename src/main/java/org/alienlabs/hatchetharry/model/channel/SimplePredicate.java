package org.alienlabs.hatchetharry.model.channel;

import javax.annotation.Nullable;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Player;

import com.google.common.base.Predicate;

// TODO remove me
public class SimplePredicate implements Predicate<Object>
{
	@Override
	public boolean apply(@Nullable final Object input)
	{
		final Player p = HatchetHarrySession.get().getPlayer();

		if (p != null)
		{
			final String fromSession = HatchetHarrySession.get().getPlayer().getName();
			final String fromEvent = ((JoinGameNotificationCometChannel)input).getPlayerName();
			final boolean cond = fromSession.equals(fromEvent);

			return cond;
		}

		return false;
	}
}
