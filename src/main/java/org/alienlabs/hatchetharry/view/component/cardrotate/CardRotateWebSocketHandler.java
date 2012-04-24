package org.alienlabs.hatchetharry.view.component.cardrotate;

import java.io.Serializable;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.websocket.WebSocket;
import org.atmosphere.websocket.WebSocketEventListenerAdapter;
import org.atmosphere.websocket.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CardRotateWebSocketHandler extends WebSocketHandler
		implements
			Serializable,
			AtmosphereResourceEventListener
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(CardRotateWebSocketHandler.class);

	private final String message;

	public CardRotateWebSocketHandler(final String _message)
	{
		this.message = _message;
	}

	@Override
	public void onTextMessage(final WebSocket webSocket, final String _message)
	{
		final AtmosphereResource r = webSocket.resource();
		final Broadcaster b = this.lookupBroadcaster(r.getRequest().getPathInfo());

		if ((_message != null) && (_message.indexOf("message") != -1))
		{
			b.broadcast(_message.substring("message=".length()));
			CardRotateWebSocketHandler.LOGGER.info("# card rotate message 1");
		}
		CardRotateWebSocketHandler.LOGGER.info("# card rotate message 2");
	}

	@Override
	public void onOpen(final WebSocket webSocket)
	{
		// Accept the handshake by suspending the response.
		final AtmosphereResource r = webSocket.resource();
		final Broadcaster b = this.lookupBroadcaster(r.getRequest().getPathInfo());
		r.setBroadcaster(b);
		r.addEventListener(new WebSocketEventListenerAdapter());
		r.suspend(-1);
		b.broadcast(this.message);
		CardRotateWebSocketHandler.LOGGER.info("# card rotate onOpen");
	}

	Broadcaster lookupBroadcaster(final String pathInfo)
	{
		final String[] decodedPath = pathInfo.split("/");
		final Broadcaster b = BroadcasterFactory.getDefault().lookup(
				decodedPath[decodedPath.length - 1], true);
		b.setID("cardRotate");
		return b;
	}

	@Override
	public void onSuspend(final AtmosphereResourceEvent event)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume(final AtmosphereResourceEvent event)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnect(final AtmosphereResourceEvent event)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onBroadcast(final AtmosphereResourceEvent event)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onThrowable(final AtmosphereResourceEvent event)
	{
		// TODO Auto-generated method stub

	}

}
