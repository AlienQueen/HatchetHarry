package org.alienlabs.hatchetharry.serverSideTest.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.atmosphere.EventBus;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.atmosphere.cpr.AsyncSupport;
import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceFactory;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.util.SimpleBroadcaster;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContextLoaderBaseTest
{
	public static final ClassPathXmlApplicationContext CLASS_PATH_XML_APPLICATION_CONTEXT = new ClassPathXmlApplicationContext(
			new String[] { "applicationContext.xml", "applicationContextTest.xml" });
	protected static transient WicketTester tester;
	protected static HatchetHarryApplication webApp;
	public static transient ApplicationContext context;
	protected static String pageDocument;
	protected static PersistenceService persistenceService;

	@BeforeClass
	public static void setUpBeforeClass()
	{
		SpringContextLoaderBaseTest.webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;


			@Override
			public void init()
			{
				SpringContextLoaderBaseTest.context = SpringContextLoaderBaseTest.CLASS_PATH_XML_APPLICATION_CONTEXT;
				this.getComponentInstantiationListeners()
						.add(new SpringComponentInjector(this, SpringContextLoaderBaseTest.context,
								true));
				// We'll ask Emond to enable unit testing in EventBus
				// this.eventBus = new EventBusMock(this);
			}

			@Override
			public EventBus getEventBus()
			{
				return new EventBusMock(this);
			}
		};

		SpringContextLoaderBaseTest.tester = new WicketTester(SpringContextLoaderBaseTest.webApp);

		SpringContextLoaderBaseTest.persistenceService = SpringContextLoaderBaseTest.context
				.getBean(PersistenceService.class);

		// start and render the test page
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);

		// assert rendered page class
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		SpringContextLoaderBaseTest.pageDocument = SpringContextLoaderBaseTest.tester
				.getLastResponse().getDocument();
	}

	@AfterClass
	public static void tearDownAfterClass()
	{
		SpringContextLoaderBaseTest.context.getBean(PersistenceService.class).resetDb();
	}

	public static Long startAGameAndPlayACard(final WicketTester _tester,
			final ApplicationContext _context)
	{
		// Create game
		_tester.assertComponent("createGameLink", AjaxLink.class);
		_tester.clickLink("createGameLink", true);

		final FormTester createGameForm = _tester.newFormTester("createGameWindow:content:form");
		createGameForm.setValue("name", "Zala");
		createGameForm.setValue("sideInput", "0");
		createGameForm.setValue("deckParent:decks", "0");
		createGameForm.submit();

		Player p = SpringContextLoaderBaseTest.persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());


		// Retrieve PlayCardFromHandBehavior
		_tester.assertComponent("playCardPlaceholder", WebMarkupContainer.class);
		_tester.assertComponent("playCardPlaceholder:playCardLink", WebMarkupContainer.class);
		final WebMarkupContainer playCardLink = (WebMarkupContainer)_tester
				.getComponentFromLastRenderedPage("playCardPlaceholder:playCardLink");
		final PlayCardFromHandBehavior pcfhb = (PlayCardFromHandBehavior)playCardLink
				.getBehaviors().get(0);

		// For the moment, we should have no card in the battlefield
		final Long gameId = HatchetHarrySession.get().getGameId();
		final List<MagicCard> allCardsInBattlefield = SpringContextLoaderBaseTest.persistenceService
				.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		// Play a card
		_tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(0).getUuid());
		_tester.executeBehavior(pcfhb);

		// We still should not have more cards that the number of cards in the
		// deck
		p = SpringContextLoaderBaseTest.persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());

		return gameId;
	}

}

class EventBusMock extends EventBus
{
	private final List<Object> events = new ArrayList<Object>();
	static AtmosphereFramework f;

	static
	{
		EventBusMock.f = new AtmosphereFramework()
		{

			@Override
			public boolean isShareExecutorServices()
			{
				return true;
			}
		};

		EventBusMock.f.setBroadcasterFactory(new MyBroadcasterFactory());
		EventBusMock.f.setAsyncSupport(Mockito.mock(AsyncSupport.class));
		try
		{
			EventBusMock.f.init(new ServletConfig()
			{
				@Override
				public String getServletName()
				{
					return "void";
				}

				@Override
				public ServletContext getServletContext()
				{
					return Mockito.mock(ServletContext.class);
				}

				@Override
				public String getInitParameter(final String name)
				{
					return null;
				}

				@Override
				public Enumeration<String> getInitParameterNames()
				{
					return null;
				}
			});
		}
		catch (final ServletException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertNotNull(EventBusMock.f.getBroadcasterFactory());
	}

	public EventBusMock(final WebApplication application)
	{
		super(application, EventBusMock.f.getBroadcasterFactory().get());
	}

	@Override
	public void post(final Object event, final String resourceUuid)
	{
		this.events.add(event);

		final AtmosphereResource resource = AtmosphereResourceFactory.getDefault().find(
				resourceUuid);
		if (resource != null)
		{
			this.post(event, resource);
		}
	}

	public List<Object> getEvents()
	{
		return this.events;
	}

}

final class MyBroadcasterFactory extends BroadcasterFactory
{

	@Override
	public Broadcaster get()
	{
		return null;
	}

	@Override
	public Broadcaster get(final Object id)
	{
		return null;
	}

	@Override
	public <T extends Broadcaster> T get(final Class<T> c, final Object id)
	{
		return null;
	}

	@Override
	public void destroy()
	{

	}

	@Override
	public boolean add(final Broadcaster b, final Object id)
	{
		return false;
	}

	@Override
	public boolean remove(final Broadcaster b, final Object id)
	{
		return false;
	}

	@Override
	public <T extends Broadcaster> T lookup(final Class<T> c, final Object id)
	{
		return null;
	}

	@Override
	public <T extends Broadcaster> T lookup(final Class<T> c, final Object id,
			final boolean createIfNull)
	{
		return null;
	}

	@Override
	public <T extends Broadcaster> T lookup(final Object id)
	{
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Broadcaster> T lookup(final Object id, final boolean createIfNull)
	{
		final T sb = (T)new SimpleBroadcaster();
		final AtmosphereConfig conf = new AtmosphereConfig(EventBusMock.f);
		Assert.assertNotNull(conf.framework());
		Assert.assertTrue(conf.framework().isShareExecutorServices());
		try
		{
			sb.initialize("/*", new URI("/"), conf);
		}
		catch (final URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb;
	}

	@Override
	public void removeAllAtmosphereResource(final AtmosphereResource r)
	{

	}

	@Override
	public boolean remove(final Object id)
	{
		return false;
	}

	@Override
	public Collection<Broadcaster> lookupAll()
	{
		final SimpleBroadcaster sb = new SimpleBroadcaster();
		final Collection<Broadcaster> all = new ArrayList<Broadcaster>();
		all.add(sb);
		return all;
	}

}
