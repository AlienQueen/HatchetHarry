package org.alienlabs.hatchetharry.serverSideTest.util;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.apache.wicket.Page;
import org.apache.wicket.atmosphere.config.AtmosphereLogLevel;
import org.apache.wicket.atmosphere.config.AtmosphereTransport;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HatchetHarryApplicationMock extends HatchetHarryApplication
{
	private static final long serialVersionUID = 1L;
	static final Logger LOGGER = LoggerFactory.getLogger(HatchetHarryApplication.class);

	@Override
	public void init()
	{
		SpringContextLoaderBaseTest.context = SpringContextLoaderBaseTest.CLASS_PATH_XML_APPLICATION_CONTEXT;
		this.getComponentInstantiationListeners().add(
				new SpringComponentInjector(this, SpringContextLoaderBaseTest.context, true));
		// We'll ask Emond to enable unit testing in EventBus
		// this.eventBus = new EventBusMock(this);
		this.eventBus = new EventBusMock(this);
		this.eventBus.addRegistrationListener(this);
		this.eventBus.getParameters().setTransport(AtmosphereTransport.WEBSOCKET);
		this.eventBus.getParameters().setLogLevel(AtmosphereLogLevel.DEBUG);
	}

	@Override
	public EventBusMock getEventBus()
	{
		return new EventBusMock(this);
	}

	@Override
	public void resourceRegistered(final String uuid, final Page page)
	{
		final Long playerId = HatchetHarrySession.get().getPlayer().getId();
		HatchetHarryApplicationMock.LOGGER.info("uuid added: " + uuid + ", for playerId: "
				+ playerId);
		HatchetHarryApplication.cometResources.put(playerId, uuid);
	}

	@Override
	public void resourceUnregistered(final String uuid)
	{
		try
		{
			final Long playerId = HatchetHarrySession.get().getPlayer().getId();
			HatchetHarryApplicationMock.LOGGER.info("uuid removed: " + uuid + ", for playerId: "
					+ playerId);
			HatchetHarryApplication.cometResources.remove(playerId);
		}
		catch (final Exception e)
		{
			HatchetHarryApplicationMock.LOGGER.error(e.getMessage());
		}
	}

}
