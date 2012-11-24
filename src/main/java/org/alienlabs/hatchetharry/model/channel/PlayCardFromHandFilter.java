package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

public class PlayCardFromHandFilter implements Predicate<Object>
{
	static final Logger LOGGER = LoggerFactory.getLogger(PlayCardFromHandFilter.class);

	@Override
	public boolean apply(final Object input)
	{
		final PlayCardFromHandCometChannel event = (PlayCardFromHandCometChannel)input;

		PlayCardFromHandFilter.LOGGER.info("&&& "
				+ HatchetHarrySession.get().getPlayer().getName().equals(event.getPlayerName()));

		return HatchetHarrySession.get().getPlayer().getName().equals(event.getPlayerName());
	}

}
