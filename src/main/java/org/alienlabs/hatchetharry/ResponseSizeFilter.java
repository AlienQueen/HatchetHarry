package org.alienlabs.hatchetharry;

import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.HeaderConfig;
import org.atmosphere.cpr.PerRequestBroadcastFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseSizeFilter implements PerRequestBroadcastFilter
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ResponseSizeFilter.class);

	@Override
	public BroadcastAction filter(final AtmosphereResource r, final Object originalMessage,
			final Object message)
	{
		final AtmosphereRequest request = r.getRequest();
		if ("true".equalsIgnoreCase(request.getHeader(HeaderConfig.X_ATMOSPHERE_TRACKMESSAGESIZE)))
		{
			final String msg = message.toString();
			ResponseSizeFilter.LOGGER.info("length in ResponseSizeFilter: " + msg.length());

			// if (msg.length() > 69)
			// {
			// r.getResponse().write(msg.length() + "\r\n".length() + "|");
			return new BroadcastAction(BroadcastAction.ACTION.CONTINUE, msg.length()
					+ "\r\n".length() + "|");
			// }
			// return new BroadcastAction(BroadcastAction.ACTION.ABORT,
			// message);

		}
		return new BroadcastAction(BroadcastAction.ACTION.CONTINUE, message);
	}

	@Override
	public BroadcastAction filter(final Object originalMessage, final Object message)
	{
		ResponseSizeFilter.LOGGER.info("BroadcastAction in ResponseSizeFilter");
		final String msg = message.toString();
		ResponseSizeFilter.LOGGER.info("length in filter: " + msg.length());

		// if (msg.length() > 69)
		// {
		return new BroadcastAction(BroadcastAction.ACTION.CONTINUE, message);
		// }
		// return new BroadcastAction(BroadcastAction.ACTION.ABORT, message);
	}
}
