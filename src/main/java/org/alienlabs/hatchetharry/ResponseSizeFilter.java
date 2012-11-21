package org.alienlabs.hatchetharry;

import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.HeaderConfig;
import org.atmosphere.cpr.PerRequestBroadcastFilter;

public class ResponseSizeFilter implements PerRequestBroadcastFilter
{
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

				if ((null != id) && (!id.equals(HatchetHarrySession.get().getId())))
				{
					return new BroadcastAction(BroadcastAction.ACTION.ABORT, msg);
				}
			}

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
