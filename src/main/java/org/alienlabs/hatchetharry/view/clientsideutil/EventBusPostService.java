package org.alienlabs.hatchetharry.view.clientsideutil;

import java.math.BigInteger;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nostromo on 21/08/14.
 */
public class EventBusPostService
{
	static final Logger LOGGER = LoggerFactory.getLogger(EventBusPostService.class);

	public static void post(List<BigInteger> players, Object... messages)
	{
		if ((messages.length > 0) && (players != null) && (!players.isEmpty()))
		{
			for (BigInteger player : players)
			{
				final String pageUuid = HatchetHarryApplication.getCometResources().get(
					player.longValue());
				EventBusPostService.LOGGER.info("pageUuid: " + pageUuid);
				for (Object message : messages)
				{
					HatchetHarryApplication.get().getEventBus().post(message, pageUuid);
				}
			}
		}
	}
}
