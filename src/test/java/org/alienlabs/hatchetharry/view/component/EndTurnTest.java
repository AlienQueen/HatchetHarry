package org.alienlabs.hatchetharry.view.component;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.atmosphere.EventBus;
import org.apache.wicket.atmosphere.EventBusMock;
import org.apache.wicket.atmosphere.MapperContextMock;
import org.apache.wicket.atmosphere.config.AtmosphereLogLevel;
import org.apache.wicket.atmosphere.config.AtmosphereTransport;
import org.apache.wicket.core.request.mapper.IMapperContext;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EndTurnTest
{
	public static final ClassPathXmlApplicationContext CLASS_PATH_XML_APPLICATION_CONTEXT = new ClassPathXmlApplicationContext(
			new String[] { "applicationContext.xml", "applicationContextTest.xml" });
	protected static transient WicketTester tester;
	public static HatchetHarryApplication webApp;
	public static transient ApplicationContext context;
	protected static String pageDocument;
	protected static PersistenceService persistenceService;

	@BeforeClass
	public static void setUpBeforeClass()
	{
		EndTurnTest.webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;
			private MapperContextMock mapperContext;

			@Override
			public void init()
			{
				EndTurnTest.context = EndTurnTest.CLASS_PATH_XML_APPLICATION_CONTEXT;
				this.getComponentInstantiationListeners().add(
						new SpringComponentInjector(this, EndTurnTest.context, true));


				this.mapperContext = new MapperContextMock();
				this.eventBus = new EventBusMock(this, this.mapperContext);
				this.eventBus.addRegistrationListener(this);
				this.eventBus.getParameters().setTransport(AtmosphereTransport.WEBSOCKET);
				this.eventBus.getParameters().setLogLevel(AtmosphereLogLevel.DEBUG);
			}

			@Override
			public EventBus getEventBus()
			{
				return this.eventBus;
			}

			@Override
			protected IMapperContext newMapperContext()
			{
				return this.mapperContext;
			}

		};

		EndTurnTest.tester = new WicketTester(EndTurnTest.webApp);
	}

	@Test
	public void testEndTurn()
	{

		final EventBusMock bus = ((EventBusMock)EndTurnTest.webApp.getEventBus());
		bus.registerPage(bus.getResource().uuid(), EndTurnTest.tester.startPage(HomePage.class));
		EndTurnTest.tester.assertRenderedPage(HomePage.class);

		EndTurnTest.pageDocument = EndTurnTest.tester.getLastResponse().getDocument();

		EndTurnTest.tester.clickLink("endTurnPlaceholder:endTurnLink", true);

		EndTurnTest.pageDocument = EndTurnTest.tester.getLastResponse().getDocument();
		System.out.println("### " + HatchetHarryApplication.get().getEventBus());
		System.out.println("### " + EndTurnTest.pageDocument);

		System.out.println("###" + bus.getEvents().get(0));
		Assert.assertFalse("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ajax-response></ajax-response>"
				.equals(EndTurnTest.pageDocument));
		// Assert.assertNotEquals("", EndTurnTest.pageDocument);
	}
}
