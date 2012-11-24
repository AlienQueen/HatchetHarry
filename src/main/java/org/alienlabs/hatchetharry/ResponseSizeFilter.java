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
