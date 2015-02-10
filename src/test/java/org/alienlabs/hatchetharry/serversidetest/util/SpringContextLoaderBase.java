package org.alienlabs.hatchetharry.serversidetest.util;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.zone.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Application;
import org.apache.wicket.DefaultPageManagerProvider;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.atmosphere.AtmosphereBehavior;
import org.apache.wicket.atmosphere.EventBus;
import org.apache.wicket.atmosphere.config.AtmosphereLogLevel;
import org.apache.wicket.atmosphere.config.AtmosphereTransport;
import org.apache.wicket.atmosphere.tester.AtmosphereTester;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.page.*;
import org.apache.wicket.pageStore.DefaultPageStore;
import org.apache.wicket.pageStore.IDataStore;
import org.apache.wicket.pageStore.IPageStore;
import org.apache.wicket.pageStore.memory.HttpSessionDataStore;
import org.apache.wicket.pageStore.memory.PageNumberEvictionStrategy;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.serialize.java.JavaSerializer;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.atmosphere.cpr.*;
import org.atmosphere.util.SimpleBroadcaster;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class) @ContextConfiguration(locations = {
		"classpath:applicationContext.xml",
		"classpath:applicationContextTest.xml" }) public class SpringContextLoaderBase
{
	protected static AtmosphereTester waTester;
	protected static transient WicketTester tester;
	private static HatchetHarryApplication webApp;
	protected static PersistenceService persistenceService;
	protected AtmosphereResourceFactory resourceFactory;
	protected AtmosphereConfig config;
	protected DefaultPageManagerProvider pageManagerProvider;

	@Autowired protected ApplicationContext context;

	@Before public void setUp() throws Exception
	{
		webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;

			@Override public void init()
			{
				this.getComponentInstantiationListeners()
						.add(new SpringComponentInjector(this, SpringContextLoaderBase.this.context,
								true));

				SimpleBroadcaster broadcaster = new SimpleBroadcaster();
				AtmosphereFramework framework = new AtmosphereFramework();
				SpringContextLoaderBase.this.config = new AtmosphereConfig(framework);
				BroadcasterFactory broadcasterFactory = new MyTesterBroadcasterFactory(config,
						broadcaster);
				SpringContextLoaderBase.this.resourceFactory = new AtmosphereResourceFactory(
						broadcasterFactory);
				framework.atmosphereFactory();
				broadcaster.initialize("wicket-atmosphere-tester", config);
				broadcaster.setBroadcasterConfig(
						new BroadcasterConfig(null, config, "wicket-atmosphere-tester"));
				this.eventBus = new EventBus(this, broadcaster);
				this.eventBus.addRegistrationListener(this);
				this.eventBus.getParameters().setTransport(AtmosphereTransport.WEBSOCKET);
				this.eventBus.getParameters().setLogLevel(AtmosphereLogLevel.INFO);

				this.getMarkupSettings().setStripWicketTags(false);
				this.getDebugSettings().setOutputComponentPath(true);

				/*pageManagerProvider = new DefaultPageManagerProvider(this)
				{
					@Override public IPageManager get(IPageManagerContext _context)
					{
						IDataStore dataStore = new HttpSessionDataStore(_context,
								new PageNumberEvictionStrategy(10));
						IPageStore pageStore = new DefaultPageStore(new JavaSerializer(getName()),
								dataStore, getStoreSettings().getInmemoryCacheSize());
						return new PageStoreManager(application.getName(), pageStore, _context);
					}
				};
				setPageManagerProvider(pageManagerProvider);*/
			}
		};

		// start and render the test page
		tester = new WicketTester(webApp);
		HomePage homePage = new HomePage(new PageParameters());
		waTester = new AtmosphereTester(tester, homePage);

		EventBus.get().getBroadcaster().getBroadcasterConfig().getAtmosphereConfig()
				.resourcesFactory().registerUuidForFindCandidate(
				EventBus.get().getBroadcaster().getBroadcasterConfig().getAtmosphereConfig()
						.resourcesFactory()
						.create(this.config, AtmosphereBehavior.getUUID(homePage)));
		tester.startPage(homePage);
		webApp.getPageManagerProvider().get(new DefaultPageManagerContext()).touchPage(homePage);
		HatchetHarrySession.get().getPageManager().touchPage(homePage);

		persistenceService = this.context.getBean(PersistenceService.class);
		Assert.assertNotNull(persistenceService);
	}

	@After public void tearDown()
	{
		webApp.newSession(tester.getRequestCycle().getRequest(),
				tester.getRequestCycle().getResponse());
		persistenceService.resetDb();
	}

	public void startAGameAndPlayACard(final String... pageParameters) throws Exception
	{
		// Create game
		final String paramName = pageParameters.length > 1 ? pageParameters[0] : "";
		final String paramValue = pageParameters.length > 1 ? pageParameters[1] : "";
		SpringContextLoaderBase.tester
				.startPage(new HomePage(new PageParameters().add(paramName, paramValue)));
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		SpringContextLoaderBase.tester.assertComponent("createMatchLink", AjaxLink.class);
		SpringContextLoaderBase.tester.clickLink("createMatchLink", true);

		final FormTester createGameForm = SpringContextLoaderBase.tester
				.newFormTester("createMatchWindow:content:form");
		createGameForm.setValue("name", "Zala");
		createGameForm.setValue("sideInput", "1");
		createGameForm.setValue("deckParent:decks", "1");
		createGameForm.setValue("formats", "1");
		createGameForm.setValue("numberOfPlayers", "2");

		if ((pageParameters.length > 0) && ("ajaxSubmit".equals(pageParameters[0])))
		{
			SpringContextLoaderBase.tester
					.executeAjaxEvent("createMatchWindow:content:form:submit", "onclick");
		}
		else
		{
			createGameForm.submit();
		}

		final PlayCardFromHandBehavior pcfhb = getFirstPlayCardFromHandBehavior();
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		Player p = SpringContextLoaderBase.persistenceService
				.getAllPlayersOfGame(HatchetHarrySession.get().getGameId().longValue()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());

		// For the moment, we should have no card in the battlefield
		final Long gameId = HatchetHarrySession.get().getGameId();
		final List<MagicCard> allCardsInBattlefield = SpringContextLoaderBase.persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		// Play a card
		SpringContextLoaderBase.tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(0).getUuid());
		SpringContextLoaderBase.tester.executeBehavior(pcfhb);

		// One card on the battlefield, 6 in the hand
		Assert.assertEquals(1,
				SpringContextLoaderBase.persistenceService.getAllCardsInBattlefieldForAGame(gameId)
						.size());
		Assert.assertEquals(6, SpringContextLoaderBase.persistenceService
				.getAllCardsInHandForAGameAndADeck(gameId, p.getDeck().getDeckId()).size());

		// We still should not have more cards that the number of cards in the
		// deck
		p = SpringContextLoaderBase.persistenceService
				.getAllPlayersOfGame(HatchetHarrySession.get().getGameId().longValue()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());
	}

	// Retrieve PlayCardFromHandBehavior
	protected static PlayCardFromHandBehavior getFirstPlayCardFromHandBehavior()
	{
		tester.assertComponent("galleryParent:gallery:handCards:0", ListItem.class);
		final ListItem<MagicCard> playCardLink = (ListItem<MagicCard>)tester
				.getComponentFromLastRenderedPage("galleryParent:gallery:handCards:0");
		final PlayCardFromHandBehavior b = (PlayCardFromHandBehavior)playCardLink.getBehaviors()
				.get(0);
		Assert.assertNotNull(b);
		return b;
	}
}

class MyTesterBroadcasterFactory extends DefaultBroadcasterFactory
{
	private final SimpleBroadcaster singleBroadcaster;

	MyTesterBroadcasterFactory(AtmosphereConfig c, SimpleBroadcaster broadcaster)
	{
		super(SimpleBroadcaster.class,
				BroadcasterLifeCyclePolicy.ATMOSPHERE_RESOURCE_POLICY.NEVER.name(), c);

		this.singleBroadcaster = broadcaster;

		// expose myself as BroadcasterFactory.getDefault();
		factory = this;
	}

	@SuppressWarnings("unchecked") @Override public <T extends Broadcaster> T lookup(Class<T> c,
			Object id, boolean createIfNull, boolean unique)
	{
		return (T)singleBroadcaster;
	}
}
