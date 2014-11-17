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

	public static void post(final List<BigInteger> players, final Object... messages)
	{
		if ((messages.length > 0) && (players != null) && (!players.isEmpty()))
		{
			for (final BigInteger player : players)
			{
				final String pageUuid = HatchetHarryApplication.getCometResources().get(
						player.longValue());
				EventBusPostService.LOGGER.info("pageUuid: " + pageUuid);

				for (final Object message : messages)
				{
					try
					{
						HatchetHarryApplication.get().getEventBus().post(message, pageUuid);
					}
					catch (Exception e1)
					{
						LOGGER.error("Error posting " + message + " to bus", e1);

						try
						{
							Thread.sleep(500);
						}
						catch (InterruptedException e2)
						{
							LOGGER.error("Interrupted thread in EventBusPostService#post()", e2);
						}

						try
						{
							HatchetHarryApplication.get().getEventBus().post(message, pageUuid);
						}
						catch (Exception e3)
						{
							LOGGER.error("Fatal error posting " + message + " to bus", e3);
						}
					}
				}
			}
		}
	}

}
