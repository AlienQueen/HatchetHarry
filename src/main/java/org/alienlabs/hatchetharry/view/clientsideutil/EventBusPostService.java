package org.alienlabs.hatchetharry.view.clientsideutil;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.apache.wicket.atmosphere.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by nostromo on 21/08/14.
 */
public class EventBusPostService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(EventBusPostService.class);

	private EventBusPostService()
	{
	}

	public static void post(final List<BigInteger> players, final Object... messages)
	{
		if ((messages.length > 0) && (players != null) && (!players.isEmpty()))
		{
			for (final BigInteger player : players)
			{
				final String pageUuid = HatchetHarryApplication.getCometResources().get(
						Long.valueOf(player.longValue()));
				EventBusPostService.LOGGER.info("pageUuid: " + pageUuid);

				for (final Object message : messages)
				{
					System.out.println("### " + message);
					try
					{
						EventBus.get().post(message, pageUuid);
					}
					catch (final Exception e1)
					{
						LOGGER.error("Error posting " + message + " to bus", e1);

						try
						{
							Thread.sleep(500);
						}
						catch (final InterruptedException e2)
						{
							LOGGER.error("Interrupted thread in EventBusPostService#post()", e2);
						}

						try
						{
							EventBus.get().post(message, pageUuid);
						}
						catch (final Exception e3)
						{
							LOGGER.error("Fatal error posting " + message + " to bus", e3);
						}
					}
				}
			}
		}
	}

}
