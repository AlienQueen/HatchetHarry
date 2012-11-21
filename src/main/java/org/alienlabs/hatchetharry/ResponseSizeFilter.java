package org.alienlabs.hatchetharry;

import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.HeaderConfig;
import org.atmosphere.cpr.PerRequestBroadcastFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseSizeFilter implements PerRequestBroadcastFilter
{
	static final Logger LOGGER = LoggerFactory.getLogger(ResponseSizeFilter.class);

	@Override
	public BroadcastAction filter(final AtmosphereResource r, final Object originalMessage,
			final Object message)
	{
		final AtmosphereRequest request = r.getRequest();
		if ("true".equalsIgnoreCase(request.getHeader(HeaderConfig.X_ATMOSPHERE_TRACKMESSAGESIZE))
				&& (message != null) && String.class.isAssignableFrom(message.getClass()))
		{
			String msg = message.toString();

			if (msg.indexOf("var toId = \"") != -1)
			{
				String id = msg.split("var toId = \"")[1];
				id = id.split("\";")[0];

				String sessionId = null;

				try
				{
					sessionId = HatchetHarrySession.get().getId();
				}
				catch (final Exception e)
				{
					ResponseSizeFilter.LOGGER.error("error in ResponseSizeFilter", e);
				}

				if ((null != id) && (null != sessionId) && (!id.equals(sessionId)))
				{
					ResponseSizeFilter.LOGGER.info("aborting for id: " + id);
					return new BroadcastAction(BroadcastAction.ACTION.ABORT, msg);
				}
			}

			ResponseSizeFilter.LOGGER.info("continuing");
			msg = msg.length() + "<|msg|>" + msg;
			return new BroadcastAction(BroadcastAction.ACTION.CONTINUE, msg);

		}
		return new BroadcastAction(BroadcastAction.ACTION.CONTINUE, message);
	}

	@Override
	public BroadcastAction filter(final Object originalMessage, final Object message)
	{
		return new BroadcastAction(message);
	}
}
