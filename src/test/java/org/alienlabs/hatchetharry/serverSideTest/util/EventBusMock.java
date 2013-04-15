package org.alienlabs.hatchetharry.serverSideTest.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.atmosphere.EventBus;
import org.apache.wicket.protocol.http.WebApplication;

public class EventBusMock extends EventBus
{
	private static List<Object> messages = new ArrayList<Object>();
	private static EventBus eventBus;

	public EventBusMock(final WebApplication application)
	{
		super();
		EventBusMock.eventBus = this;
	}

	@Override
	public void post(final Object event, final String resourceUuid)
	{
		EventBusMock.messages.add(event);
	}

	public static EventBus get()
	{
		return EventBusMock.eventBus;
	}

	public static List<Object> getMessages()
	{
		return EventBusMock.messages;
	}


}
